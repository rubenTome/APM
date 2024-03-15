package com.example.panchagapp.ui.notifications_calendar

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.example.panchagapp.ui.profile.HistorialFragment
import com.example.panchagapp.ui.profile.ProfileFragment
import java.lang.IllegalArgumentException

class CalendarViewPagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm){


    override fun getCount(): Int {
        return 2
    }

    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> PartidosFragment()
            1 -> ClasificacionFragment()
            else -> throw IllegalArgumentException("Invalid position")
        }
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return when (position) {
            0 -> "Partidos"
            1 -> "Clasificación"
            else -> null
        }
    }

}