package com.example.matrixlab.ui.reflow

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.matrixlab.databinding.FragmentEstudoBinding

class EstudoFragment : Fragment() {

    private var _binding: FragmentEstudoBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val estudoViewModel =
            ViewModelProvider(this).get(EstudoViewModel::class.java)

        _binding = FragmentEstudoBinding.inflate(inflater, container, false)
        val root: View = binding.root
        val textView: TextView = binding.textEstudo
        estudoViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}