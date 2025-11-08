package com.example.matrixlab.render

import android.content.Context
import android.opengl.GLSurfaceView

class SimpleGLSurfaceView(context: Context) : GLSurfaceView(context) {

    private val renderer: VectorRenderer

    init {
        setEGLContextClientVersion(2)
        renderer = VectorRenderer()
        setRenderer(renderer)

        // 💡 Permite transparência do plano de fundo do OpenGL
        setZOrderOnTop(true)
        holder.setFormat(android.graphics.PixelFormat.TRANSLUCENT)
    }

    fun setVector(x: Float, y: Float, z: Float) {
        renderer.setVector(com.example.matrixlab.data.Vec3(x, y, z))
    }

    fun setBackgroundColor(r: Float, g: Float, b: Float, a: Float) {
        renderer.setBackgroundColor(r, g, b, a)
    }
}
