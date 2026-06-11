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
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.matrixlab.databinding.FragmentBancoquestoesBinding
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class BancoQuestoesFragment : Fragment() {

    private val viewModel: BancoQuestoesViewModel by viewModels()
    private var historicoChat = StringBuilder()

    private var _binding: FragmentBancoquestoesBinding? = null
    private val binding get() = _binding!!

    // Palavras que indicam intenção de GERAR questão
    private val palavrasGerador = listOf(
        "gerar", "gere", "criar", "crie", "nova questão", "nova questao",
        "adicionar questão", "adicionar questao", "fazer questão", "fazer questao",
        "montar questão", "montar questao", "produzir", "elaborar"
    )

    private val service: LangChainService by lazy {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(logging)
            .connectTimeout(120, TimeUnit.SECONDS)
            .readTimeout(120, TimeUnit.SECONDS)
            .writeTimeout(120, TimeUnit.SECONDS)
            .build()

        Retrofit.Builder()
            .baseUrl("http://matrix-server.local:8000/")
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(LangChainService::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBancoquestoesBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        configurarWebView()

        viewModel.historicoHtml.observe(viewLifecycleOwner) { html ->
            if (!html.isNullOrEmpty() && html != historicoChat.toString()) {
                historicoChat = StringBuilder(html)
                exibirHtml(wrapComKatex(html))
            } else if (html.isNullOrEmpty() && historicoChat.isEmpty()) {
                exibirHtml(htmlVazio())
            }
        }

        // Um único botão — decide internamente qual agente chamar
        binding.QButton.setOnClickListener {
            despacharParaAgente()
        }
    }

    // ── ROTEADOR: decide qual agente usar ─────────────────────────────────
    private fun despacharParaAgente() {
        val texto = binding.QInput.text.toString().trim()
        if (texto.isEmpty()) return

        val ehGerador = palavrasGerador.any { texto.lowercase().contains(it) }

        if (ehGerador) {
            gerarNovaQuestao(texto)
        } else {
            consultarAgente(texto)
        }
    }

    // ── AGENTE 1: Consulta (/chat) ────────────────────────────────────────
    private fun consultarAgente(pergunta: String) {
        adicionarMensagemAoChat("Você", pergunta, isUsuario = true)
        binding.QInput.text?.clear()

        val loadingId = "loading_${System.currentTimeMillis()}"
        historicoChat.append("<div id='$loadingId' class='balao agente'>⏳ O Agente está pensando...</div>")
        atualizarWebView()
        binding.QButton.isEnabled = false

        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val resposta = service.enviarPergunta(
                    ChatRequest(
                        message = pergunta,
                        sessionId = viewModel.sessionId
                    )
                )

                viewModel.salvarSessionId(resposta.sessionId)
                removerLoading(loadingId)
                adicionarMensagemAoChat("Agente", resposta.response, isUsuario = false)
                Log.d("MATRIXLAB", "[CONSULTA] ${resposta.response}")

            } catch (e: Exception) {
                removerLoading(loadingId)
                adicionarMensagemAoChat("Erro", "Falha na conexão: ${e.message}", isUsuario = false)
                Log.e("MATRIXLAB", "[CONSULTA] Erro: ${e.message}")
            } finally {
                binding.QButton.isEnabled = true
            }
        }
    }

    // ── AGENTE 2: Gerador (/generate) ─────────────────────────────────────
    private fun gerarNovaQuestao(instrucao: String) {
        adicionarMensagemAoChat("Você", instrucao, isUsuario = true)
        binding.QInput.text?.clear()

        val loadingId = "loading_${System.currentTimeMillis()}"
        historicoChat.append("<div id='$loadingId' class='balao agente'>⏳ Gerando nova questão, aguarde...</div>")
        atualizarWebView()
        binding.QButton.isEnabled = false

        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val resposta = service.gerarNovaQuestao(
                    GenerateRequest(
                        message = instrucao,
                        threadId = "generator-${System.currentTimeMillis()}"
                    )
                )

                removerLoading(loadingId)

                val conteudoComBadge = """
                    <div style="margin-bottom:6px;">
                      <span style="background:#E8F5E9;color:#2E7D32;border:1px solid #A5D6A7;
                                   border-radius:999px;padding:2px 10px;font-size:12px;">
                        ✅ Nova questão salva no banco
                      </span>
                    </div>
                    ${resposta.response}
                """.trimIndent()

                adicionarMensagemAoChat("Agente Gerador", conteudoComBadge, isUsuario = false)
                Log.d("MATRIXLAB", "[GERADOR] ${resposta.response}")

            } catch (e: Exception) {
                removerLoading(loadingId)
                adicionarMensagemAoChat("Erro", "Falha ao gerar: ${e.message}", isUsuario = false)
                Log.e("MATRIXLAB", "[GERADOR] Erro: ${e.message}")
            } finally {
                binding.QButton.isEnabled = true
            }
        }
    }

    // ── HELPERS ───────────────────────────────────────────────────────────

    private fun removerLoading(loadingId: String) {
        val tag = "<div id='$loadingId' class='balao agente'>⏳ O Agente está pensando...</div>"
        val tagGerador = "<div id='$loadingId' class='balao agente'>⏳ Gerando nova questão, aguarde...</div>"
        listOf(tag, tagGerador).forEach { t ->
            val i = historicoChat.indexOf(t)
            if (i != -1) historicoChat.delete(i, i + t.length)
        }
    }

    private fun adicionarMensagemAoChat(autor: String, conteudo: String, isUsuario: Boolean) {
        val estiloClasse = if (isUsuario) "usuario" else "agente"
        historicoChat.append("""
            <div class="balao $estiloClasse">
                <div class="autor">$autor</div>
                <div class="conteudo">$conteudo</div>
            </div>
        """.trimIndent())
        atualizarWebView()
    }

    private fun atualizarWebView() {
        val htmlCompleto = wrapComKatex(historicoChat.toString())
        exibirHtml(htmlCompleto)
        viewModel.atualizarHistorico(historicoChat.toString())
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun configurarWebView() {
        binding.QWebView.apply {
            settings.javaScriptEnabled = true
            settings.domStorageEnabled = true
            settings.loadWithOverviewMode = true
            settings.useWideViewPort = true
            setBackgroundColor(Color.TRANSPARENT)
            webViewClient = object : WebViewClient() {
                override fun shouldOverrideUrlLoading(view: WebView?, url: String?) = true
            }
        }
    }

    private fun wrapComKatex(conteudoHtml: String): String = """
        <!DOCTYPE html>
        <html>
        <head>
          <meta charset="UTF-8">
          <meta name="viewport" content="width=device-width, initial-scale=1.0">
          <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/katex@0.16.9/dist/katex.min.css">
          <script src="https://cdn.jsdelivr.net/npm/katex@0.16.9/dist/katex.min.js"></script>
          <script src="https://cdn.jsdelivr.net/npm/katex@0.16.9/dist/contrib/auto-render.min.js"></script>
          <style>
            body {
              font-family: sans-serif;
              font-size: 15px;
              padding: 10px;
              color: #1a1a1a;
              background: #f8f9fa;
              line-height: 1.5;
              display: flex;
              flex-direction: column;
            }
            .balao {
              margin-bottom: 12px;
              padding: 10px 14px;
              border-radius: 15px;
              max-width: 85%;
              word-wrap: break-word;
            }
            .usuario {
              align-self: flex-end;
              background: #6200EE;
              color: white;
              border-bottom-right-radius: 2px;
              margin-left: auto;
            }
            .agente {
              align-self: flex-start;
              background: white;
              color: #1a1a1a;
              border-bottom-left-radius: 2px;
              border: 1px solid #e0e0e0;
              box-shadow: 0 2px 4px rgba(0,0,0,0.05);
            }
            .autor {
              font-size: 11px;
              font-weight: bold;
              text-transform: uppercase;
              margin-bottom: 4px;
              opacity: 0.8;
            }
            .usuario .autor { color: #E0E0E0; }
            .agente .autor { color: #6200EE; }
            p { margin: 4px 0; }
            .katex-display { overflow-x: auto; padding: 4px 0; margin: 0; }
          </style>
        </head>
        <body>
          $conteudoHtml
          <div id="anchor"></div>
          <script>
            function renderizar() {
              renderMathInElement(document.body, {
                delimiters: [
                  {left: '$$', right: '$$', display: true},
                  {left: '$',  right: '$',  display: false},
                  {left: '\\(', right: '\\)', display: false},
                  {left: '\\[', right: '\\]', display: true}
                ],
                throwOnError: false
              });
              document.getElementById('anchor').scrollIntoView();
            }
            document.addEventListener("DOMContentLoaded", renderizar);
            window.scrollTo(0, document.body.scrollHeight);
          </script>
        </body>
        </html>
    """.trimIndent()

    private fun exibirHtml(html: String) {
        binding.QWebView.loadDataWithBaseURL(
            "https://cdn.jsdelivr.net",
            html,
            "text/html",
            "UTF-8",
            null
        )
    }

    private fun htmlVazio() = wrapComKatex("""
        <div style="text-align:center; padding: 40px 16px; color: #999;">
          <p style="font-size:15px;">Digite uma pergunta e toque em <b>Enviar</b></p>
          <p style="font-size:13px;">Para consultar: <i>"Explique produto vetorial"</i></p>
          <p style="font-size:13px;">Para gerar questão: <i>"Gere uma questão de determinante"</i></p>
        </div>
    """)

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
