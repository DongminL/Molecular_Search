package com.example.canchem.ui.myFavorite

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.View.OnTouchListener
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.drawerlayout.widget.DrawerLayout.DrawerListener
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.canchem.R
import com.example.canchem.data.source.FavoriteData
import com.example.canchem.data.source.FavoriteRecyclerViewAdapter
import com.example.canchem.data.source.MyFavoriteInterface
import com.example.canchem.data.source.SearchData
import com.example.canchem.data.source.SearchHistoryInterface
import com.example.canchem.data.source.SearchRecyclerViewAdapter
import com.example.canchem.databinding.ActivityMyFavoriteBinding
import com.example.canchem.databinding.ActivityMenuBinding
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

class MyFavoriteActivity : AppCompatActivity() {
    private val ip : String = "192.168.45.233"
    //recyclerViewAdapter
    private lateinit var adapter: FavoriteRecyclerViewAdapter
    private lateinit var binding: ActivityMyFavoriteBinding
    var mDatas = mutableListOf<FavoriteData>() // 즐겨찾기 데이터 리스트 변수

    companion object{
        private var instance: MyFavoriteActivity? = null
        fun getInstance(): MyFavoriteActivity? {
            return instance
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMyFavoriteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val drawer = binding.myFavorite


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
//                Toast.makeText(this@MyFavoriteActivity,"파이어베이스 성공!", Toast.LENGTH_SHORT).show()

                // retrofit 변수 생성
                val retrofit = Retrofit.Builder()
                    .baseUrl("http://$ip:8080/")
                    .addConverterFactory(GsonConverterFactory.create()) //kotlin to json(역 일수도)
                    .build()

                // retrofit객체 생성
                val myFavoriteService = retrofit.create(MyFavoriteInterface::class.java)
                val call = myFavoriteService.getFavoriteInfo(accessToken)


                call.enqueue(object : Callback<ArrayList<FavoriteData>> {
                    override fun onResponse(call: Call<ArrayList<FavoriteData>>, response: Response<ArrayList<FavoriteData>>) { //요청성공시
                        if (response.isSuccessful) {
//                            Toast.makeText(this@SearchHistoryActivity,"retrofit도 성공!", Toast.LENGTH_SHORT).show()
                            mDatas = response.body()?.toMutableList() ?: mutableListOf() //여기에 retrofit으로 springboot에서 받은 검색기록 추가.
                            initializelist()
                            recyclerView(mDatas)
                            Toast.makeText(this@MyFavoriteActivity, mDatas.toString(), Toast.LENGTH_SHORT).show()
                        } else {
//                    Toast.makeText(this@SearchHistoryActivity, "SearchHistoryActivity Error", Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onFailure(call: Call<ArrayList<FavoriteData>>, t: Throwable) { //요청실패시
                        Toast.makeText(this@MyFavoriteActivity, "SearchHistoryActivity Server cannot 통신", Toast.LENGTH_SHORT).show()
                        Log.e("call error", t.toString())
                    }
                })
            }

            override fun onCancelled(error: DatabaseError) {
                Log.w(ContentValues.TAG, "Failed to read value.", error.toException())
            }
        })


        //side menu
        Log.i("btnHamburger", binding.btnHamburgerMyFavorite.toString())
        binding.btnHamburgerMyFavorite.setOnClickListener {
            drawer.openDrawer(Gravity.RIGHT)
//                Toast.makeText(this, "햄버거클릭딸깍", Toast.LENGTH_SHORT)
            val intent = Intent(this@MyFavoriteActivity, SideMenu::class.java)
            intent.putExtra("Activity", "SearchHistoryActivity")
            startActivity(intent)

        }

//        binding.btnSignout.setOnClickListener{
//            Toast.makeText(this, "회원탈퇴클릭딸깍", Toast.LENGTH_SHORT)
//            MainActivity.naverDeleteToken()
//        }
    }
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        val binding = ActivityMyFavoriteBinding.inflate(layoutInflater)
//        setContentView(binding.root)
//
//
//        //firebase에 저장된 토큰 가져오기
//        val database = Firebase.database
//        val tokenInFirebase = database.getReference("Token")
//        var accessToken : String? = null
//        tokenInFirebase.addValueEventListener(object: ValueEventListener {
//            override fun onDataChange(snapshot: DataSnapshot) {
//                // This method is called once with the initial value and again
//                // whenever data at this location is updated.
//                accessToken = snapshot.getValue().toString()
//            }
//
//            override fun onCancelled(error: DatabaseError) {
//                Log.w(ContentValues.TAG, "Failed to read value.", error.toException())
//            }
//        })
//
//        // retrofit 변수 생성
//        val retrofit = Retrofit.Builder()
//            .baseUrl("http://172.20.10.6:8080/")
//            .addConverterFactory(GsonConverterFactory.create()) //kotlin to json(역 일수도)
//            .build()
//
//        // retrofit객체 생성
//        val favoriteService = retrofit.create(MyFavoriteInterface::class.java)
//        val call = favoriteService.getSearchInfo(accessToken)
//
//        call.enqueue(object : Callback<ArrayList<FavoriteData>> {
//            override fun onResponse(call: Call<ArrayList<FavoriteData>>, response: Response<ArrayList<FavoriteData>>) { //요청성공시
//                if (response.isSuccessful) {
//                    mDatas = response.body()?.toMutableList() ?: mutableListOf() //여기에 retrofit으로 springboot에서 받은 검색기록 추가.
//                } else {
//                    Toast.makeText(this@MyFavoriteActivity, "SearchHistoryActivity Error", Toast.LENGTH_SHORT).show()
//                }
//
//            }
//
//            override fun onFailure(call: Call<ArrayList<FavoriteData>>, t: Throwable) { //요청실패시
//                Toast.makeText(this@MyFavoriteActivity, "SearchHistoryActivity Server cannot 통신", Toast.LENGTH_SHORT).show()
//                Log.e("call error", t.toString())
//            }
//        })
//
//        //recyclerView 함수 호출. 아래에 있음
//        initializelist()
//        recyclerView()
//
//
//        //side menu
//        binding.btnHamburgerMyFavorite.setOnClickListener {
//            val drawer = binding.myFavorite
//            if (!drawer.isDrawerOpen(Gravity.RIGHT)) {
//                drawer.openDrawer(Gravity.RIGHT)
////                val intent = Intent(this@MyFavoriteActivity,SideMenu::class.java)
////                startActivity(intent)
//            }
//        }
//    }

    @SuppressLint("MissingSuperCall")
    override fun onBackPressed() {
        finish()
    }

    fun recyclerView(mData: MutableList<FavoriteData>){
        val adapter = FavoriteRecyclerViewAdapter() //어댑터 객체 만듦
        adapter.datalist = mData //데이터 넣어줌
        binding.recyclerView.adapter = adapter //리사이클러뷰에 어댑터 연결
        binding.recyclerView.layoutManager = LinearLayoutManager(this) //레이아웃 매니저 연결
    }

    fun initializelist(){ //임의로 데이터 넣어서 만들어봄
//        adapter.datalist = mutableListOf(FavoriteData("aa"),FavoriteData("bb"))
//        with(mDatas){
//            add(FavoriteData("H2O"))
//            add(FavoriteData("H2O"))
//            add(FavoriteData("H2O"))
//            add(FavoriteData("H2O"))
//        }
    }

    fun deleteData(favoriteData: FavoriteData){
        mDatas.remove(favoriteData)
        Toast.makeText(this, "$adapter", Toast.LENGTH_SHORT).show()
        adapter?.notifyDataSetChanged()
    }

    fun sayHello(){
        Toast.makeText(this, "hellooooo", Toast.LENGTH_SHORT).show()
    }
}