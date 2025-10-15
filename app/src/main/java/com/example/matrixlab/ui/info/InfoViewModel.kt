package com.example.matrixlab.ui.info

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class InfoViewModel : ViewModel() {

    // LiveData para o Título (os Fragments irão 'observar' este dado)
    // O LiveData é usado para que a UI reaja a mudanças no dado.
    private val _text = MutableLiveData<String>().apply {
        value = "MatrixLab - Informações do Projeto"
    }
    val text: LiveData<String> = _text

    // Exemplo: Método para carregar a versão do aplicativo ou nomes da equipe
    fun carregarDadosInfo() {
        // Lógica de Negócio:
        // Aqui você faria a busca de dados de um Repositório (API, Banco de Dados, etc.)
        // Quando os dados chegarem, você atualiza o LiveData:
        // _text.value = "Dados carregados com sucesso."
    }
}