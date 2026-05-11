package com.example.matrixlab.ui.reflow

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import androidx.fragment.app.Fragment
import com.example.matrixlab.R

class Vetorial34 : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_vetorial34, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fun loadLatex(webView: WebView?, formula: String) {
            if (webView == null) return
            val html = """
                <html><head>
                <script type="text/javascript" async src="https://cdnjs.cloudflare.com/ajax/libs/mathjax/2.7.7/MathJax.js?config=TeX-MML-AM_CHTML"></script>
                <style>body{font-size:16px; color:#333; background:transparent; margin:0; padding:0; text-align:center;}</style>
                </head><body>$formula</body></html>
            """.trimIndent()
            webView.settings.javaScriptEnabled = true
            webView.loadDataWithBaseURL(null, html, "text/html", "UTF-8", null)
        }

        // Condição fundamental: d(P, F) = d(P, d)
        loadLatex(view.findViewById(R.id.math_condicao_parabola),
            "\\[ d(P, F) = d(P, d) \\]")

        // Equação da parábola com eixo de simetria horizontal (Ox)
        loadLatex(view.findViewById(R.id.math_parabola_ox),
            "\\[ y^2 = 2px \\]")

        // Equação da parábola com eixo de simetria vertical (Oy)
        loadLatex(view.findViewById(R.id.math_parabola_oy),
            "\\[ x^2 = 2py \\]")
    }
}