package com.example.canchem.data.source

import okhttp3.Interceptor
import okhttp3.Response

class AccessTokenInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
//        //JWT 가져오기
//        var token = TOKEN
        var token = 2 //아무값






        val request = chain.request()
            .newBuilder()
            .addHeader("JWT", "$token")
            .build()
        val response = chain.proceed(request)

        return response
    }

}