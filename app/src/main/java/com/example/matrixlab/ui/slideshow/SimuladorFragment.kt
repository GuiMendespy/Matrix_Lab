package com.example.matrixlab.ui.slideshow

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.matrixlab.render.SimpleGLSurfaceView

class SimulatorFragment : Fragment() {
    private val viewModel: SimulatorViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = ComposeView(requireContext()).apply {

        setContent {
            // 🚫 Ignora o tema escuro do sistema:
            CompositionLocalProvider(
                LocalContentColor provides Color.Black,
                LocalTextStyle provides LocalTextStyle.current.copy(color = Color.Black)
            ) {
                // Força a flag de modo escuro para falso dentro deste Composable
                val darkTheme = false

                // Cria esquema de cores claras fixo
                val lightColors = lightColorScheme(
                    primary = Color(0xFF1565C0),
                    onPrimary = Color.White,
                    secondary = Color(0xFF64B5F6),
                    background = Color(0xFFFDFDFD),
                    surface = Color(0xFFFFFFFF),
                    onBackground = Color.Black,
                    onSurface = Color.Black
                )

                // Aplica manualmente o tema claro
                MaterialTheme(
                    colorScheme = if (darkTheme) darkColorScheme() else lightColors
                ) {
                    val vector by viewModel.vector.collectAsState()

                    Column(
                        Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                    ) {
                        Text(
                            "Simulador Vetorial 3D",
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Spacer(Modifier.height(8.dp))

                        AndroidView(
                            factory = { ctx ->
                                SimpleGLSurfaceView(ctx).apply {
                                    setVector(vector.x, vector.y, vector.z)
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f)
                        )

                        Spacer(Modifier.height(12.dp))
                        Text(
                            "X = %.2f, Y = %.2f, Z = %.2f".format(vector.x, vector.y, vector.z),
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Spacer(Modifier.height(8.dp))

                        Text("Eixo X", color = MaterialTheme.colorScheme.onBackground)
                        Slider(
                            value = vector.x,
                            onValueChange = {
                                viewModel.updateVector(it, vector.y, vector.z)
                            },
                            valueRange = -3f..3f
                        )

                        Text("Eixo Y", color = MaterialTheme.colorScheme.onBackground)
                        Slider(
                            value = vector.y,
                            onValueChange = {
                                viewModel.updateVector(vector.x, it, vector.z)
                            },
                            valueRange = -3f..3f
                        )

                        Text("Eixo Z", color = MaterialTheme.colorScheme.onBackground)
                        Slider(
                            value = vector.z,
                            onValueChange = {
                                viewModel.updateVector(vector.x, vector.y, it)
                            },
                            valueRange = -3f..3f
                        )
                    }
                }
            }
        }
    }
}
