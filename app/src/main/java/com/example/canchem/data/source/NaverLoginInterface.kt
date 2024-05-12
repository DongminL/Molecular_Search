package com.example.canchem.data.source

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface NaverLoginInterface{
    @POST("api/login/naver")
    fun getLoginToken(
        //@Header("Authorization") accessToken: String?,  // Bearer AccessToken
        @Body naverToken: NaverToken
    ) : Call<Token>
}