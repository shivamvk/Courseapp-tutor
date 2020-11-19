package com.mindful.courseapp_tutor.ui.activity

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import com.mindful.courseapp_tutor.R
import com.mindful.courseapp_tutor.di.CourseTutorApplication
import com.mindful.networklibrary.sharedprefs.PreferencesHelper
import javax.inject.Inject
import com.mindful.networklibrary.sharedprefs.PreferencesHelper.get

class SplashActivity : AppCompatActivity() {

    @Inject lateinit var prefs: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (application as CourseTutorApplication).getDeps().inject(this)
        setContentView(R.layout.activity_splash)
        supportActionBar?.hide()
        Handler().postDelayed(Runnable {
            if (prefs[PreferencesHelper.SharedPrefKeys.USER_TOKEN.toString(), ""]?.isEmpty()!!) {
                startActivity(Intent(this, LoginActivity::class.java))
            } else {
                startActivity(Intent(this, MainActivity::class.java))
            }
            finish()
        }, 2000)
    }
}