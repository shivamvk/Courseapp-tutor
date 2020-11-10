package com.mindful.courseapp_tutor.ui.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
import com.mindful.courseapp_tutor.R
import com.mindful.courseapp_tutor.databinding.ActivityMainBinding
import com.mindful.courseapp_tutor.ui.fragment.HomeFragment

class MainActivity : AppCompatActivity(), BottomNavigationView.OnNavigationItemSelectedListener {

    lateinit var binding: ActivityMainBinding
    var homeFragment = HomeFragment()
    var activeFragment: Fragment = homeFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.bottomNav.setOnNavigationItemSelectedListener(this)
        supportFragmentManager.beginTransaction().add(R.id.container, homeFragment, "Home").hide(homeFragment).commit()
        goto(homeFragment)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.nav_home -> goto(homeFragment)
        }
        return true
    }

    private fun goto(fragment: Fragment) {
        supportFragmentManager.beginTransaction().hide(activeFragment).show(fragment).commit()
        activeFragment = fragment
    }
}