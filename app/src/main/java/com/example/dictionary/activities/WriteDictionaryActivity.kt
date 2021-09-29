package com.example.dictionary.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.dictionary.R
import kotlinx.android.synthetic.main.activity_write_dictionary.*

class WriteDictionaryActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        setContentView(R.layout.activity_write_dictionary)
        var date = intent.getSerializableExtra("date")
        tv_date.text = date.toString()
        btn_back.setOnClickListener {
            finish()
        }
    }
}