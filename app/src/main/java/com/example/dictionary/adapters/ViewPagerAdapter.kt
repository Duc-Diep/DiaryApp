package com.example.dictionary.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.example.dictionary.fragments.MonthFragment
import java.time.LocalDate

class ViewPagerAdapter(fragmentManager: FragmentManager, var fragList: ArrayList<MonthFragment>) :
    FragmentPagerAdapter(fragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {


    override fun getCount(): Int {
        return 3
    }


    override fun getItem(position: Int): Fragment {
        return fragList[position]
    }


    fun setCalendar(currentMonth: LocalDate,value:Int) {
        var prevMonth = currentMonth.minusMonths(1)

        var nextMonth = currentMonth.plusMonths(1)

        fragList[0].updateUI(prevMonth,value,false)
        fragList[1].updateUI(currentMonth,value,true)
        fragList[2].updateUI(nextMonth,value,false)
    }
}