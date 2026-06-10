package com.example.matrixlab.ui.reflow

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import androidx.fragment.app.Fragment
import com.example.matrixlab.R

class Linear11 : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_linear11, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fun loadLatex(webView: WebView?, formula: String) {
            if (webView == null) return
            val html = """
                <html><head>
                <script type="text/javascript" async src="https://cdnjs.cloudflare.com/ajax/libs/mathjax/2.7.7/MathJax.js?config=TeX-MML-AM_CHTML"></script>
                <style>body{font-size:14px; color:#444; background:transparent; margin:0; padding:5px; line-height:1.5;}</style>
                </head><body>$formula</body></html>
            """.trimIndent()
            webView.settings.javaScriptEnabled = true
            webView.loadDataWithBaseURL(null, html, "text/html", "UTF-8", null)
        }

        // Definição de vetores e operações no Rn
        loadLatex(view.findViewById(R.id.math_vetores_rn), """
            \[
            \begin{aligned}
            \mathbb{R}^n &= \{ (x_1, x_2, \dots, x_n) \mid x_i \in \mathbb{R} \} \\
            u+v &= (x_1+y_1, \dots, x_n+y_n) \\
            \alpha u &= (\alpha x_1, \dots, \alpha x_n) \\
            u \cdot v &= \sum_{i=1}^{n} x_i y_i
            \end{aligned}
            \]
        """.trimIndent())

        // Axiomas de Adição
                loadLatex(view.findViewById(R.id.math_axiomas_adicao), """
            \[
            \begin{aligned}
            1. & \ (u+v)+w = u+(v+w) \\
            2. & \ u+v = v+u \\
            3. & \ \exists \mathbf{0} \mid u+\mathbf{0} = u \\
            4. & \ \forall u, \exists -u \mid u+(-u) = \mathbf{0}
            \end{aligned}
            \]
        """.trimIndent())

        // Axiomas de Multiplicação por Escalar
                loadLatex(view.findViewById(R.id.math_axiomas_escalar), """
            \[
            \begin{aligned}
            1. & \ \alpha(\beta u) = (\alpha\beta)u \\
            2. & \ (\alpha+\beta)u = \alpha u + \beta u \\
            3. & \ \alpha(u+v) = \alpha u + \alpha v \\
            4. & \ 1u = u
            \end{aligned}
            \]
        """.trimIndent())
            }
}