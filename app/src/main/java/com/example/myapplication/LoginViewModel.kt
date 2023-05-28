package com.example.myapplication

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class LoginViewModel(mylist: List<User>) : ViewModel(){

    val userList : LiveData<List<User>>
        get() = list
    private val list = MutableLiveData<List<User>>()

    /*init{
        val job = Job()
        val scope = CoroutineScope(job)
        scope.launch {
            val userLis = ServiceCreator.create<UserService>().getUserList().await()
            for(user in userLis){
                Log.d("LoginActivity: ", "${user.username}, ${user.password}")
            }

        }

    }*/
    // 初始化，获取用户名列表
    init{
        list.value = mylist
    }

}