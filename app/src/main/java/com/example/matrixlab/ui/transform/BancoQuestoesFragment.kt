package com.example.matrixlab.ui.transform

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.matrixlab.databinding.FragmentBancoquestoesBinding
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class BancoQuestoesFragment : Fragment() {

    private var _binding: FragmentBancoquestoesBinding? = null
    private val binding get() = _binding!!

    // 1. Instanciar o serviço do Retrofit (O Agente)
    private val service: LangChainService by lazy {
        // Isso ajuda a debugar: mostra toda a conversa no Logcat
        val logging = okhttp3.logging.HttpLoggingInterceptor().apply {
            level = okhttp3.logging.HttpLoggingInterceptor.Level.BODY
        }

        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(logging) // <--- Adicione isso
            .connectTimeout(60, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .build()

        Retrofit.Builder()
            // TROCAMOS O IP AQUI:
            .baseUrl("http://192.168.1.80:8000/")
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(LangChainService::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBancoquestoesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 2. Configurar o clique do botão que já existe no seu XML
        binding.QButton.setOnClickListener {
            gerarQuestaoComAgente()
        }
    }

    private fun gerarQuestaoComAgente() {
        // Mostra um texto temporário enquanto a IA responde
        binding.QText.text = "O Agente está pensando..."

        // 3. Chamar a API usando Coroutine (lifecycleScope)
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                // Envia a pergunta para o seu servidor Python/LangChain
                val resposta = service.enviarPergunta(ChatRequest("Gere uma questão de prova sobre matrizes"))

                // 4. Atualiza a tela com a resposta da IA
                binding.QText.text = resposta.response
                Log.d("SUCESSO", "O Agente disse: ${resposta.response}")

            } catch (e: Exception) {
                // Caso o servidor esteja desligado ou haja erro de rede
                binding.QText.text = "Erro ao conectar com o Agente: ${e.message}"
                Log.e("ERRO", "Falha na conexão: ${e.message}")
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}