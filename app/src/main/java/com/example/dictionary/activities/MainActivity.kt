package com.example.dictionary.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.ViewPager
import com.example.dictionary.R
import com.example.dictionary.adapters.DayOfWeekAdapter
import com.example.dictionary.adapters.DictionaryAdapter
import com.example.dictionary.adapters.ViewPagerAdapter
import com.example.dictionary.fragments.MonthFragment
import com.example.dictionary.helpers.SQLHelper
import com.example.dictionary.objects.Day
import com.example.dictionary.objects.DaysOfWeek
import com.example.dictionary.utils.AppPreferences
import kotlinx.android.synthetic.main.activity_main.*
import java.io.*
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.ArrayList


private const val PAGE_CENTER = 1
private val format: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

class MainActivity : AppCompatActivity() {
    lateinit var localDate: LocalDate
    lateinit var fragList: ArrayList<MonthFragment>
    lateinit var pageAdapter: ViewPagerAdapter
    lateinit var listDayOfWeek: ArrayList<DaysOfWeek>
    lateinit var sqlHelper: SQLHelper
    var valueFirstDayOfWeek = 0
    var daysOfWeekAdapter: DayOfWeekAdapter? = null
    private val updateDictionary =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == RESULT_OK) {
                pageAdapter.setCalendar(localDate,valueFirstDayOfWeek)
            }
        }

    var focusPage = 1
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportActionBar?.hide()
        initView()
        btn_list_dic.setOnClickListener {
            var intent = Intent(this@MainActivity, ListDictionaryActivity::class.java)
            updateDictionary.launch(intent)
        }
        btn_backup.setOnClickListener {
            backupData()
        }
        btn_restore.setOnClickListener {
            restoreEvent()
        }
    }

    private fun restoreEvent() {
        var list = getListLine()
        if(list.isEmpty()){
            Toast.makeText(this,"Chưa backup dữ liệu, không thể restore",Toast.LENGTH_SHORT).show()
            return
        }
        var listEvent = ArrayList<Day>()
        for (element in list){
            var listResult = getEventInfor(element)
            listEvent.add(Day(LocalDate.parse(listResult[0], format),false,false,listResult[1]))
        }
        sqlHelper.deleteAllEvent()
        sqlHelper.addRestoreEvent(listEvent)
        pageAdapter.setCalendar(localDate,valueFirstDayOfWeek)
        Toast.makeText(this,"Restore thành công",Toast.LENGTH_SHORT).show()
    }

    private fun initView() {
        sqlHelper = SQLHelper(this)
        reloadData()
        localDate = LocalDate.now()
        tv_month.text = "Tháng ${localDate.month.value} - ${localDate.year}"
        //setup ViewPager
        setupViewPager()
        //setup day of week
        setUpDaysOfWeek(valueFirstDayOfWeek)
    }

    private fun backupData() {
        try {
            var listEvent = sqlHelper.getAllEvent()
            if (listEvent.isEmpty()){
                Toast.makeText(this,"Chưa có dữ liệu để backup",Toast.LENGTH_SHORT).show()
                return
            }
            val file = File(filesDir, "backup.csv")
            val fileOutputStream = FileOutputStream(file)
            val outputStreamWriter = OutputStreamWriter(fileOutputStream, "UTF-8")
            val bw = BufferedWriter(outputStreamWriter)
            for (element in listEvent) {
                if (element.eventContent.contains(",") || element.eventContent.contains("\n")) {
                    bw.write("${element.date},\"${element.eventContent}\"")
                } else {
                    bw.write("${element.date},${element.eventContent}")
                }
                bw.newLine()
            }
            bw.close()
            Toast.makeText(this, "Lưu trữ thành công", Toast.LENGTH_SHORT).show()
        } catch (ex: Exception) {
            Toast.makeText(this, "Lưu trữ thất bại $ex", Toast.LENGTH_SHORT).show()
        }
    }

    private fun getEventInfor(line: String): List<String> {
        var result = ArrayList<String>()
        var stack = Stack<Char>()
        var str = StringBuilder()
        for (i in line.indices) {
            var ch = line[i]
            if (ch == '\"') {
                if (str.length > 0 && stack.size % 2 == 0) {
                    str.append(ch)
                }
            } else if (ch == ',' && stack.size % 2 == 0){
                result.add(str.toString())
                stack.clear()
                str = StringBuilder()
            }else if (ch == ',' && stack.size % 2 != 0){
                str.append(ch)
            }else{
                str.append(ch)
            }
        }
        result.add(str.toString())
        return result
    }

    fun getListLine():List<String>{
        var listLine = ArrayList<String>()
        try {
            val file = File(filesDir, "backup.csv")
            val fileInputStream = FileInputStream(file)
            val inputStreamReader = InputStreamReader(fileInputStream, "UTF-8")
            val bw = BufferedReader(inputStreamReader)

                var line: String? = bw.readLine()
                var strTemp = ""
                while (line != null) {
                    while (countQuotes(line.toString())%2==1){
                        strTemp += "$line\n"
                        line = bw.readLine()
                    }
                    if (strTemp!=""){
                        strTemp = strTemp.substring(0,strTemp.length-1)
                        listLine.add(strTemp)
                        strTemp=""
                    }else{
                        listLine.add(line.toString())
                    }
                    line = bw.readLine()
                }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return listLine
    }

    fun countQuotes(line: String):Int{
        var count = 0
        for (i in line.indices){
            var ch = line[i].toString()
            if (ch=="\""){
                count++
            }
        }
        return count
    }

    private fun setupViewPager() {
        fragList = ArrayList()

        fragList.apply {
            add(MonthFragment.newInstance(localDate.minusMonths(1), false))
            add(MonthFragment.newInstance(localDate, true))
            add(MonthFragment.newInstance(localDate.plusMonths(1), false))
        }
        pageAdapter = ViewPagerAdapter(supportFragmentManager, fragList)
        view_pager.adapter = pageAdapter
        view_pager.offscreenPageLimit = 3
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
                if (focusPage < PAGE_CENTER) {
                    tv_month.text =
                        "Tháng ${localDate.minusMonths(1).month.value} - ${localDate.minusMonths(1).year}"
                } else if (focusPage > PAGE_CENTER) {
                    tv_month.text =
                        "Tháng ${localDate.plusMonths(1).month.value} - ${localDate.plusMonths(1).year}"
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

    private fun reloadData() {
        AppPreferences.init(this)
        AppPreferences.firstDayOfWeek = 0
        AppPreferences.checkedDay = LocalDate.now().dayOfMonth
        AppPreferences.checkedMonth = LocalDate.now().monthValue
    }
}