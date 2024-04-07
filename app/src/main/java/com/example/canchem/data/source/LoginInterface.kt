package com.example.canchem.data.source

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface LoginInterface{
    @POST("api/login/naver")
    fun getLoginToken(
        //@Header("Authorization") accessToken: String?,  // Baerer AccessToken
        @Body naverToken: NaverToken


//        @Body user_id : String?,
//        @Body email : String?,
//        @Body name : String?,
//        @Body nickname : String?,
//        @Body mobile : String?,
//        @Body gender : String?,
//        @Body profile_image : String?


//        @Query("user_id") user_id : String?,
//        @Query("email") email : String?,
//        @Query("name") name : String?,
//        @Query("nickname") nickname : String?,
//        @Query("mobile") mobile : String?,
//        @Query("gender") gender : String?,
//        @Query("profile_image") profile_image : String?
    ) : Call<Token>
}