package com.example.matrixlab.render

import android.opengl.GLES20
import android.opengl.GLSurfaceView
import android.opengl.Matrix
import com.example.matrixlab.data.Vec3
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class VectorRenderer : GLSurfaceView.Renderer {

    private val projMatrix = FloatArray(16)
    private val viewMatrix = FloatArray(16)
    private val mvpMatrix = FloatArray(16)
    private var currentVector = Vec3(1f, 1f, 0f)

    fun setVector(v: Vec3) { currentVector = v }

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        GLES20.glClearColor(0f, 0f, 0f, 1f)
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        GLES20.glViewport(0, 0, width, height)
        val ratio = width.toFloat() / height
        Matrix.frustumM(projMatrix, 0, -ratio, ratio, -1f, 1f, 3f, 10f)
    }

    override fun onDrawFrame(gl: GL10?) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT or GLES20.GL_DEPTH_BUFFER_BIT)

        Matrix.setLookAtM(viewMatrix, 0, 0f, 0f, 5f, 0f, 0f, 0f, 0f, 1f, 0f)
        Matrix.multiplyMM(mvpMatrix, 0, projMatrix, 0, viewMatrix, 0)

        drawAxis()
        drawVector(currentVector)
    }

    private fun drawAxis() {
        // Aqui você desenha eixos com GL_LINES
    }

    private fun drawVector(v: Vec3) {
        // Desenhar linha do (0,0,0) até (v.x, v.y, v.z)
        // Use VBOs ou simples GL_LINES
    }
}
