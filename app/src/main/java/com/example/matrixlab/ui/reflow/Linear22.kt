package com.example.matrixlab.ui.reflow

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import androidx.fragment.app.Fragment
import com.example.matrixlab.R

class Linear22 : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_linear22, container, false)
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

        // Definição de Núcleo
        loadLatex(view.findViewById(R.id.math_def_nucleo),
            "\\[ N(T) = \\{ v \\in V \\mid T(v) = \\mathbf{0} \\} \\]")

        // Exemplo de sistema para Núcleo
        loadLatex(view.findViewById(R.id.math_exemplo_nucleo), """
            \[
            \begin{cases} 
            x + y = 0 \\ 
            2x - y = 0 
            \end{cases} \implies x=0, y=0 \implies N(T) = \{ (0,0) \}
            \]
        """.trimIndent())

        // Definição de Imagem
        loadLatex(view.findViewById(R.id.math_def_imagem),
            "\\[ Im(T) = \\{ w \\in W \\mid T(v) = w \\text{ para algum } v \\in V \\} \\]")

        // Teorema da Dimensão
        loadLatex(view.findViewById(R.id.math_teorema_dimensao),
            "\\[ \\dim(N(T)) + \\dim(Im(T)) = \\dim(V) \\]")
    }
}