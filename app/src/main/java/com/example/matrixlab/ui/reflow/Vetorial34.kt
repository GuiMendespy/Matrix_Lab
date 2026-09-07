package com.example.matrixlab.ui.reflow

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import androidx.fragment.app.Fragment
import com.example.matrixlab.R

class Vetorial34 : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_vetorial34, container, false)
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

        // Visualização SVG Parábola
        val webVisualizacao = view.findViewById<WebView>(R.id.math_parabola_visualizacao)
        val svgHtml = """
            <html><body style="display:flex; justify-content:center; align-items:center; background:transparent; margin:0; padding:10px;">
            <svg width="260" height="200" viewBox="0 0 260 200">
                <!-- Diretriz -->
                <line x1="20" y1="170" x2="240" y2="170" stroke="red" stroke-width="3" />
                <text x="245" y="175" font-size="14" font-weight="bold" fill="red">d</text>
                
                <!-- Eixo de simetria -->
                <line x1="130" y1="20" x2="130" y2="190" stroke="#AAA" stroke-width="1" stroke-dasharray="2" />
                
                <!-- Parábola -->
                <path d="M 30,30 Q 130,280 230,30" stroke="#1A73E8" stroke-width="3" fill="none" />
                
                <!-- Foco -->
                <circle cx="130" cy="80" r="5" fill="black" />
                <text x="135" y="75" font-size="14" font-weight="bold" fill="black">F</text>
                
                <!-- Vértice -->
                <circle cx="130" cy="125" r="5" fill="black" />
                <text x="135" y="140" font-size="14" font-weight="bold" fill="black">V</text>
                
                <!-- Parâmetro p -->
                <line x1="140" y1="80" x2="140" y2="170" stroke="green" stroke-width="1" stroke-dasharray="2" />
                <text x="145" y="130" font-size="12" fill="green">p</text>
            </svg>
            </body></html>
        """.trimIndent()
        webVisualizacao?.settings?.javaScriptEnabled = true
        webVisualizacao?.loadDataWithBaseURL(null, svgHtml, "text/html", "UTF-8", null)

        // Equação da Parábola
        loadLatex(view.findViewById(R.id.math_eq_parabola), """
            <b>Eixo de simetria sobre o eixo y:</b><br>
            \[ x^2 = 2py \]
            <b>Eixo de simetria sobre o eixo x:</b><br>
            \[ y^2 = 2px \]
            onde \(p\) é a distância do foco à diretriz.
        """.trimIndent())

        // Exercícios Resolvidos
        loadLatex(view.findViewById(R.id.math_ex_resolvidos_34), """
            <div style="text-align:left; font-size:14px;">
            <b>Exemplo 1:</b> Determine a equação da parábola de foco \(F(0, 3)\) e diretriz \(y = -3\).<br>
            O eixo de simetria é o eixo y. A distância do foco à diretriz é \(p = 6\).<br>
            Equação: \(x^2 = 2(6)y \Rightarrow x^2 = 12y\).
            <br><br>
            <b>Exemplo 2:</b> Dada \(y^2 = -8x\), determine o foco e a diretriz.<br>
            \(2p = -8 \Rightarrow p = -4\).<br>
            Concavidade voltada para a esquerda (eixo x).<br>
            <b>Foco:</b> \(F(-2, 0)\).<br>
            <b>Diretriz:</b> \(x = 2\).
            </div>
        """.trimIndent())
    }
}