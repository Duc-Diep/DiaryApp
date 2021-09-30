package com.example.dictionary.helpers

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.dictionary.objects.Day
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

const val DB_NAME = "Dictionary.db"
const val DB_TABLE = "Notes"
const val DB_VERSION = 1

class SQLHelper(context: Context) : SQLiteOpenHelper(context, DB_NAME, null, DB_VERSION) {
    lateinit var sqlIteDatabase:SQLiteDatabase
    lateinit var contentValue:ContentValues
    override fun onCreate(db: SQLiteDatabase) {
        val queryCreateTable =
            "CREATE TABLE $DB_TABLE(id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, date Text, event Text)"
        db.execSQL(queryCreateTable)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        if (newVersion!=oldVersion){
            db.execSQL("DROP TABLE IF EXISTS $DB_TABLE")
            onCreate(db)
        }
    }

    fun addOrUpdateEvent(day: Day) {
        sqlIteDatabase = writableDatabase
        contentValue = ContentValues()
        contentValue.put("date", day.date.toString())
        contentValue.put("event", day.eventContent)
        if (isExists(day.date.toString())){
            sqlIteDatabase.update(DB_TABLE, contentValue, "date=?", arrayOf(day.date.toString()))
        }
        else
        sqlIteDatabase.insert(DB_TABLE, null, contentValue)
    }



    fun isExists(date: String): Boolean {
        sqlIteDatabase = readableDatabase
        val cursor: Cursor = sqlIteDatabase.rawQuery(
            "SELECT * FROM $DB_TABLE where date = ?",
            arrayOf(date)
        )
        return cursor.count == 1
    }

    fun getAllEvent(): List<Day> {
        val list: MutableList<Day> = ArrayList()
        sqlIteDatabase = readableDatabase
        val cursor: Cursor = sqlIteDatabase.query(
            false,
            DB_TABLE,
            null,
            null,
            null,
            null,
            null,
            null,
            null
        )
        while (cursor.moveToNext()) {
            val date = cursor.getString(cursor.getColumnIndex("date"))
            val event = cursor.getString(cursor.getColumnIndex("event"))
            val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-d")
            list.add(Day(LocalDate.parse(date,formatter),false,false,event))
        }
        cursor.close()
        list.sortBy { it.date }
        return list
    }

    fun deleteAllEvent(){
        sqlIteDatabase = writableDatabase
        sqlIteDatabase.delete(DB_TABLE,null,null)
    }
}