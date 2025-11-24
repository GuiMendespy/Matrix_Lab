package com.example.matrixlab.render

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View

/**
 * Overlay transparente que desenha labels em coordenadas normalizadas (0..1).
 * Use OverlayView.TickLabel(position2D = (nx to ny), text = "1.0")
 */
class OverlayView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : View(context, attrs) {

    data class TickLabel(val position2D: Pair<Float, Float>, val text: String)

    var tickLabels: List<TickLabel> = emptyList()
        set(value) {
            field = value
            postInvalidate()
        }

    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.GRAY
        textSize = 7f * resources.displayMetrics.density // scale with DPI
        textAlign = Paint.Align.CENTER
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        for (label in tickLabels) {
            val nx = label.position2D.first
            val ny = label.position2D.second
            if (!nx.isFinite() || !ny.isFinite()) continue
            val px = nx * width.toFloat()
            val py = (1f - ny) * height.toFloat() // convert NDC->canvas coords
            canvas.drawText(label.text, px, py, textPaint)
        }
    }
}