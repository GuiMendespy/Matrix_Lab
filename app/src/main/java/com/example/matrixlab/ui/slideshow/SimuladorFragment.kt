package com.example.matrixlab.ui.slideshow

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.fragment.app.Fragment
import com.example.matrixlab.render.SimpleGLSurfaceView
import com.example.matrixlab.data.Vec3
import com.example.matrixlab.data.GeometricObject
import kotlin.random.Random

data class EscalarResult(
    val id: String = java.util.UUID.randomUUID().toString(),
    val name: String,
    val value: Float
)

class SimulatorFragment : Fragment() {

    private val aleatorio = Random(System.currentTimeMillis())
    private var listaMestreObjetos = mutableStateListOf<GeometricObject>()
    private var listaMestreEscalares = mutableStateListOf<EscalarResult>()

    private fun gerarCorAleatoria(): FloatArray {
        return floatArrayOf(aleatorio.nextFloat(), aleatorio.nextFloat(), aleatorio.nextFloat(), 1.0f)
    }

    private fun processarComandoTexto(text: String) {
        if (text.isBlank()) return
        val parts = text.split(";").map { it.trim() }.filter { it.isNotEmpty() }

        for (p in parts) {
            val lower = p.lowercase()
            try {
                // --- 1. VERIFICAÇÃO DE QUÁDRICAS (PRIORIDADE) ---
                if (lower.startsWith("esfera") || lower.startsWith("elipsoide") || 
                    lower.startsWith("hiperboloide") || lower.startsWith("paraboloide") || lower.startsWith("cone")) {
                    
                    val params = Regex("""\(([^)]+)\)""").find(p)?.groupValues?.get(1)?.split(",")?.mapNotNull { it.trim().toFloatOrNull() }?.toFloatArray()
                    
                    when {
                        lower.startsWith("esfera") && params?.size == 1 -> {
                            val r = params[0]
                            listaMestreObjetos.add(GeometricObject.Quadric(GeometricObject.QuadricType.ESFERA, floatArrayOf(r, r, r), name = "Esfera"))
                        }
                        lower.startsWith("elipsoide") && params?.size == 3 -> {
                            listaMestreObjetos.add(GeometricObject.Quadric(GeometricObject.QuadricType.ELIPSOIDE, params, name = "Elipsoide"))
                        }
                        lower.startsWith("hiperboloide1") && params?.size == 3 -> {
                            listaMestreObjetos.add(GeometricObject.Quadric(GeometricObject.QuadricType.HIPERBOLOIDE_1, params, name = "Hiperboloide 1F"))
                        }
                        lower.startsWith("hiperboloide2") && params?.size == 3 -> {
                            listaMestreObjetos.add(GeometricObject.Quadric(GeometricObject.QuadricType.HIPERBOLOIDE_2, params, name = "Hiperboloide 2F"))
                        }
                        lower.startsWith("paraboloide") && params?.size == 2 -> {
                            listaMestreObjetos.add(GeometricObject.Quadric(GeometricObject.QuadricType.PARABOLOIDE_ELIP, params, name = "Paraboloide"))
                        }
                    }
                    continue
                }

                // --- 2. OPERAÇÕES VETORIAIS POR ÍNDICE ---
                val indices = Regex("""\d+""").findAll(p).map { it.value.toInt() - 1 }.toList()
                if (lower.startsWith("soma") || lower.startsWith("sub") || lower.startsWith("dot") || 
                    lower.startsWith("vetorial") || lower.startsWith("misto") || lower.startsWith("proj")) {
                    
                    try {
                        if (lower.startsWith("soma") && indices.size >= 2) {
                            val o1 = listaMestreObjetos[indices[0]] as? GeometricObject.Vector
                            val o2 = listaMestreObjetos[indices[1]] as? GeometricObject.Vector
                            if (o1 != null && o2 != null) listaMestreObjetos.add(GeometricObject.Vector(o1.vec + o2.vec, name = "Soma", color = gerarCorAleatoria()))
                        } else if (lower.startsWith("vetorial") && indices.size >= 2) {
                            val o1 = listaMestreObjetos[indices[0]] as? GeometricObject.Vector
                            val o2 = listaMestreObjetos[indices[1]] as? GeometricObject.Vector
                            if (o1 != null && o2 != null) listaMestreObjetos.add(GeometricObject.Vector(o1.vec cross o2.vec, name = "ProdVet", color = gerarCorAleatoria()))
                        }
                    } catch (e: Exception) {}
                    continue
                }

                // --- 3. CRIAÇÃO DE PONTOS, VETORES, RETAS E PLANOS ---
                val nome = if (p.contains("=")) p.split("=")[0].trim() else ""
                val coords = Regex("""\(([^)]+)\)""").find(p)?.groupValues?.get(1)?.split(",")?.mapNotNull { it.trim().toFloatOrNull() }

                // Ponto ou Vetor
                if (coords?.size == 3 && !lower.contains("t")) {
                    val vec = Vec3(coords[0], coords[1], coords[2])
                    if (nome.isNotEmpty() && nome[0].isUpperCase()) {
                        listaMestreObjetos.add(GeometricObject.Point(vec, name = nome))
                    } else {
                        listaMestreObjetos.add(GeometricObject.Vector(vec, name = if(nome.isEmpty()) "v${listaMestreObjetos.size+1}" else nome, color = gerarCorAleatoria()))
                    }
                    continue
                }

                // Reta
                if (lower.contains("+") && lower.contains("t")) {
                    val matches = Regex("""\(([^)]+)\)""").findAll(p).map { it.groupValues[1] }.toList()
                    if (matches.size >= 2) {
                        val p0 = matches[0].split(",").map { it.trim().toFloat() }
                        val v = matches[1].split(",").map { it.trim().toFloat() }
                        listaMestreObjetos.add(GeometricObject.Line(Vec3(p0[0], p0[1], p0[2]), Vec3(v[0], v[1], v[2]), name = if(nome.isEmpty()) "r" else nome))
                        continue
                    }
                }

                // Plano
                if (lower.contains("x") || lower.contains("y") || lower.contains("z")) {
                    val a = extractCoeff(lower, "x"); val b = extractCoeff(lower, "y"); val c = extractCoeff(lower, "z")
                    val dParts = lower.split("=")[0].replace(Regex("""[xyz]"""), " ").split(" ")
                    val d = dParts.mapNotNull { it.trim().toFloatOrNull() }.lastOrNull() ?: 0f
                    listaMestreObjetos.add(GeometricObject.Plane(a, b, c, d, name = if(p.contains(":")) p.split(":")[0].trim() else "pi"))
                    continue
                }

            } catch (e: Exception) { Log.e("Simulator", "Erro ao parsear: $p", e) }
        }
    }

    private fun extractCoeff(text: String, variable: String): Float {
        val pattern = Regex("""([-+]?\d*\.?\d*)""" + variable)
        val match = pattern.find(text) ?: return 0f
        val s = match.groupValues[1]
        return when {
            s == "" || s == "+" -> 1f; s == "-" -> -1f; else -> s.toFloat()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return ComposeView(requireContext()).apply {
            setContent {
                MaterialTheme {
                    var inputText by remember { mutableStateOf("") }
                    LaunchedEffect(Unit) { arguments?.getString("comando")?.let { if(it.isNotBlank()) processarComandoTexto(it) } }

                    Column(modifier = Modifier.fillMaxSize().background(Color(0xFFECEFF1))) {
                        Box(modifier = Modifier.fillMaxWidth().weight(0.6f).padding(8.dp)) {
                            Card(shape = RoundedCornerShape(16.dp), elevation = CardDefaults.cardElevation(8.dp), modifier = Modifier.fillMaxSize()) {
                                AndroidView(
                                    factory = { ctx -> SimpleGLSurfaceView(ctx).apply { setObjects(listaMestreObjetos) } },
                                    update = { view -> view.setObjects(listaMestreObjetos) },
                                    modifier = Modifier.fillMaxSize()
                                )
                            }
                        }

                        Card(shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp), colors = CardDefaults.cardColors(containerColor = Color.White), modifier = Modifier.fillMaxWidth().weight(0.4f)) {
                            Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                                Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                                    OutlinedTextField(value = inputText, onValueChange = { inputText = it }, label = { Text("Insira o Comando") }, placeholder = { Text("elipsoide(2,1,1); v=(1,1,1)") }, modifier = Modifier.weight(1f))
                                    Spacer(Modifier.width(8.dp))
                                    Button(onClick = { processarComandoTexto(inputText); inputText = "" }) { Text("Inserir") }
                                }
                                Spacer(Modifier.height(8.dp))
                                LazyColumn(modifier = Modifier.fillMaxSize()) {
                                    items(listaMestreObjetos) { obj ->
                                        ItemRow(obj, onToggle = {
                                            val idx = listaMestreObjetos.indexOf(obj)
                                            val updated = when(obj) {
                                                is GeometricObject.Vector -> obj.copy(isVisible = !obj.isVisible)
                                                is GeometricObject.Point -> obj.copy(isVisible = !obj.isVisible)
                                                is GeometricObject.Line -> obj.copy(isVisible = !obj.isVisible)
                                                is GeometricObject.Plane -> obj.copy(isVisible = !obj.isVisible)
                                                is GeometricObject.Quadric -> obj.copy(isVisible = !obj.isVisible)
                                            }
                                            listaMestreObjetos[idx] = updated
                                        }, onDelete = { listaMestreObjetos.remove(obj) })
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @Composable
    fun ItemRow(obj: GeometricObject, onToggle: () -> Unit, onDelete: () -> Unit) {
        val tipoPt = when(obj) {
            is GeometricObject.Vector -> "Vetor"; is GeometricObject.Point -> "Ponto"; is GeometricObject.Line -> "Reta"; is GeometricObject.Plane -> "Plano"; is GeometricObject.Quadric -> "Quádrica"
        }
        Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(16.dp).background(color = Color(obj.color[0], obj.color[1], obj.color[2], obj.color[3]), shape = CircleShape))
            Spacer(Modifier.width(12.dp))
            Text(text = "${obj.name} ($tipoPt)", modifier = Modifier.weight(1f), fontSize = 14.sp)
            IconButton(onClick = onToggle) { Icon(imageVector = if (obj.isVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff, contentDescription = null) }
            IconButton(onClick = onDelete) { Text("X", color = Color.Red, fontWeight = FontWeight.Bold) }
        }
    }
}
