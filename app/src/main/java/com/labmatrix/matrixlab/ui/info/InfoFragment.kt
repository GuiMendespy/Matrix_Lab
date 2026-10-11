package com.labmatrix.matrixlab.ui.info

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.labmatrix.matrixlab.MainActivity
import com.labmatrix.matrixlab.R
import com.google.android.material.bottomnavigation.BottomNavigationView

class InfoFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Infla o layout XML do fragmento
        return inflater.inflate(R.layout.fragment_info, container, false)
    }
}
