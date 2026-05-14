package com.example.matrixlab.ui.reflow

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import androidx.fragment.app.Fragment
import com.example.matrixlab.R

class Linear13 : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_linear13, container, false)
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

        // Definição de Combinação Linear
        loadLatex(view.findViewById(R.id.math_comb_linear), """
            \[ v = a_1 v_1 + a_2 v_2 + \dots + a_n v_n \]
        """.trimIndent())

        // Exemplo Polinômio P2
        loadLatex(view.findViewById(R.id.math_exemplo_polinomio), """
            \[
            \begin{aligned}
            v &= 3v_1 + 4v_2 \\
            7x^2 + 11x - 26 &= 3(5x^2 - 3x + 2) + 4(-2x^2 + 5x - 8)
            \end{aligned}
            \]
        """.trimIndent())

        // Definição LI e LD
        loadLatex(view.findViewById(R.id.math_li_ld), """
            \[
            \begin{aligned}
            &a_1 v_1 + a_2 v_2 + \dots + a_n v_n = \mathbf{0} \\ \\
            \text{LI: } & a_1 = a_2 = \dots = a_n = 0 \text{ (única)} \\
            \text{LD: } & \exists a_i \neq 0 \text{ (infinitas)}
            \end{aligned}
            \]
        """.trimIndent())
    }
}