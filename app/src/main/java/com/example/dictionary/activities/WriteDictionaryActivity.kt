package com.example.dictionary.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.dictionary.R
import com.example.dictionary.helpers.SQLHelper
import com.example.dictionary.objects.Day
import kotlinx.android.synthetic.main.activity_write_dictionary.*
import java.time.LocalDate

class WriteDictionaryActivity : AppCompatActivity() {
    lateinit var date:LocalDate
    lateinit var eventContent:String
    lateinit var sqlHelper:SQLHelper
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        setContentView(R.layout.activity_write_dictionary)
        sqlHelper = SQLHelper(this)
        date = intent.getSerializableExtra("date") as LocalDate
        eventContent = intent.getStringExtra("event") as String
        tv_date.text = date.toString()
        edt_event.setText(eventContent)
        btn_back.setOnClickListener {
            setResult(RESULT_CANCELED)
            finish()
        }

        btn_save.setOnClickListener {
            if (edt_event.text.toString()!=""){
                sqlHelper.addOrUpdateEvent(Day(date,false,false,edt_event.text.toString()))
                Toast.makeText(this@WriteDictionaryActivity,"Lưu thành công",Toast.LENGTH_SHORT).show()
                setResult(RESULT_OK)
                finish()
            }else{
                Toast.makeText(this@WriteDictionaryActivity,"Chưa nhập thông tin",Toast.LENGTH_SHORT).show()
            }

        }

    }
}