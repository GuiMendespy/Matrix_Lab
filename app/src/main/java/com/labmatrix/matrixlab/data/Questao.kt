package com.labmatrix.matrixlab.data

data class Questao(
    val id: Int,
    val assunto: String,
    val enunciado: String,
    val opcoes: List<String>,
    val respostaCorreta: Int, // Índice de 0 a 3 correspondente às opções A, B, C, D
    val explicacao: String = ""
)
