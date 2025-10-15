package com.example.matrixlab.data

import android.content.Context
import android.content.SharedPreferences

// Constantes para SharedPreferences
private const val PREFS_NAME = "matrixlab_user_prefs"
private const val KEY_CONTADOR = "contador_acessos"

/**
 * Repositório responsável por lidar com o salvamento e carregamento
 * de dados simples e persistentes do usuário.
 * * Neste caso, usa SharedPreferences.
 */
class UserRepository(context: Context) {

    // Objeto SharedPreferences para persistência de dados
    private val prefs: SharedPreferences =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    /**
     * Carrega o contador de acessos salvo, ou retorna 0 se for a primeira vez.
     */
    fun loadAccessCount(): Int {
        // Retorna o valor de 'KEY_CONTADOR'. Se não existir, retorna 0 (valor padrão).
        return prefs.getInt(KEY_CONTADOR, 0)
    }

    /**
     * Salva o novo valor do contador de acessos de forma assíncrona.
     */
    fun saveAccessCount(count: Int) {
        // Usa o editor para salvar e aplica a mudança (apply é assíncrono)
        prefs.edit().putInt(KEY_CONTADOR, count).apply()
    }
}