package com.example.matrixlab.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.matrixlab.R
import com.example.matrixlab.MainActivity
import com.google.android.material.bottomnavigation.BottomNavigationView

class TelaInicialFragment : Fragment() {

    // NENHUM VideoView é necessário

    // Função auxiliar para controlar a visibilidade dos elementos da MainActivity
    private fun setMainElementsVisibility(isVisible: Boolean) {
        val visibility = if (isVisible) View.VISIBLE else View.GONE
        val mainActivity = activity as? MainActivity

        // Controle da ActionBar (Esconde se não for visível, mostra se for)
        if (isVisible) {
            mainActivity?.supportActionBar?.show()
        } else {
            mainActivity?.supportActionBar?.hide()
        }

        // Controle dos outros elementos (FAB, BottomNavView)
        mainActivity?.let {
            // ID bottom_nav_view é assumido a partir do seu MainActivity.kt
            it.findViewById<BottomNavigationView>(R.id.bottom_nav_view)?.visibility = visibility
            it.findViewById<View>(R.id.fab)?.visibility = visibility
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_tela__inicial, container, false)

        // --- Configuração dos Botões (Chamada para reexibir antes de navegar) ---

        val btnStartSimulator = view.findViewById<Button>(R.id.btnStartSimulator)
        btnStartSimulator.setOnClickListener {
            // 🔑 REEXIBE TUDO antes da navegação para que o próximo Fragment veja a barra
            setMainElementsVisibility(true)
            findNavController().navigate(R.id.nav_Estudo)
        }

        val btnInfo = view.findViewById<Button>(R.id.btnInfo)
        btnInfo.setOnClickListener {
            setMainElementsVisibility(true)
            findNavController().navigate(R.id.nav_info)
        }

        val btnTutorial = view.findViewById<Button>(R.id.btnTutorial)
        btnTutorial.setOnClickListener {
            setMainElementsVisibility(true)
            findNavController().navigate(R.id.nav_tutorial)
        }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 🔑 OCULTA ELEMENTOS: Garante que a barra não apareça na tela inicial
        setMainElementsVisibility(false)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // IMPORTANTE: Removemos a lógica de mostrar daqui, pois ela é agora tratada NO CLIQUE DO BOTÃO.
        // Isso impede que a barra pisque ou falhe ao reaparecer.
    }
}