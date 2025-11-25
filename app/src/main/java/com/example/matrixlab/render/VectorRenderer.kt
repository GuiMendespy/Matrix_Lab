package com.example.matrixlab.render

import android.opengl.GLES20
import android.opengl.GLSurfaceView
import android.opengl.Matrix
import android.util.Log
import com.example.matrixlab.data.Vec3
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10
import kotlin.math.*
import java.util.Locale
import android.os.SystemClock
import android.os.Handler
import android.os.Looper

private const val TAG = "VectorRenderer"

class VectorRenderer : GLSurfaceView.Renderer {

    // --- matrices ---
    private val projMatrix = FloatArray(16)
    private val viewMatrix = FloatArray(16)
    private val mvpMatrix = FloatArray(16)

    // --- GL program handles ---
    private var program = 0
    private var positionHandle = -1
    private var colorHandle = -1
    private var mvpHandle = -1

    // --- state (public API modifies these) ---
    @Volatile private var vectors: List<Vec3> = listOf(Vec3(1f,1f,0f)) // now supports multiple vectors
    private var angleX = 0f   // azimuth (degrees)
    private var angleY = 20f  // elevation (degrees)
    private var zoomScale = 1.0f
    private val minZoom = 0.25f
    private val maxZoom = 4.0f

    private var viewWidth = 1
    private var viewHeight = 1

    // overlay labels callback
    var onLabelsUpdated: ((List<OverlayView.TickLabel>) -> Unit)? = null
    private val uiHandler = Handler(Looper.getMainLooper())
    private var lastLabelsPost = 0L
    private val labelsPostIntervalMs = 50L

    // constants
    private val AXIS_LENGTH = 100f
    private val BASE_RADIUS = 6f
    private val FOV = 45f
    private val NEAR = 0.1f
    private val FAR = 200f

    // temp
    @Volatile private var pendingClearColor = floatArrayOf(1f, 1f, 1f, 1f)

    // PUBLIC API
    /**
     * Replace all vectors to be drawn.
     */
    fun setVectors(list: List<Vec3>) {
        vectors = list.toList()
    }

    /** Backwards compatible single-vector setter */
    fun setVector(v: Vec3) { setVectors(listOf(v)) }

    fun applyRotation(dx: Float, dy: Float) {
        angleX = (angleX + dx * 0.5f) % 360f
        angleY = (angleY + dy * 0.5f).coerceIn(-89f, 89f)
    }
    fun applyPinchScale(scaleFactor: Float) {
        if (scaleFactor.isFinite() && scaleFactor > 0f) {
            zoomScale = (zoomScale / scaleFactor).coerceIn(minZoom, maxZoom)
        }
    }
    fun setClearColor(r: Float, g: Float, b: Float, a: Float) {
        pendingClearColor[0] = r; pendingClearColor[1] = g; pendingClearColor[2] = b; pendingClearColor[3] = a
    }

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        try {
            GLES20.glEnable(GLES20.GL_BLEND)
            GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA)
            GLES20.glEnable(GLES20.GL_DEPTH_TEST)
            GLES20.glDepthFunc(GLES20.GL_LEQUAL)

            val vs = """
                uniform mat4 uMVPMatrix;
                attribute vec4 vPosition;
                void main() {
                    gl_Position = uMVPMatrix * vPosition;
                }
            """.trimIndent()
            val fs = """
                precision mediump float;
                uniform vec4 vColor;
                void main() {
                    gl_FragColor = vColor;
                }
            """.trimIndent()

            val vsId = loadShader(GLES20.GL_VERTEX_SHADER, vs)
            val fsId = loadShader(GLES20.GL_FRAGMENT_SHADER, fs)
            if (vsId == 0 || fsId == 0) {
                Log.e(TAG, "Shader creation failed")
                program = 0
                return
            }

            program = GLES20.glCreateProgram().also {
                GLES20.glAttachShader(it, vsId)
                GLES20.glAttachShader(it, fsId)
                GLES20.glLinkProgram(it)
            }

            val linkStatus = IntArray(1)
            GLES20.glGetProgramiv(program, GLES20.GL_LINK_STATUS, linkStatus, 0)
            if (linkStatus[0] == 0) {
                val info = GLES20.glGetProgramInfoLog(program)
                Log.e(TAG, "Program link failed: $info")
                GLES20.glDeleteProgram(program)
                program = 0
            } else {
                positionHandle = GLES20.glGetAttribLocation(program, "vPosition")
                colorHandle = GLES20.glGetUniformLocation(program, "vColor")
                mvpHandle = GLES20.glGetUniformLocation(program, "uMVPMatrix")
            }
        } catch (ex: Throwable) {
            Log.e(TAG, "onSurfaceCreated exception", ex)
            program = 0
        }
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        viewWidth = width; viewHeight = height
        GLES20.glViewport(0, 0, width, height)
    }

    override fun onDrawFrame(gl: GL10?) {
        try {
            GLES20.glClearColor(1f,1f,1f,1f)
            GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT or GLES20.GL_DEPTH_BUFFER_BIT)
            if (program == 0) return

            val ratio = if (viewHeight != 0) viewWidth.toFloat() / viewHeight.toFloat() else 1f
            Matrix.perspectiveM(projMatrix, 0, FOV, ratio, NEAR, FAR)

            val radius = BASE_RADIUS / zoomScale
            val azi = Math.toRadians(angleX.toDouble())
            val elev = Math.toRadians(angleY.toDouble())
            val camX = (radius * cos(elev) * sin(azi)).toFloat()
            val camY = (radius * sin(elev)).toFloat()
            val camZ = (radius * cos(elev) * cos(azi)).toFloat()

            Matrix.setLookAtM(
                viewMatrix, 0,
                camX, camY, camZ,
                0f, 0f, 0f,
                0f, 1f, 0f
            )

            Matrix.multiplyMM(mvpMatrix, 0, projMatrix, 0, viewMatrix, 0)

            // draw scene
            drawGrid(size = 20f, spacing = computeNiceSpacingForGrid(radius))
            drawAxes()
            val labels = computeTickLabels(camX, camY, camZ)
            val now = SystemClock.uptimeMillis()
            if (labels.isNotEmpty() && now - lastLabelsPost >= labelsPostIntervalMs) {
                lastLabelsPost = now
                uiHandler.post {
                    try { onLabelsUpdated?.invoke(labels) } catch (ex: Throwable) { Log.e(TAG, "onLabelsUpdated exception", ex) }
                }
            }
            drawTicks(camX, camY, camZ)

            // DRAW ALL VECTORS (each with different color)
            drawVectors(camX, camY, camZ)
        } catch (ex: Throwable) {
            Log.e(TAG, "Exception in onDrawFrame", ex)
        }
    }

    // ---------------- scene primitives ----------------

    private fun drawAxes() {
        try {
            val axisLen = 10f * zoomScale

            // X
            drawLine(Vec3(-axisLen, 0f, 0f), Vec3(axisLen, 0f, 0f), floatArrayOf(0f, 1f, 0f, 1f))
            drawArrowHead(Vec3(axisLen, 0f, 0f), Vec3(axisLen - 0.3f * zoomScale, 0f, 0f), 0.06f * zoomScale, floatArrayOf(0f, 1f, 0f, 1f))
            drawLetterAtEnd(Vec3(axisLen + 0.1f * zoomScale, 0f, 0f), "X")

            // Y
            drawLine(Vec3(0f, -axisLen, 0f), Vec3(0f, axisLen, 0f), floatArrayOf(0f, 0f, 1f, 1f))
            drawArrowHead(Vec3(0f, axisLen, 0f), Vec3(0f, axisLen - 0.3f * zoomScale, 0f), 0.06f * zoomScale, floatArrayOf(0f, 0f, 1f, 1f))
            drawLetterAtEnd(Vec3(0f, axisLen + 0.1f * zoomScale, 0f), "Y")

            // Z
            drawLine(Vec3(0f, 0f, -axisLen), Vec3(0f, 0f, axisLen), floatArrayOf( 1f, 0f, 0f, 1f))
            drawArrowHead(Vec3(0f, 0f, axisLen), Vec3(0f, 0f, axisLen - 0.3f * zoomScale), 0.06f * zoomScale, floatArrayOf(1f, 0f, 0f, 1f))
            drawLetterAtEnd(Vec3(0f, 0f, axisLen + 0.1f * zoomScale), "Z")
        } catch (ex: Throwable) {
            Log.e(TAG, "drawAxes exception", ex)
        }
    }

    private fun drawGrid(size: Float, spacing: Float) {
        try {
            val col = floatArrayOf(0.85f, 0.85f, 0.85f, 1f)
            val half = size
            var x = -half
            while (x <= half + 0.0001f) {
                drawLine(Vec3(x, 0f, -half), Vec3(x, 0f, half), col)
                x += spacing
            }
            var z = -half
            while (z <= half + 0.0001f) {
                drawLine(Vec3(-half, 0f, z), Vec3(half, 0f, z), col)
                z += spacing
            }
        } catch (ex: Throwable) {
            Log.e(TAG, "drawGrid exception", ex)
        }
    }

    // ---------------- ticks & labels ----------------
    private fun computeTickLabels(camX: Float, camY: Float, camZ: Float): List<OverlayView.TickLabel> {
        val out = mutableListOf<OverlayView.TickLabel>()
        try {
            val physSpacing = 0.25f * zoomScale
            val axisLen = 10f * zoomScale
            val steps = (axisLen / physSpacing).toInt()

            val tickValueStep = physSpacing / zoomScale

            for (i in -steps..steps) {
                if (i == 0) continue
                val pos = i * physSpacing

                val worldX = Vec3(pos, -physSpacing * 0.45f, 0f)
                val scrX = projectWorldToScreen(worldX)
                if (scrX.isOnScreen(viewWidth, viewHeight)) {
                    val value = i * (physSpacing / zoomScale)
                    val valStr = niceValueString(value)
                    out.add(OverlayView.TickLabel(scrX.toNxNy(viewWidth, viewHeight), valStr))
                }

                val worldY = Vec3(physSpacing * 0.45f, pos, 0f)
                val scrY = projectWorldToScreen(worldY)
                if (scrY.isOnScreen(viewWidth, viewHeight)) {
                    val value = i * (physSpacing / zoomScale)
                    out.add(OverlayView.TickLabel(scrY.toNxNy(viewWidth, viewHeight), niceValueString(value)))
                }

                val worldZ = Vec3(0f, -physSpacing * 0.45f, pos)
                val scrZ = projectWorldToScreen(worldZ)
                if (scrZ.isOnScreen(viewWidth, viewHeight)) {
                    val value = i * (physSpacing / zoomScale)
                    out.add(OverlayView.TickLabel(scrZ.toNxNy(viewWidth, viewHeight), niceValueString(value)))
                }
            }
        } catch (ex: Throwable) {
            Log.e(TAG, "computeTickLabels exception", ex)
        }
        return out
    }

    private fun drawTicks(camX: Float, camY: Float, camZ: Float) {
        val spacing = 0.25f * zoomScale
        val tickSize = 0.02f * zoomScale

        val axisLen = 2f * zoomScale
        val steps = (axisLen / spacing).toInt()

        for (i in -steps..steps) {
            if (i == 0) continue

            val pos = i * spacing

            drawLine(
                Vec3(pos, -tickSize, 0f),
                Vec3(pos, tickSize, 0f),
                floatArrayOf(0f,0f,0f,1f)
            )

            drawLine(
                Vec3(-tickSize, pos, 0f),
                Vec3(tickSize, pos, 0f),
                floatArrayOf(0f,0f,0f,1f)
            )

            drawLine(
                Vec3(0f, -tickSize, pos),
                Vec3(0f, tickSize, pos),
                floatArrayOf(0f,0f,0f,1f)
            )
        }
    }

    // ---------------- vectors (multi) ----------------

    private fun drawVectors(camX: Float, camY: Float, camZ: Float) {
        try {
            val current = vectors.toList()
            val total = current.size
            if (total == 0) return

            for ((i, v) in current.withIndex()) {
                val color = colorForIndex(i, total)
                drawLine(Vec3(0f, 0f, 0f), v, color)
                val camDist = sqrt(camX*camX + camY*camY + camZ*camZ)
                drawArrowHead(v, Vec3(0f,0f,0f), 0.2f * (camDist / BASE_RADIUS), color)
            }
        } catch (ex: Throwable) {
            Log.e(TAG, "drawVectors exception", ex)
        }
    }

    // ---------------- low level drawing ----------------

    private fun drawLine(start: Vec3, end: Vec3, color: FloatArray) {
        if (program == 0) return
        try {
            val vertices = floatArrayOf(start.x, start.y, start.z, end.x, end.y, end.z)
            val fb = makeBuffer(vertices)
            GLES20.glUseProgram(program)
            bindHandles(color, fb)
            GLES20.glLineWidth(2f)
            GLES20.glDrawArrays(GLES20.GL_LINES, 0, 2)
            GLES20.glDisableVertexAttribArray(positionHandle)
        } catch (ex: Throwable) {
            Log.e(TAG, "drawLine exception", ex)
        }
    }

    private fun drawArrowHead(tip: Vec3, baseDir: Vec3, size: Float, color: FloatArray) {
        if (program == 0) return
        try {
            val dir = Vec3(tip.x - baseDir.x, tip.y - baseDir.y, tip.z - baseDir.z)
            val len = sqrt(dir.x*dir.x + dir.y*dir.y + dir.z*dir.z).coerceAtLeast(1e-6f)
            val nx = dir.x/len; val ny = dir.y/len; val nz = dir.z/len
            val sideA = 0.5f * size
            val vertices = floatArrayOf(
                tip.x, tip.y, tip.z,
                tip.x - nx*size - ny*sideA, tip.y - ny*size + nx*sideA, tip.z - nz*size,
                tip.x - nx*size + ny*sideA, tip.y - ny*size - nx*sideA, tip.z - nz*size
            )
            val fb = makeBuffer(vertices)
            GLES20.glUseProgram(program)
            bindHandles(color, fb)
            GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, 3)
            GLES20.glDisableVertexAttribArray(positionHandle)
        } catch (ex: Throwable) {
            Log.e(TAG, "drawArrowHead exception", ex)
        }
    }

    // ---------------- labels & helpers ----------------
    private fun drawLetterAtEnd(worldPos: Vec3, letter: String) {
        val screen = projectWorldToScreen(worldPos)
        if (!screen.isFinite()) return
        val sizePx = (12f * (1f / zoomScale)).coerceIn(8f, 36f)
        when (letter) {
            "X" -> drawLetterX(screen, sizePx)
            "Y" -> drawLetterY(screen, sizePx)
            "Z" -> drawLetterZ(screen, sizePx)
        }
    }

    private fun drawLetterX(screen: Vec2, sPx: Float) {
        val p1 = Vec2(screen.x - sPx, screen.y - sPx)
        val p2 = Vec2(screen.x + sPx, screen.y + sPx)
        val p3 = Vec2(screen.x - sPx, screen.y + sPx)
        val p4 = Vec2(screen.x + sPx, screen.y - sPx)
        val color = floatArrayOf(0f, 1f, 0f, 1f)
        drawRawLine(p1, p2, color)
        drawRawLine(p3, p4, color)
    }

    private fun drawLetterY(screen: Vec2, sPx: Float) {
        val topLeft = Vec2(screen.x - sPx, screen.y - sPx)
        val topRight = Vec2(screen.x + sPx, screen.y - sPx)
        val center = Vec2(screen.x, screen.y)
        val bottom = Vec2(screen.x, screen.y + sPx)
        val color = floatArrayOf(0f, 0f, 1f, 1f)
        drawRawLine(topLeft, center, color)
        drawRawLine(topRight, center, color)
        drawRawLine(center, bottom, color)
    }

    private fun drawLetterZ(screen: Vec2, sPx: Float) {
        val topLeft = Vec2(screen.x - sPx, screen.y - sPx)
        val topRight = Vec2(screen.x + sPx, screen.y - sPx)
        val botLeft = Vec2(screen.x - sPx, screen.y + sPx)
        val botRight = Vec2(screen.x + sPx, screen.y + sPx)
        val color = floatArrayOf(1f, 0f, 0f, 1f)
        drawRawLine(topLeft, topRight, color)
        drawRawLine(topRight, botLeft, color)
        drawRawLine(botLeft, botRight, color)
    }

    private fun drawRawLine(px1: Float, py1: Float, px2: Float, py2: Float, color: FloatArray) {
        if (program == 0) return
        val cx1 = (px1 / viewWidth.toFloat()) * 2f - 1f
        val cy1 = 1f - (py1 / viewHeight.toFloat()) * 2f
        val cx2 = (px2 / viewWidth.toFloat()) * 2f - 1f
        val cy2 = 1f - (py2 / viewHeight.toFloat()) * 2f
        val vertices = floatArrayOf(cx1, cy1, 0f, cx2, cy2, 0f)
        val fb = makeBuffer(vertices)
        GLES20.glUseProgram(program)
        positionHandle = GLES20.glGetAttribLocation(program, "vPosition")
        colorHandle = GLES20.glGetUniformLocation(program, "vColor")
        mvpHandle = GLES20.glGetUniformLocation(program, "uMVPMatrix")
        GLES20.glEnableVertexAttribArray(positionHandle)
        GLES20.glVertexAttribPointer(positionHandle, 3, GLES20.GL_FLOAT, false, 0, fb)
        GLES20.glUniform4fv(colorHandle, 1, color, 0)
        val identity = FloatArray(16); Matrix.setIdentityM(identity, 0)
        GLES20.glUniformMatrix4fv(mvpHandle, 1, false, identity, 0)
        GLES20.glLineWidth(2f)
        GLES20.glDrawArrays(GLES20.GL_LINES, 0, 2)
        GLES20.glDisableVertexAttribArray(positionHandle)
    }

    private fun drawRawLine(p1: Vec2, p2: Vec2, color: FloatArray) {
        drawRawLine(p1.x, p1.y, p2.x, p2.y, color)
    }

    // ---------------- projection helpers ----------------

    private fun projectWorldToScreen(p: Vec3): Vec2 {
        val tmp = FloatArray(4)
        val inVec = floatArrayOf(p.x, p.y, p.z, 1f)
        Matrix.multiplyMV(tmp, 0, mvpMatrix, 0, inVec, 0)
        if (tmp[3] == 0f) return Vec2(Float.NaN, Float.NaN)
        val ndcX = tmp[0] / tmp[3]
        val ndcY = tmp[1] / tmp[3]
        val sx = (ndcX * 0.5f + 0.5f) * viewWidth.toFloat()
        val sy = (1f - (ndcY * 0.5f + 0.5f)) * viewHeight.toFloat()
        return Vec2(sx, sy)
    }

    // ---------------- misc helpers ----------------

    private fun bindHandles(color: FloatArray, vertexBuffer: java.nio.FloatBuffer) {
        if (program == 0) return
        positionHandle = GLES20.glGetAttribLocation(program, "vPosition")
        colorHandle = GLES20.glGetUniformLocation(program, "vColor")
        mvpHandle = GLES20.glGetUniformLocation(program, "uMVPMatrix")
        GLES20.glEnableVertexAttribArray(positionHandle)
        GLES20.glVertexAttribPointer(positionHandle, 3, GLES20.GL_FLOAT, false, 0, vertexBuffer)
        GLES20.glUniform4fv(colorHandle, 1, color, 0)
        GLES20.glUniformMatrix4fv(mvpHandle, 1, false, mvpMatrix, 0)
    }

    private fun makeBuffer(vertices: FloatArray): java.nio.FloatBuffer {
        return java.nio.ByteBuffer.allocateDirect(vertices.size * 4)
            .order(java.nio.ByteOrder.nativeOrder())
            .asFloatBuffer()
            .apply {
                put(vertices)
                position(0)
            }
    }

    private fun loadShader(type: Int, code: String): Int {
        val shader = GLES20.glCreateShader(type)
        GLES20.glShaderSource(shader, code)
        GLES20.glCompileShader(shader)
        val compiled = IntArray(1)
        GLES20.glGetShaderiv(shader, GLES20.GL_COMPILE_STATUS, compiled, 0)
        if (compiled[0] == 0) {
            Log.e(TAG, "Shader compile failed: ${GLES20.glGetShaderInfoLog(shader)}")
            GLES20.glDeleteShader(shader)
            return 0
        }
        return shader
    }

    // ---------------- color generation ----------------

    /**
     * Return RGBA float array for vector index i among total vectors.
     * Uses HSV hue sweep for distinct colors.
     */
    private fun colorForIndex(i: Int, total: Int): FloatArray {
        if (total <= 1) return floatArrayOf(0f, 0f, 0f, 1f)
        val hue = (i.toFloat() / total.toFloat()) * 360f        // 0..360
        val rgb = hsvToRgb(hue, 0.7f, 0.9f)                     // saturation/val tuned
        return floatArrayOf(rgb[0], rgb[1], rgb[2], 1f)
    }

    private fun hsvToRgb(h: Float, s: Float, v: Float): FloatArray {
        val hh = ((h % 360f) + 360f) % 360f
        val c = v * s
        val x = c * (1f - abs((hh / 60f) % 2f - 1f))
        val m = v - c
        val (r1, g1, b1) = when {
            hh < 60f -> Triple(c, x, 0f)
            hh < 120f -> Triple(x, c, 0f)
            hh < 180f -> Triple(0f, c, x)
            hh < 240f -> Triple(0f, x, c)
            hh < 300f -> Triple(x, 0f, c)
            else -> Triple(c, 0f, x)
        }
        return floatArrayOf(r1 + m, g1 + m, b1 + m)
    }

    // ---------------- tick spacing & helpers ----------------
    private fun computeNiceSpacingForTicks(camX: Float, camY: Float, camZ: Float): Float {
        val camDist = sqrt(camX*camX + camY*camY + camZ*camZ)
        val desired = camDist * 0.12f
        return niceNumber(desired)
    }

    private fun computeNiceSpacingForGrid(camRadius: Float): Float {
        val desired = camRadius * 0.5f
        return niceNumber(desired).coerceAtLeast(0.5f)
    }

    private fun niceNumber(value: Float): Float {
        if (!value.isFinite() || value <= 0f) return 0.1f
        val exp = floor(log10(value.toDouble())).toInt()
        val base = 10.0.pow(exp.toDouble()).toFloat()
        val frac = value / base
        val niceFrac = when {
            frac < 1.5f -> 1f
            frac < 3.5f -> 2f
            frac < 7.5f -> 5f
            else -> 10f
        }
        return niceFrac * base
    }

    private fun niceValueString(v: Float): String {
        val av = abs(v)
        return when {
            av >= 1000f -> String.format(Locale.US, "%.0f", v)
            av >= 1f -> String.format(Locale.US, "%.2f", v).trimEnd('0').trimEnd('.')
            av >= 0.01f -> String.format(Locale.US, "%.3f", v).trimEnd('0').trimEnd('.')
            else -> String.format(Locale.US, "%.4f", v).trimEnd('0').trimEnd('.')
        }
    }

    // ---------------- small helper types & extensions ----------------

    private data class Vec2(val x: Float, val y: Float) {
        fun isOnScreen(w: Int, h: Int): Boolean {
            if (!x.isFinite() || !y.isFinite()) return false
            return x >= 0f && x <= w && y >= 0f && y <= h
        }
        fun toNxNy(w: Int, h: Int): Pair<Float, Float> {
            val nx = (x / w.toFloat()).coerceIn(0f, 1f)
            val ny = 1f - (y / h.toFloat()).coerceIn(0f, 1f)
            return Pair(nx, ny)
        }
        fun isFinite(): Boolean = x.isFinite() && y.isFinite()
    }
}
