package com.example.matrixlab.data

import android.graphics.Color
import java.util.UUID

data class Vec3(
    val x: Float,
    val y: Float,
    val z: Float,
    val id: String = UUID.randomUUID().toString(),
    val name: String = "v",
    val color: FloatArray = floatArrayOf(0f, 0f, 0f, 1f), // RGBA padrão Preto
    val isVisible: Boolean = true,
    val isDotted: Boolean = false // Se true, renderiza como linha tracejada (ótimo para projeções)
) {
    // Operações básicas matemáticas mantidas
    operator fun plus(other: Vec3) = Vec3(this.x + other.x, this.y + other.y, this.z + other.z)
    operator fun minus(o: Vec3) = Vec3(x - o.x, y - o.y, z - o.z)
    operator fun times(s: Float) = Vec3(x * s, y * s, z * s)
    infix fun dot(o: Vec3): Float = x * o.x + y * o.y + z * o.z
    infix fun cross(o: Vec3): Vec3 = Vec3(
        y * o.z - z * o.y,
        z * o.x - x * o.z,
        x * o.y - y * o.x
    )

    // Magnitude/Norma do vetor
    fun length(): Float = kotlin.math.sqrt(x*x + y*y + z*z)
}