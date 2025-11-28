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

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                val lightColors = lightColorScheme(
                    primary = Color(0xFF1565C0),
                    onPrimary = Color.White,
                    secondary = Color(0xFF64B5F6),
                    background = Color(0xFFFFFFFF), // Fundo branco principal
                    surface = Color(0xFFFFFFFF),
                    onBackground = Color.Black,
                    onSurface = Color.Black
                )

                MaterialTheme(colorScheme = lightColors) {
                    val vector by viewModel.vector.collectAsState()

                    // --- BOX PRINCIPAL (estrutura externa) ---
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 5.dp, vertical = 60.dp)
                    ) {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            // --- TÍTULO ---
                            Text(
                                text = "Simulador Linear 3D",
                                style = MaterialTheme.typography.titleLarge,
                                color = Color.Black,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.fillMaxWidth()
                            )

                            Spacer(Modifier.height(16.dp))

                            // --- PAINEL PRINCIPAL (fundo branco) ---
                            Surface(
                                modifier = Modifier.weight(1f),
                                shape = MaterialTheme.shapes.medium,
                                color = MaterialTheme.colorScheme.background
                            ) {
                                // --- BOX INTERNO DO SIMULADOR ---
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(16.dp),
                                    contentAlignment = Alignment.TopCenter
                                ) {
                                    // --- SURFACE INTERNA (painel do simulador) ---
                                    Surface(
                                        shape = RoundedCornerShape(16.dp),
                                        color = Color(0xFFF5F5F5), // fundo cinza claro
                                        tonalElevation = 4.dp,
                                        shadowElevation = 8.dp,
                                        border = BorderStroke(1.dp, Color(0xFFE0E0E0)),
                                        modifier = Modifier.fillMaxSize()
                                    ) {
                                        Column(
                                            modifier = Modifier
                                                .fillMaxSize()
                                                .padding(16.dp),
                                            horizontalAlignment = Alignment.CenterHorizontally
                                        ) {
                                            // --- ÁREA OPENGL ---
                                            AndroidView(
                                                factory = { ctx ->
                                                    SimpleGLSurfaceView(ctx).apply {
                                                        setZOrderOnTop(true) // necessário para transparência
                                                        setBackgroundColor(0f, 0f, 0f, 0f) // transparente inicialmente
                                                        setVector(vector.x, vector.y, vector.z)
                                                    }
                                                },
                                                update = { glView ->
                                                    glView.setVector(vector.x, vector.y, vector.z)
                                                    // Aqui você pode mudar a cor do simulador sem afetar o resto
                                                    glView.setBackgroundColor(1f, 1f, 1f, 1f) // fundo branco só dentro do simulador
                                                },
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .weight(1f)
                                            )


                                            Spacer(Modifier.height(16.dp))

                                            // --- TEXTO DOS VALORES ---
                                            Text(
                                                "X = %.2f | Y = %.2f | Z = %.2f".format(
                                                    vector.x,
                                                    vector.y,
                                                    vector.z
                                                ),
                                                color = MaterialTheme.colorScheme.onBackground
                                            )

                                            Spacer(Modifier.height(8.dp))

                                            // --- SLIDERS ---
                                            Text("Eixo X", color = MaterialTheme.colorScheme.onBackground)
                                            Slider(
                                                value = vector.x,
                                                onValueChange = { newX ->
                                                    viewModel.updateVector(newX, vector.y, vector.z)
                                                },
                                                valueRange = -5f..5f
                                            )

                                            Text("Eixo Y", color = MaterialTheme.colorScheme.onBackground)
                                            Slider(
                                                value = vector.y,
                                                onValueChange = { newY ->
                                                    viewModel.updateVector(vector.x, newY, vector.z)
                                                },
                                                valueRange = -5f..5f
                                            )

                                            Text("Eixo Z", color = MaterialTheme.colorScheme.onBackground)
                                            Slider(
                                                value = vector.z,
                                                onValueChange = { newZ ->
                                                    viewModel.updateVector(vector.x, vector.y, newZ)
                                                },
                                                valueRange = -5f..5f
                                            )

                                            Spacer(Modifier.height(16.dp))

                                            // --- BOTÕES ---
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                                            ) {
                                                Button(
                                                    onClick = { viewModel.updateVector(1f, 1f, 1f) },
                                                    modifier = Modifier.weight(1f)
                                                ) {
                                                    Text("Resetar")
                                                }

                                                Button(
                                                    onClick = {
                                                        viewModel.updateVector(
                                                            vector.x * 1.2f,
                                                            vector.y * 1.2f,
                                                            vector.z * 1.2f
                                                        )
                                                    },
                                                    modifier = Modifier.weight(1f)
                                                ) {
                                                    Text("Aplicar Escala")
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
    }
}
