package com.example.matrixlab.ui.reflow

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.matrixlab.R
// Adicione os imports para ExpandedSection e ExpandedSubSection, se existirem na sua ViewModel
import com.example.matrixlab.ui.reflow.ExpandedSection
import com.example.matrixlab.ui.reflow.ExpandedSubSection

// Nota: A classe EstudoViewModel não está definida neste arquivo, assumindo que ela existe
// no seu projeto.
class EstudoFragment : Fragment() {

    // Assumindo que EstudoViewModel e as classes necessárias estão definidas.
    private val estudoViewModel: EstudoViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_estudo, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // --- 1. REFERÊNCIAS DE UI (NÍVEL 1: CATEGORIA) ---
        val btnAlgLinear = view.findViewById<Button>(R.id.btn_alg_linear_principal)
        val btnAlgVetorial = view.findViewById<Button>(R.id.btn_alg_vetorial_principal)
        val layoutAlgLinear = view.findViewById<LinearLayout>(R.id.layout_alg_linear_conteudo)
        val layoutAlgVetorial = view.findViewById<LinearLayout>(R.id.layout_alg_vetorial_conteudo)

        // --- 2. REFERÊNCIAS DE UI (NÍVEL 2: ESTÁGIOS - Vetorial + Linear) ---
        val btnEstagioVetorial1 = view.findViewById<Button>(R.id.btn_estagio_vetorial_1)
        val btnEstagioVetorial2 = view.findViewById<Button>(R.id.btn_estagio_vetorial_2)
        val btnEstagioVetorial3 = view.findViewById<Button>(R.id.btn_estagio_vetorial_3)

        val btnEstagioLinear1 = view.findViewById<Button>(R.id.btn_estagio_linear_1)
        val btnEstagioLinear2 = view.findViewById<Button>(R.id.btn_estagio_linear_2)
        val btnEstagioLinear3 = view.findViewById<Button>(R.id.btn_estagio_linear_3)

        // --- 3. REFERÊNCIAS DE UI (NÍVEL 3: LAYOUTS DE TÓPICOS - Linear) ---
        val layoutTopicosLinear1 = view.findViewById<LinearLayout>(R.id.layout_topicos_linear_1)
        val layoutTopicosLinear2 = view.findViewById<LinearLayout>(R.id.layout_topicos_linear_2)
        val layoutTopicosLinear3 = view.findViewById<LinearLayout>(R.id.layout_topicos_linear_3)

        val layoutTopicosVetorial1 = view.findViewById<LinearLayout>(R.id.layout_topicos_vetorial_1)
        val layoutTopicosVetorial2 = view.findViewById<LinearLayout>(R.id.layout_topicos_vetorial_2)
        val layoutTopicosVetorial3 = view.findViewById<LinearLayout>(R.id.layout_topicos_vetorial_3)


        // --- 4. REFERÊNCIAS DE UI (NÍVEL 3: BOTÕES DE TÓPICOS - Vetorial) ---
        val btnTopicoVetorial1_1 = view.findViewById<Button>(R.id.btn_topico_vetorial_1_1)
        val btnTopicoVetorial1_2 = view.findViewById<Button>(R.id.btn_topico_vetorial_1_2)
        val btnTopicoVetorial1_3 = view.findViewById<Button>(R.id.btn_topico_vetorial_1_3)
        val btnTopicoVetorial1_4 = view.findViewById<Button>(R.id.btn_topico_vetorial_1_4)
        val btnTopicoVetorial1_5 = view.findViewById<Button>(R.id.btn_topico_vetorial_1_5)
        val btnTopicoVetorial1_6 = view.findViewById<Button>(R.id.btn_topico_vetorial_1_6)

        val btnTopicoVetorial2_1 = view.findViewById<Button>(R.id.btn_topico_vetorial_2_1)
        val btnTopicoVetorial2_2 = view.findViewById<Button>(R.id.btn_topico_vetorial_2_2)
        val btnTopicoVetorial2_3 = view.findViewById<Button>(R.id.btn_topico_vetorial_2_3)
        val btnTopicoVetorial2_4 = view.findViewById<Button>(R.id.btn_topico_vetorial_2_4)

        val btnTopicoVetorial3_1 = view.findViewById<Button>(R.id.btn_topico_vetorial_3_1)
        val btnTopicoVetorial3_2 = view.findViewById<Button>(R.id.btn_topico_vetorial_3_2)
        val btnTopicoVetorial3_3 = view.findViewById<Button>(R.id.btn_topico_vetorial_3_3)
        val btnTopicoVetorial3_4 = view.findViewById<Button>(R.id.btn_topico_vetorial_3_4)
        val btnTopicoVetorial3_5 = view.findViewById<Button>(R.id.btn_topico_vetorial_3_5)


        // --- 5. REFERÊNCIAS DE UI (NÍVEL 3: BOTÕES DE TÓPICOS - Linear) ---
        val btnTopicoLinear1_1 = view.findViewById<Button>(R.id.btn_topico_linear_1_1)
        val btnTopicoLinear1_2 = view.findViewById<Button>(R.id.btn_topico_linear_1_2)
        val btnTopicoLinear1_3 = view.findViewById<Button>(R.id.btn_topico_linear_1_3)
        val btnTopicoLinear1_4 = view.findViewById<Button>(R.id.btn_topico_linear_1_4)

        val btnTopicoLinear2_1 = view.findViewById<Button>(R.id.btn_topico_linear_2_1)
        val btnTopicoLinear2_2 = view.findViewById<Button>(R.id.btn_topico_linear_2_2)
        val btnTopicoLinear2_3 = view.findViewById<Button>(R.id.btn_topico_linear_2_3)
        val btnTopicoLinear2_4 = view.findViewById<Button>(R.id.btn_topico_linear_2_4)
        val btnTopicoLinear2_5 = view.findViewById<Button>(R.id.btn_topico_linear_2_5)
        val btnTopicoLinear2_6 = view.findViewById<Button>(R.id.btn_topico_linear_2_6)

        val btnTopicoLinear3_1 = view.findViewById<Button>(R.id.btn_topico_linear_3_1)
        val btnTopicoLinear3_2 = view.findViewById<Button>(R.id.btn_topico_linear_3_2)
        val btnTopicoLinear3_3 = view.findViewById<Button>(R.id.btn_topico_linear_3_3)
        val btnTopicoLinear3_4 = view.findViewById<Button>(R.id.btn_topico_linear_3_4)

        // =======================================================
        // AÇÕES: CLIQUE NÍVEL 1 (Categoria Principal)
        // =======================================================
        btnAlgLinear.setOnClickListener {
            estudoViewModel.toggleExpansion(ExpandedSection.ALGEBRA_LINEAR)
        }
        btnAlgVetorial.setOnClickListener {
            estudoViewModel.toggleExpansion(ExpandedSection.ALGEBRA_VETORIAL)
        }

        // =======================================================
        // AÇÕES: CLIQUE NÍVEL 2 (Estágios)
        // =======================================================
        btnEstagioLinear1.setOnClickListener {
            estudoViewModel.toggleSubExpansion(ExpandedSubSection.LINEAR_ESTAGIO_1)
        }
        btnEstagioLinear2.setOnClickListener {
            estudoViewModel.toggleSubExpansion(ExpandedSubSection.LINEAR_ESTAGIO_2)
        }
        btnEstagioLinear3.setOnClickListener {
            estudoViewModel.toggleSubExpansion(ExpandedSubSection.LINEAR_ESTAGIO_3)
        }
        btnEstagioVetorial1.setOnClickListener {
            estudoViewModel.toggleSubExpansion(ExpandedSubSection.VETORIAL_ESTAGIO_1)
        }
        btnEstagioVetorial2.setOnClickListener {
            estudoViewModel.toggleSubExpansion(ExpandedSubSection.VETORIAL_ESTAGIO_2)
        }
        btnEstagioVetorial3.setOnClickListener {
            estudoViewModel.toggleSubExpansion(ExpandedSubSection.VETORIAL_ESTAGIO_3)
        }

        // =======================================================
        // AÇÕES: CLIQUE NÍVEL 3 (Tópicos - Agora usando os objetos Button)
        // =======================================================

        // --- ÁLGEBRA VETORIAL (AV) ---
        // AV - ESTÁGIO 1
        btnTopicoVetorial1_1.setOnClickListener { estudoViewModel.onTopicClicked("Definição de Vetor e Operações") }
        btnTopicoVetorial1_2.setOnClickListener { estudoViewModel.onTopicClicked("Dependência e Independência Linear") }
        btnTopicoVetorial1_3.setOnClickListener { estudoViewModel.onTopicClicked("Sistemas de Coordenadas no Espaço") }
        btnTopicoVetorial1_4.setOnClickListener { estudoViewModel.onTopicClicked("Norma, Ângulo e Produto Interno") }
        btnTopicoVetorial1_5.setOnClickListener { estudoViewModel.onTopicClicked("Produto Vetorial") }
        btnTopicoVetorial1_6.setOnClickListener { estudoViewModel.onTopicClicked("Produto Misto") }

        // AV - ESTÁGIO 2
        btnTopicoVetorial2_1.setOnClickListener { estudoViewModel.onTopicClicked("Retas") }
        btnTopicoVetorial2_2.setOnClickListener { estudoViewModel.onTopicClicked("Planos") }
        btnTopicoVetorial2_3.setOnClickListener { estudoViewModel.onTopicClicked("Posições Relativas, Interseções e Ângulos") }
        btnTopicoVetorial2_4.setOnClickListener { estudoViewModel.onTopicClicked("Distâncias") }

        // AV - ESTÁGIO 3
        btnTopicoVetorial3_1.setOnClickListener { estudoViewModel.onTopicClicked("Circunferências") }
        btnTopicoVetorial3_2.setOnClickListener { estudoViewModel.onTopicClicked("Elipses") }
        btnTopicoVetorial3_3.setOnClickListener { estudoViewModel.onTopicClicked("Hipérboles") }
        btnTopicoVetorial3_4.setOnClickListener { estudoViewModel.onTopicClicked("Parábolas") }
        btnTopicoVetorial3_5.setOnClickListener { estudoViewModel.onTopicClicked("Quádricas") }


        // --- ÁLGEBRA LINEAR (AL) ---
        // AL - ESTÁGIO 1
        btnTopicoLinear1_1.setOnClickListener { estudoViewModel.onTopicClicked("Espaços Vetoriais") }
        btnTopicoLinear1_2.setOnClickListener { estudoViewModel.onTopicClicked("Subespaços Vetoriais") }
        btnTopicoLinear1_3.setOnClickListener { estudoViewModel.onTopicClicked("Combinação Linear, Dependência e Indep.") }
        btnTopicoLinear1_4.setOnClickListener { estudoViewModel.onTopicClicked("Bases e Dimensão") }

        // AL - ESTÁGIO 2
        btnTopicoLinear2_1.setOnClickListener { estudoViewModel.onTopicClicked("Transformações Lineares") }
        btnTopicoLinear2_2.setOnClickListener { estudoViewModel.onTopicClicked("Núcleo e Imagem da TL") }
        btnTopicoLinear2_3.setOnClickListener { estudoViewModel.onTopicClicked("Transformação Inversa") }
        btnTopicoLinear2_4.setOnClickListener { estudoViewModel.onTopicClicked("Isomorfismos") }
        btnTopicoLinear2_5.setOnClickListener { estudoViewModel.onTopicClicked("Matriz da Transformação Linear") }
        btnTopicoLinear2_6.setOnClickListener { estudoViewModel.onTopicClicked("Matriz de Mudança de Base") }

        // AL - ESTÁGIO 3
        btnTopicoLinear3_1.setOnClickListener { estudoViewModel.onTopicClicked("Autovalor e Autovetor") }
        btnTopicoLinear3_2.setOnClickListener { estudoViewModel.onTopicClicked("Polinômio Característico") }
        btnTopicoLinear3_3.setOnClickListener { estudoViewModel.onTopicClicked("Diagonalização de Operadores") }
        btnTopicoLinear3_4.setOnClickListener { estudoViewModel.onTopicClicked("Polinômio Minimal") }

        // =======================================================
        // 🔑 OBSERVAÇÃO NÍVEL 1 (Categoria Principal)
        // =======================================================
        estudoViewModel.expandedSection.observe(viewLifecycleOwner) { section ->
            // Controla a visibilidade dos layouts de Estágios
            layoutAlgLinear.visibility =
                if (section == ExpandedSection.ALGEBRA_LINEAR) View.VISIBLE else View.GONE
            layoutAlgVetorial.visibility =
                if (section == ExpandedSection.ALGEBRA_VETORIAL) View.VISIBLE else View.GONE

            // Atualiza o texto/símbolo dos botões principais
            btnAlgLinear.text =
                if (section == ExpandedSection.ALGEBRA_LINEAR) "Álgebra Linear ➖" else "Álgebra Linear ➕"
            btnAlgVetorial.text =
                if (section == ExpandedSection.ALGEBRA_VETORIAL) "Álgebra Vetorial ➖" else "Álgebra Vetorial ➕"
        }

        // =======================================================
        // 🔑 OBSERVAÇÃO NÍVEL 2 (Estágios)
        // =======================================================
        estudoViewModel.expandedSubSection.observe(viewLifecycleOwner) { subSection ->

            // Lógica para 1º ESTÁGIO LINEAR
            layoutTopicosLinear1.visibility =
                if (subSection == ExpandedSubSection.LINEAR_ESTAGIO_1) View.VISIBLE else View.GONE
            btnEstagioLinear1.text =
                if (subSection == ExpandedSubSection.LINEAR_ESTAGIO_1) "1º MÓDULO ➖" else "1º MÓDULO ➕"

            // Lógica para 2º ESTÁGIO LINEAR
            val isLinear2Open = subSection == ExpandedSubSection.LINEAR_ESTAGIO_2
            layoutTopicosLinear2.visibility = if (isLinear2Open) View.VISIBLE else View.GONE
            btnEstagioLinear2.text = if (isLinear2Open) "2º MÓDULO ➖" else "2º MÓDULO ➕"

            // Lógica para 3º ESTÁGIO LINEAR
            val isLinear3Open = subSection == ExpandedSubSection.LINEAR_ESTAGIO_3
            layoutTopicosLinear3.visibility = if (isLinear3Open) View.VISIBLE else View.GONE
            btnEstagioLinear3.text = if (isLinear3Open) "3º MÓDULO ➖" else "3º MÓDULO ➕"

            // Lógica para 1º ESTÁGIO VETORIAL
            val isVetorial1Open = subSection == ExpandedSubSection.VETORIAL_ESTAGIO_1
            layoutTopicosVetorial1.visibility = if (isVetorial1Open) View.VISIBLE else View.GONE
            btnEstagioVetorial1.text = if (isVetorial1Open) "1º MÓDULO ➖" else "1º MÓDULO ➕"

            // Lógica para 2º ESTÁGIO VETORIAL
            val isVetorial2Open = subSection == ExpandedSubSection.VETORIAL_ESTAGIO_2
            layoutTopicosVetorial2.visibility = if (isVetorial2Open) View.VISIBLE else View.GONE
            btnEstagioVetorial2.text = if (isVetorial2Open) "2º MÓDULO ➖" else "2º MÓDULO ➕"

            // Lógica para 3º ESTÁGIO VETORIAL
            val isVetorial3Open = subSection == ExpandedSubSection.VETORIAL_ESTAGIO_3
            layoutTopicosVetorial3.visibility = if (isVetorial3Open) View.VISIBLE else View.GONE
            btnEstagioVetorial3.text = if (isVetorial3Open) "3º MÓDULO ➖" else "3º MÓDULO ➕"
        }
    }
}
