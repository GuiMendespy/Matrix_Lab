package com.labmatrix.matrixlab.ui.reflow

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

// Enumeração para representar qual seção principal (Nível 1) está aberta.
enum class ExpandedSection {
    NONE, // Nenhuma seção aberta
    ALGEBRA_LINEAR,
    ALGEBRA_VETORIAL
}

// 🔑 NOVO ENUM: Para rastrear qual Estágio (Nível 2) está aberto.
enum class ExpandedSubSection {
    NONE,
    LINEAR_ESTAGIO_1,
    LINEAR_ESTAGIO_2,
    LINEAR_ESTAGIO_3,
    VETORIAL_ESTAGIO_1,
    VETORIAL_ESTAGIO_2,
    VETORIAL_ESTAGIO_3,
}

class EstudoViewModel : ViewModel() {

    // ESTADO NÍVEL 1 (Categoria Principal)
    private val _expandedSection = MutableLiveData<ExpandedSection>().apply {
        value = ExpandedSection.NONE
    }
    val expandedSection: LiveData<ExpandedSection> = _expandedSection

    // 🔑 NOVO ESTADO NÍVEL 2 (Estágio)
    private val _expandedSubSection = MutableLiveData<ExpandedSubSection>().apply {
        value = ExpandedSubSection.NONE
    }
    val expandedSubSection: LiveData<ExpandedSubSection> = _expandedSubSection


    // Lógica para alternar o estado de expansão do Nível 1
    fun toggleExpansion(section: ExpandedSection) {
        if (_expandedSection.value == section) {
            // Se a seção clicada já estiver aberta, feche-a
            _expandedSection.value = ExpandedSection.NONE
        } else {
            // Se abrir uma nova seção principal, feche qualquer subseção aberta
            _expandedSubSection.value = ExpandedSubSection.NONE // 🔑 Zera o Nível 2
            _expandedSection.value = section
        }
    }

    // 🔑 NOVA LÓGICA: Alternar o estado de expansão do Nível 2 (Estágios)
    fun toggleSubExpansion(subSection: ExpandedSubSection) {
        if (_expandedSubSection.value == subSection) {
            // Se o sub-estágio clicado já estiver aberto, feche-o
            _expandedSubSection.value = ExpandedSubSection.NONE
        } else {
            // Abra o sub-estágio clicado
            _expandedSubSection.value = subSection
        }
    }

    // AÇÃO NÍVEL 3: Função genérica para tratar o clique nos Tópicos
    fun onTopicClicked(topicName: String) {
        // Lógica de Navegação: Aqui você chamaria o Fragment de Visualização do Conteúdo,
        // passando 'topicName' para saber o que exibir.
        // Exemplo: Log.d("EstudoViewModel", "Carregar conteúdo: $topicName")
    }

    // (Mantendo a lógica de paginação, caso você use em outros lugares)
    private val _paginaAtual = MutableLiveData<Int>().apply { value = 1 }
    val paginaAtual: LiveData<Int> = _paginaAtual
    private val totalPaginas = 15

    fun avancarPagina() {
        _paginaAtual.value?.let { pagina ->
            if (pagina < totalPaginas) {
                _paginaAtual.value = pagina + 1
            }
        }
    }

    fun voltarPagina() {
        _paginaAtual.value?.let { pagina ->
            if (pagina > 1) {
                _paginaAtual.value = pagina - 1
            }
        }
    }
}