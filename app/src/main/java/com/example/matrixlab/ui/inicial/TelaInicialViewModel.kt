package com.example.matrixlab.ui.inicial

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.matrixlab.data.UserRepository // Importação necessária

class TelaInicialViewModel(private val repository: UserRepository) : ViewModel() {

    // 1. DADO PERSISTENTE: LiveData para o contador de acessos
    private val _accessCount = MutableLiveData<Int>()
    val accessCount: LiveData<Int> = _accessCount

    // DADO SIMPLES: LiveData para a mensagem de boas-vindas
    private val _mensagemBoasVindas = MutableLiveData<String>().apply {
        value = "MatrixLab"
    }
    val mensagemBoasVindas: LiveData<String> = _mensagemBoasVindas

    init {
        // 2. Lógica ao iniciar: Carrega o dado salvo
        _accessCount.value = repository.loadAccessCount()

        // 3. Lógica de Negócio: Incrementa e salva o novo valor
        incrementAndSaveAccessCount()
    }

    // Lógica principal: Incrementa o contador e salva no disco
    private fun incrementAndSaveAccessCount() {
        val currentCount = _accessCount.value ?: 0
        val newCount = currentCount + 1

        _accessCount.value = newCount             // 1. Atualiza o LiveData (View reage)
        repository.saveAccessCount(newCount)    // 2. Salva permanentemente (Persistência)
    }

    // Funções de Ação (Delegadas pelo Fragment)
    fun onStartSimulatorClicked() { /* Lógica de pré-navegação aqui */ }
    fun onInfoClicked() { /* Lógica de pré-navegação aqui */ }
    fun onTutorialClicked() { /* Lógica de pré-navegação aqui */ }


    // 4. CLASSE FACTORY: Necessária para que o Fragment instancie o ViewModel com o 'repository'
    class Factory(private val repository: UserRepository) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(TelaInicialViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return TelaInicialViewModel(repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}