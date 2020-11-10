package com.mindful.courseapp_tutor.di

import android.app.Application
import com.mindful.networklibrary.NetworkModule

class CourseTutorApplication : Application() {
    lateinit var dagger: AppComponent

    override fun onCreate() {
        super.onCreate();
        initDagger()
    }

    private fun initDagger() {
        dagger = DaggerAppComponent.builder().networkModule(
            NetworkModule(applicationContext) // This is where we pass the context to our module
        ).build()
    }

    fun getDeps(): AppComponent {
        return dagger!!
    }
}