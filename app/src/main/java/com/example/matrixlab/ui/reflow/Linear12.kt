package com.example.matrixlab.ui.reflow

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import androidx.fragment.app.Fragment
import com.example.matrixlab.R

class Linear12 : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_linear12, container, false)
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

        // Condições Necessárias e Suficientes
        loadLatex(view.findViewById(R.id.math_teorema_subespaco), """
            \[
            \begin{aligned}
            i) & \ \mathbf{0} \in S \\
            ii) & \ \forall u, v \in S \implies u + v \in S \\
            iii) & \ \forall \alpha \in \mathbb{R}, \forall u \in S \implies \alpha u \in S
            \end{aligned}
            \]
        """.trimIndent())

        // Exemplos de subespaços em R2 e R3
        loadLatex(view.findViewById(R.id.math_exemplos_subespaco), """
            \[
            \begin{aligned}
            \text{Reta na Origem: } & S = \{ (x, y) \in \mathbb{R}^2 \mid ax + by = 0 \} \\
            \text{Plano no } \mathbb{R}^3 \text{: } & S = \{ (x, y, z) \mid ax + by + cz = 0 \} \\
            \text{Verificação: } & \text{Se } \mathbf{0} \notin S \text{, então } S \text{ não é subespaço.}
            \end{aligned}
            \]
        """.trimIndent())
    }
}