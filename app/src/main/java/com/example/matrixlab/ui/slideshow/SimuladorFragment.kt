package com.example.matrixlab.ui.slideshow

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.matrixlab.databinding.FragmentSimuladorBinding

class SimuladorFragment : Fragment() {

    private var _binding: FragmentSimuladorBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val simuladorViewModel =
            ViewModelProvider(this).get(SimuladorViewModel::class.java)

        _binding = FragmentSimuladorBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.textSimulador
        simuladorViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }


        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}