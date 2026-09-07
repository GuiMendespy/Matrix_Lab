package com.example.matrixlab.ui.reflow

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import androidx.fragment.app.Fragment
import com.example.matrixlab.R

class Vetorial33 : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_vetorial33, container, false)
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

        // Visualização SVG Hipérbole
        val webVisualizacao = view.findViewById<WebView>(R.id.math_hiperbole_visualizacao)
        val svgHtml = """
            <html><body style="display:flex; justify-content:center; align-items:center; background:transparent; margin:0; padding:10px;">
            <svg width="260" height="200" viewBox="0 0 260 200">
                <!-- Eixos -->
                <line x1="10" y1="100" x2="250" y2="100" stroke="#AAA" stroke-width="1" />
                <line x1="130" y1="10" x2="130" y2="190" stroke="#AAA" stroke-width="1" />
                
                <!-- Assíntotas -->
                <line x1="30" y1="20" x2="230" y2="180" stroke="#EEE" stroke-width="1.5" stroke-dasharray="5,5" />
                <line x1="30" y1="180" x2="230" y2="20" stroke="#EEE" stroke-width="1.5" stroke-dasharray="5,5" />
                
                <!-- Curvas da Hipérbole -->
                <path d="M 50,20 Q 110,100 50,180" stroke="#1A73E8" stroke-width="3" fill="none" />
                <path d="M 210,20 Q 150,100 210,180" stroke="#1A73E8" stroke-width="3" fill="none" />
                
                <!-- Focos -->
                <circle cx="30" cy="100" r="4" fill="red" />
                <circle cx="230" cy="100" r="4" fill="red" />
                <text x="25" y="120" font-size="14" font-weight="bold" fill="red">F1</text>
                <text x="225" y="120" font-size="14" font-weight="bold" fill="red">F2</text>
                
                <!-- Vértices -->
                <circle cx="80" cy="100" r="3" fill="black" />
                <circle cx="180" cy="100" r="3" fill="black" />
                <text x="80" y="90" font-size="12" fill="black">A1</text>
                <text x="175" y="90" font-size="12" fill="black">A2</text>
            </svg>
            </body></html>
        """.trimIndent()
        webVisualizacao?.settings?.javaScriptEnabled = true
        webVisualizacao?.loadDataWithBaseURL(null, svgHtml, "text/html", "UTF-8", null)

        // Equação da Hipérbole
        loadLatex(view.findViewById(R.id.math_eq_hiperbole), """
            <b>Focos sobre o eixo x:</b><br>
            \[ \frac{x^2}{a^2} - \frac{y^2}{b^2} = 1 \]
            <b>Focos sobre o eixo y:</b><br>
            \[ \frac{y^2}{a^2} - \frac{x^2}{b^2} = 1 \]
            <br>
            <b>Assíntotas:</b> \[ y = \pm \frac{b}{a}x \]
        """.trimIndent())

        // Relação Fundamental
        loadLatex(view.findViewById(R.id.math_rel_hiperbole), """
            <b>Relação Pitagórica:</b> \[ c^2 = a^2 + b^2 \]
            <br>
            <b>Excentricidade:</b> \[ e = \frac{c}{a} > 1 \]
        """.trimIndent())

        // Exercícios Resolvidos
        loadLatex(view.findViewById(R.id.math_ex_resolvidos_33), """
            <div style="text-align:left; font-size:14px;">
            <b>Exemplo 1:</b> Dada a hipérbole \(\frac{x^2}{16} - \frac{y^2}{9} = 1\), determine os focos e as assíntotas.<br>
            \(a^2 = 16 \Rightarrow a = 4\), \(b^2 = 9 \Rightarrow b = 3\).<br>
            \(c^2 = 16 + 9 = 25 \Rightarrow c = 5\).<br>
            <b>Focos:</b> \(F_1(-5, 0)\) e \(F_2(5, 0)\). <b>Assíntotas:</b> \(y = \pm \frac{3}{4}x\).
            <br><br>
            <b>Exemplo 2:</b> Determine a equação da hipérbole equilátera (\(a=b\)) com focos \((\pm 4, 0)\).<br>
            \(c = 4\). Como \(a=b\), temos \(c^2 = a^2 + a^2 = 2a^2\).<br>
            \(16 = 2a^2 \Rightarrow a^2 = 8\).<br>
            <b>Equação:</b> \(\frac{x^2}{8} - \frac{y^2}{8} = 1 \Rightarrow x^2 - y^2 = 8\).
            </div>
        """.trimIndent())
    }
}