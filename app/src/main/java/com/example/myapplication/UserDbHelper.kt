package com.example.myapplication

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper


class UserDbHelper(context: Context, name : String, version: Int) : SQLiteOpenHelper(context,name,null, version) {
    private val createUser = "create table User(" +
            "id integer primary key autoincrement," +
            "username text," +
            "password text," +
            "avatar blob)"

    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL(createUser)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("drop table if exists User")
        onCreate(db)
    }

}
