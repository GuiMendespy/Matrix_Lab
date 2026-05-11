package com.example.matrixlab.ui.reflow

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView // Importante adicionar este import
import androidx.fragment.app.Fragment
import com.example.matrixlab.R


class Vetorial13 : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Conecta o XML ao fragmento
        return inflater.inflate(R.layout.fragment_vetorial13, container, false)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fun loadLatex(webView: WebView, formula: String) {
            val html = """
            <html><head>
            <script type="text/javascript" async src="https://cdnjs.cloudflare.com/ajax/libs/mathjax/2.7.7/MathJax.js?config=TeX-MML-AM_CHTML"></script>
            <style>body{font-size:15px; color:#444; background:transparent; margin:0; padding:0;}</style>
            </head><body>$formula</body></html>
        """.trimIndent()
            webView.settings.javaScriptEnabled = true
            webView.loadDataWithBaseURL(null, html, "text/html", "UTF-8", null)
        }

        // 1. Vetores iniciais - Usando barras duplas apenas nos comandos LaTeX
        loadLatex(view.findViewById(R.id.math_v1_v2), "\\( \\vec{v}_1 \\) e \\( \\vec{v}_2 \\)")

        // 2. Lista de equações - Usando barras simples dentro de strings triplas (Raw Strings)
        // No Kotlin, as strings triplas """ tratam a barra invertida como literal,
        // facilitando a escrita do LaTeX.
                loadLatex(view.findViewById(R.id.math_list_equations), """
            \[ \vec{u} = 5\vec{v}_1 + 4\vec{v}_2 \]
            \[ \vec{v} = -2\vec{v}_1 + 3\vec{v}_2 \]
            \[ \vec{w} = -4\vec{v}_1 - 3\vec{v}_2 \]
            \[ \vec{t} = 3\vec{v}_1 - 2\vec{v}_2 \]
            \[ \vec{x} = 4\vec{v}_1 + 0\vec{v}_2 \]
            \[ \vec{y} = 0\vec{v}_1 + 2\vec{v}_2 \]
        """.trimIndent())

        // 3. Combinação Linear (OK)
                loadLatex(view.findViewById(R.id.math_comb_linear), "\\[ \\\\vec{v} = a_1\\\\vec{v}_1 + a_2\\\\vec{v}_2 \\]")

        // 4. Base Canônica (OK)
                loadLatex(view.findViewById(R.id.math_canonica), "\\[ \\\\vec{i} = (1, 0) \\\\quad \\\\text{e} \\\\quad \\\\vec{j} = (0, 1) \\]")

        // 5. Equação Final - CORRIGIDO O ID PARA math_v_final
                loadLatex(view.findViewById(R.id.math_v_final), "\\[ \\\\vec{v} = x\\\\vec{i} + y\\\\vec{j} \\]")
            }
}
