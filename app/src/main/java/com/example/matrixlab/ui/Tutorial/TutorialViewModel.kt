package com.example.matrixlab.ui.tutorial // Ajuste o nome do pacote se necessário

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class TutorialViewModel : ViewModel() {

    // LiveData que guarda o número do passo atual
    private val _passoAtual = MutableLiveData<Int>()
    val passoAtual: LiveData<Int> = _passoAtual

    // Variável que sobrevive à rotação de tela
    private var totalPassos = 5

    init {
        _passoAtual.value = 1 // Inicializa o tutorial no primeiro passo
    }

    fun proximoPasso() {
        _passoAtual.value?.let { passo ->
            if (passo < totalPassos) {
                _passoAtual.value = passo + 1
            }
        }
    }

    fun passoAnterior() {
        _passoAtual.value?.let { passo ->
            if (passo > 1) {
                _passoAtual.value = passo - 1
            }
        }
    }
}