package com.example.matrixlab.ui.transform

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.matrixlab.databinding.FragmentBancoquestoesBinding
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class BancoQuestoesFragment : Fragment() {

    private var _binding: FragmentBancoquestoesBinding? = null
    private val binding get() = _binding!!

    private val service: LangChainService by lazy {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(logging)
            .connectTimeout(60, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .build()

        Retrofit.Builder()
            .baseUrl("http://matrix-server.local:8000/")
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(LangChainService::class.java)
    }

    override fun onCreateView(//mostrar o layout
        inflater: LayoutInflater,//infla o layout para que objetos imgens de tela se transformem em objetos
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBancoquestoesBinding.inflate(inflater, container, false)
        return binding.root //retornar para tela
    }

    @SuppressLint("SetJavaScriptEnabled")//agente desativa os avisos c isso
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {//aqui fazemos a configuracao de tela e clique do botao
        super.onViewCreated(view, savedInstanceState)

        // Configura a WebView para renderizar HTML + KaTeX
        configurarWebView()

        // ajeita para deixar a tela mais amigavel
        exibirHtml(htmlVazio())

        binding.QButton.setOnClickListener {//é o butão normal
            gerarQuestaoComAgente()
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun configurarWebView() {//configuração da pagina web view
        binding.QWebView.apply { //o javascript vem por padroa desativado , entao eu tivei aqui
            settings.javaScriptEnabled = true        // KaTeX precisa de JS
            settings.domStorageEnabled = true
            settings.loadWithOverviewMode = true
            settings.useWideViewPort = true
            setBackgroundColor(Color.TRANSPARENT)    // fundo transparente

            // Evita que links abram o navegador externo
            webViewClient = object : WebViewClient() {
                override fun shouldOverrideUrlLoading(view: WebView?, url: String?) = true
            }
        }
    }

    private fun gerarQuestaoComAgente() {//parte de trabalho com agentes
        val pergunta = binding.QInput.text.toString().trim()

        if (pergunta.isEmpty()) {
            exibirHtml("<p style='color:#999;'>Digite uma pergunta antes de gerar.</p>")
            return
        }

        // Mostra loading enquanto o agente pensa
        exibirHtml(htmlLoading())
        binding.QButton.isEnabled = false //deaticado o botao

        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val resposta = service.enviarPergunta(ChatRequest(pergunta))

                // Agente retornou HTML com KaTeX — monta a página e renderiza
                exibirHtml(wrapComKatex(resposta.response))

                Log.d("MATRIXLAB", "Resposta recebida: ${resposta.response}")

            } catch (e: Exception) {
                exibirHtml(htmlErro(e.message ?: "Erro desconhecido"))
                Log.e("MATRIXLAB", "Falha na conexão: ${e.message}")
            } finally {
                binding.QButton.isEnabled = true
            }
        }
    }

    //aqui temos a montagem da pagina completa
    private fun wrapComKatex(conteudoHtml: String): String = """
        <!DOCTYPE html>
        <html>
        <head>
          <meta charset="UTF-8">
          <meta name="viewport" content="width=device-width, initial-scale=1.0">
          <link rel="stylesheet"
            href="https://cdn.jsdelivr.net/npm/katex@0.16.9/dist/katex.min.css">
          <script src="https://cdn.jsdelivr.net/npm/katex@0.16.9/dist/katex.min.js"></script>
          <script src="https://cdn.jsdelivr.net/npm/katex@0.16.9/dist/contrib/auto-render.min.js"></script>
          <style>
            body {
              font-family: sans-serif;
              font-size: 15px;
              padding: 8px 4px;
              color: #1a1a1a;
              background: transparent;
              line-height: 1.8;
            }
            .secao {
              margin-bottom: 20px;
              padding: 12px 14px;
              background: #F5F3FF;
              border-left: 3px solid #6200EE;
              border-radius: 8px;
            }
            .titulo {
              font-weight: bold;
              font-size: 14px;
              color: #3700B3;
              margin: 0 0 8px 0;
              text-transform: uppercase;
              letter-spacing: 0.5px;
            }
            .chip {
              display: inline-block;
              background: #EDE7F6;
              color: #4527A0;
              border-radius: 999px;
              padding: 3px 12px;
              font-size: 13px;
              margin: 2px 4px 2px 0;
            }
            p { margin: 6px 0; }
            .katex-display { overflow-x: auto; padding: 4px 0; }
          </style>
        </head>
        <body>
          $conteudoHtml
          <script>
            document.addEventListener("DOMContentLoaded", function() {
              renderMathInElement(document.body, {
                delimiters: [
                  {left: '$$', right: '$$', display: true},
                  {left: '$',  right: '$',  display: false}
                ],
                throwOnError: false
              });
            });
          </script>
        </body>
        </html>
    """.trimIndent()

    // ================================================================
    // Funções auxiliares de estado da UI
    // ================================================================

    private fun exibirHtml(html: String) {
        // pega o html e coloca dentro de um web view, renderizar, atualizar tela
        binding.QWebView.loadDataWithBaseURL(
            "https://cdn.jsdelivr.net",//baixar os recursos katex para formatção matematica
            html,
            "text/html",
            "UTF-8",
            null
        )
    }

    private fun htmlLoading() = wrapComKatex("""
        <div style="text-align:center; padding: 40px 0; color: #6200EE;">
          <p style="font-size:16px;">⏳ O Agente está pensando...</p>
        </div>
    """)

    private fun htmlVazio() = wrapComKatex("""
        <div style="text-align:center; padding: 40px 16px; color: #999;">
          <p style="font-size:15px;">Digite uma pergunta e toque em <b>Gerar</b></p>
          <p style="font-size:13px;">Exemplo: <i>"Calcule os autovalores da matriz do PDF"</i></p>
        </div>
    """)

    private fun htmlErro(msg: String) = wrapComKatex("""
        <div style="padding:12px; background:#FFEBEE; border-left:3px solid #B00020; border-radius:8px;">
          <p style="color:#B00020; font-weight:bold; margin:0 0 4px;">Erro de conexão</p>
          <p style="color:#600; font-size:13px; margin:0;">$msg</p>
          <p style="color:#900; font-size:12px; margin:8px 0 0;">Verifique se o servidor Python está rodando.</p>
        </div>
    """)

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}