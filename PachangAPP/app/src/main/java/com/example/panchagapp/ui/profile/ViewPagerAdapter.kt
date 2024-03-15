package com.example.panchagapp.ui.profile

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.example.panchagapp.ui.profile.HistorialFragment
import com.example.panchagapp.ui.profile.ProfileFragment
import java.lang.IllegalArgumentException

class ViewPagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm){


    override fun getCount(): Int {
        return 2
    }

    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> ProfileFragment()
            1 -> HistorialFragment()
            else -> throw IllegalArgumentException("Invalid position")
        }
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return when (position) {
            0 -> "Perfil"
            1 -> "Historial"
            else -> null
        }
    }

}