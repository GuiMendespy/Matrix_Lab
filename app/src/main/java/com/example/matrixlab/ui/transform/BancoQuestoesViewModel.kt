package com.example.matrixlab.ui.transform // Ajuste o nome do pacote se necessário

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class BancoQuestoesViewModel : ViewModel() {

    // LiveData: A questão atual sendo exibida (pode ser uma String ou um objeto de dados)
    private val _questao = MutableLiveData<String>()
    val questao: LiveData<String> = _questao

    // LiveData: Se o usuário já respondeu a questão atual (para controlar o botão "Próximo")
    private val _respondida = MutableLiveData<Boolean>()
    val respondida: LiveData<Boolean> = _respondida

    init {
        carregarProximaQuestao()
    }

    fun carregarProximaQuestao() {
        // Lógica de Negócio: Buscar a próxima questão no Repositório/Banco de Dados
        _questao.value = "Qual é o vetor nulo no espaço R3?" // Exemplo da sua lógica de Álgebra Linear
        _respondida.value = false
    }

    fun verificarResposta(respostaUsuario: String) {
        // Lógica de Negócio: Comparar respostaUsuario com a resposta correta
        // Se correta: atualizar placar e mostrar feedback
        _respondida.value = true
    }
}