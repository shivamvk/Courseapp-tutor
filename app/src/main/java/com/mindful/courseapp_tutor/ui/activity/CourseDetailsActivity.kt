package com.mindful.courseapp_tutor.ui.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.mindful.courseapp_tutor.R
import com.mindful.courseapp_tutor.databinding.ActivityCourseDetailsBinding
import com.mindful.courseapp_tutor.ui.adapter.ChapterListAdapter

class CourseDetailsActivity : AppCompatActivity() {

    lateinit var binding: ActivityCourseDetailsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_course_details)
        supportActionBar?.hide()
        binding.rvChapters.layoutManager = LinearLayoutManager(this)
        binding.rvChapters.adapter = ChapterListAdapter(this)

        binding.btAddChapter.setOnClickListener {
            startActivity(Intent(
                this, AddChapterActivity::class.java
            ))
        }
    }
}