package com.example.matrixlab.ui.slideshow

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SimuladorViewModel : ViewModel() {

    // LiveData que guarda a matriz A inserida pelo usuário (o estado de input)
    // Usamos List<List<Double>> como um tipo simples para representar a matriz
    private val _matrizA = MutableLiveData<List<List<Double>>>()
    val matrizA: LiveData<List<List<Double>>> = _matrizA

    // LiveData que guarda o resultado do último cálculo realizado (o estado de output)
    private val _resultadoCalculo = MutableLiveData<String>()
    val resultadoCalculo: LiveData<String> = _resultadoCalculo

    // LiveData para feedback de erro para o usuário (ex: matriz não quadrada)
    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: MutableLiveData<String?> = _errorMessage

    // Método que o Fragment chama para atualizar a matriz de entrada
    fun setMatrizA(dadosMatriz: List<List<Double>>) {
        _matrizA.value = dadosMatriz
        _errorMessage.value = null // Limpa erros anteriores
    }

    // Lógica de Negócio: Calcula o determinante
    fun calcularDeterminante() {
        val matriz = _matrizA.value
        if (matriz == null || matriz.isEmpty() || matriz.size != matriz[0].size) {
            _errorMessage.value = "Erro: Determinante exige matriz quadrada."
            return
        }

        // 🔑 Lógica de Negócio: Chamar o algoritmo matemático real
        val determinante = realizarCalculoMatematico(matriz)

        _resultadoCalculo.value = "Determinante calculado: $determinante"
        _errorMessage.value = null
    }

    // Método privado onde a complexidade da Álgebra Linear reside
    private fun realizarCalculoMatematico(matriz: List<List<Double>>): Double {
        // Implemente aqui os algoritmos de cálculo de matrizes
        return 0.0 // Valor placeholder
    }
}