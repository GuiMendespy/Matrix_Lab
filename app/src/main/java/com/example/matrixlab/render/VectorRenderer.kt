package com.example.matrixlab.render

import android.opengl.GLES20
import android.opengl.GLSurfaceView
import android.opengl.Matrix
import android.graphics.Color
import com.example.matrixlab.data.Vec3
import com.example.matrixlab.data.GeometricObject
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10
import kotlin.math.*
import java.util.Locale
import android.os.SystemClock
import android.os.Handler
import android.os.Looper

class VectorRenderer : GLSurfaceView.Renderer {

    private val projMatrix = FloatArray(16)
    private val viewMatrix = FloatArray(16)
    private val mvpMatrix = FloatArray(16)

    private var program = 0
    private var positionHandle = -1
    private var colorHandle = -1
    private var mvpHandle = -1

    @Volatile private var objects: List<GeometricObject> = emptyList()
    private var angleX = 45f
    private var angleY = 20f
    private var zoomScale = 1.0f
    private val minZoom = 0.05f
    private val maxZoom = 100.0f

    private var viewWidth = 1
    private var viewHeight = 1

    var onLabelsUpdated: ((List<OverlayView.TickLabel>) -> Unit)? = null
    private val uiHandler = Handler(Looper.getMainLooper())
    private var lastLabelsPost = 0L
    private val labelsPostIntervalMs = 16L

    private val BASE_RADIUS = 10f
    private val NEAR = 0.01f
    private val FAR = 2000f

    private var clearColor = floatArrayOf(1f, 1f, 1f, 1f)

    // PUBLIC API
    fun setObjects(list: List<GeometricObject>) { objects = list.toList() }
    fun setVectors(list: List<Vec3>) { objects = list.map { GeometricObject.Vector(it, name = it.name, color = it.color, isVisible = it.isVisible, isDotted = it.isDotted) } }
    fun setVector(v: Vec3) { setVectors(listOf(v)) }

    fun applyRotation(dx: Float, dy: Float) {
        angleX = (angleX + dx * 0.4f) % 360f
        angleY = (angleY + dy * 0.4f).coerceIn(-89f, 89f)
    }

    fun applyPinchScale(sf: Float) {
        if (sf.isFinite() && sf > 0f) zoomScale = (zoomScale * sf).coerceIn(minZoom, maxZoom)
    }

    fun setClearColor(r: Float, g: Float, b: Float, a: Float) { clearColor = floatArrayOf(r, g, b, a) }

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        GLES20.glEnable(GLES20.GL_BLEND)
        GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA)
        GLES20.glEnable(GLES20.GL_DEPTH_TEST)
        GLES20.glDepthFunc(GLES20.GL_LEQUAL)

        val vs = "uniform mat4 uMVPMatrix; attribute vec4 vPosition; void main() { gl_Position = uMVPMatrix * vPosition; }"
        val fs = "precision mediump float; uniform vec4 vColor; void main() { gl_FragColor = vColor; }"

        val vsId = GLES20.glCreateShader(GLES20.GL_VERTEX_SHADER).also { GLES20.glShaderSource(it, vs); GLES20.glCompileShader(it) }
        val fsId = GLES20.glCreateShader(GLES20.GL_FRAGMENT_SHADER).also { GLES20.glShaderSource(it, fs); GLES20.glCompileShader(it) }
        
        program = GLES20.glCreateProgram().also {
            GLES20.glAttachShader(it, vsId); GLES20.glAttachShader(it, fsId); GLES20.glLinkProgram(it)
        }
        positionHandle = GLES20.glGetAttribLocation(program, "vPosition")
        colorHandle = GLES20.glGetUniformLocation(program, "vColor")
        mvpHandle = GLES20.glGetUniformLocation(program, "uMVPMatrix")
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        viewWidth = width; viewHeight = height
        GLES20.glViewport(0, 0, width, height)
    }

    override fun onDrawFrame(gl: GL10?) {
        GLES20.glClearColor(clearColor[0], clearColor[1], clearColor[2], clearColor[3])
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT or GLES20.GL_DEPTH_BUFFER_BIT)
        if (program == 0) return

        val ratio = viewWidth.toFloat() / viewHeight.toFloat()
        Matrix.perspectiveM(projMatrix, 0, 45f, ratio, NEAR, FAR)

        val radius = BASE_RADIUS / zoomScale
        val azi = Math.toRadians(angleX.toDouble())
        val elev = Math.toRadians(angleY.toDouble())
        val camX = (radius * cos(elev) * sin(azi)).toFloat()
        val camY = (radius * sin(elev)).toFloat()
        val camZ = (radius * cos(elev) * cos(azi)).toFloat()

        Matrix.setLookAtM(viewMatrix, 0, camX, camY, camZ, 0f, 0f, 0f, 0f, 1f, 0f)
        Matrix.multiplyMM(mvpMatrix, 0, projMatrix, 0, viewMatrix, 0)

        val spacing = computeNiceSpacing(radius)
        val limit = max(radius * 2.5f, 50f)
        
        drawGrid(limit, spacing)
        drawAxes(limit, spacing, radius)
        
        val labels = computeLabels(radius, spacing, limit)
        if (SystemClock.uptimeMillis() - lastLabelsPost > labelsPostIntervalMs) {
            lastLabelsPost = SystemClock.uptimeMillis()
            uiHandler.post { onLabelsUpdated?.invoke(labels) }
        }

        objects.forEach { if (it.isVisible) drawObject(it) }
    }

    private fun computeNiceSpacing(radius: Float): Float {
        val raw = radius / 8f
        val exp = floor(log10(raw.toDouble())).toInt()
        val base = 10.0.pow(exp.toDouble()).toFloat()
        val frac = raw / base
        return when {
            frac < 1.2f -> 1f; frac < 2.2f -> 2f; frac < 3.5f -> 2.5f; frac < 7.5f -> 5f; else -> 10f
        } * base
    }

    private fun drawAxes(limit: Float, spacing: Float, radius: Float) {
        drawLine(Vec3(-limit, 0f, 0f), Vec3(limit, 0f, 0f), floatArrayOf(0f, 0.8f, 0f, 1f)) // X
        drawLine(Vec3(0f, -limit, 0f), Vec3(0f, limit, 0f), floatArrayOf(0f, 0.4f, 1f, 1f)) // Y
        drawLine(Vec3(0f, 0f, -limit), Vec3(0f, 0f, limit), floatArrayOf(1f, 0.1f, 0f, 1f)) // Z
        
        val ts = (radius * 0.02f).coerceAtLeast(0.005f)
        var i = -limit
        while (i <= limit) {
            if (abs(i) > 1e-4f) {
                drawLine(Vec3(i, -ts, 0f), Vec3(i, ts, 0f), floatArrayOf(0f, 0f, 0f, 0.4f))
                drawLine(Vec3(-ts, i, 0f), Vec3(ts, i, 0f), floatArrayOf(0f, 0f, 0f, 0.4f))
                drawLine(Vec3(0f, -ts, i), Vec3(0f, ts, i), floatArrayOf(0f, 0f, 0f, 0.4f))
            }
            i += spacing
        }
    }

    private fun computeLabels(radius: Float, spacing: Float, limit: Float): List<OverlayView.TickLabel> {
        val out = mutableListOf<OverlayView.TickLabel>()
        val labelDist = radius * 0.85f
        
        val sx = projectWorldToScreen(Vec3(labelDist, 0f, 0f))
        if (sx.z > 0) out.add(OverlayView.TickLabel(sx.toPair(), "X", color = Color.parseColor("#00AA00")))
        val sy = projectWorldToScreen(Vec3(0f, labelDist, 0f))
        if (sy.z > 0) out.add(OverlayView.TickLabel(sy.toPair(), "Y", color = Color.parseColor("#0055FF")))
        val sz = projectWorldToScreen(Vec3(0f, 0f, labelDist))
        if (sz.z > 0) out.add(OverlayView.TickLabel(sz.toPair(), "Z", color = Color.parseColor("#DD0000")))

        // Escala numérica dinâmica em X, Y e Z
        var i = -radius * 1.5f
        while (i <= radius * 1.5f) {
            if (abs(i) > 1e-4f) {
                val label = String.format(Locale.US, if(spacing < 1f) "%.2f" else "%.1f", i).trimEnd('0').trimEnd('.')
                
                // Labels Eixo X
                val sX = projectWorldToScreen(Vec3(i, -0.1f/zoomScale, 0f))
                if (sX.z > 0 && sX.isOnScreen()) out.add(OverlayView.TickLabel(sX.toPair(), label))
                
                // Labels Eixo Y
                val sY = projectWorldToScreen(Vec3(0.1f/zoomScale, i, 0f))
                if (sY.z > 0 && sY.isOnScreen()) out.add(OverlayView.TickLabel(sY.toPair(), label))
                
                // Labels Eixo Z
                val sZ = projectWorldToScreen(Vec3(0f, -0.1f/zoomScale, i))
                if (sZ.z > 0 && sZ.isOnScreen()) out.add(OverlayView.TickLabel(sZ.toPair(), label))
            }
            i += spacing
        }

        objects.filter { it.isVisible }.forEach { obj ->
            val pos = when(obj) {
                is GeometricObject.Vector -> obj.vec; is GeometricObject.Point -> obj.pos; is GeometricObject.Line -> obj.point; else -> null
            }
            if (pos != null) {
                val p = projectWorldToScreen(pos); if (p.z > 0) out.add(OverlayView.TickLabel(p.toPair(), obj.name))
            }
        }
        return out
    }

    private fun drawGrid(limit: Float, spacing: Float) {
        val col = floatArrayOf(0.9f, 0.9f, 0.9f, 0.3f)
        var i = -limit
        while (i <= limit) {
            drawLine(Vec3(i, 0f, -limit), Vec3(i, 0f, limit), col)
            drawLine(Vec3(-limit, 0f, i), Vec3(limit, 0f, i), col)
            i += spacing
        }
    }

    private fun drawObject(obj: GeometricObject) {
        when (obj) {
            is GeometricObject.Vector -> {
                drawLine(Vec3(0f, 0f, 0f), obj.vec, obj.color)
                if (!obj.isDotted) drawArrowHead(obj.vec, Vec3(0f, 0f, 0f), 0.35f / zoomScale, obj.color)
            }
            is GeometricObject.Point -> {
                val s = 0.08f / zoomScale
                drawLine(Vec3(obj.pos.x-s, obj.pos.y, obj.pos.z), Vec3(obj.pos.x+s, obj.pos.y, obj.pos.z), obj.color)
                drawLine(Vec3(obj.pos.x, obj.pos.y-s, obj.pos.z), Vec3(obj.pos.x, obj.pos.y+s, obj.pos.z), obj.color)
                drawLine(Vec3(obj.pos.x, obj.pos.y, obj.pos.z-s), Vec3(obj.pos.x, obj.pos.y, obj.pos.z+s), obj.color)
            }
            is GeometricObject.Line -> drawLine(obj.point + obj.direction * -200f, obj.point + obj.direction * 200f, obj.color)
            is GeometricObject.Plane -> drawPlane(obj, 50f)
            is GeometricObject.Quadric -> drawQuadric(obj)
        }
    }

    private fun drawPlane(p: GeometricObject.Plane, s: Float) {
        val n = Vec3(p.a, p.b, p.c); if (n.length() < 1e-6f) return
        val center = n * (-p.d / (n dot n))
        val u = if (abs(p.a) < 0.9f) Vec3(1f, 0f, 0f) else Vec3(0f, 1f, 0f)
        val v1 = (n cross u).let { it * (1f / it.length()) }; val v2 = (n cross v1).let { it * (1f / it.length()) }
        val vts = floatArrayOf(
            (center+v1*s+v2*s).x, (center+v1*s+v2*s).y, (center+v1*s+v2*s).z,
            (center-v1*s+v2*s).x, (center-v1*s+v2*s).y, (center-v1*s+v2*s).z,
            (center-v1*s-v2*s).x, (center-v1*s-v2*s).y, (center-v1*s-v2*s).z,
            (center+v1*s+v2*s).x, (center+v1*s+v2*s).y, (center+v1*s+v2*s).z,
            (center-v1*s-v2*s).x, (center-v1*s-v2*s).y, (center-v1*s-v2*s).z,
            (center+v1*s-v2*s).x, (center+v1*s-v2*s).y, (center+v1*s-v2*s).z
        )
        val fb = makeBuffer(vts); GLES20.glUseProgram(program); bindHandles(p.color, fb); GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, 6)
    }

    private fun drawQuadric(q: GeometricObject.Quadric) {
        val steps = 30; val col = q.color; val p = q.params
        when (q.type) {
            GeometricObject.QuadricType.ELIPSOIDE, GeometricObject.QuadricType.ESFERA -> {
                if (p.size < 3) return
                for (i in 0 until steps) {
                    val lat1 = PI.toFloat()*i/steps; val lat2 = PI.toFloat()*(i+1)/steps
                    for (j in 0..steps) {
                        val lon1 = 2*PI.toFloat()*j/steps; val lon2 = 2*PI.toFloat()*(j+1)/steps
                        val x1 = p[0]*sin(lat1)*cos(lon1); val y1 = p[1]*sin(lat1)*sin(lon1); val z1 = p[2]*cos(lat1)
                        val x2 = p[0]*sin(lat1)*cos(lon2); val y2 = p[1]*sin(lat1)*sin(lon2); val z2 = z1
                        val x3 = p[0]*sin(lat2)*cos(lon1); val y3 = p[1]*sin(lat2)*sin(lon1); val z3 = p[2]*cos(lat2)
                        drawLine(Vec3(x1,y1,z1), Vec3(x2,y2,z2), col); drawLine(Vec3(x1,y1,z1), Vec3(x3,y3,z3), col)
                    }
                }
            }
            GeometricObject.QuadricType.HIPERBOLOIDE_1 -> {
                if (p.size < 3) return
                for (i in -steps/2..steps/2) {
                    val z = i * 0.5f; val scale = sqrt(1 + (z*z)/(p[2]*p[2]))
                    for (j in 0 until steps) {
                        val a1 = 2*PI.toFloat()*j/steps; val a2 = 2*PI.toFloat()*(j+1)/steps
                        drawLine(Vec3(p[0]*scale*cos(a1), p[1]*scale*sin(a1), z), Vec3(p[0]*scale*cos(a2), p[1]*scale*sin(a2), z), col)
                        val zN = (i+1)*0.5f; val sN = sqrt(1 + (zN*zN)/(p[2]*p[2]))
                        drawLine(Vec3(p[0]*scale*cos(a1), p[1]*scale*sin(a1), z), Vec3(p[0]*sN*cos(a1), p[1]*sN*sin(a1), zN), col)
                    }
                }
            }
            GeometricObject.QuadricType.HIPERBOLOIDE_2 -> {
                if (p.size < 3) return
                for (side in listOf(-1, 1)) {
                    for (i in 0..steps/2) {
                        val z = side * (p[2] + i * 0.2f); val scale = sqrt(max(0f, (z*z)/(p[2]*p[2]) - 1))
                        for (j in 0 until steps) {
                            val a1 = 2*PI.toFloat()*j/steps; val a2 = 2*PI.toFloat()*(j+1)/steps
                            drawLine(Vec3(p[0]*scale*cos(a1), p[1]*scale*sin(a1), z), Vec3(p[0]*scale*cos(a2), p[1]*scale*sin(a2), z), col)
                        }
                    }
                }
            }
            GeometricObject.QuadricType.PARABOLOIDE_ELIP -> {
                if (p.size < 2) return
                for (i in 0..steps) {
                    val r = i * 0.2f; val z = r * r
                    for (j in 0 until steps) {
                        val a1 = 2*PI.toFloat()*j/steps; val a2 = 2*PI.toFloat()*(j+1)/steps
                        drawLine(Vec3(p[0]*r*cos(a1), p[1]*r*sin(a1), z), Vec3(p[0]*r*cos(a2), p[1]*r*sin(a2), z), col)
                        val rN = (i+1)*0.2f; val zN = rN*rN
                        drawLine(Vec3(p[0]*r*cos(a1), p[1]*r*sin(a1), z), Vec3(p[0]*rN*cos(a1), p[1]*rN*sin(a1), zN), col)
                    }
                }
            }
            else -> {}
        }
    }

    private fun drawLine(s: Vec3, e: Vec3, col: FloatArray) {
        val fb = makeBuffer(floatArrayOf(s.x, s.y, s.z, e.x, e.y, e.z)); GLES20.glUseProgram(program); bindHandles(col, fb); GLES20.glLineWidth(2f); GLES20.glDrawArrays(GLES20.GL_LINES, 0, 2)
    }

    private fun drawArrowHead(tip: Vec3, base: Vec3, size: Float, color: FloatArray) {
        val dir = (tip - base).let { if (it.length() > 0) it * (1f / it.length()) else Vec3(1f,0f,0f) }
        val side = (dir cross (if (abs(dir.y) < 0.9f) Vec3(0f,1f,0f) else Vec3(1f,0f,0f))).let { it * (size * 0.4f / it.length()) }
        val p1 = tip - dir * size + side; val p2 = tip - dir * size - side
        val fb = makeBuffer(floatArrayOf(tip.x, tip.y, tip.z, p1.x, p1.y, p1.z, p2.x, p2.y, p2.z)); GLES20.glUseProgram(program); bindHandles(color, fb); GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, 3)
    }

    private fun projectWorldToScreen(p: Vec3): Vec3 {
        val tmp = FloatArray(4); Matrix.multiplyMV(tmp, 0, mvpMatrix, 0, floatArrayOf(p.x, p.y, p.z, 1f), 0)
        if (tmp[3] <= 0f) return Vec3(-1000f,-1000f,-1f)
        return Vec3((tmp[0]/tmp[3]*0.5f+0.5f)*viewWidth, (1f-(tmp[1]/tmp[3]*0.5f+0.5f))*viewHeight, tmp[3])
    }

    private fun bindHandles(c: FloatArray, b: java.nio.FloatBuffer) {
        GLES20.glEnableVertexAttribArray(positionHandle); GLES20.glVertexAttribPointer(positionHandle, 3, GLES20.GL_FLOAT, false, 0, b); GLES20.glUniform4fv(colorHandle, 1, c, 0); GLES20.glUniformMatrix4fv(mvpHandle, 1, false, mvpMatrix, 0)
    }

    private fun makeBuffer(v: FloatArray) = java.nio.ByteBuffer.allocateDirect(v.size*4).order(java.nio.ByteOrder.nativeOrder()).asFloatBuffer().put(v).apply { position(0) }
    private fun Vec3.toPair() = Pair(x/viewWidth, 1f - y/viewHeight)
    private fun Vec3.isOnScreen() = x in 0f..viewWidth.toFloat() && y in 0f..viewHeight.toFloat()
}
