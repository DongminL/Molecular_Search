package com.example.canchem.data.source

import retrofit2.Call
import retrofit2.http.DELETE
import retrofit2.http.Header

interface GoogleSignoutInterface {
    @DELETE("api/logout")
    fun signout(
        @Header("Authorization") accessToken: String?,  // Baerer AccessToken
    ) : Call<Token>
}