package com.example.whatsappclone.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.whatsappclone.databinding.FragmentAboutUsBinding

class AboutUsFragment : Fragment() {

    private var aboutUsBinding : FragmentAboutUsBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        aboutUsBinding = FragmentAboutUsBinding.inflate(
            inflater, container, false
        )
        return aboutUsBinding?.root
    }
}