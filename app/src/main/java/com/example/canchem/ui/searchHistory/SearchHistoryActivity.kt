package com.example.canchem.ui.searchHistory

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.canchem.R
import com.example.canchem.data.source.NaverLoginInterface
import com.example.canchem.data.source.SearchData
import com.example.canchem.data.source.SearchHistoryInterface
import com.example.canchem.data.source.SearchRecyclerViewAdapter
import com.example.canchem.data.source.Token
import com.example.canchem.databinding.ActivityMainBinding
import com.example.canchem.databinding.ActivitySearchHistoryBinding
import com.example.canchem.ui.main.MainActivity
import com.example.canchem.ui.myPage.SideMenu
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class SearchHistoryActivity : AppCompatActivity() {
    private val ip : String = "192.168.45.233"
    //recyclerViewAdapter
    private lateinit var adapter: SearchRecyclerViewAdapter
    private lateinit var binding:ActivitySearchHistoryBinding
    var mDatas = mutableListOf<SearchData>() // 검색기록 데이터 리스트 변수

    companion object {
        private var instance: SearchHistoryActivity? = null

        fun getInstance(): SearchHistoryActivity? {
            return instance
        }

        fun deleteData(searchData: SearchData){
            val activity = instance ?: return
            activity.mDatas.remove(searchData)
            activity.adapter?.notifyDataSetChanged()
            Toast.makeText(activity, "Adapter: ${activity.adapter}", Toast.LENGTH_SHORT).show()
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val drawer = binding.searchHistory


        val extra = intent.extras
        if(extra?.getString("key") == "btnXClicked"){
            drawer.closeDrawer(Gravity.RIGHT)
        }


        //firebase에 저장된 토큰 가져오기
        val database = Firebase.database
        val tokenInFirebase = database.getReference("Token")
        var accessToken : String? = null
        tokenInFirebase.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                accessToken = snapshot.getValue().toString()
//                Toast.makeText(this@SearchHistoryActivity,"파이어베이스 성공!", Toast.LENGTH_SHORT).show()

                // retrofit 변수 생성
                val retrofit = Retrofit.Builder()
                    .baseUrl("http://$ip:8080/")
                    .addConverterFactory(GsonConverterFactory.create()) //kotlin to json(역 일수도)
                    .build()

                // retrofit객체 생성
                val searchService = retrofit.create(SearchHistoryInterface::class.java)
                val call = searchService.getSearchInfo(accessToken)


                call.enqueue(object : Callback<ArrayList<SearchData>> {
                    override fun onResponse(call: Call<ArrayList<SearchData>>, response: Response<ArrayList<SearchData>>) { //요청성공시
                        if (response.isSuccessful) {
//                            Toast.makeText(this@SearchHistoryActivity,"retrofit도 성공!", Toast.LENGTH_SHORT).show()
                            mDatas = response.body()?.toMutableList() ?: mutableListOf() //여기에 retrofit으로 springboot에서 받은 검색기록 추가.
                            initializelist()
                            recyclerView(mDatas)
                            Toast.makeText(this@SearchHistoryActivity, mDatas.toString(), Toast.LENGTH_SHORT).show()
                        } else {
//                    Toast.makeText(this@SearchHistoryActivity, "SearchHistoryActivity Error", Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onFailure(call: Call<ArrayList<SearchData>>, t: Throwable) { //요청실패시
                        Toast.makeText(this@SearchHistoryActivity, "SearchHistoryActivity Server cannot 통신", Toast.LENGTH_SHORT).show()
                        Log.e("call error", t.toString())
                    }
                })
            }

            override fun onCancelled(error: DatabaseError) {
                Log.w(ContentValues.TAG, "Failed to read value.", error.toException())
            }
        })


        //여기에 데이터 받아와서 넣기
//        mDatas = mutableListOf(SearchData("a"),SearchData("b"))



        //이게 회원탈퇴.
        //사이드메뉴 회원탈퇴 누르면 이거 작동시키면 됨.
        binding.btnSignout.setOnClickListener{
            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra("function", "signout")
            startActivity(intent)
        }

        //recyclerView 함수 호출. 아래에 있음


        //side menu
        Log.i("btnHamburger", binding.btnHamburgerSearchHistoiry.toString())
        binding.btnHamburgerSearchHistoiry.setOnClickListener {
            drawer.openDrawer(Gravity.RIGHT)
//                Toast.makeText(this, "햄버거클릭딸깍", Toast.LENGTH_SHORT)
            val intent = Intent(this@SearchHistoryActivity, SideMenu::class.java)
            intent.putExtra("Activity", "SearchHistoryActivity")
            startActivity(intent)

        }

//        binding.btnSignout.setOnClickListener{
//            Toast.makeText(this, "회원탈퇴클릭딸깍", Toast.LENGTH_SHORT)
//            MainActivity.naverDeleteToken()
//        }
    }

    @SuppressLint("MissingSuperCall")
    override fun onBackPressed() {
        finish()
    }

    fun recyclerView(mData: MutableList<SearchData>){
        val adapter = SearchRecyclerViewAdapter() //어댑터 객체 만듦
        adapter.datalist = mData //데이터 넣어줌
        binding.recyclerView.adapter = adapter //리사이클러뷰에 어댑터 연결
        binding.recyclerView.layoutManager = LinearLayoutManager(this) //레이아웃 매니저 연결
    }

    fun initializelist(){ //임의로 데이터 넣어서 만들어봄
//        adapter.datalist = mutableListOf(SearchData("aa"),SearchData("bb"))
//        with(mDatas){
//            add(SearchData("H2O"))
//            add(SearchData("H2O"))
//            add(SearchData("H2O"))
//            add(SearchData("H2O"))
//        }
    }

//    fun deleteData(searchData: SearchData){
//        mDatas.remove(searchData)
//        Toast.makeText(this, "$adapter", Toast.LENGTH_SHORT).show()
//        adapter?.notifyDataSetChanged()
//    }

    fun sayHello(){
        Toast.makeText(this, "hellooooo", Toast.LENGTH_SHORT).show()
    }
}