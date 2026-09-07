package com.example.matrixlab.ui.reflow

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import androidx.fragment.app.Fragment
import com.example.matrixlab.R

class Vetorial31 : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_vetorial31, container, false)
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

        // Equação da Circunferência
        loadLatex(view.findViewById(R.id.math_eq_circunferencia), """
            <b>Equação Reduzida:</b> (Centro em \(C(h, k)\))<br>
            \[ (x - h)^2 + (y - k)^2 = r^2 \]
            <br>
            <b>Equação Geral:</b> (Desenvolvendo os produtos notáveis)<br>
            \[ x^2 + y^2 + Dx + Ey + F = 0 \]
            onde \(D = -2h\), \(E = -2k\) e \(F = h^2 + k^2 - r^2\).
        """.trimIndent())

        // Exemplo
        loadLatex(view.findViewById(R.id.math_ex_circunferencia), """
            <div style="text-align:left;">
            <b>Exemplo 1:</b> Determine a equação da circunferência de centro \(C(3, -1)\) e raio \(r = 4\):<br>
            \[ (x - 3)^2 + (y - (-1))^2 = 4^2 \Rightarrow (x - 3)^2 + (y + 1)^2 = 16 \]
            </div>
        """.trimIndent())

        // Visualização SVG
        val webVisualizacao = view.findViewById<WebView>(R.id.math_circ_visualizacao)
        val svgHtml = """
            <html><body style="display:flex; justify-content:center; align-items:center; background:transparent; margin:0; padding:10px;">
            <svg width="260" height="260" viewBox="0 0 260 260">
                <!-- Grid background -->
                <defs>
                    <pattern id="grid" width="20" height="20" patternUnits="userSpaceOnUse">
                        <path d="M 20 0 L 0 0 0 20" fill="none" stroke="#F0F0F0" stroke-width="1"/>
                    </pattern>
                </defs>
                <rect width="260" height="260" fill="url(#grid)" />
                
                <!-- Eixos -->
                <line x1="10" y1="130" x2="250" y2="130" stroke="#AAA" stroke-width="1" />
                <line x1="130" y1="10" x2="130" y2="250" stroke="#AAA" stroke-width="1" />
                
                <!-- Circunferência -->
                <circle cx="130" cy="130" r="90" stroke="#1A73E8" stroke-width="3" fill="rgba(26, 115, 232, 0.1)" />
                
                <!-- Centro -->
                <circle cx="130" cy="130" r="5" fill="red" />
                <text x="135" y="145" font-size="14" font-weight="bold" fill="red">C(h,k)</text>
                
                <!-- Ponto P -->
                <circle cx="193" cy="67" r="5" fill="#1A73E8" />
                <text x="200" y="60" font-size="14" font-weight="bold" fill="#1A73E8">P(x,y)</text>
                
                <!-- Raio -->
                <line x1="130" y1="130" x2="193" y2="67" stroke="black" stroke-width="2" stroke-dasharray="5,5" />
                <text x="155" y="95" font-size="16" font-weight="bold" fill="black">r</text>
            </svg>
            </body></html>
        """.trimIndent()
        webVisualizacao.loadDataWithBaseURL(null, svgHtml, "text/html", "UTF-8", null)

        // Mais Exemplos
        loadLatex(view.findViewById(R.id.math_mais_ex_circ), """
            <div style="text-align:left; font-size:14px;">
            <b>2. Encontre o centro e o raio:</b><br>
            Dada a equação: \(x^2 + y^2 - 4x + 6y - 3 = 0\)<br><br>
            Agrupando termos de x e y:<br>
            \((x^2 - 4x) + (y^2 + 6y) = 3\)<br>
            Completando os quadrados:<br>
            \((x^2 - 4x + 4) + (y^2 + 6y + 9) = 3 + 4 + 9\)<br>
            \((x - 2)^2 + (y + 3)^2 = 16\)<br>
            <b>Centro:</b> \(C(2, -3)\), <b>Raio:</b> \(r = 4\).
            <br><br>
            <b>3. Circunferência que passa pela origem:</b><br>
            Centro \(C(1, 1)\) e passa por \(O(0, 0)\):<br>
            \(r = \sqrt{(1-0)^2 + (1-0)^2} = \sqrt{2}\)<br>
            Equação: \((x - 1)^2 + (y - 1)^2 = 2\).
            </div>
        """.trimIndent())
    }
}