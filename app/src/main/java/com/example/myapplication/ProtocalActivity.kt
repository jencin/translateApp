package com.example.myapplication


import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import com.example.myapplication.databinding.ActivityProtocalBinding

class ProtocalActivity : BaseActivity() {
    private lateinit var binding : ActivityProtocalBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProtocalBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.protocol.text = get()

    }

    private fun get() : String{
        return "您好，欢迎使用纯真翻译APP，本软件由理塘纯真有限公司开发, 在您同意本服务协议后，一切解释权归本司所有。\n".repeat(200)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.tool_bar_login, menu)
        menu?.findItem(R.id.register)?.isVisible = false
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            android.R.id.home -> {
                finish()
                return true;
            }
        }

        return super.onOptionsItemSelected(item)
    }
}