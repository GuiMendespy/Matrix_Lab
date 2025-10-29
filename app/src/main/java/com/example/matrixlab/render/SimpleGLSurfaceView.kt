package com.example.matrixlab.render

import android.content.Context
import android.opengl.GLES20
import android.opengl.GLSurfaceView
import android.opengl.Matrix
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10
import kotlin.math.sqrt

class SimpleGLSurfaceView(context: Context) : GLSurfaceView(context) {
    private val renderer: VectorRenderer

    init {
        setEGLContextClientVersion(2)
        renderer = VectorRenderer()
        setRenderer(renderer)
        renderMode = RENDERMODE_WHEN_DIRTY
    }

    fun setVector(x: Float, y: Float, z: Float) {
        renderer.setVector(x, y, z)
        requestRender()
    }

    private class VectorRenderer : Renderer {
        private val line = FloatArray(6)
        private val mvpMatrix = FloatArray(16)
        private val projectionMatrix = FloatArray(16)
        private val viewMatrix = FloatArray(16)
        private var program = 0

        override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
            GLES20.glClearColor(0f, 0f, 0f, 1f)
            val vertexShader = """
                attribute vec3 vPosition;
                uniform mat4 uMVPMatrix;
                void main() {
                    gl_Position = uMVPMatrix * vec4(vPosition, 1.0);
                }
            """
            val fragmentShader = """
                precision mediump float;
                void main() {
                    gl_FragColor = vec4(0.2, 0.8, 1.0, 1.0);
                }
            """
            program = createProgram(vertexShader, fragmentShader)
        }

        override fun onDrawFrame(gl: GL10?) {
            GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT or GLES20.GL_DEPTH_BUFFER_BIT)
            Matrix.setLookAtM(viewMatrix, 0, 0f, 0f, 4f, 0f, 0f, 0f, 0f, 1f, 0f)
            Matrix.multiplyMM(mvpMatrix, 0, projectionMatrix, 0, viewMatrix, 0)

            GLES20.glUseProgram(program)
            val positionHandle = GLES20.glGetAttribLocation(program, "vPosition")
            val mvpMatrixHandle = GLES20.glGetUniformLocation(program, "uMVPMatrix")

            GLES20.glUniformMatrix4fv(mvpMatrixHandle, 1, false, mvpMatrix, 0)
            GLES20.glEnableVertexAttribArray(positionHandle)
            GLES20.glVertexAttribPointer(positionHandle, 3, GLES20.GL_FLOAT, false, 0, toBuffer(line))
            GLES20.glDrawArrays(GLES20.GL_LINES, 0, 2)
            GLES20.glDisableVertexAttribArray(positionHandle)
        }

        override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
            GLES20.glViewport(0, 0, width, height)
            val ratio = width.toFloat() / height
            Matrix.frustumM(projectionMatrix, 0, -ratio, ratio, -1f, 1f, 2f, 10f)
        }

        fun setVector(x: Float, y: Float, z: Float) {
            val length = sqrt(x*x + y*y + z*z)
            line[0] = 0f; line[1] = 0f; line[2] = 0f
            line[3] = x / length; line[4] = y / length; line[5] = z / length
        }

        private fun createProgram(vertex: String, fragment: String): Int {
            val vShader = loadShader(GLES20.GL_VERTEX_SHADER, vertex)
            val fShader = loadShader(GLES20.GL_FRAGMENT_SHADER, fragment)
            val program = GLES20.glCreateProgram()
            GLES20.glAttachShader(program, vShader)
            GLES20.glAttachShader(program, fShader)
            GLES20.glLinkProgram(program)
            return program
        }

        private fun loadShader(type: Int, code: String): Int {
            val shader = GLES20.glCreateShader(type)
            GLES20.glShaderSource(shader, code)
            GLES20.glCompileShader(shader)
            return shader
        }

        private fun toBuffer(array: FloatArray) =
            java.nio.ByteBuffer.allocateDirect(array.size * 4).order(java.nio.ByteOrder.nativeOrder()).asFloatBuffer().apply {
                put(array)
                position(0)
            }
    }
}
