package com.example.matrixlab.ui.reflow

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import androidx.fragment.app.Fragment
import com.example.matrixlab.R

class Vetorial32 : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_vetorial32, container, false)
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

        // Visualização SVG Elipse
        val webVisualizacao = view.findViewById<WebView>(R.id.math_elipse_visualizacao)
        val svgHtml = """
            <html><body style="display:flex; justify-content:center; align-items:center; background:transparent; margin:0; padding:10px;">
            <svg width="260" height="180" viewBox="0 0 260 180">
                <!-- Eixos -->
                <line x1="10" y1="90" x2="250" y2="90" stroke="#AAA" stroke-width="1" />
                <line x1="130" y1="10" x2="130" y2="170" stroke="#AAA" stroke-width="1" />
                
                <!-- Elipse -->
                <ellipse cx="130" cy="90" rx="100" ry="60" stroke="#1A73E8" stroke-width="3" fill="rgba(26, 115, 232, 0.1)" />
                
                <!-- Focos -->
                <circle cx="50" cy="90" r="4" fill="red" />
                <circle cx="210" cy="90" r="4" fill="red" />
                <text x="45" y="110" font-size="14" font-weight="bold" fill="red">F1</text>
                <text x="205" y="110" font-size="14" font-weight="bold" fill="red">F2</text>
                
                <!-- Vértices -->
                <circle cx="30" cy="90" r="3" fill="black" />
                <circle cx="230" cy="90" r="3" fill="black" />
                <text x="15" y="100" font-size="12" fill="black">A1</text>
                <text x="235" y="100" font-size="12" fill="black">A2</text>
                
                <!-- Parâmetros -->
                <text x="170" y="80" font-size="16" font-weight="bold" fill="black">a</text>
                <text x="90" y="80" font-size="16" font-weight="bold" fill="black">c</text>
                <text x="135" y="60" font-size="16" font-weight="bold" fill="black">b</text>
            </svg>
            </body></html>
        """.trimIndent()
        webVisualizacao?.settings?.javaScriptEnabled = true
        webVisualizacao?.loadDataWithBaseURL(null, svgHtml, "text/html", "UTF-8", null)

        // Equação da Elipse
        loadLatex(view.findViewById(R.id.math_eq_elipse), """
            <b>Eixo maior sobre o eixo x:</b><br>
            \[ \frac{x^2}{a^2} + \frac{y^2}{b^2} = 1, \quad a > b \]
            <br>
            <b>Eixo maior sobre o eixo y:</b><br>
            \[ \frac{y^2}{a^2} + \frac{x^2}{b^2} = 1, \quad a > b \]
        """.trimIndent())

        // Relação Fundamental
        loadLatex(view.findViewById(R.id.math_rel_elipse), """
            <b>Relação Pitagórica:</b> \[ a^2 = b^2 + c^2 \]
            <br>
            <b>Excentricidade:</b> \[ e = \frac{c}{a} \]
        """.trimIndent())

        // Exercícios Resolvidos
        loadLatex(view.findViewById(R.id.math_ex_resolvidos_32), """
            <div style="text-align:left; font-size:14px;">
            <b>Exemplo 1:</b> Dada a elipse \(\frac{x^2}{25} + \frac{y^2}{9} = 1\), determine os focos e a excentricidade.<br>
            \(a^2 = 25 \Rightarrow a = 5\), \(b^2 = 9 \Rightarrow b = 3\).<br>
            \(c^2 = 25 - 9 = 16 \Rightarrow c = 4\).<br>
            <b>Focos:</b> \(F_1(-4, 0)\) e \(F_2(4, 0)\). <b>Excentricidade:</b> \(e = 0,8\).
            <br><br>
            <b>Exemplo 2:</b> Determine a equação da elipse com focos \(F(0, \pm 2)\) e eixo maior \(6\).<br>
            Eixo maior \(2a = 6 \Rightarrow a = 3\). Focos no eixo y \(\Rightarrow c = 2\).<br>
            \(b^2 = a^2 - c^2 = 3^2 - 2^2 = 9 - 4 = 5\).<br>
            <b>Equação:</b> \(\frac{x^2}{5} + \frac{y^2}{9} = 1\).
            </div>
        """.trimIndent())
    }
}