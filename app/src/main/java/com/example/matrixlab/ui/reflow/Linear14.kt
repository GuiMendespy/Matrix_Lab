package com.example.matrixlab.ui.reflow

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import androidx.fragment.app.Fragment
import com.example.matrixlab.R

class Linear14 : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_linear14, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fun loadLatex(webView: WebView?, formula: String) {
            if (webView == null) return
            val html = """
                <html><head>
                <script type="text/javascript" async src="https://cdnjs.cloudflare.com/ajax/libs/mathjax/2.7.7/MathJax.js?config=TeX-MML-AM_CHTML"></script>
                <style>body{font-size:14px; color:#444; background:transparent; margin:0; padding:10px; line-height:1.6;}</style>
                </head><body>$formula</body></html>
            """.trimIndent()
            webView.settings.javaScriptEnabled = true
            webView.loadDataWithBaseURL(null, html, "text/html", "UTF-8", null)
        }

        // Requisitos de Base
        loadLatex(view.findViewById(R.id.math_requisitos_base), """
            \[
            \begin{aligned}
            1) & \ B \text{ é linearmente independente (LI)} \\
            2) & \ [B] = V \text{ (B gera o espaço V)}
            \end{aligned}
            \]
        """.trimIndent())

        // Exemplo Base Canônica do R2
        loadLatex(view.findViewById(R.id.math_base_canonica), """
            \[
            \begin{aligned}
            B &= \{ (1, 0), (0, 1) \} \\
            (x, y) &= x(1, 0) + y(0, 1)
            \end{aligned}
            \]
        """.trimIndent())

        // Definição de Dimensão
        loadLatex(view.findViewById(R.id.math_dimensao), """
            \[
            \begin{aligned}
            \text{Se } B = \{v_1, \dots, v_n\} \implies \dim(V) = n \\
            \dim(\mathbb{R}^2) = 2, \quad \dim(\mathbb{R}^3) = 3 \\
            \dim(P_n) = n+1
            \end{aligned}
            \]
        """.trimIndent())
    }
}