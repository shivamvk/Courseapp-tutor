package com.mindful.courseapp_tutor.ui.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.mindful.courseapp_tutor.R
import com.mindful.courseapp_tutor.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.startLive.setOnClickListener {
            startActivity(Intent(
                this, LiveVideoTeachActivity::class.java
            ))
        }
    }
}