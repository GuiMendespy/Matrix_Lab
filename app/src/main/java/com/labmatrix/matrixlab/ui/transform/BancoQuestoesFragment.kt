package com.labmatrix.matrixlab.ui.transform

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.google.android.material.chip.Chip
import com.labmatrix.matrixlab.R
import com.labmatrix.matrixlab.data.Questao
import com.labmatrix.matrixlab.databinding.FragmentBancoquestoesBinding
import com.labmatrix.matrixlab.utils.NsdHelper
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class BancoQuestoesFragment : Fragment() {

    private val viewModel: BancoQuestoesViewModel by viewModels()

    private var _binding: FragmentBancoquestoesBinding? = null
    private val binding get() = _binding!!

    private var service: LangChainService? = null
    private var nsdHelper: NsdHelper? = null

    private var urlDescobertaMdns: String? = null
    private val emulatorUrl = "http://10.0.2.2:8000/"
    private val wifiLocalIpUrl = "http://192.168.0.22:8000/"

    private fun buildService(baseUrl: String): LangChainService {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(logging)
            .connectTimeout(5, TimeUnit.SECONDS)
            .readTimeout(15, TimeUnit.SECONDS)
            .writeTimeout(15, TimeUnit.SECONDS)
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Inicia o serviço mDNS para descobrir o servidor na rede Wi-Fi local
        iniciarDescobertaMdns()

        configurarListeners()
        configurarObservadores()
    }

    private fun iniciarDescobertaMdns() {
        try {
            nsdHelper = NsdHelper(requireContext()) { baseUrl ->
                activity?.runOnUiThread {
                    Log.d("MATRIXLAB", "Servidor mDNS descoberto: $baseUrl")
                    urlDescobertaMdns = baseUrl
                    if (viewModel.modoOnline.value == true && service == null) {
                        conectarAoServidorLocal()
                    }
                }
            }
            nsdHelper?.startDiscovery()
        } catch (e: Exception) {
            Log.e("MATRIXLAB", "Erro ao iniciar mDNS: ${e.message}")
        }
    }

    private fun conectarAoServidorLocal(onConcluido: (() -> Unit)? = null) {
        val candidatos = mutableListOf<Pair<String, String>>()

        urlDescobertaMdns?.let { candidatos.add(Pair(it, "mDNS ($it)")) }
        candidatos.add(Pair(emulatorUrl, "Emulador (10.0.2.2)"))
        candidatos.add(Pair(wifiLocalIpUrl, "Wi-Fi (192.168.0.22)"))

        testarListaCandidatos(candidatos, 0, onConcluido)
    }

    private fun testarListaCandidatos(
        candidatos: List<Pair<String, String>>,
        index: Int,
        onConcluido: (() -> Unit)? = null
    ) {
        if (index >= candidatos.size) {
            service = null
            val msg = "🔴 Servidor Local não encontrado. Certifique-se de executar 'python main.py' no seu computador."
            Toast.makeText(context, msg, Toast.LENGTH_LONG).show()
            binding.tvModoBadge.text = "[DESCONECTADO]"
            binding.tvModoBadge.setTextColor(Color.parseColor("#C62828"))
            onConcluido?.invoke()
            return
        }

        val (url, origem) = candidatos[index]
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val candidato = buildService(url)
                val health = candidato.verificarHealth()
                if (health.status == "ok") {
                    service = candidato
                    val nomeServico = health.service ?: "Agente Tutor"
                    val msg = "🟢 Conectado ao $nomeServico ($origem)!"
                    Toast.makeText(context, msg, Toast.LENGTH_LONG).show()
                    binding.tvModoBadge.text = "[ONLINE - LOCAL]"
                    binding.tvModoBadge.setTextColor(Color.parseColor("#2E7D32"))
                    onConcluido?.invoke()
                    return@launch
                } else {
                    testarListaCandidatos(candidatos, index + 1, onConcluido)
                }
            } catch (_: Exception) {
                testarListaCandidatos(candidatos, index + 1, onConcluido)
            }
        }
    }

    private fun configurarListeners() {
        // PASSO 1: Seleção de modo
        binding.btnModoOnline.setOnClickListener {
            viewModel.selecionarModo(isOnline = true)
            conectarAoServidorLocal()
        }

        binding.btnModoOffline.setOnClickListener {
            viewModel.selecionarModo(isOnline = false)
        }

        // PASSO 2: Seleção de assunto via Chips
        binding.chipGroupAssuntos.setOnCheckedStateChangeListener { group, checkedIds ->
            if (checkedIds.isNotEmpty()) {
                val chip = group.findViewById<Chip>(checkedIds.first())
                val assunto = chip?.text?.toString() ?: "Vetores"
                viewModel.selecionarAssunto(assunto)
            }
        }

        // PASSO 3: Navegação de cards (< 1/10 >)
        binding.btnSetaAnterior.setOnClickListener {
            viewModel.questaoAnterior()
        }

        binding.btnSetaProximo.setOnClickListener {
            viewModel.proximaQuestao()
        }

        // Seleção de opções da questão
        binding.radioGroupOpcoes.setOnCheckedChangeListener { _, checkedId ->
            val opcaoIndex = when (checkedId) {
                R.id.radioOpcaoA -> 0
                R.id.radioOpcaoB -> 1
                R.id.radioOpcaoC -> 2
                R.id.radioOpcaoD -> 3
                else -> -1
            }
            if (opcaoIndex != -1) {
                viewModel.responderQuestao(opcaoIndex)
            }
        }

        // Botão para resetar o fluxo e trocar assunto/modo
        binding.btnTrocarAssunto.setOnClickListener {
            viewModel.resetarFluxo()
        }

        // PASSO 4: Envio de dúvida para o agente online
        binding.btnEnviarDuvida.setOnClickListener {
            enviarDuvidaParaAssistente()
        }
    }

    private fun configurarObservadores() {
        // Observa seleção do modo (Online/Offline)
        viewModel.modoOnline.observe(viewLifecycleOwner) { isOnline ->
            if (isOnline == null) {
                // Passo 1: Exibe apenas a seleção de modo
                binding.cardSelecaoModo.visibility = View.VISIBLE
                binding.cardSelecaoAssunto.visibility = View.GONE
                binding.cardQuestao.visibility = View.GONE
                binding.cardChatAssistente.visibility = View.GONE
            } else {
                // Passo 2: Oculta seleção de modo e exibe seleção de assunto se assunto não selecionado
                binding.cardSelecaoModo.visibility = View.GONE
                if (viewModel.assuntoSelecionado.value == null) {
                    binding.cardSelecaoAssunto.visibility = View.VISIBLE
                    binding.cardQuestao.visibility = View.GONE
                    binding.cardChatAssistente.visibility = View.GONE
                }
            }
        }

        // Observa seleção de assunto
        viewModel.assuntoSelecionado.observe(viewLifecycleOwner) { assunto ->
            if (!assunto.isNullOrEmpty()) {
                binding.cardSelecaoAssunto.visibility = View.GONE
                binding.cardQuestao.visibility = View.VISIBLE

                val isOnline = viewModel.modoOnline.value == true
                binding.cardChatAssistente.visibility = if (isOnline) View.VISIBLE else View.GONE
            }
        }

        // Observa a lista de questões e índice atual
        viewModel.indiceAtual.observe(viewLifecycleOwner) { index ->
            val lista = viewModel.questoes.value ?: emptyList()
            if (lista.isNotEmpty() && index in lista.indices) {
                exibirQuestao(lista[index], index, lista.size)
            }
        }

        // Observa as respostas marcadas pelo usuário
        viewModel.respostasUsuario.observe(viewLifecycleOwner) { mapaRespostas ->
            val indexAtual = viewModel.indiceAtual.value ?: 0
            val lista = viewModel.questoes.value ?: emptyList()
            if (lista.isNotEmpty() && indexAtual in lista.indices) {
                val questaoAtual = lista[indexAtual]
                val respostaEscolhida = mapaRespostas[indexAtual]
                atualizarFeedbackResposta(questaoAtual, respostaEscolhida)
            }
        }

        // Observa resposta do assistente
        viewModel.respostaAssistente.observe(viewLifecycleOwner) { resposta ->
            if (!resposta.isNullOrEmpty()) {
                binding.tvRespostaAssistente.visibility = View.VISIBLE
                val textoLimpo = formatarMatematicaParaTexto(resposta)
                val htmlString = textoLimpo
                    .replace(Regex("\\*\\*(.*?)\\*\\*"), "<b>$1</b>")
                    .replace("\n", "<br/>")
                val textoFormatado = androidx.core.text.HtmlCompat.fromHtml(
                    "<b>🤖 Agente Tutor:</b><br/><br/>$htmlString",
                    androidx.core.text.HtmlCompat.FROM_HTML_MODE_COMPACT
                )
                binding.tvRespostaAssistente.text = textoFormatado
            } else {
                binding.tvRespostaAssistente.visibility = View.GONE
            }
        }

        // Observa estado de carregamento do assistente
        viewModel.isCarregandoAssistente.observe(viewLifecycleOwner) { carregando ->
            binding.btnEnviarDuvida.isEnabled = !carregando
            if (carregando) {
                binding.tvRespostaAssistente.visibility = View.VISIBLE
                binding.tvRespostaAssistente.text = "⏳ O Agente está analisando sua dúvida..."
            }
        }
    }

    private fun exibirQuestao(questao: Questao, index: Int, total: Int) {
        binding.tvAssuntoHeader.text = "Assunto: ${questao.assunto}"
        val isOnline = viewModel.modoOnline.value == true
        if (!isOnline) {
            binding.tvModoBadge.text = "[OFFLINE]"
            binding.tvModoBadge.setTextColor(Color.parseColor("#757575"))
        }

        binding.tvEnunciadoQuestao.text = "Questão ${index + 1}: ${questao.enunciado}"

        // Preenche as opções A, B, C, D
        if (questao.opcoes.size >= 4) {
            binding.radioOpcaoA.text = questao.opcoes[0]
            binding.radioOpcaoB.text = questao.opcoes[1]
            binding.radioOpcaoC.text = questao.opcoes[2]
            binding.radioOpcaoD.text = questao.opcoes[3]
        }

        // Atualiza o contador de navegação (< 1/10 >)
        binding.tvContadorNavegacao.text = "${index + 1}/$total"

        // Desabilita/Habilita setas nas extremidades
        binding.btnSetaAnterior.isEnabled = index > 0
        binding.btnSetaProximo.isEnabled = index < total - 1

        // Restaura a seleção do usuário se já respondeu
        val respostaAnterior = viewModel.respostasUsuario.value?.get(index)
        binding.radioGroupOpcoes.clearCheck()
        if (respostaAnterior != null) {
            when (respostaAnterior) {
                0 -> binding.radioOpcaoA.isChecked = true
                1 -> binding.radioOpcaoB.isChecked = true
                2 -> binding.radioOpcaoC.isChecked = true
                3 -> binding.radioOpcaoD.isChecked = true
            }
        }

        atualizarFeedbackResposta(questao, respostaAnterior)
    }

    private fun atualizarFeedbackResposta(questao: Questao, respostaEscolhida: Int?) {
        if (respostaEscolhida == null) {
            binding.tvFeedbackResposta.visibility = View.GONE
        } else {
            binding.tvFeedbackResposta.visibility = View.VISIBLE
            if (respostaEscolhida == questao.respostaCorreta) {
                binding.tvFeedbackResposta.text = "✅ Resposta Correta!\n${questao.explicacao}"
                binding.tvFeedbackResposta.setBackgroundColor(Color.parseColor("#E8F5E9"))
                binding.tvFeedbackResposta.setTextColor(Color.parseColor("#2E7D32"))
            } else {
                val letraCorreta = when (questao.respostaCorreta) {
                    0 -> "A"
                    1 -> "B"
                    2 -> "C"
                    else -> "D"
                }
                binding.tvFeedbackResposta.text = "❌ Resposta Incorreta (Correta: Opção $letraCorreta)\n${questao.explicacao}"
                binding.tvFeedbackResposta.setBackgroundColor(Color.parseColor("#FFEBEE"))
                binding.tvFeedbackResposta.setTextColor(Color.parseColor("#C62828"))
            }
        }
    }

    private fun enviarDuvidaParaAssistente() {
        val duvidaText = binding.etDuvidaUser.text?.toString()?.trim() ?: ""
        if (duvidaText.isEmpty()) return

        if (service == null) {
            Toast.makeText(context, "Conectando ao servidor local...", Toast.LENGTH_SHORT).show()
            conectarAoServidorLocal {
                if (service != null) {
                    enviarDuvidaParaAssistente()
                }
            }
            return
        }

        val lista = viewModel.questoes.value ?: emptyList()
        val index = viewModel.indiceAtual.value ?: 0
        val questaoAtual = if (lista.isNotEmpty() && index in lista.indices) lista[index] else null

        val promptContexto = if (questaoAtual != null) {
            "Sobre a questão '${questaoAtual.enunciado}': $duvidaText"
        } else {
            duvidaText
        }

        binding.etDuvidaUser.text?.clear()
        viewModel.setCarregandoAssistente(true)

        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val s = service!!
                val response = s.enviarPergunta(
                    ChatRequest(
                        message = promptContexto,
                        sessionId = viewModel.sessionId
                    )
                )
                viewModel.salvarSessionId(response.sessionId)
                viewModel.carregarRespostaAssistente(response.response)
            } catch (e: retrofit2.HttpException) {
                val errorBody = e.response()?.errorBody()?.string()
                val mensagem = if (errorBody?.contains("detail") == true) {
                    try {
                        org.json.JSONObject(errorBody).optString("detail", e.message())
                    } catch (_: Exception) {
                        e.message()
                    }
                } else {
                    e.message()
                }
                viewModel.carregarRespostaAssistente("⚠️ $mensagem")
            } catch (e: Exception) {
                viewModel.carregarRespostaAssistente("⚠️ Erro ao conectar com o assistente local: ${e.localizedMessage}")
            }
        }
    }

    private fun formatarMatematicaParaTexto(texto: String): String {
        var t = texto
        while (t.contains("\\sqrt{")) {
            t = t.replace(Regex("""\\sqrt\{([^}]+)\}"""), "√($1)")
        }
        while (t.contains("\\frac{")) {
            t = t.replace(Regex("""\\frac\{([^}]+)\}\{([^}]+)\}"""), "($1 / $2)")
        }
        return t
            .replace("\\sqrt", "√")
            .replace("\\cdot", "·")
            .replace("\\times", "×")
            .replace("\\theta", "θ")
            .replace("\\cos", "cos")
            .replace("\\sin", "sin")
            .replace("\\tan", "tan")
            .replace("$$", "")
            .replace("$", "")
            .trim()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        nsdHelper?.stopDiscovery()
        _binding = null
    }
}
