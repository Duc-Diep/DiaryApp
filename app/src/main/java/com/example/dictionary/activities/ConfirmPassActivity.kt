package com.example.dictionary.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.example.dictionary.R
import com.example.dictionary.utils.AppPreferences
import kotlinx.android.synthetic.main.activity_confirm_pass.*

class ConfirmPassActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_confirm_pass)
        supportActionBar?.hide()
        AppPreferences.init(this)
        if (AppPreferences.passwordApp!=""){
            tv_info.text = getString(R.string.infor2)
            edt_confirm_password.visibility = View.GONE
        }
        btn_login.setOnClickListener {
            checkInfor()
        }

    }

    private fun checkInfor() {
        var pass = edt_password.text.toString()
        if (AppPreferences.passwordApp==""){
            var confirmpass = edt_confirm_password.text.toString()
            if (pass==""||confirmpass==""){
                Toast.makeText(this,"Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show()
                return
            }
            if (pass!=confirmpass){
                Toast.makeText(this,"Xác nhận mật khẩu chưa chính xác", Toast.LENGTH_SHORT).show()
                return
            }
            AppPreferences.passwordApp = pass
        }else{
            if (AppPreferences.passwordApp != pass){
                Toast.makeText(this,"Mật khẩu không chính xác", Toast.LENGTH_SHORT).show()
                return
            }
        }
        progress_bar.visibility = View.VISIBLE
        startActivity(Intent(this,MainActivity::class.java))
        finish()

    }
}