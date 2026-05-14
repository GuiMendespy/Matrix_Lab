package com.example.matrixlab.ui.reflow

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import androidx.fragment.app.Fragment
import com.example.matrixlab.R

class Linear24 : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_linear24, container, false)
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

        // Definição de Isomorfismo e existência da inversa
        loadLatex(view.findViewById(R.id.math_def_isomorfismo), """
            \[ T: V \to W \text{ é bijetora} \implies \exists T^{-1}: W \to V \]
            \[ V \cong W \iff \dim(V) = \dim(W) \]
        """.trimIndent())

        // Exemplo de cálculo e verificação de núcleo
        loadLatex(view.findViewById(R.id.math_exemplo_isomorfismo), """
            \[
            \begin{aligned}
            T(x,y) &= (0,0) \implies \begin{cases} 2x + y = 0 \\ 3x + 2y = 0 \end{cases} \\
            \det(A) &= 2(2) - 3(1) = 1 \neq 0 \\
            N(T) &= \{ (0,0) \} \implies \text{É Isomorfismo}
            \end{aligned}
            \]
        """.trimIndent())
    }
}