package com.example.canchem.data.source

import com.google.gson.annotations.SerializedName
import com.navercorp.nid.NaverIdLoginSDK

data class Token(
    @SerializedName("Authorization") val accessToken : String?
//    val refreshToken : String?, //var?
//    val expiresAt : String?,
//    val tokenType : String?,
//    val state : String? //var?
)