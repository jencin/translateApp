package com.example.myapplication

import android.content.Intent
import android.graphics.Color
import android.graphics.ColorMatrix
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.View.OnClickListener
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.myapplication.databinding.ActivityMainBinding
import es.dmoral.toasty.Toasty
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class LoginActivity : BaseActivity(), View.OnClickListener{
    private lateinit var binding : ActivityMainBinding
    //private lateinit var sp: SharedPreferences
    private val context = this
    private lateinit var viewModel : LoginViewModel
    private var isInit : Boolean = false
    private var ischeck : Boolean = false
    private val dbHelper = UserDbHelper(this, "UserDb.db", 1)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        // 创建携程，获取用户名单
        val job = Job()
        val scope = CoroutineScope(job)
        scope.launch {
            val mylist = ServiceCreator.create<UserService>().getUserList().await()
            delay(1000)
            runOnUiThread{
                viewModel = ViewModelProvider(context, LoginViewModelFactory(mylist)).get(LoginViewModel::class.java)
                isInit = true
                binding.progressBar.visibility = View.INVISIBLE
            }
        }


        setSupportActionBar(binding.toolbar)
        // 渲染登录界面
        supportActionBar?.let{
            it.setDisplayHomeAsUpEnabled(true)
        }
        getProtocol()

        binding.loginButton.setOnClickListener(this)
        binding.checkUp.setOnClickListener(this)
        binding.visitor.setOnClickListener(this)


    }



    override fun onDestroy() {
        super.onDestroy()
        ActivityCollector.remove(this)
    }



    /**
     * 设置用户协议的点击事件
     */
    private fun getProtocol(){
        val textView = findViewById<TextView>(R.id.user_accord)
        val text = "已阅读并同意服务协议"
        val ss = SpannableString(text)
        val clickableSpan = object : ClickableSpan() {
            override fun onClick(textView: View) {
                // Your code here
                //Log.d("MainActivity: ", "click ok!!!")
                // 启动第二个activity，这个activity是用来展示用户协议的
                val intent = Intent(this@LoginActivity, ProtocalActivity::class.java)
                startActivity(intent)

            }
            override fun updateDrawState(ds: TextPaint) {
                //Log.d("LoginActivity: ", "update background")
                super.updateDrawState(ds)
                ds.isUnderlineText = false
                ds.bgColor = Color.TRANSPARENT
            }
        }
        val fgSpan = ForegroundColorSpan(Color.BLUE)
        ss.setSpan(clickableSpan, 6, 10, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        ss.setSpan(fgSpan, 6, 10, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        textView.text = ss
        textView.movementMethod = LinkMovementMethod.getInstance()
    }
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.tool_bar_login, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            android.R.id.home -> {
                ActivityCollector.finish()
                return true
            }
            R.id.register -> {
                //Log.d("LoginActivity: ", "注册")
                val intent = Intent(this, RegisterActivity::class.java)
                startActivity(intent)

            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun loading(){
        // 加载progressbar
    }

    override fun onClick(v: View?) {
        if(!isInit){
            //Toasty.makeText(this,"sorry! please await to load data from internet", Toast.LENGTH_SHORT).show()
            Toasty.warning(this, "请等待应用数据加载完毕", Toast.LENGTH_SHORT,true).show()
        }
        else{
            when(v?.id){
                binding.checkUp.id ->{
                    ischeck = true
                }
                binding.loginButton.id -> {
                    val username = binding.usernameEditText.text.toString()
                    val password = binding.passwordEditText.text.toString()
                    if(!ischeck){
                        Toasty.warning(this, "请先同意用户协议", Toast.LENGTH_SHORT,true).show()
                    }
                    else if(username == "" || password == ""){
                        Toasty.warning(this, "用户名或密码不可以为空", Toast.LENGTH_SHORT, true).show();
                    }else{
                        // 服务器端登录的用户,使用默认头像？
                        if(checkServer(username, password)) return


                        // 数据库内存储的用户登录
                        checkDatabase(username, password)
                    }
                }
                binding.visitor.id -> {
                    val intent = Intent(this, TranslateActivity::class.java).apply {
                        putExtra("mode", 0)
                    }
                    startActivity(intent)
                }
            }
        }
    }

    private fun checkDatabase(username: String, password : String){
        val db = dbHelper.readableDatabase
        val cursor = db.query("User",
            arrayOf("password"), "username = ?", arrayOf(username), null, null, null, null)
        var flag = false
        if(cursor.moveToFirst()){
            val getPassword = cursor.getString(cursor.getColumnIndexOrThrow("password"))
            if(getPassword == password){
                // 如果密码和用户名正确
                flag = true
                val intent = Intent(this@LoginActivity, TranslateActivity::class.java)
                intent.apply {
                    putExtra("username", username)
                    putExtra("password", password)
                    putExtra("mode", 1)
                }
                startActivity(intent)
            }
        }
        cursor.close()
        if(!flag){
            Toasty.error(this@LoginActivity, "密码或用户名错误", Toast.LENGTH_SHORT).show()
        }
    }

    private fun checkServer(username : String, password: String) : Boolean{
        for(user in viewModel.userList.value!!){
           if(user.username == username && user.password == password){
               val intent = Intent(this@LoginActivity, TranslateActivity::class.java)
               intent.apply {
                   putExtra("username", username)
                   putExtra("password", password)
                   putExtra("avatarTo", ByteArray(0))
                   putExtra("mode", 1)
               }
               return true
           }
        }

        return false
    }
}