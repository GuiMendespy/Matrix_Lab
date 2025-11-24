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

class SimulatorFragment : Fragment() {
    private val viewModel: SimuladorViewModel by viewModels()

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
                    val vector by viewModel.vector.collectAsState()

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
                                            // Single AndroidView that holds GLSurface + Overlay internally
                                            AndroidView(
                                                factory = { ctx ->
                                                    SimpleGLSurfaceView(ctx).apply {
                                                        setBackgroundColor(
                                                            0f,
                                                            0f,
                                                            0f,
                                                            0f
                                                        ) // keep transparent
                                                        setVector(vector.x, vector.y, vector.z)
                                                    }
                                                },
                                                update = { view ->
                                                    val values = inputText.split(",")
                                                        .mapNotNull { it.trim().toFloatOrNull() }
                                                    if (values.size == 3) {
                                                        view.setVector(
                                                            values[0],
                                                            values[1],
                                                            values[2]
                                                        )
                                                    } else {
                                                        // keep model vector from viewModel if needed
                                                        view.setVector(vector.x, vector.y, vector.z)
                                                    }
                                                },
                                                modifier = Modifier.fillMaxWidth().weight(1f)
                                            )

                                            Spacer(Modifier.height(8.dp))

                                            OutlinedTextField(
                                                value = inputText,
                                                onValueChange = { inputText = it },
                                                label = { Text("Digite o vetor (x, y, z)") },
                                                placeholder = { Text("Ex: 1, 2, 3") },
                                                modifier = Modifier.fillMaxWidth(),
                                                singleLine = true
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
