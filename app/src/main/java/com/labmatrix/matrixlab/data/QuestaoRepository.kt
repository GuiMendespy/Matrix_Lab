package com.labmatrix.matrixlab.data

object QuestaoRepository {

    val assuntosDisponiveis = listOf(
        "Vetores",
        "Matrizes",
        "Sistemas Lineares"
    )

    private val bancoQuestoes = listOf(
        // === MATRIZES ===
        Questao(
            id = 1,
            assunto = "Matrizes",
            enunciado = "Calcule o determinante da matriz:\n\nM = [  4  -2 ]\n    [  1   3 ]",
            opcoes = listOf(
                "A) det(M) = 10",
                "B) det(M) = 12",
                "C) det(M) = 14",
                "D) det(M) = 2"
            ),
            respostaCorreta = 2,
            explicacao = "O determinante de uma matriz 2x2 é a diagonal principal menos a secundária: (4 * 3) - (-2 * 1) = 12 + 2 = 14."
        ),
        Questao(
            id = 2,
            assunto = "Matrizes",
            enunciado = "Considere a matriz:\n\nA = [ 2  1 ]\n    [ 5  3 ]\n\nDetermine:\na) o determinante da matriz A\nb) a matriz inversa de A, caso exista",
            opcoes = listOf(
                "A) det(A) = 1 e A⁻¹ = [ 3  -1 / -5  2 ]",
                "B) det(A) = 2 e A⁻¹ não existe",
                "C) det(A) = 1 e A⁻¹ não existe",
                "D) det(A) = 11 e A⁻¹ = [ 3  -1 / -5  2 ]"
            ),
            respostaCorreta = 0,
            explicacao = "det(A) = (2 * 3) - (1 * 5) = 1. A matriz inversa existe e é A⁻¹ = [ 3  -1 / -5  2 ]."
        ),
        Questao(
            id = 3,
            assunto = "Matrizes",
            enunciado = "Dada a matriz A:\n\nA = [ 1  4  7 ]\n    [ 2  5  8 ]\n\nDetermine a matriz transposta de A (indicada por Aᵀ).",
            opcoes = listOf(
                "A) Aᵀ = [ 2  1 / 5  4 / 8  7 ]",
                "B) Aᵀ = [ 1  4 / 2  5 / 7  8 ]",
                "C) Aᵀ = [ 1  2 / 4  5 / 7  8 ]",
                "D) Aᵀ = [ 7  4  1 / 8  5  2 ]"
            ),
            respostaCorreta = 2,
            explicacao = "A matriz transposta troca linhas por colunas: Aᵀ = [ 1  2 / 4  5 / 7  8 ]."
        ),
        Questao(
            id = 4,
            assunto = "Matrizes",
            enunciado = "Seja I a matriz identidade de ordem 2x2. Determine a matriz resultante da operação 3I.",
            opcoes = listOf(
                "A) 3I = [ 3  3 / 3  3 ]",
                "B) 3I = [ 3  0 / 0  3 ]",
                "C) 3I = [ 1  0 / 0  1 ]",
                "D) 3I = [ 3  0 / 0  1 ]"
            ),
            respostaCorreta = 1,
            explicacao = "Multiplicando a matriz identidade por 3 temos: 3 * [[1,0],[0,1]] = [[3,0],[0,3]]."
        ),
        Questao(
            id = 5,
            assunto = "Matrizes",
            enunciado = "Considere as matrizes:\n\nA = [ 2  1 ]     B = [ 1 ]\n    [ 3  4 ]         [ 2 ]\n\nDetermine a matriz produto C = A x B.",
            opcoes = listOf(
                "A) C = [ 2 / 8 ]",
                "B) C = [ 4 / 7 ]",
                "C) C = [ 3 / 11 ]",
                "D) C = [ 4 / 11 ]"
            ),
            respostaCorreta = 3,
            explicacao = "C1 = (2*1) + (1*2) = 4; C2 = (3*1) + (4*2) = 11. Logo, C = [ 4 / 11 ]."
        ),
        Questao(
            id = 6,
            assunto = "Matrizes",
            enunciado = "Dadas as matrizes de ordem 2x2:\n\nA = [ 1  3 ]     B = [ 4  -1 ]\n    [ 2  5 ]         [ 0   2 ]\n\nDetermine a matriz resultante da soma A + B.",
            opcoes = listOf(
                "A) A + B = [ 5  4 / 2  3 ]",
                "B) A + B = [ 5  2 / 2  7 ]",
                "C) A + B = [ 4  2 / 2  10 ]",
                "D) A + B = [ 5  2 / 0  7 ]"
            ),
            respostaCorreta = 1,
            explicacao = "Somando elemento a elemento: [1+4, 3+(-1) / 2+0, 5+2] = [ 5  2 / 2  7 ]."
        ),

        // === VETORES ===
        Questao(
            id = 7,
            assunto = "Vetores",
            enunciado = "Considere os vetores u = (1, 0) e v = (1, 1). Determine o cosseno do ângulo θ formado entre esses dois vetores.",
            opcoes = listOf(
                "A) cos(θ) = 1",
                "B) cos(θ) = √2 / 2",
                "C) cos(θ) = 1 / 2",
                "D) cos(θ) = 0"
            ),
            respostaCorreta = 1,
            explicacao = "cos(θ) = (u • v) / (||u|| * ||v||) = 1 / (1 * √2) = √2 / 2."
        ),
        Questao(
            id = 8,
            assunto = "Vetores",
            enunciado = "Determine a área do paralelogramo definido pelos vetores tridimensionais u = (2, 0, 0) e v = (0, 3, 0).",
            opcoes = listOf(
                "A) Área = 5 u.a.",
                "B) Área = 0 u.a.",
                "C) Área = 6 u.a.",
                "D) Área = √13 u.a."
            ),
            respostaCorreta = 2,
            explicacao = "A área do paralelogramo é igual ao módulo do produto vetorial: ||u x v|| = ||(0, 0, 6)|| = 6 u.a."
        ),
        Questao(
            id = 9,
            assunto = "Vetores",
            enunciado = "Determine o volume do paralelepípedo cujas arestas são definidas pelos vetores u = (1, 1, 0), v = (0, 2, 0) e w = (0, 0, 4).",
            opcoes = listOf(
                "A) Volume = 8 u.v.",
                "B) Volume = 6 u.v.",
                "C) Volume = 4 u.v.",
                "D) Volume = 2 u.v."
            ),
            respostaCorreta = 0,
            explicacao = "O volume do paralelepípedo é igual ao módulo do determinante dos três vetores: Volume = |8| = 8 u.v."
        ),
        Questao(
            id = 10,
            assunto = "Vetores",
            enunciado = "Dados os vetores tridimensionais u = (1, -1, 2) e v = (3, 0, 1), determine o vetor resultante do produto vetorial u x v.",
            opcoes = listOf(
                "A) u x v = (-1, 5, 3)",
                "B) u x v = (-1, 7, 3)",
                "C) u x v = (1, -5, -3)",
                "D) u x v = (-1, -5, 3)"
            ),
            respostaCorreta = 0,
            explicacao = "Calculando o determinante i, j, k: i = -1, j = 5, k = 3 -> u x v = (-1, 5, 3)."
        ),
        Questao(
            id = 11,
            assunto = "Vetores",
            enunciado = "Calcule o produto misto [u, v, w] entre os vetores u = (2, 0, 0), v = (0, 1, 0) e w = (0, 0, 3).",
            opcoes = listOf(
                "A) [u, v, w] = 0",
                "B) [u, v, w] = 5",
                "C) [u, v, w] = 6",
                "D) [u, v, w] = 2"
            ),
            respostaCorreta = 2,
            explicacao = "O produto misto é o determinante da matriz diagonal: 2 * 1 * 3 = 6."
        ),
        Questao(
            id = 12,
            assunto = "Vetores",
            enunciado = "Determine o vetor projeção ortogonal do vetor u = (4, 3) sobre o vetor v = (2, 0).",
            opcoes = listOf(
                "A) proj_v(u) = (2, 0)",
                "B) proj_v(u) = (4, 0)",
                "C) proj_v(u) = (0, 3)",
                "D) proj_v(u) = (4, 3)"
            ),
            respostaCorreta = 1,
            explicacao = "proj_v(u) = [(u • v) / ||v||²] * v = [8 / 4] * (2, 0) = (4, 0)."
        ),
        Questao(
            id = 13,
            assunto = "Vetores",
            enunciado = "Dado o vetor u = (-3, 4), determine o módulo do vetor resultante da operação 2u.",
            opcoes = listOf(
                "A) |2u| = 5",
                "B) |2u| = 10",
                "C) |2u| = 20",
                "D) |2u| = √14"
            ),
            respostaCorreta = 1,
            explicacao = "2u = (-6, 8) => ||2u|| = sqrt((-6)² + 8²) = sqrt(100) = 10."
        ),
        Questao(
            id = 14,
            assunto = "Vetores",
            enunciado = "Determine o módulo (ou comprimento) do vetor bidimensional v = (3, 4).",
            opcoes = listOf(
                "A) |v| = 7",
                "B) |v| = 5",
                "C) |v| = √7",
                "D) |v| = 25"
            ),
            respostaCorreta = 1,
            explicacao = "||v|| = sqrt(3² + 4²) = sqrt(9 + 16) = sqrt(25) = 5."
        ),
        Questao(
            id = 15,
            assunto = "Vetores",
            enunciado = "Calcule o produto escalar entre os vetores u = (1, -3) e v = (4, 2).",
            opcoes = listOf(
                "A) u · v = 10",
                "B) u · v = -10",
                "C) u · v = 2",
                "D) u · v = -2"
            ),
            respostaCorreta = 3,
            explicacao = "u • v = (1 * 4) + (-3 * 2) = 4 - 6 = -2."
        ),
        Questao(
            id = 16,
            assunto = "Vetores",
            enunciado = "Determine o produto vetorial a x b entre os vetores da base canônica a = (1, 0, 0) e b = (0, 1, 0).",
            opcoes = listOf(
                "A) a x b = (1, 1, 0)",
                "B) a x b = (0, 0, 0)",
                "C) a x b = (0, 0, 1)",
                "D) a x b = (0, 1, 0)"
            ),
            respostaCorreta = 2,
            explicacao = "O produto vetorial dos vetores canônicos i e j resulta no vetor k = (0, 0, 1)."
        ),
        Questao(
            id = 17,
            assunto = "Vetores",
            enunciado = "Considere os vetores tridimensionais:\n\na = (1, 2, 3)\nb = (2, 1, 0)\n\nCalcule:\na) o produto vetorial a x b\nb) o módulo do vetor resultante",
            opcoes = listOf(
                "A) a x b = (-3, 6, -3) e |a x b| = 3√6",
                "B) a x b = (-3, 6, -3) e |a x b| = √54",
                "C) a x b = (3, -6, 3) e |a x b| = 3√6",
                "D) a x b = (-3, 6, -3) e |a x b| = 6√3"
            ),
            respostaCorreta = 0,
            explicacao = "a x b = (-3, 6, -3) e ||a x b|| = sqrt(9 + 36 + 9) = sqrt(54) = 3√6."
        ),
        Questao(
            id = 18,
            assunto = "Vetores",
            enunciado = "Considere os vetores:\n\nu = (2, 3)\nv = (4, -1)\n\nDetermine:\na) a soma u + v\nb) o produto escalar u · v",
            opcoes = listOf(
                "A) u + v = (6, 2) e u · v = 5",
                "B) u + v = (6, 2) e u · v = 11",
                "C) u + v = (6, 4) e u · v = 5",
                "D) u + v = (2, 4) e u · v = 8"
            ),
            respostaCorreta = 0,
            explicacao = "Soma: (2+4, 3-1) = (6, 2). Produto escalar: (2*4) + (3*-1) = 8 - 3 = 5."
        ),
        Questao(
            id = 19,
            assunto = "Vetores",
            enunciado = "Dados os vetores u = (5, 8) e v = (2, 3), determine o vetor resultante da subtração u - v.",
            opcoes = listOf(
                "A) u - v = (7, 11)",
                "B) u - v = (3, 11)",
                "C) u - v = (3, 5)",
                "D) u - v = (5, 5)"
            ),
            respostaCorreta = 2,
            explicacao = "Subtração por componentes: (5 - 2, 8 - 3) = (3, 5)."
        ),
        Questao(
            id = 20,
            assunto = "Vetores",
            enunciado = "Dado o vetor u = (0, 6), determine o seu respectivo vetor unitário.",
            opcoes = listOf(
                "A) u_unitario = (0, 6)",
                "B) u_unitario = (0, 1)",
                "C) u_unitario = (1, 1)",
                "D) u_unitario = (0, 0)"
            ),
            respostaCorreta = 1,
            explicacao = "||u|| = 6. O vetor unitário é (0, 6) / 6 = (0, 1)."
        ),

        // === SISTEMAS LINEARES ===
        Questao(
            id = 21,
            assunto = "Sistemas Lineares",
            enunciado = "Considere o sistema linear:\n\n    2x + y = 7\n     x - y = 1\n\nResolva o sistema utilizando o método da substituição ou escalonamento.",
            opcoes = listOf(
                "A) x = 8/3  e  y = 5/3",
                "B) x = 3    e  y = 1",
                "C) x = 2    e  y = 3",
                "D) x = 8/3  e  y = 1"
            ),
            respostaCorreta = 0,
            explicacao = "Somando as equações: 3x = 8 => x = 8/3. Substituindo: 8/3 - y = 1 => y = 5/3."
        )
    )

    fun obterQuestoesPorAssunto(assunto: String, quantidade: Int = 10): List<Questao> {
        val questoesDoAssunto = bancoQuestoes.filter { it.assunto.equals(assunto, ignoreCase = true) }
        if (questoesDoAssunto.isEmpty()) {
            return bancoQuestoes.take(quantidade)
        }
        if (questoesDoAssunto.size >= quantidade) {
            return questoesDoAssunto.take(quantidade)
        }
        val extras = bancoQuestoes.filter { !it.assunto.equals(assunto, ignoreCase = true) }
        return (questoesDoAssunto + extras).take(quantidade)
    }
}
