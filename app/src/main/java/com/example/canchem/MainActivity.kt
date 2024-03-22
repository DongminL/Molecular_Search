package com.example.canchem

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.canchem.databinding.ActivityMainBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.Scope
import com.google.android.gms.tasks.Task
import com.navercorp.nid.NaverIdLoginSDK
import com.navercorp.nid.oauth.NidOAuthLogin
import com.navercorp.nid.oauth.OAuthLoginCallback
import com.navercorp.nid.profile.NidProfileCallback
import com.navercorp.nid.profile.data.NidProfileResponse


class MainActivity : AppCompatActivity() {

    lateinit var googleSignInClient : GoogleSignInClient
    lateinit var resultLauncher : ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        /* View Binding 설정 */
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        enableEdgeToEdge()  // 상태표시줄 투명하게 만듦

        /* 네아로 SDK 객체 초기화 */
        val naverClientId = getString(R.string.naver_client_id) // 발급 받은 naver client id 값
        val naverClientSecret = getString(R.string.naver_client_secret) // 발급 받은 naver client secret 값
        val naverClientName = getString(R.string.naver_client_name) // 어플 이름
        NaverIdLoginSDK.initialize(this, naverClientId, naverClientSecret , naverClientName)    // 네아로 객체 초기화

        googleAuthLauncher()
        googleSignInClient = getGoogleClient()

        /* Google 로그인 버튼 클릭*/
        binding.btnGoogleLogin.setOnClickListener {
            resultLauncher.launch(googleSignInClient.signInIntent)

            /* 로그인한 상태 */
            binding.btnNaverLogin.visibility = View.INVISIBLE
            binding.btnGoogleLogin.visibility = View.INVISIBLE
            binding.btnNaverLogout.visibility = View.VISIBLE
        }

        /* 네이버 로그인 버튼 클릭 */
        binding.btnNaverLogin.setOnClickListener {
            /* 사용자 정보 가져오기*/
            val nidProfileCallback = object : NidProfileCallback<NidProfileResponse> {
                override fun onSuccess(response: NidProfileResponse) {
                    val userId = response.profile?.id   // 고유 아이디
                    val email = response.profile?.email // 이메일
                    val mobile = response.profile?.mobile   // 휴대폰 번호

                    Toast.makeText(this@MainActivity, "네이버 로그인 성공\n" +
                            "user id : ${userId}\n" +
                            "email: ${email}\n" +
                            "mobile : ${mobile}", Toast.LENGTH_SHORT).show()
                }
                override fun onFailure(httpState : Int, message: String) {
                    val errorCode = NaverIdLoginSDK.getLastErrorCode().code
                    val errorDesc = NaverIdLoginSDK.getLastErrorDescription()

                    Toast.makeText(this@MainActivity, "네이버 로그인 실패\n" +
                            "Error Code : ${errorCode}\n" +
                            "Error Description : ${errorDesc}", Toast.LENGTH_SHORT).show()
                }
                override fun onError(errorCode: Int, message: String) {
                    onFailure(errorCode, message)
                }
            }

            /* 네이버 Access Token 받기 */
            val oauthLoginCallback = object : OAuthLoginCallback {
                override fun onSuccess() {
                    // 네이버 로그인 인증이 성공했을 때 수행할 코드 추가
                    val accessToken = NaverIdLoginSDK.getAccessToken().toString()   // 접근 토큰
                    val refreshToken = NaverIdLoginSDK.getRefreshToken().toString() // 갱신 토큰
                    val expiresAt = NaverIdLoginSDK.getExpiresAt().toString()   // 만료 기한 (초)
                    val type = NaverIdLoginSDK.getTokenType().toString()    // 토큰 타입
                    val state = NaverIdLoginSDK.getState().toString()   // 로그인 인스턴트의 현재 상태

                    Toast.makeText(this@MainActivity, "Access Token : ${accessToken}", Toast.LENGTH_SHORT).show()

                    NidOAuthLogin().callProfileApi(nidProfileCallback)

                    /* 로그인한 상태 */
                    binding.btnNaverLogin.visibility = View.INVISIBLE
                    binding.btnGoogleLogin.visibility = View.INVISIBLE
                    binding.btnNaverLogout.visibility = View.VISIBLE
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

        /* 로그아웃 버튼 클릭 */
        binding.btnNaverLogout.setOnClickListener {
            naverLogout()   // 네이버 로그아웃

            /* 로그아웃한 상태 */
            binding.btnNaverLogout.visibility = View.INVISIBLE
            binding.btnNaverLogin.visibility = View.VISIBLE
            binding.btnGoogleLogin.visibility = View.VISIBLE
        }

        /* 탈퇴 버튼 클릭 */
        binding.btnNaverDelete.setOnClickListener {
            naverDeleteToken()  // 네이버 연동 해제

            /* 로그아웃한 상태 */
            binding.btnNaverLogout.visibility = View.INVISIBLE
            binding.btnNaverLogin.visibility = View.VISIBLE
            binding.btnGoogleLogin.visibility = View.VISIBLE
        }
    }
    /* 네이버 로그아웃 */
    private fun naverLogout() {
        NaverIdLoginSDK.logout()
        Toast.makeText(this@MainActivity, "네이버 로그아웃 성공", Toast.LENGTH_SHORT).show()
    }

    /* 네이버 연동 해제 */
    private fun naverDeleteToken() {
        NidOAuthLogin().callDeleteTokenApi(object : OAuthLoginCallback {
            override fun onSuccess() {
                // 서버에서 토큰 삭제에 성공한 상태
                Toast.makeText(this@MainActivity, "네이버 연동 해제 성공", Toast.LENGTH_SHORT).show()
            }
            override fun onFailure(httpStatus: Int, message: String) {
                // 서버에서 토큰 삭제에 실패했어도 클라이언트에 있는 토큰은 삭제되어 로그아웃된 상태
                // 클라이언트에 토큰 정보가 없기 때문에 추가로 처리할 수 있는 작업은 없음
                Log.e(TAG, "errorCode: ${NaverIdLoginSDK.getLastErrorCode().code}")
                Log.e(TAG, "errorDesc: ${NaverIdLoginSDK.getLastErrorDescription()}")
            }
            override fun onError(errorCode: Int, message: String) {
                // 서버에서 토큰 삭제에 실패했어도 클라이언트에 있는 토큰은 삭제되어 로그아웃된 상태
                // 클라이언트에 토큰 정보가 없기 때문에 추가로 처리할 수 있는 작업은 없음
                onFailure(errorCode, message)
            }
        })
    }

    /* Google 사용자 정보 요청 */
    private fun getGoogleClient() : GoogleSignInClient {
        val googleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestServerAuthCode(getString(R.string.google_client_id))    // OAuth Client ID 넘기기
            .requestId()    // ID 요청
            .requestEmail() // Email 요청
            .build()

        return GoogleSignIn.getClient(this@MainActivity, googleSignInOptions)
    }

    /* Google 사용자 정보 받아오기 */
    private fun googleAuthLauncher() {
        resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            val task : Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(result.data)

            try {
                val account = task.getResult(ApiException::class.java)

                val userId = account?.id
                val email = account?.email
                val token = account?.idToken

                Toast.makeText(this@MainActivity, "구글 로그인 성공\n" +
                        "user id : ${userId}\n" +
                        "email : ${email}\n" +
                        "token : ${token}", Toast.LENGTH_SHORT).show()

            } catch (e: ApiException) {
                Log.e("Error : ",e.stackTraceToString())
            }
        }
    }

    /* Google 로그아웃 */
    private fun googleLogout() {
        googleSignInClient.signOut()
        Toast.makeText(this@MainActivity, "구글 로그아웃 성공", Toast.LENGTH_SHORT).show()
    }

    /* Google 연동 해제 */
    private fun googleDelete() {
        googleSignInClient.revokeAccess()
        Toast.makeText(this@MainActivity, "구글 연동 해제 성공", Toast.LENGTH_SHORT).show()
    }
}