package com.example.myapplication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView.LayoutManager
import com.example.myapplication.databinding.ActivityHistoryBinding

class HistoryActivity : BaseActivity() {
    private lateinit var binding : ActivityHistoryBinding
    private val dbHelper = MyDatabaseHelper(this,"History.db",1)
    private val recordList = ArrayList<Record>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        supportActionBar?.let {
            // 设置返回键
            it.setDisplayHomeAsUpEnabled(true)
        }
        // 从数据库中读取数据
        initData()

        val adapter = RecordAdapter(recordList)
        val layoutmanager = LinearLayoutManager(this)
        binding.recordList.layoutManager = layoutmanager
        binding.recordList.adapter = adapter
    }

    private fun initData(){
        val db = dbHelper.readableDatabase
        val cursor = db.query("Record", null, null, null, null, null, null)
        if(cursor.moveToFirst()){
            do{
                val curId = cursor.getInt(cursor.getColumnIndexOrThrow("id"))
                val fromType = cursor.getString(cursor.getColumnIndexOrThrow("fromType"))
                val toType = cursor.getString(cursor.getColumnIndexOrThrow("toType"))
                val content = cursor.getString(cursor.getColumnIndexOrThrow("content"))
                val result = cursor.getString(cursor.getColumnIndexOrThrow("result"))
                recordList.add(Record(curId, fromType, toType, content, result))
            }while (cursor.moveToNext())
        }
        cursor.close()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            android.R.id.home -> {
                finish()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
}