package com.example.whatsappclone.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.whatsappclone.databinding.FragmentCallBinding

class CallFragment : Fragment() {

    private var callBinding : FragmentCallBinding? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        callBinding = FragmentCallBinding.inflate(
            inflater, container, false
        )
        return callBinding?.root
    }

}