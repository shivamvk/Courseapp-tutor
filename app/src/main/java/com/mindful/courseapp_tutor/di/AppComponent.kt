package com.mindful.courseapp_tutor.di

import com.mindful.courseapp_tutor.ui.activity.LiveVideoTeachActivity
import com.mindful.courseapp_tutor.ui.activity.LoginActivity
import com.mindful.courseapp_tutor.ui.activity.SplashActivity
import com.mindful.networklibrary.NetworkModule
import dagger.Component
import javax.inject.Singleton

@Component(modules = [NetworkModule::class])
@Singleton
interface AppComponent {
    fun inject(activity: LoginActivity)
    fun inject(activity: LiveVideoTeachActivity)
    fun inject(activity: SplashActivity)
    //create fun inject() for all the activities, fragments etc using dagger
}