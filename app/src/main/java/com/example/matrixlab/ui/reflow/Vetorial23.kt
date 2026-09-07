package com.example.matrixlab.ui.reflow

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import androidx.fragment.app.Fragment
import com.example.matrixlab.R

class Vetorial23 : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_vetorial23, container, false)
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

        // Equação do Paralelogramo
        loadLatex(view.findViewById(R.id.math_eq_paralelogramo), 
            "\\[ P = A + h(\\vec{B}-\\vec{A}) + t(\\vec{C}-\\vec{A}) \\text{ com } 0 \\le h,t \\le 1 \\]")

        // Ângulo entre planos e reta/plano
        loadLatex(view.findViewById(R.id.math_angulo_planos), """
            \[ \cos \theta = \frac{|\vec{n}_1 \cdot \vec{n}_2|}{|\vec{n}_1| |\vec{n}_2|} \]
            <br>
            \[ \sin \phi = \frac{|\vec{v} \cdot \vec{n}|}{|\vec{v}| |\vec{n}|} \]
        """.trimIndent())

        // Exercícios Resolvidos
        loadLatex(view.findViewById(R.id.math_ex_resolvidos_23), """
            <div style="text-align:left; font-size:14px;">
            <b>1. Interseção entre Reta e Plano:</b><br>
            Reta \(r: (x,y,z) = (1,0,1) + t(1,2,-1)\)<br>
            Plano \(\pi: x + y + z - 2 = 0\)<br><br>
            Substituindo a reta no plano:<br>
            \((1+t) + (2t) + (1-t) - 2 = 0\)<br>
            \(2 + 2t - 2 = 0 \Rightarrow 2t = 0 \Rightarrow t = 0\)<br>
            Ponto de interseção: \(I(1, 0, 1)\).
            <br><br>
            <b>2. Ângulo entre Planos:</b><br>
            \(\pi_1: x + y - 1 = 0 \Rightarrow \vec{n}_1 = (1, 1, 0)\)<br>
            \(\pi_2: z - 2 = 0 \Rightarrow \vec{n}_2 = (0, 0, 1)\)<br>
            \[ \cos \theta = \frac{|(1,1,0) \cdot (0,0,1)|}{\sqrt{2} \cdot 1} = 0 \Rightarrow \theta = 90^\circ \]
            Os planos são ortogonais.
            </div>
        """.trimIndent())
    }
}