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
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.matrixlab.databinding.FragmentBancoquestoesBinding
import com.example.matrixlab.utils.NsdHelper
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

    private val palavrasGerador = listOf(
        "gerar", "gere", "criar", "crie", "nova questão", "nova questao",
        "adicionar questão", "adicionar questao", "fazer questão", "fazer questao",
        "montar questão", "montar questao", "produzir", "elaborar"
    )

    private var service: LangChainService? = null
    private var nsdHelper: NsdHelper? = null

    private fun buildService(baseUrl: String): LangChainService {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(logging)
            .connectTimeout(120, TimeUnit.SECONDS)
            .readTimeout(120, TimeUnit.SECONDS)
            .writeTimeout(120, TimeUnit.SECONDS)
            .build()

        return Retrofit.Builder()
            .baseUrl(baseUrl)
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

        // 1. Conecta ao servidor do Render por padrão
        val renderUrl = "https://agente-algebra-linear.onrender.com/"
        service = buildService(renderUrl)
        Log.d("RENDER", "Conectando ao Render: $renderUrl")

        // 2. Mantém o NSD para o caso de você rodar o servidor localmente (ele substituirá a URL se encontrar algo no Wi-Fi)
        nsdHelper = NsdHelper(requireContext()) { baseUrl ->
            activity?.runOnUiThread {
                Log.d("NSD", "Servidor local encontrado: $baseUrl")
                service = buildService(baseUrl)
                Toast.makeText(context, "Conectado ao servidor LOCAL", Toast.LENGTH_SHORT).show()
            }
        }
        nsdHelper?.startDiscovery()

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
        val s = service
        if (s == null) {
            Toast.makeText(context, "Servidor não encontrado. Aguarde a conexão...", Toast.LENGTH_SHORT).show()
            return
        }

        adicionarMensagemAoChat("Você", pergunta, isUsuario = true)
        binding.QInput.text?.clear()

        val loadingId = "loading_${System.currentTimeMillis()}"
        historicoChat.append("<div id='$loadingId' class='balao agente'>⏳ O Agente está pensando...</div>")
        atualizarWebView()
        binding.QButton.isEnabled = false

        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val resposta = s.enviarPergunta(
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
        val s = service
        if (s == null) {
            Toast.makeText(context, "Servidor não encontrado. Aguarde a conexão...", Toast.LENGTH_SHORT).show()
            return
        }

        adicionarMensagemAoChat("Você", instrucao, isUsuario = true)
        binding.QInput.text?.clear()

        val loadingId = "loading_${System.currentTimeMillis()}"
        historicoChat.append("<div id='$loadingId' class='balao agente'>⏳ Gerando nova questão, aguarde...</div>")
        atualizarWebView()
        binding.QButton.isEnabled = false

        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val resposta = s.gerarNovaQuestao(
                    GenerateRequest(
                        message = instrucao,
                        sessionId = viewModel.sessionId // Usa o ID da sessão atual
                    )
                )

                viewModel.salvarSessionId(resposta.sessionId) // Salva o ID retornado
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
          <script src="https://cdn.jsdelivr.net/npm/marked/marked.min.js"></script>
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
            
            /* Ajustes para Markdown */
            .conteudo h1, .conteudo h2, .conteudo h3, .conteudo h4, .conteudo h5, .conteudo h6 {
              margin-top: 10px;
              margin-bottom: 5px;
              font-weight: bold;
              line-height: 1.2;
            }
            .conteudo h1 { font-size: 1.4em; }
            .conteudo h2 { font-size: 1.3em; }
            .conteudo h3 { font-size: 1.2em; }
            .conteudo h4 { font-size: 1.1em; }
            .conteudo p { margin-bottom: 8px; }
            .conteudo p:last-child { margin-bottom: 0; }
            .conteudo ul, .conteudo ol { margin-left: 20px; margin-bottom: 8px; }
          </style>
        </head>
        <body>
          $conteudoHtml
          <div id="anchor"></div>
          <script>
            function renderizar() {
              // 1. Processar Markdown nos conteúdos das mensagens
              document.querySelectorAll('.conteudo').forEach(el => {
                if (!el.dataset.markdownProcessed) {
                  // marked.parse converte o texto Markdown em HTML
                  el.innerHTML = marked.parse(el.innerHTML.trim());
                  el.dataset.markdownProcessed = "true";
                }
              });

              // 2. Processar KaTeX
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
        nsdHelper?.stopDiscovery()
        _binding = null
    }
}
