package com.example.matrixlab.ui.slideshow

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.fragment.app.Fragment
import com.example.matrixlab.render.SimpleGLSurfaceView
import com.example.matrixlab.data.Vec3
import kotlin.random.Random

// Modelo de dados simples para segurar os resultados escalares na lista
data class EscalarResult(
    val id: String = java.util.UUID.randomUUID().toString(),
    val name: String,
    val value: Float
)

class SimulatorFragment : Fragment() {

    private val aleatorio = Random(System.currentTimeMillis())

    // Lista para o que vai pro motor 3D
    private var listaMestreVetores = mutableStateListOf<Vec3>()

    // NOVA LISTA: Para exibir os números no painel estilo GeoGebra
    private var listaMestreEscalares = mutableStateListOf<EscalarResult>()

    private fun gerarCorAleatoria(): FloatArray {
        return floatArrayOf(aleatorio.nextFloat(), aleatorio.nextFloat(), aleatorio.nextFloat(), 1.0f)
    }

    private fun processarComandoTexto(text: String) {
        if (text.isBlank()) return
        // Separamos as partes por ";" mas mantemos as letras maiúsculas/minúsculas originais
        // para preservar os nomes que o usuário digitar (ex: "VetorA" vs "vetora")
        val parts = text.split(";").map { it.trim() }.filter { it.isNotEmpty() }

        val novosVetoresDigitados = mutableListOf<Vec3>()
        var comando: String? = null

        for (p in parts) {
            val lower = p.lowercase()
            if (lower.startsWith("soma") || lower.startsWith("sub") ||
                lower.startsWith("escalar") || lower.startsWith("vetorial") ||
                lower.startsWith("misto") || lower.startsWith("proj") ||
                lower.startsWith("dot") || lower.startsWith("prodescalar")) {
                comando = lower
                continue
            }

            // Verifica se o usuário definiu um nome usando "=" (Ex: k1 = (1,2,3))
            var nomeDefinidoPeloUsuario: String? = null
            val expressaoVetor: String

            if (p.contains("=")) {
                val lados = p.split("=")
                nomeDefinidoPeloUsuario = lados[0].trim()
                expressaoVetor = lados[1].trim()
            } else {
                expressaoVetor = p
            }

            val inside = Regex("""\(([^)]+)\)""").find(expressaoVetor)?.groupValues?.get(1) ?: expressaoVetor
            val nums = inside.split(",").mapNotNull { it.trim().toFloatOrNull() }

            if (nums.size == 3) {
                // Se o usuário digitou um nome, usa ele. Se não, gera o padrão v1, v2...
                val nomeFinal = nomeDefinidoPeloUsuario ?: "v${listaMestreVetores.size + novosVetoresDigitados.size + 1}"

                novosVetoresDigitados.add(
                    Vec3(nums[0], nums[1], nums[2], name = nomeFinal, color = gerarCorAleatoria())
                )
            }
        }

        // Adiciona os vetores normais à lista
        listaMestreVetores.addAll(novosVetoresDigitados)
        if (comando == null) return

        // Captura todos os números de índices (ex: "soma 1+2" -> pega 1 e 2)
        val indices = Regex("""\d+""").findAll(comando).map { it.value.toInt() - 1 }.toList()

        try {
            when {
                // 1. SOMA (ex: soma 1+2)
                comando.startsWith("soma") -> {
                    if (indices.size >= 2) {
                        val u = listaMestreVetores[indices[0]]
                        val v = listaMestreVetores[indices[1]]
                        listaMestreVetores.add((u + v).copy(name = "${u.name}+${v.name}", color = gerarCorAleatoria()))
                    }
                }
                // 2. SUBTRAÇÃO (ex: sub 1-2)
                comando.startsWith("sub") -> {
                    if (indices.size >= 2) {
                        val u = listaMestreVetores[indices[0]]
                        val v = listaMestreVetores[indices[1]]
                        listaMestreVetores.add((u - v).copy(name = "${u.name}-${v.name}", color = gerarCorAleatoria()))
                    }
                }
                // 3. PRODUTO ESCALAR (ex: dot 1 2)
                comando.startsWith("dot") || comando.startsWith("prodescalar") -> {
                    if (indices.size >= 2) {
                        val u = listaMestreVetores[indices[0]]
                        val v = listaMestreVetores[indices[1]]
                        val res = u dot v
                        listaMestreEscalares.add(EscalarResult(name = "${u.name} · ${v.name}", value = res))
                    }
                }
                // 4. PRODUTO POR ESCALAR (ex: escalar 1 * 2.5)
                comando.startsWith("escalar") -> {
                    val k = Regex("""[-<=?]*\d+\.\d+|[-<=?]*\d+""").findAll(comando).map { it.value.toFloat() }.lastOrNull() ?: 2f
                    if (indices.isNotEmpty()) {
                        val u = listaMestreVetores[indices[0]]
                        listaMestreVetores.add((u * k).copy(name = "$k*${u.name}", color = gerarCorAleatoria()))
                    }
                }
                // 5. PRODUTO VETORIAL (ex: vetorial 1x2)
                comando.startsWith("vetorial") -> {
                    if (indices.size >= 2) {
                        val u = listaMestreVetores[indices[0]]
                        val v = listaMestreVetores[indices[1]]
                        listaMestreVetores.add((u cross v).copy(name = "${u.name}×${v.name}", color = gerarCorAleatoria()))
                    }
                }
                // 6. PRODUTO MISTO (ex: misto 1 2 3)
                comando.startsWith("misto") -> {
                    if (indices.size >= 3) {
                        val u = listaMestreVetores[indices[0]]
                        val v = listaMestreVetores[indices[1]]
                        val w = listaMestreVetores[indices[2]]
                        val res = u dot (v cross w)
                        listaMestreEscalares.add(EscalarResult(name = "[${u.name}, ${v.name}, ${w.name}]", value = res))
                    }
                }
                // 7. PROJEÇÃO VETORIAL (ex: proj 1 2)
                comando.startsWith("proj") -> {
                    if (indices.size >= 2) {
                        val u = listaMestreVetores[indices[0]]
                        val v = listaMestreVetores[indices[1]]
                        val vNorm2 = v dot v
                        if (vNorm2 > 0f) {
                            val proj = v * ((u dot v) / vNorm2)
                            listaMestreVetores.add(proj.copy(name = "proj_${v.name}(${u.name})", color = u.color, isDotted = true))
                        }
                    }
                }
            }
        } catch (e: Exception) {
            android.util.Log.e("Simulator", "Erro ao processar operação", e)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return ComposeView(requireContext()).apply {
            setContent {
                MaterialTheme {
                    var inputText by remember { mutableStateOf("") }

                    Column(modifier = Modifier.fillMaxSize().background(Color(0xFFECEFF1))) {

                        // ÁREA DO GRÁFICO (60% da Altura)
                        Box(modifier = Modifier.fillMaxWidth().weight(0.6f).padding(8.dp)) {
                            Card(
                                shape = RoundedCornerShape(16.dp),
                                elevation = CardDefaults.cardElevation(8.dp),
                                modifier = Modifier.fillMaxSize()
                            ) {
                                AndroidView(
                                    factory = { ctx ->
                                        SimpleGLSurfaceView(ctx).apply {
                                            setBackgroundColor(1f, 1f, 1f, 1f)
                                            setVectors(listaMestreVetores)
                                        }
                                    },
                                    update = { view ->
                                        view.setVectors(listaMestreVetores)
                                    },
                                    modifier = Modifier.fillMaxSize()
                                )
                            }
                        }

                        // PAINEL DE CONTROLE (40% da Altura)
                        Card(
                            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            modifier = Modifier.fillMaxWidth().weight(0.4f)
                        ) {
                            Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {

                                // Caixa de Entrada
                                Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                                    OutlinedTextField(
                                        value = inputText,
                                        onValueChange = { inputText = it },
                                        label = { Text("Comando ou Vetor") },
                                        placeholder = { Text("Ex: (1,2,3); escalar 1·2") },
                                        modifier = Modifier.weight(1f),
                                        maxLines = 2
                                    )
                                    Spacer(Modifier.width(8.dp))
                                    Button(onClick = {
                                        processarComandoTexto(inputText)
                                        inputText = ""
                                    }) {
                                        Text("Inserir")
                                    }
                                }

                                Spacer(Modifier.height(8.dp))

                                // LISTA ESTILO GEOGEBRA (Renderiza vetores E escalares)
                                LazyColumn(modifier = Modifier.fillMaxSize()) {

                                    // SESSÃO 1: Os Vetores Geométricos
                                    items(listaMestreVetores) { vetor ->
                                        Row(
                                            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .size(16.dp)
                                                    .background(
                                                        color = Color(vetor.color[0], vetor.color[1], vetor.color[2], vetor.color[3]),
                                                        shape = CircleShape
                                                    )
                                            )
                                            Spacer(Modifier.width(12.dp))

                                            Text(
                                                text = "${vetor.name} = (${vetor.x}, ${vetor.y}, ${vetor.z})",
                                                modifier = Modifier.weight(1f),
                                                fontSize = 15.sp,
                                                color = if (vetor.isVisible) Color.Black else Color.Gray
                                            )

                                            IconButton(onClick = {
                                                val index = listaMestreVetores.indexOf(vetor)
                                                if (index != -1) {
                                                    listaMestreVetores[index] = vetor.copy(isVisible = !vetor.isVisible)
                                                }
                                            }) {
                                                Icon(
                                                    imageVector = if (vetor.isVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                                    contentDescription = "Ocultar",
                                                    tint = if (vetor.isVisible) MaterialTheme.colorScheme.primary else Color.Gray
                                                )
                                            }

                                            IconButton(onClick = { listaMestreVetores.remove(vetor) }) {
                                                Text("X", color = Color(0xFFD32F2F), fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 8.dp))
                                            }
                                        }
                                        HorizontalDivider(color = Color(0xFFF0F0F0))
                                    }

                                    // SESSÃO 2: Os Números/Escalares Puros (Produto Escalar e Misto)
                                    items(listaMestreEscalares) { escalar ->
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(vertical = 4.dp)
                                                .background(Color(0xFFF5F5F5), RoundedCornerShape(8.dp)) // Fundo cinza suave para diferenciar de um vetor
                                                .padding(horizontal = 8.dp, vertical = 6.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            // Texto estático indicando que é um valor Escalar (Número)
                                            Text(
                                                text = "k",
                                                color = Color(0xFF78909C),
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 14.sp,
                                                modifier = Modifier.padding(horizontal = 4.dp)
                                            )
                                            Spacer(Modifier.width(12.dp))

                                            // Nome da operação e o resultado numérico direto
                                            Text(
                                                text = "${escalar.name} = ${escalar.value}",
                                                modifier = Modifier.weight(1f),
                                                fontSize = 15.sp,
                                                fontWeight = FontWeight.Medium,
                                                color = Color(0xFF263238)
                                            )

                                            // Botão de deletar o escalar da lista
                                            IconButton(onClick = { listaMestreEscalares.remove(escalar) }) {
                                                Text("X", color = Color(0xFFD32F2F), fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 8.dp))
                                            }
                                        }
                                        HorizontalDivider(color = Color(0xFFF0F0F0))
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