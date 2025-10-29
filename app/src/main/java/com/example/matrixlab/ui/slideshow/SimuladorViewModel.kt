package com.example.matrixlab.ui.slideshow

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

data class Vector3(val x: Float, val y: Float, val z: Float)

class SimulatorViewModel : ViewModel() {
    private val _vector = MutableStateFlow(Vector3(1f, 1f, 1f))
    val vector: StateFlow<Vector3> = _vector

    fun updateVector(x: Float, y: Float, z: Float) {
        _vector.value = Vector3(x, y, z)
    }
}
