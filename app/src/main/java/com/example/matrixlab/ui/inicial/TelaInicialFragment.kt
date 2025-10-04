package com.example.matrixlab.ui.home

import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.VideoView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.matrixlab.R
import com.example.matrixlab.MainActivity
import com.google.android.material.bottomnavigation.BottomNavigationView

class TelaInicialFragment : Fragment() {

    private lateinit var bgVideoView: VideoView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_tela__inicial, container, false)

        // Configura o VideoView
        bgVideoView = view.findViewById(R.id.bgVideoView)
        val videoPath = "android.resource://${requireActivity().packageName}/${R.raw.gif_matrizlab}"
        val uri = Uri.parse(videoPath)
        bgVideoView.setVideoURI(uri)

        bgVideoView.viewTreeObserver.addOnGlobalLayoutListener {
            val videoRatio = bgVideoView.width.toFloat() / bgVideoView.height.toFloat()
            val screenRatio = resources.displayMetrics.widthPixels.toFloat() / resources.displayMetrics.heightPixels.toFloat()
            val scaleX = videoRatio / screenRatio

            if (scaleX >= 1f) {
                bgVideoView.scaleX = scaleX
            } else {
                bgVideoView.scaleY = 1f / scaleX
            }
        }

        bgVideoView.setOnPreparedListener { mp ->
            mp.isLooping = true
        }

        bgVideoView.start()

        // Botões
        val btnStartSimulator = view.findViewById<Button>(R.id.btnStartSimulator)
        btnStartSimulator.setOnClickListener {
            findNavController().navigate(R.id.nav_Estudo)
        }
        val btnInfo = view.findViewById<Button>(R.id.btnInfo)
        btnInfo.setOnClickListener {
            findNavController().navigate(R.id.nav_info)
        }
        val btnTutorial = view.findViewById<Button>(R.id.btnTutorial)
        btnTutorial.setOnClickListener {
            findNavController().navigate(R.id.nav_tutorial)
        }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Esconde os elementos do layout principal
        (activity as? MainActivity)?.let { mainActivity ->
            mainActivity.supportActionBar?.hide()
            mainActivity.findViewById<View>(R.id.fab)?.visibility = View.GONE
            mainActivity.findViewById<BottomNavigationView>(R.id.bottom_nav_view)?.visibility = View.GONE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // Mostra os elementos novamente ao sair da tela
        (activity as? MainActivity)?.let { mainActivity ->
            mainActivity.supportActionBar?.show()
            mainActivity.findViewById<View>(R.id.fab)?.visibility = View.VISIBLE
            mainActivity.findViewById<BottomNavigationView>(R.id.bottom_nav_view)?.visibility = View.VISIBLE
        }
    }

    override fun onResume() {
        super.onResume()
        bgVideoView.start()
    }

    override fun onPause() {
        super.onPause()
        bgVideoView.pause()
    }
}