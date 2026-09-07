package com.example.matrixlab.render

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.util.AttributeSet
import android.view.View

class OverlayView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : View(context, attrs) {

    data class TickLabel(
        val position2D: Pair<Float, Float>,
        val text: String,
        val color: Int = Color.DKGRAY
    )

    var tickLabels: List<TickLabel> = emptyList()
        set(value) {
            field = value
            postInvalidate()
        }

    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        textSize = 14f * resources.displayMetrics.density
        textAlign = Paint.Align.CENTER
        typeface = Typeface.DEFAULT_BOLD
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        for (label in tickLabels) {
            val nx = label.position2D.first
            val ny = label.position2D.second
            if (!nx.isFinite() || !ny.isFinite()) continue
            
            val px = nx * width.toFloat()
            val py = (1f - ny) * height.toFloat()
            
            textPaint.color = label.color
            canvas.drawText(label.text, px, py, textPaint)
        }
    }
}
