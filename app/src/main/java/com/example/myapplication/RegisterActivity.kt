package com.example.myapplication

import android.content.ContentValues
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.view.View.OnClickListener
import com.example.myapplication.databinding.ActivityRegisterBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class RegisterActivity : BaseActivity(), OnClickListener{
    private lateinit var binding : ActivityRegisterBinding
    private val dbHelper : UserDbHelper = UserDbHelper(this, "UserDb.db", 1)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.let {
            it.setDisplayHomeAsUpEnabled(true)
        }
        // 创建数据库
        val db = dbHelper.writableDatabase

        binding.registerButton.setOnClickListener(this)
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

    override fun onClick(v: View?) {
        when(v?.id){
            binding.registerButton.id -> {
                val username = binding.usernameEditText.text.toString()
                val password = binding.passwordEditText.text.toString()
                if(username != "" && password != ""){
                    val job = Job()
                    val scope = CoroutineScope(job)
                    scope.launch{
                        runOnUiThread{
                            binding.progressBar.visibility = View.VISIBLE
                        }
                        val db = dbHelper.writableDatabase
                        val values = ContentValues().apply {
                            put("username", username)
                            put("password", password)
                        }
                        db.insert("User", null, values)
                        delay(1000)
                        runOnUiThread{
                            binding.progressBar.visibility = View.INVISIBLE
                            finish()
                        }
                    }
                }
            }
        }
    }
}