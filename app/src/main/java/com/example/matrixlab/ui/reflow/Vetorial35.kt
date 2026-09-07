package com.example.matrixlab.ui.reflow

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import androidx.fragment.app.Fragment
import com.example.matrixlab.R

class Vetorial35 : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_vetorial35, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fun loadLatex(webView: WebView?, formula: String) {
            if (webView == null) return
            val html = """
                <html><head>
                <script type="text/javascript" async src="https://cdnjs.cloudflare.com/ajax/libs/mathjax/2.7.7/MathJax.js?config=TeX-MML-AM_CHTML"></script>
                <style>body{font-size:15px; color:#333; background:transparent; margin:0; padding:10px; text-align:left;}</style>
                </head><body>$formula</body></html>
            """.trimIndent()
            webView.settings.javaScriptEnabled = true
            webView.loadDataWithBaseURL(null, html, "text/html", "UTF-8", null)
        }

        fun loadSvg(webView: WebView?, svgCode: String) {
            if (webView == null) return
            val html = """
                <html><body style="display:flex; justify-content:center; align-items:center; background:transparent; margin:0; padding:10px;">
                <svg width="250" height="200" viewBox="0 0 250 200">$svgCode</svg>
                </body></html>
            """.trimIndent()
            webView.settings.javaScriptEnabled = true
            webView.loadDataWithBaseURL(null, html, "text/html", "UTF-8", null)
        }

        // Equação Geral
        loadLatex(view.findViewById(R.id.math_eq_quadrica), """
            <b>Equação Geral:</b>
            \[ Ax^2 + By^2 + Cz^2 + Dxy + Exz + Fyz + Gx + Hy + Iz + J = 0 \]
            <br>
            A classificação depende dos coeficientes e dos sinais após a redução da equação por translação e rotação de eixos.
        """.trimIndent())

        // 1. Elipsoide
        loadSvg(view.findViewById(R.id.img_elipsoide), """
            <ellipse cx="125" cy="100" rx="90" ry="60" stroke="#1A73E8" stroke-width="2" fill="rgba(26, 115, 232, 0.1)" />
            <ellipse cx="125" cy="100" rx="90" ry="20" stroke="#1A73E8" stroke-width="1" fill="none" stroke-dasharray="4" />
            <ellipse cx="125" cy="100" rx="30" ry="60" stroke="#1A73E8" stroke-width="1" fill="none" stroke-dasharray="4" />
            <line x1="35" y1="100" x2="215" y2="100" stroke="#333" stroke-width="1" />
            <line x1="125" y1="40" x2="125" y2="160" stroke="#333" stroke-width="1" />
            <text x="220" y="105" font-size="12">x</text>
            <text x="120" y="35" font-size="12">z</text>
        """)
        loadLatex(view.findViewById(R.id.math_elipsoide), """
            <b>Equação Reduzida:</b>
            \[ \frac{x^2}{a^2} + \frac{y^2}{b^2} + \frac{z^2}{c^2} = 1 \]
            <b>Aplicação:</b> Modelagem de objetos celestes (planetas achatados), tanques de pressão e arquitetura (domos).
            <br><br>
            <b>Exemplo Resolvido:</b> Determine os eixos de \( \frac{x^2}{16} + \frac{y^2}{9} + \frac{z^2}{4} = 1 \).<br>
            Semi-eixos: \( a=4, b=3, c=2 \). Eixos totais: \( 8, 6, 4 \).
        """.trimIndent())

        // 2. Hiperboloide 1 Folha
        loadSvg(view.findViewById(R.id.img_hiperboloide1), """
            <path d="M 80,40 Q 125,100 80,160" stroke="#1A73E8" stroke-width="2" fill="none" />
            <path d="M 170,40 Q 125,100 170,160" stroke="#1A73E8" stroke-width="2" fill="none" />
            <ellipse cx="125" cy="40" rx="45" ry="12" stroke="#1A73E8" stroke-width="1.5" fill="rgba(26, 115, 232, 0.05)" />
            <ellipse cx="125" cy="160" rx="45" ry="12" stroke="#1A73E8" stroke-width="1.5" fill="rgba(26, 115, 232, 0.05)" />
            <ellipse cx="125" cy="100" rx="15" ry="5" stroke="#1A73E8" stroke-width="1" fill="none" stroke-dasharray="2" />
        """)
        loadLatex(view.findViewById(R.id.math_hiperboloide1), """
            <b>Equação Reduzida:</b>
            \[ \frac{x^2}{a^2} + \frac{y^2}{b^2} - \frac{z^2}{c^2} = 1 \]
            <b>Aplicação:</b> Torres de resfriamento em usinas nucleares (devido à estabilidade estrutural) e engrenagens hiperboloides.
            <br><br>
            <b>Exemplo:</b> A equação \( x^2 + y^2 - z^2 = 1 \) representa um hiperboloide de uma folha ao longo do eixo z.
        """.trimIndent())

        // 3. Hiperboloide 2 Folhas
        loadSvg(view.findViewById(R.id.img_hiperboloide2), """
            <path d="M 125,70 Q 125,20 60,20 M 125,70 Q 125,20 190,20" stroke="#1A73E8" stroke-width="2" fill="none" />
            <path d="M 125,130 Q 125,180 60,180 M 125,130 Q 125,180 190,180" stroke="#1A73E8" stroke-width="2" fill="none" />
            <ellipse cx="125" cy="20" rx="65" ry="12" stroke="#1A73E8" stroke-width="1" fill="rgba(26, 115, 232, 0.05)" />
            <ellipse cx="125" cy="180" rx="65" ry="12" stroke="#1A73E8" stroke-width="1" fill="rgba(26, 115, 232, 0.05)" />
        """)
        loadLatex(view.findViewById(R.id.math_hiperboloide2), """
            <b>Equação Reduzida:</b>
            \[ \frac{z^2}{c^2} - \frac{x^2}{a^2} - \frac{y^2}{b^2} = 1 \]
            <b>Aplicação:</b> Design de antenas parabólicas compostas e refletores ópticos.
            <br><br>
            <b>Exemplo:</b> Se \( z^2 - x^2 - y^2 = 4 \), a distância entre os vértices das duas folhas é \( 2c = 4 \).
        """.trimIndent())

        // 4. Paraboloide Elíptico
        loadSvg(view.findViewById(R.id.img_paraboloide_elip), """
            <path d="M 60,50 Q 125,180 190,50" stroke="#1A73E8" stroke-width="2" fill="none" />
            <ellipse cx="125" cy="50" rx="65" ry="15" stroke="#1A73E8" stroke-width="2" fill="rgba(26, 115, 232, 0.1)" />
            <line x1="125" y1="180" x2="125" y2="30" stroke="#333" stroke-width="1" />
        """)
        loadLatex(view.findViewById(R.id.math_paraboloide_elip), """
            <b>Equação Reduzida:</b>
            \[ z = \frac{x^2}{a^2} + \frac{y^2}{b^2} \]
            <b>Aplicação:</b> Antenas parabólicas, refletores de faróis e radiotelescópios.
            <br><br>
            <b>Exemplo:</b> \( z = x^2 + y^2 \) é um paraboloide circular (de revolução) com vértice na origem.
        """.trimIndent())

        // 5. Paraboloide Hiperbólico
        loadSvg(view.findViewById(R.id.img_paraboloide_hiper), """
            <path d="M 50,80 Q 125,150 200,80" stroke="#1A73E8" stroke-width="2" fill="none" />
            <path d="M 80,40 Q 125,120 170,40" stroke="#1A73E8" stroke-width="1" fill="none" opacity="0.5"/>
            <path d="M 50,80 L 80,40 M 200,80 L 170,40" stroke="#1A73E8" stroke-width="2" fill="none" />
            <path d="M 125,150 Q 80,100 80,40" stroke="#1A73E8" stroke-width="2" fill="none" />
            <path d="M 125,150 Q 170,100 170,40" stroke="#1A73E8" stroke-width="2" fill="none" />
        """)
        loadLatex(view.findViewById(R.id.math_paraboloide_hiper), """
            <b>Equação Reduzida:</b>
            \[ z = \frac{y^2}{b^2} - \frac{x^2}{a^2} \]
            <b>Aplicação:</b> Estruturas de telhados arquitetônicos (pela resistência e facilidade de drenagem) e superfícies de contato mecânico.
            <br><br>
            <b>Exemplo:</b> A sela \( z = y^2 - x^2 \) sobe no eixo y e desce no eixo x.
        """.trimIndent())

        // 6. Cone
        loadSvg(view.findViewById(R.id.img_cone), """
            <line x1="70" y1="40" x2="180" y2="160" stroke="#1A73E8" stroke-width="2" />
            <line x1="180" y1="40" x2="70" y2="160" stroke="#1A73E8" stroke-width="2" />
            <ellipse cx="125" cy="40" rx="55" ry="12" stroke="#1A73E8" stroke-width="1.5" fill="rgba(26, 115, 232, 0.05)" />
            <ellipse cx="125" cy="160" rx="55" ry="12" stroke="#1A73E8" stroke-width="1.5" fill="rgba(26, 115, 232, 0.05)" />
            <circle cx="125" cy="100" r="3" fill="red" />
        """)
        loadLatex(view.findViewById(R.id.math_cone), """
            <b>Equação Reduzida:</b>
            \[ \frac{x^2}{a^2} + \frac{y^2}{b^2} - \frac{z^2}{c^2} = 0 \]
            <b>Aplicação:</b> Óptica, propagação de ondas e geometria projetiva.
            <br><br>
            <b>Exemplo:</b> No cone \( z^2 = x^2 + y^2 \), as seções \( z=k \) são circunferências de raio \( k \).
        """.trimIndent())
    }
}