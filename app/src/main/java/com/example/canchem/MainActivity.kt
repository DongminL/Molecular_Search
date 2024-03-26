package com.example.canchem

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.canchem.databinding.ActivityMainBinding




class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        val binding = ActivityMainBinding.inflate(layoutInflater)

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        //액티비티 변경
        val intent = Intent(this, HomeActivity::class.java)

        binding.btnLogin.setOnClickListener{
            startActivity(intent)
        }
//        binding.btnLogin.setOnClickListener{
//            binding.btnLogin.visibility = View.INVISIBLE
//            binding.btnLogout.visibility = View.VISIBLE
//
//        }
//        binding.btnLogout.setOnClickListener{
//            binding.btnLogout.visibility = View.INVISIBLE
//            binding.btnLogin.visibility = View.VISIBLE
//        }
    }
}