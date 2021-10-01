package com.example.dictionary.activities

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import com.example.dictionary.R
import com.example.dictionary.adapters.DictionaryAdapter
import com.example.dictionary.helpers.SQLHelper
import com.example.dictionary.objects.Day
import kotlinx.android.synthetic.main.activity_list_dictionary.*
import java.util.ArrayList


class ListDictionaryActivity : AppCompatActivity() {
    lateinit var sqlHelper: SQLHelper
    lateinit var listDictionary: ArrayList<Day>
    lateinit var dictionaryAdapter: DictionaryAdapter
    private val updateDictionary =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == RESULT_OK) {
                listDictionary = sqlHelper.getAllEvent()
                dictionaryAdapter = DictionaryAdapter(this, listDictionary)
                setupRcv()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list_dictionary)
        supportActionBar?.hide()
        sqlHelper = SQLHelper(this)
        initView()
        btn_back.setOnClickListener {
            setResult(RESULT_OK)
            finish()
        }
        edt_search.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                var listTemp: ArrayList<Day> = ArrayList()
                for (element in listDictionary) {
                    if (element.eventContent.toLowerCase().contains(s.toString().toLowerCase())) {
                        listTemp.add(element)
                    }
                }
                dictionaryAdapter = DictionaryAdapter(this@ListDictionaryActivity, listTemp)
                setupRcv()
            }

            override fun afterTextChanged(s: Editable?) {

            }

        })

    }

    fun initView() {
        listDictionary = sqlHelper.getAllEvent()
        if (listDictionary.isNotEmpty()) {
            tv_no_event.visibility = View.GONE
        }
        dictionaryAdapter = DictionaryAdapter(this, listDictionary)
        setupRcv()
    }

    fun setupRcv() {
        dictionaryAdapter.setOnClickItem {
            var intent = Intent(this@ListDictionaryActivity, WriteDictionaryActivity::class.java)
            intent.putExtra("date", it.date)
            intent.putExtra("event", it.eventContent)
            updateDictionary.launch(intent)
        }
        dictionaryAdapter.setOnDeleteItem {
            AlertDialog.Builder(this).setTitle("Xác nhận xóa?")
                .setMessage("Bạn có chắc chắn muốn xóa sự kiện này không?")
                .setPositiveButton("Có",object : DialogInterface.OnClickListener{
                    override fun onClick(dialog: DialogInterface?, which: Int) {
                        sqlHelper.deleteEventByDate(it.date.toString())
                        listDictionary.remove(it)
                        dictionaryAdapter.notifyDataSetChanged()
                    }

                })
                .setNegativeButton("Không",null)
                .show()
        }
        rcv_dictionary.adapter = dictionaryAdapter
    }

    override fun onBackPressed() {
        setResult(RESULT_OK)
        super.onBackPressed()
    }
}