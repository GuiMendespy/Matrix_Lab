package com.example.matrixlab.ui.reflow

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import androidx.fragment.app.Fragment
import com.example.matrixlab.R

class Vetorial22 : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_vetorial22, container, false)
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

        // Equação Geral do Plano (Normal)
        loadLatex(view.findViewById(R.id.math_eq_geral_plano), """
            \[ \vec{n} \cdot (P - A) = 0 \]
            \[ ax + by + cz + d = 0 \]
        """.trimIndent())

        // Equação Vetorial/Paramétrica do Plano
        loadLatex(view.findViewById(R.id.math_param_plano), """
            \[ P = A + h\vec{u} + t\vec{v} \]
            <br>
            \[ \begin{cases} x = x_0 + a_1 h + a_2 t \\ y = y_0 + b_1 h + b_2 t \\ z = z_0 + c_1 h + c_2 t \end{cases} \]
        """.trimIndent())
    }
}