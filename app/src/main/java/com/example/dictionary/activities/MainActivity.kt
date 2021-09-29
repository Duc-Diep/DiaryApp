package com.example.dictionary.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.viewpager.widget.ViewPager
import com.example.dictionary.R
import com.example.dictionary.adapters.DayOfWeekAdapter
import com.example.dictionary.adapters.ViewPagerAdapter
import com.example.dictionary.fragments.MonthFragment
import com.example.dictionary.objects.DaysOfWeek
import com.example.dictionary.utils.AppPreferences
import kotlinx.android.synthetic.main.activity_main.*
import java.time.LocalDate

private const val PAGE_CENTER = 1

class MainActivity : AppCompatActivity() {
    lateinit var localDate: LocalDate
    lateinit var fragList: ArrayList<MonthFragment>
    lateinit var pageAdapter: ViewPagerAdapter
    lateinit var listDayOfWeek: ArrayList<DaysOfWeek>
    var valueFirstDayOfWeek = 0
    var daysOfWeekAdapter: DayOfWeekAdapter? = null

    var focusPage = 1
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportActionBar?.hide()
        reloadData()
        localDate = LocalDate.now()
        tv_month.text = "Tháng ${localDate.month.value} - ${localDate.year}"
        //setup ViewPager
        setupViewPager()
        //setup day of week
        setUpDaysOfWeek(valueFirstDayOfWeek)
    }

    private fun setupViewPager() {
        fragList = ArrayList()

        fragList.apply {
            add(MonthFragment.newInstance(localDate.minusMonths(1),false))
            add(MonthFragment.newInstance(localDate,true))
            add(MonthFragment.newInstance(localDate.plusMonths(1),false))
        }
        pageAdapter = ViewPagerAdapter(supportFragmentManager, fragList)
        view_pager.adapter = pageAdapter
        view_pager.offscreenPageLimit=3
        view_pager.setCurrentItem(1, false)
        view_pager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {

            }

            override fun onPageSelected(position: Int) {
                focusPage = position
                if (focusPage < PAGE_CENTER){
                    tv_month.text = "Tháng ${localDate.minusMonths(1).month.value} - ${localDate.minusMonths(1).year}"
                }else if(focusPage> PAGE_CENTER){
                    tv_month.text = "Tháng ${localDate.plusMonths(1).month.value} - ${localDate.plusMonths(1).year}"
                }


            }

            override fun onPageScrollStateChanged(state: Int) {
                if (state == ViewPager.SCROLL_STATE_IDLE) {
                    if (focusPage < PAGE_CENTER) {
                        localDate = localDate.minusMonths(1)
                    } else if (focusPage > PAGE_CENTER) {
                        localDate = localDate.plusMonths(1)
                    }
                    pageAdapter.setCalendar(localDate, valueFirstDayOfWeek)
                    view_pager.setCurrentItem(1, false)
                }
            }

        })
    }


    private fun setUpDaysOfWeek(firstValue: Int) {
        listDayOfWeek = ArrayList()
        var index = firstValue
        for (i in 0..6) {
            if (index == 6) {
                addDays(index)
                index = 0
            } else {
                addDays(index)
                index++
            }

        }
        daysOfWeekAdapter = DayOfWeekAdapter(this, listDayOfWeek)
        daysOfWeekAdapter!!.setOnItemClick {
            setUpDaysOfWeek(it)
            AppPreferences.firstDayOfWeek = it
            valueFirstDayOfWeek = it
            pageAdapter.setCalendar(localDate, it)
        }
        rcv_days_of_week?.adapter = daysOfWeekAdapter

    }

    private fun addDays(value: Int) {
        when (value) {
            0 -> listDayOfWeek.add(DaysOfWeek("MON", 0))
            1 -> listDayOfWeek.add(DaysOfWeek("TUE", 1))
            2 -> listDayOfWeek.add(DaysOfWeek("WED", 2))
            3 -> listDayOfWeek.add(DaysOfWeek("THU", 3))
            4 -> listDayOfWeek.add(DaysOfWeek("FRI", 4))
            5 -> listDayOfWeek.add(DaysOfWeek("SAT", 5))
            6 -> listDayOfWeek.add(DaysOfWeek("SUN", 6))
        }
    }

    private fun reloadData(){
        AppPreferences.init(this)
        AppPreferences.firstDayOfWeek = 0
        AppPreferences.checkedDay = LocalDate.now().dayOfMonth
        AppPreferences.checkedMonth = LocalDate.now().monthValue
    }
}