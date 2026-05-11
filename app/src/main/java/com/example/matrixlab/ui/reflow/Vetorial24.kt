package com.example.matrixlab.ui.reflow

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import androidx.fragment.app.Fragment
import com.example.matrixlab.R

class Vetorial24 : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_vetorial24, container, false)
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

        // Distância entre dois pontos
        loadLatex(view.findViewById(R.id.math_dist_pontos),
            "\\[ d(P_1, P_2) = \\sqrt{(x_2-x_1)^2 + (y_2-y_1)^2 + (z_2-z_1)^2} \\]")

        // Distância ponto a reta
        loadLatex(view.findViewById(R.id.math_dist_ponto_reta),
            "\\[ d(P, r) = \\frac{|\\vec{v} \\times \\vec{AP}|}{|\\vec{v}|} \\]")

        // Distância ponto a plano
        loadLatex(view.findViewById(R.id.math_dist_ponto_plano),
            "\\[ d(P_0, \\pi) = \\frac{|ax_0 + by_0 + cz_0 + d|}{\\sqrt{a^2 + b^2 + c^2}} \\]")
    }
}