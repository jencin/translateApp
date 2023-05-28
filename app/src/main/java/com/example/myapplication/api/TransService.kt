package com.example.myapplication.api

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface TransService {
    @GET("?")
    fun getuData(@Query("from") from: String,
                 @Query("to") to: String,
                 @Query("appid") appid: String,
                 @Query("salt") salt: String,
                 @Query("sign") sign: String,
                 @Query("q") q: String) : Call<TranslateResult>
}