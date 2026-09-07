package com.example.matrixlab.data

import java.util.UUID

sealed class GeometricObject {
    abstract val id: String
    abstract val name: String
    abstract val color: FloatArray
    abstract val isVisible: Boolean

    data class Vector(
        val vec: Vec3,
        override val id: String = UUID.randomUUID().toString(),
        override val name: String = "v",
        override val color: FloatArray = floatArrayOf(0f, 0f, 0f, 1f),
        override val isVisible: Boolean = true,
        val isDotted: Boolean = false
    ) : GeometricObject()

    data class Point(
        val pos: Vec3,
        override val id: String = UUID.randomUUID().toString(),
        override val name: String = "P",
        override val color: FloatArray = floatArrayOf(1f, 0f, 0f, 1f),
        override val isVisible: Boolean = true
    ) : GeometricObject()

    data class Line(
        val point: Vec3,
        val direction: Vec3,
        override val id: String = UUID.randomUUID().toString(),
        override val name: String = "r",
        override val color: FloatArray = floatArrayOf(0f, 0f, 0f, 1f),
        override val isVisible: Boolean = true
    ) : GeometricObject()

    data class Plane(
        val a: Float,
        val b: Float,
        val c: Float,
        val d: Float,
        override val id: String = UUID.randomUUID().toString(),
        override val name: String = "pi",
        override val color: FloatArray = floatArrayOf(0.2f, 0.6f, 1.0f, 0.4f),
        override val isVisible: Boolean = true
    ) : GeometricObject()

    data class Quadric(
        val type: QuadricType,
        val params: FloatArray, // coefficients for reduced form or general form
        override val id: String = UUID.randomUUID().toString(),
        override val name: String = "Q",
        override val color: FloatArray = floatArrayOf(0.8f, 0.4f, 0.1f, 0.6f),
        override val isVisible: Boolean = true
    ) : GeometricObject()

    enum class QuadricType {
        ELIPSOIDE,
        HIPERBOLOIDE_1,
        HIPERBOLOIDE_2,
        PARABOLOIDE_ELIP,
        PARABOLOIDE_HIPER,
        CONE,
        ESFERA,
        CILINDRO_ELIP,
        CILINDRO_HIPER,
        CILINDRO_PARAB
    }
}
