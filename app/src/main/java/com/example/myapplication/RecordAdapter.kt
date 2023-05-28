package com.example.myapplication

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class RecordAdapter(val recordList: List<Record>) : RecyclerView.Adapter<RecordAdapter.ViewHolder>() {
    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view){
        val fromType : TextView = view.findViewById(R.id.source)
        val toType : TextView = view.findViewById(R.id.dest)
        val srcLang : TextView = view.findViewById(R.id.src_lang)
        val destLang : TextView = view.findViewById(R.id.dest_lang)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.record_item, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return recordList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val record = recordList[position]
        holder.fromType.text = getName(record.fromType)
        holder.toType.text = getName(record.toType)
        holder.srcLang.text = record.content
        holder.destLang.text = record.result
    }

    private fun getName(str: String) : String{
        return when(str){
            "zh" -> "中文"
            "en" -> "英语"
            "jp" -> "日语"
            "ru" -> "俄语"
            "kor" -> "韩语"
            else -> "xxx"
        }
    }
}