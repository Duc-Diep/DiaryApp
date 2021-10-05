package com.example.dictionary.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import com.example.dictionary.R
import com.example.dictionary.activities.WriteDictionaryActivity
import com.example.dictionary.adapters.CalendarAdapter
import com.example.dictionary.adapters.DictionaryAdapter
import com.example.dictionary.helpers.SQLHelper
import com.example.dictionary.objects.Day
import com.example.dictionary.utils.AppPreferences
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_month.view.*
import java.time.LocalDate
import java.time.YearMonth


class MonthFragment : Fragment() {
    lateinit var dayAdapter: CalendarAdapter
    var isPageCenter = false
    lateinit var selectedDate: LocalDate
    lateinit var daysInMonth: ArrayList<Day>
    private var valueFirstDayOfWeek: Int = 0
    lateinit var sqlHelper:SQLHelper
    lateinit var listEvent:List<Day>
    private val updateDictionary =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
            if (it.resultCode== AppCompatActivity.RESULT_OK){
                updateUI(selectedDate,valueFirstDayOfWeek,true)
            }
        }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppPreferences.init(context)
        sqlHelper = SQLHelper(requireContext())
        listEvent = sqlHelper.getAllEvent()
        arguments?.let {
            selectedDate = it.getSerializable("month") as LocalDate
            isPageCenter = it.getBoolean("isCenter")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var view = inflater.inflate(R.layout.fragment_month, container, false)
        valueFirstDayOfWeek = AppPreferences.firstDayOfWeek
        //set up when load app
        getDaysInMonth(selectedDate, valueFirstDayOfWeek)
        view.rcv_days.adapter = dayAdapter
        val dividerVer = DividerItemDecoration(
            context,
            RecyclerView.VERTICAL
        )
        view.rcv_days.addItemDecoration(dividerVer)

        return view
    }

    //add day to list
    private fun getDaysInMonth(localDate: LocalDate, value: Int) {
        daysInMonth = ArrayList()
        var monthValue = localDate.monthValue
        var year = localDate.year
        var yearMonth = YearMonth.from(localDate)
        var lengthOfMonth = yearMonth.lengthOfMonth()
        var firstOfMonth = localDate.withDayOfMonth(1)
        var dayOfWeek = firstOfMonth.dayOfWeek.value + 7
        var numberDayOfPreviousWeek = dayOfWeek
        var previousMonthLength = YearMonth.from(localDate.minusMonths(1)).lengthOfMonth()
        var indexDay = 1
        var countNumberDayOfPreviousWeek = 0
        for (i in 1..50) {
            if (i < dayOfWeek) {
                if (monthValue==1){
                    daysInMonth.add(
                        Day(
                            LocalDate.of(year-1,12,previousMonthLength - numberDayOfPreviousWeek + 2),
                            false,
                            false,
                            ""
                        )
                    )
                }else{
                    daysInMonth.add(
                        Day(
                            LocalDate.of(year,monthValue-1,previousMonthLength - numberDayOfPreviousWeek + 2),
                            false,
                            false,
                            ""
                        )
                    )
                }

                numberDayOfPreviousWeek--
                countNumberDayOfPreviousWeek++
            } else if (i >= dayOfWeek && i <= lengthOfMonth + dayOfWeek) {
                if (i == (lengthOfMonth + dayOfWeek)) {
                    indexDay = 1
                } else {
                    if (LocalDate.of(year,monthValue,indexDay).toString()==AppPreferences.checkedDay) {
                        daysInMonth.add(Day(LocalDate.of(year,monthValue,indexDay), true, true,getEventByDay(LocalDate.of(year,monthValue,indexDay).toString())))
                    } else {
                        daysInMonth.add(Day(LocalDate.of(year,monthValue,indexDay), false, true,getEventByDay(LocalDate.of(year,monthValue,indexDay).toString())))
                    }
                    indexDay++
                }

            } else {
                if (monthValue==12){
                    daysInMonth.add(Day(LocalDate.of(year+1,1,indexDay), false, false,""))
                }else{
                    daysInMonth.add(Day(LocalDate.of(year,monthValue+1,indexDay), false, false,""))
                }
                indexDay++
            }
        }

        //reset list day when fisrt day of week changed
        var count = value
        while (count > 0) {
            daysInMonth.removeAt(0)
            daysInMonth.add(Day(LocalDate.of(year,monthValue,indexDay), false, false,""))
            indexDay++
            count--
        }

        if (daysInMonth[6].date.dayOfMonth > 20) {
            for (i in 1..7) {
                daysInMonth.removeAt(0)
            }
        }
        if (daysInMonth.size == 49) {
            for (i in 1..7) {
                daysInMonth.removeAt(daysInMonth.size - 1)
            }
        }

        dayAdapter = CalendarAdapter(requireContext(), daysInMonth)
        dayAdapter.setOnItemClick {
            var request:SendRequest = context as SendRequest
            request.updateUI(selectedDate)
        }
        dayAdapter.setOnItemDoubleClick {
            var intent = Intent(activity,WriteDictionaryActivity::class.java)
            intent.putExtra("date",it.date)
            intent.putExtra("event",it.eventContent)
            updateDictionary.launch(intent)
        }

    }

    fun getEventByDay(date:String):String{
        var event = listEvent.find { it.date.toString()==date }
        return if (event != null) {
            event.eventContent
        }else{
            ""
        }
    }

    //update UI when scroll left or right
    fun updateUI(newMonth: LocalDate, value: Int, isCenter: Boolean) {
        isPageCenter = isCenter
        valueFirstDayOfWeek = value
        selectedDate = newMonth
        listEvent = sqlHelper.getAllEvent()
        getDaysInMonth(newMonth, valueFirstDayOfWeek)
        view?.rcv_days?.adapter = dayAdapter
    }

    interface SendRequest {
        fun updateUI(localDate: LocalDate)
    }


    companion object {
        @JvmStatic
        fun newInstance(month: LocalDate, isCenter: Boolean) =
            MonthFragment().apply {
                arguments = Bundle().apply {
                    putSerializable("month", month)
                    putBoolean("isCenter", isCenter)
                }
            }
    }

}
