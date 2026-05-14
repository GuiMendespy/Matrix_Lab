package com.example.matrixlab.ui.reflow

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import androidx.fragment.app.Fragment
import com.example.matrixlab.R

class Linear34 : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_linear34, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fun loadLatex(webView: WebView?, formula: String) {
            if (webView == null) return
            val html = """
                <html><head>
                <script type="text/javascript" async src="https://cdnjs.cloudflare.com/ajax/libs/mathjax/2.7.7/MathJax.js?config=TeX-MML-AM_CHTML"></script>
                <style>body{font-size:14px; color:#444; background:transparent; margin:0; padding:10px; line-height:1.6; text-align:center;}</style>
                </head><body>$formula</body></html>
            """.trimIndent()
            webView.settings.javaScriptEnabled = true
            webView.loadDataWithBaseURL(null, html, "text/html", "UTF-8", null)
        }

        // Propriedade m(A) = 0
        loadLatex(view.findViewById(R.id.math_polinomio_minimal),
            "\\[ m(A) = \\mathbf{0} \\quad \\text{e} \\quad \\deg(m) \\text{ é mínimo} \\]")

        // Definição de Norma e Ortogonalidade
        loadLatex(view.findViewById(R.id.math_norma_angulo), """
            \[
            \|v\| = \sqrt{\langle v, v \rangle} \quad \text{e} \quad v \perp w \iff \langle v, w \rangle = 0
            \]
        """.trimIndent())

        // Produto Interno Complexo (Conjugado)
        loadLatex(view.findViewById(R.id.math_complexo),
            "\\[ \\langle u, v \\rangle = \\overline{\\langle v, u \\rangle} \\]")
    }
}