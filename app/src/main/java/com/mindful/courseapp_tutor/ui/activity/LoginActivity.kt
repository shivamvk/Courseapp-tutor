package com.mindful.courseapp_tutor.ui.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.mindful.courseapp_tutor.R
import com.mindful.courseapp_tutor.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {

    lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login)
        supportActionBar?.hide()
        binding.btSignIn.setOnClickListener {
            startActivity(Intent(
                this, MainActivity::class.java
            ))
        }
    }
}