package com.example.matrixlab.ui.slideshow

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.matrixlab.render.SimpleGLSurfaceView
import com.example.matrixlab.data.Vec3

class SimulatorFragment : Fragment() {

    private val viewModel: SimuladorViewModel by viewModels()

    /**
     * Parser unificado para:
     *  - soma, soma 1+2, soma todos
     *  - escalar, escalar 1·2
     *  - vetorial, vetorial 1×2
     *
     * Exemplos:
     *    (1,2,3); (4,1,0); soma
     *    (1,2,3); (4,1,0); soma 1+2
     *    (1,2,3); (4,1,0); escalar
     *    (1,2,3); (4,1,0); escalar 1·2
     *    (1,2,3); (4,1,0); vetorial
     *    (1,2,3); (4,1,0); vetorial 1×2
     */
    private fun parseVectorList(text: String): List<Vec3> {

        if (text.isBlank()) return emptyList()

        val parts = text.split(";")
            .map { it.trim() }
            .filter { it.isNotEmpty() }

        val vectors = mutableListOf<Vec3>()
        var command: String? = null

        // --- (1) Coleta vetores e detecta comando (soma, escalar, vetorial)
        for (p in parts) {

            val lower = p.lowercase()

            // comandos
            if (
                lower.startsWith("soma") ||
                lower.startsWith("escalar") ||
                lower.startsWith("vetorial")
            ) {
                command = lower
                continue
            }

            // extrair parte entre parênteses
            val inside = Regex("""\(([^)]+)\)""").find(p)?.groupValues?.get(1) ?: p
            val nums = inside.split(",").mapNotNull { it.trim().toFloatOrNull() }

            if (nums.size == 3)
                vectors.add(Vec3(nums[0], nums[1], nums[2]))
        }

        // Se não tem comando, só retorna os vetores normais
        if (command == null)
            return vectors

        // --- (2) utilitário para extrair índices tipo "1+2", "1·2", "1×2"
        fun parseIndices(cmd: String): Pair<Int, Int>? {
            val nums = Regex("""\d+""").findAll(cmd).map { it.value.toInt() }.toList()
            if (nums.size >= 2) return (nums[0] - 1) to (nums[1] - 1)
            return null
        }

        // --- (3) Processamento dos comandos

        // ➤  OPERAÇÃO: SOMA
        if (command.startsWith("soma")) {

            var soma = Vec3(0f, 0f, 0f)

            when {
                // soma simples ou "soma todos"
                command == "soma" || command.contains("todos") -> {
                    for (v in vectors)
                        soma += v
                }

                // soma 1+2
                command.matches(Regex("""soma\s+\d+\+\d+.*""")) -> {
                    val (i, j) = parseIndices(command) ?: return vectors
                    if (i in vectors.indices && j in vectors.indices)
                        soma = vectors[i] + vectors[j]
                }
            }

            vectors.add(soma)
            return vectors
        }

        // ➤  OPERAÇÃO: PRODUTO ESCALAR
        if (command.startsWith("escalar")) {

            val (i, j) = parseIndices(command) ?: return vectors
            if (i in vectors.indices && j in vectors.indices) {
                val dot = vectors[i] dot vectors[j]
                vectors.add(Vec3(dot, 0f, 0f))   // Representação como vetor no eixo X
            }

            return vectors
        }

        // ➤  OPERAÇÃO: PRODUTO VETORIAL
        if (command.startsWith("vetorial")) {

            val (i, j) = parseIndices(command) ?: return vectors
            if (i in vectors.indices && j in vectors.indices) {
                val cross = vectors[i] cross vectors[j]
                vectors.add(cross)
            }

            return vectors
        }

        return vectors
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return ComposeView(requireContext()).apply {
            setContent {
                val lightColors = lightColorScheme(
                    primary = Color(0xFF1565C0),
                    onPrimary = Color.White,
                    secondary = Color(0xFF64B5F6),
                    background = Color(0xFFFFFFFF),
                    surface = Color(0xFFFFFFFF),
                    onBackground = Color.Black,
                    onSurface = Color.Black
                )

                MaterialTheme(colorScheme = lightColors) {
                    val vectors by viewModel.vectors.collectAsState()  // Agora lista

                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(top = 16.dp, start = 5.dp, end = 5.dp, bottom = 42.dp)
                    ) {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {

                            Text(
                                text = "Simulador Linear 3D",
                                style = MaterialTheme.typography.titleLarge,
                                color = Color.Black,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.fillMaxWidth()
                            )

                            Spacer(Modifier.height(4.dp))

                            Surface(
                                modifier = Modifier.weight(1f),
                                shape = MaterialTheme.shapes.medium,
                                color = MaterialTheme.colorScheme.background
                            ) {

                                Box(
                                    modifier = Modifier.fillMaxSize().padding(16.dp),
                                    contentAlignment = Alignment.TopCenter
                                ) {

                                    Surface(
                                        shape = RoundedCornerShape(16.dp),
                                        color = Color(0xFFF5F5F5),
                                        tonalElevation = 4.dp,
                                        shadowElevation = 8.dp,
                                        border = BorderStroke(1.dp, Color(0xFFE0E0E0)),
                                        modifier = Modifier.fillMaxSize()
                                    ) {

                                        var inputText by remember { mutableStateOf("") }

                                        Column(
                                            modifier = Modifier.fillMaxSize().padding(16.dp),
                                            horizontalAlignment = Alignment.CenterHorizontally
                                        ) {

                                            AndroidView(
                                                factory = { ctx ->
                                                    SimpleGLSurfaceView(ctx).apply {
                                                        setBackgroundColor(0f,0f,0f,0f)
                                                        setVectors(vectors)   // agora recebe a lista
                                                    }
                                                },
                                                update = { view ->
                                                    val parsedList = parseVectorList(inputText)

                                                    if (parsedList.isNotEmpty()) {
                                                        view.setVectors(parsedList)
                                                    } else {
                                                        view.setVectors(vectors)
                                                    }
                                                },
                                                modifier = Modifier.fillMaxWidth().weight(1f)
                                            )

                                            Spacer(Modifier.height(8.dp))

                                            OutlinedTextField(
                                                value = inputText,
                                                onValueChange = { inputText = it },
                                                label = { Text("Digite os vetores separados por ;") },
                                                placeholder = { Text("(1,2,3); (4,1,0); (-2,3,5)") },
                                                modifier = Modifier.fillMaxWidth(),
                                                singleLine = false,
                                                maxLines = 3
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
