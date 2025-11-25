package com.example.matrixlab.logic

import com.example.matrixlab.data.Vec3

object VectorParser {

    data class ParsedOperation(
        val vectors: Map<String, Vec3>,
        val operation: String?,
        val result: Vec3?
    )

    private val vectorRegex =
        Regex("""([A-Z])\s*=\s*\(\s*([0-9.-]+)\s*,\s*([0-9.-]+)\s*,\s*([0-9.-]+)\s*\)""")

    private val operationRegex =
        Regex("""operação\s*=\s*([a-zA-Z_]+)""")

    fun parseLine(text: String): ParsedOperation? {

        // ----- VETORES -----
        val vectors = mutableMapOf<String, Vec3>()

        for (match in vectorRegex.findAll(text)) {
            val name = match.groupValues[1]
            val x = match.groupValues[2].toFloat()
            val y = match.groupValues[3].toFloat()
            val z = match.groupValues[4].toFloat()

            vectors[name] = Vec3(x, y, z)
        }

        if (vectors.isEmpty()) return null

        // ----- OPERAÇÃO -----
        val op = operationRegex.find(text)?.groupValues?.get(1)

        val result = when (op) {

            "soma" -> {
                if (vectors.size >= 2)
                    vectors.values.reduce { a, b ->
                        Vec3(a.x + b.x, a.y + b.y, a.z + b.z)
                    }
                else null
            }

            "vetorial" -> {
                if (vectors.size >= 2) {
                    val (a, b) = vectors.values.toList()
                    Vec3(
                        a.y * b.z - a.z * b.y,
                        a.z * b.x - a.x * b.z,
                        a.x * b.y - a.y * b.x
                    )
                } else null
            }

            "produto_misto" -> {
                if (vectors.size >= 3) {
                    val (a, b, c) = vectors.values.toList()

                    val cross = Vec3(
                        b.y * c.z - b.z * c.y,
                        b.z * c.x - b.x * c.z,
                        b.x * c.y - b.y * c.x
                    )

                    val mixed = a.x * cross.x + a.y * cross.y + a.z * cross.z
                    Vec3(mixed, 0f, 0f)
                } else null
            }

            else -> null
        }

        return ParsedOperation(
            vectors = vectors,
            operation = op,
            result = result
        )
    }
}
