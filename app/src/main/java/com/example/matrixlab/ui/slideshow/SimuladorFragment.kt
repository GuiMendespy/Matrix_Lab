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

    /** -------------------------------------------------------------------
     *  Agora interpreta vários vetores:
     *  Ex: (1,2,3); (4,1,0); -2,3,5 → lista de Vec3
     *  ------------------------------------------------------------------- */
    private fun parseVectorList(text: String): List<Vec3> {
        if (text.isBlank()) return emptyList()

        val parts = text.split(";")        // separa por ponto e vírgula
            .map { it.trim() }
            .filter { it.isNotEmpty() }

        val list = mutableListOf<Vec3>()

        for (part in parts) {
            val regex = Regex("""\(([^)]+)\)""")
            val match = regex.find(part)

            val inside = match?.groupValues?.get(1) ?: part
            val nums = inside.split(",").mapNotNull { it.trim().toFloatOrNull() }

            if (nums.size == 3) {
                list.add(Vec3(nums[0], nums[1], nums[2]))
            }
        }

        return list
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
