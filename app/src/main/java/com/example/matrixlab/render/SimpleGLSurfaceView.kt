package com.example.matrixlab.render

import android.content.Context
import android.graphics.PixelFormat
import android.opengl.GLSurfaceView
import android.view.ScaleGestureDetector
import android.view.MotionEvent
import android.widget.FrameLayout
import com.example.matrixlab.data.Vec3
import com.example.matrixlab.data.GeometricObject

class SimpleGLSurfaceView(context: Context) : FrameLayout(context) {

    private val glView: GLSurfaceView
    private val renderer: VectorRenderer = VectorRenderer()
    private val overlay: OverlayView = OverlayView(context)

    private var previousX = 0f
    private var previousY = 0f

    private val scaleDetector: ScaleGestureDetector

    init {
        glView = GLSurfaceView(context).apply {
            setEGLContextClientVersion(2)
            setRenderer(renderer)
            renderMode = GLSurfaceView.RENDERMODE_WHEN_DIRTY
            setZOrderOnTop(false)
            setZOrderMediaOverlay(true)
            holder.setFormat(PixelFormat.TRANSLUCENT)
        }

        addView(glView, LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT))
        addView(overlay, LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT))

        renderer.onLabelsUpdated = { labels ->
            overlay.post {
                overlay.tickLabels = labels
            }
        }

        scaleDetector = ScaleGestureDetector(context,
            object : ScaleGestureDetector.SimpleOnScaleGestureListener() {
                override fun onScale(detector: ScaleGestureDetector): Boolean {
                    renderer.applyPinchScale(detector.scaleFactor)
                    glView.requestRender()
                    return true
                }
            }
        )
    }

    fun setObjects(list: List<GeometricObject>) {
        renderer.setObjects(list)
        glView.requestRender()
    }

    fun setVectors(list: List<Vec3>) {
        renderer.setVectors(list)
        glView.requestRender()
    }

    fun setVector(x: Float, y: Float, z: Float) {
        renderer.setVector(Vec3(x, y, z))
        glView.requestRender()
    }

    fun setBackgroundColor(r: Float, g: Float, b: Float, a: Float) {
        renderer.setClearColor(r, g, b, a)
        glView.requestRender()
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        scaleDetector.onTouchEvent(event)

        if (!scaleDetector.isInProgress && event.pointerCount == 1) {
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    previousX = event.x
                    previousY = event.y
                }
                MotionEvent.ACTION_MOVE -> {
                    val dx = event.x - previousX
                    val dy = event.y - previousY
                    renderer.applyRotation(dx, dy)
                    glView.requestRender()
                    previousX = event.x
                    previousY = event.y
                }
                MotionEvent.ACTION_UP -> {
                    performClick()
                }
            }
        }
        return true
    }

    override fun performClick(): Boolean {
        return super.performClick()
    }
}
