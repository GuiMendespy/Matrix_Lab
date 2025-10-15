package com.example.matrixlab.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.matrixlab.R
import com.example.matrixlab.MainActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.example.matrixlab.ui.inicial.TelaInicialViewModel
import com.example.matrixlab.data.UserRepository // Importação necessária

class TelaInicialFragment : Fragment() {

    // 🔑 1. Instanciação MVVM com Factory
    // O Repositório é criado usando o contexto do aplicativo
    private val userRepository: UserRepository by lazy {
        UserRepository(requireContext().applicationContext)
    }

    // Conecta o ViewModel, passando o Repositório via Factory
    private val telaInicialViewModel: TelaInicialViewModel by viewModels {
        TelaInicialViewModel.Factory(userRepository)
    }

    // Função auxiliar para controlar a visibilidade (mantida)
    private fun setMainElementsVisibility(isVisible: Boolean) {
        val visibility = if (isVisible) View.VISIBLE else View.GONE
        val mainActivity = activity as? MainActivity

        if (isVisible) {
            mainActivity?.supportActionBar?.show()
        } else {
            mainActivity?.supportActionBar?.hide()
        }

        mainActivity?.let {
            it.findViewById<BottomNavigationView>(R.id.bottom_nav_view)?.visibility = visibility
            it.findViewById<View>(R.id.fab)?.visibility = visibility
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_tela__inicial, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // --- 2. INSTANCIAÇÃO DOS ELEMENTOS DO XML ---
        val tituloApp = view.findViewById<TextView>(R.id.tituloApp)
        val btnStartSimulator = view.findViewById<Button>(R.id.btnStartSimulator)
        val btnInfo = view.findViewById<Button>(R.id.btnInfo)
        val btnTutorial = view.findViewById<Button>(R.id.btnTutorial)

        // 🔑 NOVO ELEMENTO: TextView para o contador
        // Você deve adicionar o ID 'textView_contador_acessos' no seu XML (próximo passo)
        val textViewContador = view.findViewById<TextView>(R.id.textView_contador_acessos)

        // --- 3. LINKS DO VIEWS MODEL -> FRAGMENT (Persistência) ---

        // Observa o contador e atualiza a UI
        telaInicialViewModel.accessCount.observe(viewLifecycleOwner) { count ->
            textViewContador.text = "Acessos do Usuário: $count"
        }

        // Observa o LiveData de texto (para o título)
        telaInicialViewModel.mensagemBoasVindas.observe(viewLifecycleOwner) { mensagem ->
            tituloApp.text = mensagem
        }

        // --- 4. LINKS DO FRAGMENT -> VIEW MODEL (Ações) ---

        btnStartSimulator.setOnClickListener {
            telaInicialViewModel.onStartSimulatorClicked()
            setMainElementsVisibility(true)
            findNavController().navigate(R.id.nav_Estudo)
        }

        btnInfo.setOnClickListener {
            telaInicialViewModel.onInfoClicked()
            setMainElementsVisibility(true)
            findNavController().navigate(R.id.nav_info)
        }

        btnTutorial.setOnClickListener {
            telaInicialViewModel.onTutorialClicked()
            setMainElementsVisibility(true)
            findNavController().navigate(R.id.nav_tutorial)
        }

        // 🔑 Lógica de UI para ocultar os elementos ao iniciar
        setMainElementsVisibility(false)
    }
}