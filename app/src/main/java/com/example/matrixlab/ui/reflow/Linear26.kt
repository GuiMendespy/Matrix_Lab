package com.example.matrixlab.ui.reflow

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import androidx.fragment.app.Fragment
import com.example.matrixlab.R

class Linear26 : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_linear26, container, false)
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

        // Relação de v nas bases A e B
        loadLatex(view.findViewById(R.id.math_relacao_bases), """
            \[
            [v]_B = M_{B,A} \cdot [v]_A
            \]
        """.trimIndent())

        // Estrutura da Matriz de Mudança de Base (n=3)
        loadLatex(view.findViewById(R.id.math_matriz_mudanca), """
            \[
            M_{B,A} = \begin{bmatrix} 
            a_{11} & a_{12} & a_{13} \\ 
            a_{21} & a_{22} & a_{23} \\ 
            a_{31} & a_{32} & a_{33} 
            \end{bmatrix}
            \]
        """.trimIndent())
    }
}