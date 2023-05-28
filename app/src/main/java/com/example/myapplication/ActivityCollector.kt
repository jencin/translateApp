package com.example.myapplication

import android.app.Activity

object ActivityCollector {
    val activities = ArrayList<Activity>()

    fun add(activity: Activity){
        activities.add(activity)
    }

    fun remove(activity: Activity){
        activities.remove(activity)
    }

    fun finish(){
        for(activity in activities){
            if(!activity.isFinishing){
                activity.finish()
            }
        }
        activities.clear()
    }
}