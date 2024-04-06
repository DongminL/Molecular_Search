package com.example.canchem

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.canchem.databinding.ActivityMainBinding

class HomeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_home)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val h_binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(h_binding.root)

        h_binding.btnNaverLogout.setOnClickListener{
            val h_intent = Intent(this@HomeActivity, MainActivity::class.java)
            startActivity(h_intent)
        }
        //val intent = Intent(this@HomeActivity, MainActivity::class.java)
        //startActivity(intent)
    }
}