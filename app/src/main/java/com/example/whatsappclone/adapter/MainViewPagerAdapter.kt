package com.example.whatsappclone.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.whatsappclone.ui.fragment.CallFragment
import com.example.whatsappclone.ui.fragment.ChatFragment
import com.example.whatsappclone.ui.fragment.StatusFragment

class MainViewPagerAdapter(fragmentActivity: FragmentActivity) :
    FragmentStateAdapter(fragmentActivity) {
    override fun getItemCount(): Int = 3

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> ChatFragment()
            1 -> StatusFragment()
            2 -> CallFragment()
            else -> ChatFragment()
        }
    }
}