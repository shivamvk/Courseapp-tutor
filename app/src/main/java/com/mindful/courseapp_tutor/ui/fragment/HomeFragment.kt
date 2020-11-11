package com.mindful.courseapp_tutor.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.mindful.courseapp_tutor.R
import com.mindful.courseapp_tutor.databinding.FragmentHomeBinding
import com.mindful.courseapp_tutor.ui.activity.LiveVideoTeachActivity
import com.mindful.courseapp_tutor.ui.adapter.CourseListAdapter

class HomeFragment: Fragment() {

    lateinit var binding: FragmentHomeBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_home, container, false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.startLive.setOnClickListener {
            startActivity(Intent(
                context,
                LiveVideoTeachActivity::class.java
            ))
        }
        binding.rvCourses.layoutManager = LinearLayoutManager(context)
        binding.rvCourses.adapter = context?.let { CourseListAdapter(it) }
    }

}