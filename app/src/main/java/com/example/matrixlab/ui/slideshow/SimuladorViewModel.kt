package com.example.matrixlab.ui.slideshow

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import com.example.matrixlab.data.Vec3

class SimuladorViewModel : ViewModel() {

    // Lista inicial contendo apenas o vetor (1,1,1)
    private val _vectors = MutableStateFlow<List<Vec3>>(listOf(Vec3(1f, 1f, 1f)))
    val vectors: StateFlow<List<Vec3>> = _vectors

    /** Substitui a lista inteira */
    fun setVectors(list: List<Vec3>) {
        _vectors.value = list
    }

    /** Adiciona um vetor à lista */
    fun addVector(v: Vec3) {
        _vectors.value = _vectors.value + v
    }

    /** Atualiza um vetor existente pelo índice */
    fun updateVector(index: Int, newVec: Vec3) {
        if (index < 0 || index >= _vectors.value.size) return
        _vectors.value = _vectors.value.toMutableList().also {
            it[index] = newVec
        }
    }

    /** Remove um vetor */
    fun removeVector(index: Int) {
        if (index < 0 || index >= _vectors.value.size) return
        _vectors.value = _vectors.value.toMutableList().also {
            it.removeAt(index)
        }
    }
}
