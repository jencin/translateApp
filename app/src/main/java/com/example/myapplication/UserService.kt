package com.example.myapplication

import retrofit2.Call
import retrofit2.http.GET

interface UserService {

    @GET("get_user.json")
    fun getUserList() : Call<List<User>>
}