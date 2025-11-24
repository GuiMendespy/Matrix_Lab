package com.example.matrixlab.ui.inicial

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class TelaInicialViewModel : ViewModel() {

    // DADO SIMPLES: LiveData para a mensagem de boas-vindas
    private val _mensagemBoasVindas = MutableLiveData<String>().apply {
        value = "MatrixLab"
    }
    val mensagemBoasVindas: LiveData<String> = _mensagemBoasVindas

    // Funções de Ação (Delegadas pelo Fragment)
    fun onStartSimulatorClicked() { /* Lógica de pré-navegação aqui */ }
    fun onInfoClicked() { /* Lógica de pré-navegação aqui */ }
    fun onTutorialClicked() { /* Lógica de pré-navegação aqui */ }
}