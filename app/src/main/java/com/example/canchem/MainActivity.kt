package com.example.canchem

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.canchem.databinding.ActivityMainBinding
import com.navercorp.nid.NaverIdLoginSDK
import com.navercorp.nid.oauth.OAuthLoginCallback


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        /* 네아로 SDK 객체 초기화 */
        val naverClientId = getString(R.string.naver_client_id) // 발급 받은 naver client id 값
        val naverClientSecret = getString(R.string.naver_client_secret) // 발급 받은 naver client secret 값
        val naverClientName = getString(R.string.naver_client_name) // 어플 이름
        NaverIdLoginSDK.initialize(this, naverClientId, naverClientSecret , naverClientName)    // 네아로 객체 초기화

        enableEdgeToEdge()

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }



        binding.btnNaverLogin.setOnClickListener {
            val oauthLoginCallback = object : OAuthLoginCallback {
                override fun onSuccess() {
                    // 네이버 로그인 인증이 성공했을 때 수행할 코드 추가
                    val accessToken = NaverIdLoginSDK.getAccessToken().toString()   // 접근 토큰
                    val refreshToken = NaverIdLoginSDK.getRefreshToken().toString() // 갱신 토큰
                    val expiresAt = NaverIdLoginSDK.getExpiresAt().toString()   // 만료 기한 (초)
                    val type = NaverIdLoginSDK.getTokenType().toString()    // 토큰 타입
                    val state = NaverIdLoginSDK.getState().toString()   // 로그인 인스턴트의 현재 상태

                    Log.i("Login Data", accessToken)
                    Log.i("Login Data", refreshToken)
                    Log.i("Login Data", expiresAt)
                    Log.i("Login Data", type)
                    Log.i("Login Data", state)
                }

                override fun onFailure(httpStatus: Int, message: String) {
                    val errorCode = NaverIdLoginSDK.getLastErrorCode().code
                    val errorDescription = NaverIdLoginSDK.getLastErrorDescription()
                    Toast.makeText(this@MainActivity,"errorCode:$errorCode, errorDesc:$errorDescription",Toast.LENGTH_SHORT).show()
                }

                override fun onError(errorCode: Int, message: String) {
                    onFailure(errorCode, message)
                }
            }

            NaverIdLoginSDK.authenticate(this, oauthLoginCallback)  // 토큰 가져오기
        }
    }
}