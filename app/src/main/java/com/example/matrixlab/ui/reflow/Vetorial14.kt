package com.example.matrixlab.ui.reflow

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView // Importante adicionar este import
import androidx.fragment.app.Fragment
import com.example.matrixlab.R

class Vetorial14 : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Conecta o XML ao fragmento
        return inflater.inflate(R.layout.fragment_vetorial14, container, false)
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

        // Módulo (Norma)
        loadLatex(view.findViewById(R.id.math_modulo), "\\[ |\\vec{v}| = \\sqrt{x^2 + y^2} \\]")

        // Produto Escalar Analítico
        loadLatex(view.findViewById(R.id.math_prod_escalar), "\\[ \\vec{u} \\cdot \\vec{v} = x_1 x_2 + y_1 y_2 + z_1 z_2 \\]")

        // Definição Geométrica
        loadLatex(view.findViewById(R.id.math_def_geometrica), "\\[ \\vec{u} \\cdot \\vec{v} = |\\vec{u}| |\\vec{v}| \\cos \\theta \\]")

        // Cálculo do Ângulo
        loadLatex(view.findViewById(R.id.math_angulo_vetores), "\\[ \\cos \\theta = \\frac{\\vec{u} \\cdot \\vec{v}}{|\\vec{u}| |\\vec{v}|} \\]")
    }
}
