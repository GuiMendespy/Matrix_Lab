package com.example.matrixlab.data

//define um vetor 3D imutável (3 dimensões sempre...)
data class Vec3(val x: Float, val y: Float, val z: Float) {
    //Operações basicas vetoriais

    //Soma vetorial
    operator fun plus(o: Vec3) = Vec3(x + o.x, y + o.y, z + o.z)
    //Subtração vetorial
    operator fun minus(o: Vec3) = Vec3(x - o.x, y - o.y, z - o.z)
    //Multiplicação por escalar
    operator fun times(s: Float) = Vec3(x * s, y * s, z * s)
    //Produto Escalar
    fun dot(o: Vec3) = x * o.x + y * o.y + z * o.z
    //Produto vetorial
    fun cross(o: Vec3) = Vec3(
        y * o.z - z * o.y,
        z * o.x - x * o.z,
        x * o.y - y * o.x
    )
}