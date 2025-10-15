package com.example.matrixlab.ui.settings

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SettingsViewModel : ViewModel() {

    // LiveData para o estado do tema escuro
    private val _isDarkModeEnabled = MutableLiveData<Boolean>().apply {
        value = false // Valor padrão (seria carregado do SharedPreferences ou DataStore)
    }
    val isDarkModeEnabled: LiveData<Boolean> = _isDarkModeEnabled

    // LiveData para o tamanho da fonte (exemplo)
    private val _fontSize = MutableLiveData<Float>().apply {
        value = 16f
    }
    val fontSize: LiveData<Float> = _fontSize

    // Lógica para alternar o tema
    fun toggleDarkMode(isEnabled: Boolean) {
        _isDarkModeEnabled.value = isEnabled
        // Lógica de Negócio: SALVAR a preferência no SharedPreferences / DataStore
    }

    // Lógica para mudar o tamanho da fonte
    fun setFontSize(size: Float) {
        _fontSize.value = size
        // Lógica de Negócio: SALVAR a preferência
    }
}