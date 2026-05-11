package com.example.matrixlab.ui.reflow

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView // Importante adicionar este import
import androidx.fragment.app.Fragment
import com.example.matrixlab.R

class Vetorial12 : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_vetorial12, container, false)
    }

    // --- MODIFICAÇÃO COMEÇA AQUI ---
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Função auxiliar para injetar o LaTeX na WebView
        fun carregarFormula(webView: WebView, conteudoLatex: String) {
            val html = """
                <html>
                <head>
                    <script type="text/javascript" async
                      src="https://cdnjs.cloudflare.com/ajax/libs/mathjax/2.7.7/MathJax.js?config=TeX-MML-AM_CHTML">
                    </script>
                    <style>
                        body { 
                            font-size: 16px; 
                            color: #212121; 
                            line-height: 1.6; 
                            margin: 0; 
                            padding: 0; 
                            background-color: transparent;
                        }
                    </style>
                </head>
                <body>
                    $conteudoLatex
                </body>
                </html>
            """.trimIndent()

            webView.settings.javaScriptEnabled = true
            webView.settings.domStorageEnabled = true
            webView.loadDataWithBaseURL(null, html, "text/html", "UTF-8", null)
        }

        // 1. Bloco inicial de vetores
        val wv1 = view.findViewById<WebView>(R.id.math_v1_v2)
        carregarFormula(wv1, "Consideremos dois vetores \\(\\vec{v}_1\\) e \\(\\vec{v}_2\\) não paralelos...")

        // 2. Lista de equações (u, v, w, t...)
        val wvList = view.findViewById<WebView>(R.id.math_list_equations)
        carregarFormula(wvList, """
            \[ \vec{u} = 5\vec{v}_1 + 4\vec{v}_2 \]
            \[ \vec{v} = -2\vec{v}_1 + 3\vec{v}_2 \]
            \[ \vec{w} = -4\vec{v}_1 - 3\vec{v}_2 \]
            \[ \vec{t} = 3\vec{v}_1 - 2\vec{v}_2 \]
        """.trimIndent())

        // 3. Combinação Linear
        val wvComb = view.findViewById<WebView>(R.id.math_comb_linear)
        carregarFormula(wvComb, "\\[ \\vec{v} = a_1\\vec{v}_1 + a_2\\vec{v}_2 \\]")

        // 4. Base Canônica
        val wvCan = view.findViewById<WebView>(R.id.math_canonica)
        carregarFormula(wvCan, "\\[ \\vec{i} = (1, 0) \\quad \\text{e} \\quad \\vec{j} = (0, 1) \\]")

        // 5. Bloco Final
        val wvFinal = view.findViewById<WebView>(R.id.math_final)
        carregarFormula(wvFinal, "Dado um vetor \\(\\vec{v}\\) qualquer: \\[ \\vec{v} = x\\vec{i} + y\\vec{j} \\]")
    }
}