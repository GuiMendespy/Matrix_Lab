package com.labmatrix.matrixlab.ui.transform

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.labmatrix.matrixlab.data.Questao
import com.labmatrix.matrixlab.data.QuestaoRepository

class BancoQuestoesViewModel : ViewModel() {

    // Modo de Estudo: true = Online (Com IA), false = Offline (Sem IA), null = Não Selecionado
    private val _modoOnline = MutableLiveData<Boolean?>(null)
    val modoOnline: LiveData<Boolean?> = _modoOnline

    // Assunto selecionado
    private val _assuntoSelecionado = MutableLiveData<String?>(null)
    val assuntoSelecionado: LiveData<String?> = _assuntoSelecionado

    // Lista de 10 questões do assunto
    private val _questoes = MutableLiveData<List<Questao>>(emptyList())
    val questoes: LiveData<List<Questao>> = _questoes

    // Índice da questão atualmente exibida (0..9)
    private val _indiceAtual = MutableLiveData<Int>(0)
    val indiceAtual: LiveData<Int> = _indiceAtual

    // Resposta selecionada pelo usuário para a questão atual
    private val _respostasUsuario = MutableLiveData<MutableMap<Int, Int>>(mutableMapOf())
    val respostasUsuario: LiveData<MutableMap<Int, Int>> = _respostasUsuario

    // Resposta gerada pelo assistente de IA
    private val _respostaAssistente = MutableLiveData<String?>(null)
    val respostaAssistente: LiveData<String?> = _respostaAssistente

    private val _isCarregandoAssistente = MutableLiveData<Boolean>(false)
    val isCarregandoAssistente: LiveData<Boolean> = _isCarregandoAssistente

    private var _sessionId: String? = null
    val sessionId: String? get() = _sessionId

    fun salvarSessionId(id: String?) {
        _sessionId = id
    }

    fun selecionarModo(isOnline: Boolean) {
        _modoOnline.value = isOnline
    }

    fun selecionarAssunto(assunto: String) {
        _assuntoSelecionado.value = assunto
        val lista = QuestaoRepository.obterQuestoesPorAssunto(assunto, 10)
        _questoes.value = lista
        _indiceAtual.value = 0
        _respostasUsuario.value = mutableMapOf()
        _respostaAssistente.value = null
    }

    fun proximaQuestao() {
        val total = _questoes.value?.size ?: 0
        val atual = _indiceAtual.value ?: 0
        if (atual < total - 1) {
            _indiceAtual.value = atual + 1
            _respostaAssistente.value = null
        }
    }

    fun questaoAnterior() {
        val atual = _indiceAtual.value ?: 0
        if (atual > 0) {
            _indiceAtual.value = atual - 1
            _respostaAssistente.value = null
        }
    }

    fun responderQuestao(opcaoIndex: Int) {
        val qIndex = _indiceAtual.value ?: return
        val mapaAtual = _respostasUsuario.value ?: mutableMapOf()
        mapaAtual[qIndex] = opcaoIndex
        _respostasUsuario.value = mapaAtual
    }

    fun carregarRespostaAssistente(resposta: String) {
        _respostaAssistente.value = resposta
        _isCarregandoAssistente.value = false
    }

    fun setCarregandoAssistente(carregando: Boolean) {
        _isCarregandoAssistente.value = carregando
    }

    fun resetarFluxo() {
        _modoOnline.value = null
        _assuntoSelecionado.value = null
        _questoes.value = emptyList()
        _indiceAtual.value = 0
        _respostasUsuario.value = mutableMapOf()
        _respostaAssistente.value = null
    }
}
