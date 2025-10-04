package com.example.matrixlab.ui.transform

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.matrixlab.R
import com.example.matrixlab.databinding.FragmentBancoquestoesBinding
import com.example.matrixlab.databinding.FragmentEstudoBinding
import com.example.matrixlab.databinding.ItemTransformBinding
import com.example.matrixlab.ui.reflow.EstudoViewModel
import com.example.matrixlab.ui.slideshow.SimuladorViewModel

/**
 * Fragment that demonstrates a responsive layout pattern where the format of the content
 * transforms depending on the size of the screen. Specifically this Fragment shows items in
 * the [RecyclerView] using LinearLayoutManager in a small screen
 * and shows items using GridLayoutManager in a large screen.
 */
class BancoQuestoesFragment : Fragment() {

    private var _binding: FragmentBancoquestoesBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val bancoQuestoesViewModel =
            ViewModelProvider(this).get(SimuladorViewModel::class.java)

        _binding = FragmentBancoquestoesBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.textBancoquestoes
        bancoQuestoesViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}