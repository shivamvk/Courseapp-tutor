package com.mindful.courseapp_tutor.ui.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.mindful.courseapp_tutor.R
import com.mindful.courseapp_tutor.databinding.ActivityCourseDetailsBinding
import com.mindful.courseapp_tutor.di.CourseTutorApplication
import com.mindful.courseapp_tutor.ui.adapter.ChapterListAdapter
import com.mindful.networklibrary.api.ApiManager
import com.mindful.networklibrary.api.ApiManagerListener
import com.mindful.networklibrary.api.ApiRoutes
import com.mindful.networklibrary.api.ApiService
import com.mindful.networklibrary.model.BaseModel
import com.mindful.networklibrary.model.UtilModel
import com.mindful.networklibrary.model.chapters.ChaptersResponse
import com.mindful.networklibrary.model.courses.Data
import javax.inject.Inject

class CourseDetailsActivity : AppCompatActivity(), ApiManagerListener {

    lateinit var binding: ActivityCourseDetailsBinding
    lateinit var course: Data
    @Inject lateinit var apiService: ApiService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (application as CourseTutorApplication).getDeps().inject(this)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_course_details)
        supportActionBar?.hide()
        course = intent.getSerializableExtra("course") as Data

        binding.tvName.text = course.name
        binding.tvCategory.text = course.category.name
        binding.tvTutor.text = course.manager.full_name
        binding.tvInstitute.text = course.owner.full_name
        binding.tvDescription.text = course.description
        binding.tvFees.text = "\u20b9 ${course.price}"
        binding.tvType.text = course.type

        binding.rvChapters.layoutManager = LinearLayoutManager(this)
        binding.btAddChapter.setOnClickListener {
            startActivity(Intent(
                this, AddChapterActivity::class.java
            ).putExtra("courseType", course.type)
                .putExtra("courseId", course._id))
        }
    }

    override fun onResume() {
        super.onResume()
        ApiManager(
            ApiRoutes.getChaptersByCourseId(course._id),
            apiService,
            UtilModel(),
            this
        ).doGETAPICall()
    }

    override fun onSuccess(dataModel: BaseModel?, response: String) {
        if (dataModel is UtilModel){
            Log.i("chapters response", "$response###")
            val data = Gson().fromJson(response, ChaptersResponse::class.java).data
            if (data.isNotEmpty()){
                binding.noItemLayout.visibility = View.GONE
                binding.rvChapters.adapter = ChapterListAdapter(this, data, course.type, course._id)
            }
        }
    }

    override fun onFailure(dataModel: BaseModel?, e: Throwable) {
        super.onFailure(dataModel, e)
        e.printStackTrace()
        Toast.makeText(this, e.localizedMessage, Toast.LENGTH_SHORT).show()
    }
}