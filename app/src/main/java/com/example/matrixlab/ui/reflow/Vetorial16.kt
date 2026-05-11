package com.example.matrixlab.ui.reflow

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.matrixlab.R
import android.webkit.WebView // Importante adicionar este import


class Vetorial16 : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Conecta o XML ao fragmento
        return inflater.inflate(R.layout.fragment_vetorial16, container, false)
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

        // Definição do Produto Misto
        loadLatex(view.findViewById(R.id.math_def_misto),
            "\\[ [\\vec{u}, \\vec{v}, \\vec{w}] = \\vec{u} \\cdot (\\vec{v} \\times \\vec{w}) \\]")

        // Determinante do Produto Misto
        loadLatex(view.findViewById(R.id.math_det_misto), """
        \[ (\vec{u}, \vec{v}, \vec{w}) = \begin{vmatrix} 
        x_1 & y_1 & z_1 \\ 
        x_2 & y_2 & z_2 \\ 
        x_3 & y_3 & z_3 
        \end{vmatrix} \]
    """.trimIndent())
    }
}
