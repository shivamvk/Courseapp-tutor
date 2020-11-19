package com.mindful.courseapp_tutor.di

import com.mindful.courseapp_tutor.ui.activity.*
import com.mindful.courseapp_tutor.ui.fragment.HomeFragment
import com.mindful.networklibrary.NetworkModule
import dagger.Component
import javax.inject.Singleton

@Component(modules = [NetworkModule::class])
@Singleton
interface AppComponent {
    fun inject(activity: LoginActivity)
    fun inject(activity: LiveVideoTeachActivity)
    fun inject(activity: SplashActivity)
    fun inject(fragment: HomeFragment)
    fun inject(activity: CourseDetailsActivity)
    fun inject(activity: AddChapterActivity)
    fun inject(activity: AddNotesActivity)
    //create fun inject() for all the activities, fragments etc using dagger
}