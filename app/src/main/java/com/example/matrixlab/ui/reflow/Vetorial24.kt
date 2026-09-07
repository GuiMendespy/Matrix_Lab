package com.example.matrixlab.ui.reflow

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import androidx.fragment.app.Fragment
import com.example.matrixlab.R

class Vetorial24 : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_vetorial24, container, false)
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

        loadLatex(view.findViewById(R.id.math_dist_pontos),
            "\\[ d(P_1, P_2) = \\sqrt{(x_2-x_1)^2 + (y_2-y_1)^2 + (z_2-z_1)^2} \\]")

        loadLatex(view.findViewById(R.id.math_dist_ponto_reta),
            "\\[ d(P, r) = \\frac{|\\vec{v} \\times \\vec{AP}|}{|\\vec{v}|} \\]")

        loadLatex(view.findViewById(R.id.math_dist_ponto_plano),
            "\\[ d(P_0, \\pi) = \\frac{|ax_0 + by_0 + cz_0 + d|}{\\sqrt{a^2 + b^2 + c^2}} \\]")

        loadLatex(view.findViewById(R.id.math_ex_dist_completo), """
            <div style="text-align:left; font-size:14px;">
            1. Pontos A(1,0,2) e B(3,-2,3):<br>
            \[ d = \sqrt{(3-1)^2 + (-2-0)^2 + (3-2)^2} = 3 \]
            <br>
            2. Ponto P(1,2,3) à Reta r: x=y=z:<br>
            Vetor v=(1,1,1), Ponto A=(0,0,0). AP=(1,2,3).<br>
            \[ v \times AP = (1, -2, 1) \]
            \[ d = \frac{\sqrt{1+4+1}}{\sqrt{1+1+1}} = \sqrt{2} \]
            <br>
            3. Ponto P(1,2,3) ao Plano 2x+3y-z+5=0:<br>
            \[ d = \frac{|2(1) + 3(2) - (3) + 5|}{\sqrt{4+9+1}} = \frac{10}{\sqrt{14}} \]
            </div>
        """.trimIndent())
    }
}