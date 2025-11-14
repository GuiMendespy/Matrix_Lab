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
import com.google.ar.sceneform.lullmodel.Vec2

private const val TAG = "VectorRenderer"

class VectorRenderer : GLSurfaceView.Renderer {

    private val projMatrix = FloatArray(16)
    private val viewMatrix = FloatArray(16)
    private val mvpMatrix = FloatArray(16)

    private var program = 0
    private var positionHandle = -1
    private var colorHandle = -1
    private var mvpHandle = -1

    // state
    private var currentVector = Vec3(1f, 1f, 0f)
    private var angleX = 0f
    private var angleY = 0f
    private var zoomScale = 1.0f
    private val minZoom = 0.25f
    private val maxZoom = 4.0f

    private var viewWidth = 1
    private var viewHeight = 1

    // clear color stored until GL thread runs
    @Volatile private var pendingClearColor = floatArrayOf(1f, 1f, 1f, 1f) // default transparent

    // callback to update overlay (on UI thread)
    var onLabelsUpdated: ((List<OverlayView.TickLabel>) -> Unit)? = null

    // small debounce for labels publish (avoid flooding UI thread)
    private var lastLabelsPost = 0L
    private val labelsPostIntervalMs = 50L // publish at most every 50ms
    private val uiHandler = Handler(Looper.getMainLooper())

    // PUBLIC API
    fun setVector(v: Vec3) { currentVector = v }
    fun applyRotation(dx: Float, dy: Float) {
        angleX += dx * 0.5f
        angleY += dy * 0.5f
    }
    fun applyPinchScale(scaleFactor: Float) {
        if (scaleFactor.isFinite() && scaleFactor > 0f) {
            zoomScale = (zoomScale / scaleFactor).coerceIn(minZoom, maxZoom)
        }
    }
    fun setClearColor(r: Float, g: Float, b: Float, a: Float) {
        // store and apply on GL thread in onDrawFrame
        pendingClearColor[0] = r
        pendingClearColor[1] = g
        pendingClearColor[2] = b
        pendingClearColor[3] = a
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
            """
            val fs = """
                precision mediump float;
                uniform vec4 vColor;
                void main() {
                    gl_FragColor = vColor;
                }
            """
            val vsId = loadShader(GLES20.GL_VERTEX_SHADER, vs)
            val fsId = loadShader(GLES20.GL_FRAGMENT_SHADER, fs)

            if (vsId == 0 || fsId == 0) {
                Log.e(TAG, "Shader creation failed: vsId=$vsId fsId=$fsId")
                program = 0
                return
            }

            program = GLES20.glCreateProgram().also {
                GLES20.glAttachShader(it, vsId)
                GLES20.glAttachShader(it, fsId)
                GLES20.glLinkProgram(it)
            }

            // check link status
            val linkStatus = IntArray(1)
            GLES20.glGetProgramiv(program, GLES20.GL_LINK_STATUS, linkStatus, 0)
            if (linkStatus[0] == 0) {
                val info = GLES20.glGetProgramInfoLog(program)
                Log.e(TAG, "Program link failed: $info")
                GLES20.glDeleteProgram(program)
                program = 0
            } else {
                // get handles once program is valid
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
        viewWidth = width
        viewHeight = height
        GLES20.glViewport(0, 0, width, height)
    }

    override fun onDrawFrame(gl: GL10?) {
        try {
            // apply pending clear color on GL thread
            GLES20.glClearColor(1f,1f,1f,1f)
            GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT or GLES20.GL_DEPTH_BUFFER_BIT)

            if (program == 0) {
                // nothing to draw if shaders failed
                return
            }

            // projection orthographic scaled by zoomScale
            val ratio = if (viewHeight != 0) viewWidth.toFloat() / viewHeight.toFloat() else 1f
            val base = 2.2f
            val halfW = ratio * base * zoomScale
            val halfH = base * zoomScale
            Matrix.orthoM(projMatrix, 0, -halfW, halfW, -halfH, halfH, -20f, 20f)

            // cavalier shear
            val l = 0.5f
            val theta = Math.toRadians(45.0).toFloat()
            val a = l * cos(theta)
            val b = l * sin(theta)
            val shear = FloatArray(16) { 0f }.also {
                it[0] = 1f; it[5] = 1f; it[10] = 1f; it[15] = 1f
                it[8] = a; it[9] = b
            }
            val tmp = FloatArray(16)
            Matrix.multiplyMM(tmp, 0, shear, 0, projMatrix, 0)
            System.arraycopy(tmp, 0, projMatrix, 0, 16)

            // view rotates by angleX/Y (clamp big values to avoid overflow)
            val safeAngleX = angleX % 360f
            val safeAngleY = angleY % 360f
            Matrix.setIdentityM(viewMatrix, 0)
            Matrix.rotateM(viewMatrix, 0, safeAngleY, 1f, 0f, 0f)
            Matrix.rotateM(viewMatrix, 0, safeAngleX, 0f, 1f, 0f)

            Matrix.multiplyMM(mvpMatrix, 0, projMatrix, 0, viewMatrix, 0)

            // draw scene
            drawAxes()
            val labels = computeTickLabels()
            val now = SystemClock.uptimeMillis()
            if (labels.isNotEmpty() && now - lastLabelsPost >= labelsPostIntervalMs) {
                lastLabelsPost = now
                uiHandler.post {
                    try {
                        onLabelsUpdated?.invoke(labels)
                    } catch (ex: Throwable) {
                        Log.e(TAG, "onLabelsUpdated handler exception", ex)
                    }
                }
            }
            drawTicks()
            drawVector(currentVector)
        } catch (ex: Throwable) {
            // catch-all to avoid GL thread crash -> log and swallow
            Log.e(TAG, "Exception in onDrawFrame", ex)
        }
    }

    // ---------- helpers (unchanged, but defensive) ----------
    private fun drawAxes() {
        try {
            drawLine(Vec3(-2f,0f,0f), Vec3(2f,0f,0f), floatArrayOf(1f,0f,0f,1f))
            drawArrowHead(Vec3(2f,0f,0f), Vec3(1f,0f,0f), 0.06f, floatArrayOf(1f,0f,0f,1f))
            drawLetterY(Vec3(2.1f,0f,0f))

            drawLine(Vec3(0f,-2f,0f), Vec3(0f,2f,0f), floatArrayOf(0f,1f,0f,1f))
            drawArrowHead(Vec3(0f,2f,0f), Vec3(0f,1f,0f), 0.06f, floatArrayOf(0f,1f,0f,1f))
            drawLetterZ(Vec3(0f,2.1f,0f))

            drawLine(Vec3(0f,0f,-2f), Vec3(0f,0f,2f), floatArrayOf(0f,0f,1f,1f))
            drawArrowHead(Vec3(0f,0f,2f), Vec3(0f,0f,1f), 0.06f, floatArrayOf(0f,0f,1f,1f))
            drawLetterX(Vec3(0f,0f,2.1f))
        } catch (ex: Throwable) {
            Log.e(TAG, "drawAxes exception", ex)
        }
    }

    private fun computeTickLabels(): List<OverlayView.TickLabel> {
        val out = mutableListOf<OverlayView.TickLabel>()
        try {
            val baseSpacing = 0.25f
            val spacing = (baseSpacing * zoomScale).coerceAtLeast(0.05f)
            val range = 2.0f
            val steps = (range / spacing).toInt()
            val tickOffsetY = -0.12f

            val ndc = FloatArray(4)
            for (i in -steps..steps) {
                if (i == 0) continue
                val pos = i * spacing
                val valStr = String.format(Locale.US, "%.1f", pos)

                // X label position
                val pos3 = floatArrayOf(pos, tickOffsetY, 0f, 1f)
                Matrix.multiplyMV(ndc, 0, mvpMatrix, 0, pos3, 0)
                if (ndc[3] != 0f) {
                    val nx = (ndc[0] / ndc[3]) * 0.5f + 0.5f
                    val ny = (ndc[1] / ndc[3]) * 0.5f + 0.5f
                    if (nx.isFinite() && ny.isFinite()) out.add(OverlayView.TickLabel(nx to ny, valStr))
                }

                // Y label
                val pos3y = floatArrayOf(0.12f, pos, 0f, 1f)
                Matrix.multiplyMV(ndc, 0, mvpMatrix, 0, pos3y, 0)
                if (ndc[3] != 0f) {
                    val nx = (ndc[0] / ndc[3]) * 0.5f + 0.5f
                    val ny = (ndc[1] / ndc[3]) * 0.5f + 0.5f
                    if (nx.isFinite() && ny.isFinite()) out.add(OverlayView.TickLabel(nx to ny, valStr))
                }

                // Z label
                val pos3z = floatArrayOf(0.06f, 0f, pos, 1f)
                Matrix.multiplyMV(ndc, 0, mvpMatrix, 0, pos3z, 0)
                if (ndc[3] != 0f) {
                    val nx = (ndc[0] / ndc[3]) * 0.5f + 0.5f
                    val ny = (ndc[1] / ndc[3]) * 0.5f + 0.5f
                    if (nx.isFinite() && ny.isFinite()) out.add(OverlayView.TickLabel(nx to ny, valStr))
                }
            }
        } catch (ex: Throwable) {
            Log.e(TAG, "computeTickLabels exception", ex)
        }
        return out
    }

    private fun drawTicks() {
        try {
            // espaçamento fixo (independe de zoom)
            val spacing = 0.25f

            // tamanho do traço fixo (independe de zoom)
            val tickHalf = 0.02f

            val range = 2.0f
            val steps = (range / spacing).toInt()

            for (i in -steps..steps) {
                val pos = i * spacing
                if (i == 0) continue

                // Eixo X (ticks paralelos ao Y)
                drawLine(
                    Vec3(pos, -tickHalf, 0f),
                    Vec3(pos, tickHalf, 0f),
                    floatArrayOf(0f, 0f, 0f, 1f)
                )

                // Eixo Y (ticks paralelos ao X)
                drawLine(
                    Vec3(-tickHalf, pos, 0f),
                    Vec3(tickHalf, pos, 0f),
                    floatArrayOf(0f, 0f, 0f, 1f)
                )

                // Eixo Z (ticks paralelos ao Y)
                drawLine(
                    Vec3(0f, -tickHalf, pos),
                    Vec3(0f, tickHalf, pos),
                    floatArrayOf(0f, 0f, 0f, 1f)
                )
            }
        } catch (ex: Throwable) {
            Log.e(TAG, "drawTicks exception", ex)
        }
    }


    private fun drawVector(v: Vec3) {
        try {
            drawLine(Vec3(0f,0f,0f), v, floatArrayOf(0f,0f,0f,1f))
            drawArrowHead(v, Vec3(0f,0f,0f), 0.08f / zoomScale, floatArrayOf(0f,0f,0f,1f))
        } catch (ex: Throwable) {
            Log.e(TAG, "drawVector exception", ex)
        }
    }

    private fun drawLine(start: Vec3, end: Vec3, color: FloatArray) {
        if (program == 0) return
        try {
            val vertices = floatArrayOf(start.x,start.y,start.z,end.x,end.y,end.z)
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
            val vertices = floatArrayOf(
                tip.x, tip.y, tip.z,
                tip.x - nx*size - ny*size*0.5f, tip.y - ny*size + nx*size*0.5f, tip.z - nz*size,
                tip.x - nx*size + ny*size*0.5f, tip.y - ny*size - nx*size*0.5f, tip.z - nz*size
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

    private fun cavaleira(v: Vec3): Vec3 {
        return Vec3(
            v.y, // novo X vem do antigo Y
            v.z, // novo Y vem do antigo Z
            v.x  // novo Z vem do antigo X
        )
    }
    // pequeno tipo 2D (coloque dentro da classe VectorRenderer)
    private data class Vec2(val x: Float, val y: Float)

    /**
     * Projetar coordenada 3D (world) -> coordenada de tela (pixels).
     * Retorna Pair(px, py) onde px ∈ [0..viewWidth], py ∈ [0..viewHeight] (0 = topo).
     */
    private fun projectWorldToScreen(p: Vec3): Vec2 {
        val tmp = FloatArray(4) { 0f }
        val inVec = floatArrayOf(p.x, p.y, p.z, 1f)
        Matrix.multiplyMV(tmp, 0, mvpMatrix, 0, inVec, 0)

        if (tmp[3] == 0f) return Vec2(-10000f, -10000f) // fora
        val ndcX = tmp[0] / tmp[3]   // -1..1
        val ndcY = tmp[1] / tmp[3]   // -1..1

        val sx = (ndcX * 0.5f + 0.5f) * viewWidth.toFloat()
        val sy = (1f - (ndcY * 0.5f + 0.5f)) * viewHeight.toFloat() // converte Y para origem no topo

        return Vec2(sx, sy)
    }

    /**
     * Desenha uma linha 2D em coordenadas de tela (pixels). Convertido para clip-space e desenhado
     * com o mesmo shader (mas usando matriz identidade para MVP).
     *
     * px,py são pixels (origin top-left). z fixado em 0.
     */
    private fun drawRawLine(px1: Float, py1: Float, px2: Float, py2: Float, color: FloatArray) {
        if (program == 0) return
        // converte pixels -> ndc/clip coords (-1..1)
        val cx1 = (px1 / viewWidth.toFloat()) * 2f - 1f
        val cy1 = 1f - (py1 / viewHeight.toFloat()) * 2f
        val cx2 = (px2 / viewWidth.toFloat()) * 2f - 1f
        val cy2 = 1f - (py2 / viewHeight.toFloat()) * 2f

        val vertices = floatArrayOf(cx1, cy1, 0f, cx2, cy2, 0f)
        val fb = makeBuffer(vertices)

        GLES20.glUseProgram(program)

        // pega handles (não usar bindHandles porque ele usa mvpMatrix)
        positionHandle = GLES20.glGetAttribLocation(program, "vPosition")
        colorHandle = GLES20.glGetUniformLocation(program, "vColor")
        mvpHandle = GLES20.glGetUniformLocation(program, "uMVPMatrix")

        GLES20.glEnableVertexAttribArray(positionHandle)
        GLES20.glVertexAttribPointer(positionHandle, 3, GLES20.GL_FLOAT, false, 0, fb)
        GLES20.glUniform4fv(colorHandle, 1, color, 0)

        // coloca matriz identidade como MVP (porque já estamos em clip space)
        val identity = FloatArray(16)
        Matrix.setIdentityM(identity, 0)
        GLES20.glUniformMatrix4fv(mvpHandle, 1, false, identity, 0)

        GLES20.glLineWidth(2f)
        GLES20.glDrawArrays(GLES20.GL_LINES, 0, 2)

        GLES20.glDisableVertexAttribArray(positionHandle)
    }
    private fun drawLetterX(pos: Vec3) {
        // projetar posição do rótulo (pequena distância do eixo)
        val screen = projectWorldToScreen(pos)
        if (!screen.x.isFinite() || !screen.y.isFinite()) return

        val sPx = 12f // tamanho em pixels (ajuste aqui para diminuir/aumentar)
        val p1 = Vec2(screen.x - sPx, screen.y - sPx)
        val p2 = Vec2(screen.x + sPx, screen.y + sPx)
        val p3 = Vec2(screen.x - sPx, screen.y + sPx)
        val p4 = Vec2(screen.x + sPx, screen.y - sPx)

        // cor azul (ou mude para a cor que quiser)
        val color = floatArrayOf(0f, 0f, 1f, 1f)
        drawRawLine(p1, p2, color)
        drawRawLine(p3, p4, color)
    }

    private fun drawLetterY(pos: Vec3) {
        val screen = projectWorldToScreen(pos)
        if (!screen.x.isFinite() || !screen.y.isFinite()) return

        val sPx = 12f
        val topLeft = Vec2(screen.x - sPx, screen.y - sPx)
        val topRight = Vec2(screen.x + sPx, screen.y - sPx)
        val center = Vec2(screen.x, screen.y)
        val bottom = Vec2(screen.x, screen.y + sPx)

        val color = floatArrayOf(1f, 0f, 0f, 1f)
        drawRawLine(topLeft, center, color)
        drawRawLine(topRight, center, color)
        drawRawLine(center, bottom, color)
    }

    private fun drawLetterZ(pos: Vec3) {
        val screen = projectWorldToScreen(pos)
        if (!screen.x.isFinite() || !screen.y.isFinite()) return

        val sPx = 12f
        val pTopLeft = Vec2(screen.x - sPx, screen.y - sPx)
        val pTopRight = Vec2(screen.x + sPx, screen.y - sPx)
        val pBotLeft = Vec2(screen.x - sPx, screen.y + sPx)
        val pBotRight = Vec2(screen.x + sPx, screen.y + sPx)

        val color = floatArrayOf(0f, 1f, 0f, 1f)
        drawRawLine(pTopLeft, pTopRight, color)
        drawRawLine(pTopRight, pBotLeft, color)
        drawRawLine(pBotLeft, pBotRight, color)
    }


    /**
     * Versão helper: recebe dois pontos de tela (Vec2) e desenha.
     */
    private fun drawRawLine(p1: Vec2, p2: Vec2, color: FloatArray) {
        drawRawLine(p1.x, p1.y, p2.x, p2.y, color)
    }



    // shaders helpers
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
        // check compile status
        val compiled = IntArray(1)
        GLES20.glGetShaderiv(shader, GLES20.GL_COMPILE_STATUS, compiled, 0)
        if (compiled[0] == 0) {
            Log.e(TAG, "Shader compile failed: " + GLES20.glGetShaderInfoLog(shader))
            GLES20.glDeleteShader(shader)
            return 0
        }
        return shader
    }
}
