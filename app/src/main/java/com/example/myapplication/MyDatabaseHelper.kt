package com.example.myapplication

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper


class MyDatabaseHelper(val context: Context, name: String, version: Int) : SQLiteOpenHelper(context, name, null, version){
    private val createRecord = "create table Record(" +
            "id integer primary key autoincrement," +
            "fromType text," +
            "toType text," +
            "content text," +
            "result text)"

    override fun onCreate(db: SQLiteDatabase?) {
        // 存储搜索记录
        db?.execSQL(createRecord)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        // 更新数据库
        db?.execSQL("drop table if exists Text")
        onCreate(db)
    }
}