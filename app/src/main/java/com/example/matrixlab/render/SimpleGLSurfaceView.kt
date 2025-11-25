package com.example.matrixlab.render

import android.content.Context
import android.graphics.PixelFormat
import android.opengl.GLSurfaceView
import android.view.ScaleGestureDetector
import android.view.MotionEvent
import android.widget.FrameLayout
import com.example.matrixlab.data.Vec3

class SimpleGLSurfaceView(context: Context) : FrameLayout(context) {

    private val glView: GLSurfaceView
    private val renderer: VectorRenderer
    private val overlay: OverlayView

    private var previousX = 0f
    private var previousY = 0f

    private val scaleDetector: ScaleGestureDetector

    init {

        // 1️⃣ Cria primeiro o renderer
        renderer = VectorRenderer()

        // 2️⃣ Só então cria o GLSurfaceView e associa o renderer corretamente
        glView = GLSurfaceView(context).apply {
            setEGLContextClientVersion(2)

            // REGISTRAR O RENDERER AQUI -- correto
            setRenderer(renderer)

            // Agora sim é seguro mudar o modo de render
            renderMode = GLSurfaceView.RENDERMODE_WHEN_DIRTY

            // Fundo transparente (overlay)
            setZOrderOnTop(false)
            setZOrderMediaOverlay(true)
            holder.setFormat(PixelFormat.TRANSLUCENT)
        }

        // overlay desenha texto/UI sobre o GL
        overlay = OverlayView(context)

        // adiciona GL + overlay
        addView(glView, LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT))
        addView(overlay, LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT))

        // liga renderer → overlay
        renderer.onLabelsUpdated = { labels ->
            overlay.post {
                overlay.tickLabels = labels
            }
        }

        // Gestos de zoom (pinça)
        scaleDetector = ScaleGestureDetector(context,
            object : ScaleGestureDetector.SimpleOnScaleGestureListener() {
                override fun onScale(detector: ScaleGestureDetector): Boolean {
                    renderer.applyPinchScale(detector.scaleFactor)
                    glView.requestRender()
                    return true
                }
            })
    }

    // --- API pública ---
    fun setVectors(list: List<Vec3>) {
        renderer.setVectors(list)
        glView.requestRender()
    }

    /** Backwards-compatible single vector setter */
    fun setVector(x: Float, y: Float, z: Float) {
        renderer.setVector(Vec3(x, y, z))
        glView.requestRender()
    }

    fun setBackgroundColor(r: Float, g: Float, b: Float, a: Float) {
        renderer.setClearColor(r, g, b, a)
        glView.requestRender()
    }

    // Rotação com 1 dedo + zoom com 2 dedos
    override fun onTouchEvent(event: MotionEvent): Boolean {
        scaleDetector.onTouchEvent(event)

        if (!scaleDetector.isInProgress && event.pointerCount == 1) {
            if (event.action == MotionEvent.ACTION_MOVE) {
                val dx = event.x - previousX
                val dy = event.y - previousY
                renderer.applyRotation(dx, dy)
                glView.requestRender()
            }
        }

        previousX = event.x
        previousY = event.y
        return true
    }

    override fun performClick(): Boolean {
        return super.performClick()
    }
}
