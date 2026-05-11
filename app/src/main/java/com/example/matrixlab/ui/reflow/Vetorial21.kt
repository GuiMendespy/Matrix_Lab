package com.example.matrixlab.ui.reflow

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import androidx.fragment.app.Fragment
import com.example.matrixlab.R

class Vetorial21 : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_vetorial21, container, false)
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

        // Equação Vetorial da Reta
        loadLatex(view.findViewById(R.id.math_eq_vetorial_reta), "\\[ P = A + t\\vec{v} \\]")

        // Equações Paramétricas e Simétricas
        loadLatex(view.findViewById(R.id.math_eq_param_simetrica), """
            \[ \text{Paramétricas:} \begin{cases} x = x_1 + at \\ y = y_1 + bt \\ z = z_1 + ct \end{cases} \]
            <br>
            \[ \text{Simétricas:} \frac{x-x_1}{a} = \frac{y-y_1}{b} = \frac{z-z_1}{c} \]
        """.trimIndent())

        // Ângulo entre Retas
        loadLatex(view.findViewById(R.id.math_angulo_retas),
            "\\[ \\cos \\theta = \\frac{|\\vec{v}_1 \\cdot \\vec{v}_2|}{|\\vec{v}_1| |\\vec{v}_2|} \\]")
    }
}