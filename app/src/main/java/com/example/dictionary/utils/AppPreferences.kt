package com.example.dictionary.utils

import android.content.Context
import android.content.SharedPreferences
import java.time.LocalDate

object AppPreferences {
    private const val NAME = "AppPreferences"
    private const val MODE = Context.MODE_PRIVATE
    private lateinit var preferences: SharedPreferences

    private val FIRST_DAYS_OF_WEEK = Pair("first_day_of_week", 0)
    private val PASSWORD_APP = Pair("password_app","")
    private val CHECKED_DAY = Pair("checked_day", LocalDate.now().toString())

    fun init(context: Context?) {
        preferences = context?.getSharedPreferences(NAME, MODE)!!
    }
    private inline fun SharedPreferences.edit(operation: (SharedPreferences.Editor) -> Unit) {
        val editor = edit() //SharedPreferences.Editor editor
        operation(editor)
        editor.apply()
    }

    var firstDayOfWeek: Int
        get() = preferences.getInt(FIRST_DAYS_OF_WEEK.first, FIRST_DAYS_OF_WEEK.second)
        set(value) = preferences.edit {
            it.putInt(FIRST_DAYS_OF_WEEK.first, value)
        }
    var checkedDay: String
        get() = preferences.getString(CHECKED_DAY.first, CHECKED_DAY.second).toString()
        set(value) = preferences.edit {
            it.putString(CHECKED_DAY.first, value)
        }

    var passwordApp: String
        get() = preferences.getString(PASSWORD_APP.first, PASSWORD_APP.second).toString()
        set(value) = preferences.edit {
            it.putString(PASSWORD_APP.first, value)
        }
}