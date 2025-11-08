package com.example.matrixlab.ui.slideshow

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

// estrutura simples para armazenar um vetor tridimensional
data class Vector3(val x: Float, val y: Float, val z: Float)

class SimuladorViewModel : ViewModel() {
    //vetor inicial é (1,1,1)
    private val _vector = MutableStateFlow(Vector3(1f, 1f, 1f))
    // stateflow é uma forma segura reativa para observar os dados na interface
    val vector: StateFlow<Vector3> = _vector

    //atualiza o valor do vetor, modificando a interface para se redesenhar
    fun updateVector(x: Float, y: Float, z: Float) {
        _vector.value = Vector3(x, y, z)
    }
}
