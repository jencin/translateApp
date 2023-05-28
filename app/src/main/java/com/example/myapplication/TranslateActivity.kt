package com.example.myapplication

import android.app.Activity
import android.content.ContentResolver
import android.content.ContentValues
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaPlayer
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import androidx.core.view.get
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.myapplication.api.TransService
import com.example.myapplication.api.TranslateResult
import com.example.myapplication.databinding.ActivityTranslateBinding
import de.hdodenhof.circleimageview.CircleImageView
import es.dmoral.toasty.Toasty
import kotlinx.coroutines.Job
import kotlinx.coroutines.coroutineScope
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.ByteArrayOutputStream
import java.math.BigInteger
import java.security.MessageDigest
import kotlin.concurrent.thread
import kotlin.random.Random

class TranslateActivity : BaseActivity() , View.OnClickListener{
    private lateinit var binding : ActivityTranslateBinding
    private val appid = "20230525001689746"
    private val key = "qNssdnzAVq3lGKtQj3X2"
    private val httpStr = "https://fanyi-api.baidu.com/api/trans/vip/translate/"

    private var fromType : String = "en"
    private var toType : String = "ch"
    private val albumCode : Int =  2
    private val dbHelper2 =  UserDbHelper(this,"UserDb.db",1)

    private lateinit var globalUsernameObj: TextView
    private lateinit var globalPasswordObj: TextView
    private lateinit var globalAvatarObj : CircleImageView

    private val mediaPlayer = MediaPlayer()

    private val srcList = listOf("zh", "en", "jp", "ru", "pl", "el", "kor", "cs", "dan", "fra")
    private val languageList = listOf("中文","英语","日语","俄语","波兰语","希腊语","韩语","捷克语","丹麦语", "法语")
    private val dstList = listOf("zh", "en", "jp", "ru", "pl", "el", "kor", "cs", "dan", "fra")
    private lateinit var DrawerToggle: ActionBarDrawerToggle

    // 记录每次的翻译源内容
    private val records = ArrayList<String>()
    // 创建数据库
    private val dhHelper by lazy {
        val helper = MyDatabaseHelper(this,"History.db",1)
        helper
    }

    private lateinit var viewModel: TranslateViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTranslateBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        supportActionBar?.let {
            it.setDisplayHomeAsUpEnabled(true)
            it.setHomeAsUpIndicator(R.drawable.ic_menu)
        }

        val mode = intent.getIntExtra("mode",0)
        viewModel = ViewModelProvider(this, TranslateViewModelFactory(mode, 0)).get(TranslateViewModel::class.java)

        viewModel.flag.observe(this, Observer { flag ->
            if(flag == 1){
                binding.fab.visibility = View.VISIBLE
                binding.resultBody.visibility = View.VISIBLE
            }else{
                binding.fab.visibility = View.GONE
                binding.resultBody.visibility = View.GONE
            }
        })
        // 初始化数据库
        // 动态改变menu
        if(viewModel.mode.value == 1){
            binding.navView.menu.clear()
            val db = dhHelper.writableDatabase
            binding.navView.inflateMenu(R.menu.nav_menu)
        }else{
            binding.navView.menu.clear()
            binding.navView.inflateMenu(R.menu.visitor_menu)
        }

        binding.fromType.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                val selectItem = parent?.getItemAtPosition(position).toString()
                fromType = getLanguageType(selectItem)

            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }

        binding.toType.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                val selectItem = parent?.getItemAtPosition(position).toString()
                toType = getLanguageType(selectItem)

            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }

        initAudio()
        binding.navView.setNavigationItemSelectedListener {it ->
            when(it.itemId){
                R.id.play -> {     // 播放音乐
                    Log.d("TranslationActivity: ", "play click")
                    if(!mediaPlayer.isPlaying){
                        mediaPlayer.start()
                    }
                }
                R.id.pause -> {    // 暂停播放音乐
                    Log.d("TranslationActivity: ", "pause click")
                    if(mediaPlayer.isPlaying){
                        mediaPlayer.pause()
                    }
                }
                R.id.exit -> {    // 退出程序
                    //Log.d("TranslationActivity: ", ActivityCollector.activities.size.toString())
                    ActivityCollector.finish()

                }
                R.id.sign_out -> {  // 注销
                    finish()
                    //Log.d("TranslationActivity: ", "resign_out click")
                }
                R.id.history -> {  // 历史
                    val intent = Intent(this,HistoryActivity::class.java)
                    //
                    startActivity(intent)
                }
            }
            val countDown = object : CountDownTimer(300,100){
                override fun onTick(millisUntilFinished: Long) {

                }

                override fun onFinish() {
                    if(it.isCheckable){
                        it.isChecked = false
                    }
                }
            }.start()
            true
        }

        // 绑定两个按钮
        binding.fab.setOnClickListener(this)
        binding.submit.setOnClickListener(this)

        // 用户模式下

        globalUsernameObj = binding.navView.getHeaderView(0).findViewById(R.id.user_name_info)
        globalAvatarObj = binding.navView.getHeaderView(0).findViewById(R.id.avatar)
        globalPasswordObj = binding.navView.getHeaderView(0).findViewById(R.id.user_password_info)


        if(viewModel.mode.value == 1){
            initUserInfo()
            globalAvatarObj.setOnClickListener{
                //Log.d("TranslateActivity: ", "click avatar ok!")
                val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
                intent.addCategory(Intent.CATEGORY_OPENABLE)
                intent.type = "image/*"
                startActivityForResult(intent, albumCode)
            }
        }

    }

    private fun initUserInfo(){
        val getUsername = intent.getStringExtra("username").toString()
        val getPassword = intent.getStringExtra("password").toString()
        Log.d("TranslateActivity: ", getUsername)
        val myAvatar = intent.getByteArrayExtra("avatar")
        globalUsernameObj.text = getUsername
        globalPasswordObj.text = getPassword
        if(myAvatar == null){
            // user mode get data from database
            //Log.d("TranslateActivity: ",myAvatar.toString())
            val db = dbHelper2.readableDatabase
            val cursor = db.query("User", arrayOf("avatar"), "username = ?", arrayOf(getUsername), null, null, null, null)
            if(cursor.moveToFirst()){
                val getAvatar = cursor.getBlob(cursor.getColumnIndexOrThrow("avatar"))
                if(getAvatar != null){
                    val bitmap = BitmapFactory.decodeByteArray(getAvatar, 0, getAvatar.size)
                    globalAvatarObj.setImageBitmap(bitmap)
                }
            }
            cursor.close()
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when(requestCode){
            albumCode -> {
                if(resultCode == Activity.RESULT_OK && data != null){
                    data.data?.let {uri ->
                        val bitmap = getBitmapFromUri(uri)
                        bitmap?.let {
                            globalAvatarObj.setImageBitmap(it)
                            // 写入数据库里面
                            val stream = ByteArrayOutputStream()
                            bitmap.compress(Bitmap.CompressFormat.PNG,100,stream)
                            val byteArray = stream.toByteArray()
                            val values = ContentValues().apply {
                                put("avatar",byteArray)
                            }
                            val username = globalUsernameObj.text.toString()
                            dbHelper2.writableDatabase.apply {
                                update("User", values, "username = ?", arrayOf(username))
                            }

                        }
                    }
                }
            }
        }
    }

    private fun getBitmapFromUri(uri: Uri) = contentResolver.openFileDescriptor(uri, "r")?.use{
        BitmapFactory.decodeFileDescriptor(it.fileDescriptor)
    }

    private fun initAudio(){
        val assetManager = assets
        val fd = assetManager.openFd("music.mp3")
        mediaPlayer.setDataSource(fd.fileDescriptor, fd.startOffset, fd.length)
        mediaPlayer.prepare()
    }


    private fun getLanguageType(language: String) : String{
        return when(language){
            "中文" -> "zh"
            "日语" -> "jp"
            "英语" -> "en"
            "俄语" -> "ru"
            "韩语" -> "kor"
            else -> "zh"
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            android.R.id.home -> {
                binding.drawerlayout.openDrawer(GravityCompat.START)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onDestroy() {
        super.onDestroy()
        ActivityCollector.remove(this)
    }



    private fun getMD5(string: String): String {
        val md = MessageDigest.getInstance("MD5")
        md.update(string.toByteArray())
        return BigInteger(1, md.digest()).toString(16)
    }

    private fun translate(content : String, fromType: String, toType: String) {
        val retrofit = Retrofit.Builder()
            .baseUrl(httpStr)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val service = retrofit.create(TransService::class.java)
        val salt = Random(1).nextInt(100).toString();
        val str = appid + content + salt + key

        val sign = getMD5(str)
        service.getuData(fromType,toType,appid, salt, sign, content).enqueue(object : Callback<TranslateResult> {
            override fun onResponse(
                call: Call<TranslateResult>,
                response: Response<TranslateResult>,
            ) {
                val result = response.body()
                //Log.d("TranslateActivity: ", "response ok")
                result?.let {
                    runOnUiThread{
                        //Log.d("TranslateActivity: ", result.trans_result.get(0).dst)
                        val res = result.trans_result.get(0).dst
                        binding.result.text = res
                        // 判断是不是游客模式，是的话就把记录插入到数据库里面
                        // 每翻译一条就加一条记录
                        // 如果当前的records存在值的话，必然是用户模式的，修改数据库
                        if(records.isNotEmpty()){
                            val db = dhHelper.writableDatabase
                            val values = ContentValues().apply {
                                put("fromType", fromType)
                                put("toType",toType)
                                put("content", records[0])
                                put("result", res)
                            }
                            db.insert("Record",null, values)
                        }
                    }
                }
            }

            override fun onFailure(call: Call<TranslateResult>, t: Throwable) {
                Log.d("TranslateActivity: ", "response fail")

            }
        })
    }

    override fun onClick(v: View?) {
        when(v?.id){
            binding.fab.id -> {
                if(viewModel.flag.value == 1){
                    viewModel.change()
                    binding.inputContent.text.clear()
                }
            }

            binding.submit.id -> {
                val content = binding.inputContent.text.toString()
                if(content == ""){
                    Toasty.warning(this, "请输入要翻译的内容", Toast.LENGTH_SHORT, true).show()
                }else if(fromType == toType){
                    Toasty.warning(this, "源语言和翻译语言不可一致!", Toast.LENGTH_SHORT, true).show()
                }
                else{
                    // 修改模式同时翻译内容
                    viewModel.change()
                    // 获取翻译过后的数据
                    val content = binding.inputContent.text.toString()
                    // 判断是不是用户模式，是的话才进行数据库的更新操作
                    if(viewModel.mode.value == 1){
                        records.add(content)
                    }
                    translate(content,fromType,toType)
                }
            }

        }
    }

}