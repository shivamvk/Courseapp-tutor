package com.mindful.courseapp_tutor.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.mindful.courseapp_tutor.R
import com.mindful.courseapp_tutor.databinding.FragmentHomeBinding
import com.mindful.courseapp_tutor.di.CourseTutorApplication
import com.mindful.courseapp_tutor.ui.activity.LiveVideoTeachActivity
import com.mindful.courseapp_tutor.ui.adapter.CourseListAdapter
import com.mindful.networklibrary.api.ApiManager
import com.mindful.networklibrary.api.ApiManagerListener
import com.mindful.networklibrary.api.ApiRoutes
import com.mindful.networklibrary.api.ApiService
import com.mindful.networklibrary.model.BaseModel
import com.mindful.networklibrary.model.courses.CoursesResponse
import com.mindful.networklibrary.model.courses.Data
import javax.inject.Inject

class HomeFragment: Fragment(), ApiManagerListener {

    lateinit var binding: FragmentHomeBinding
    @Inject lateinit var apiService: ApiService

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        (requireActivity().application as CourseTutorApplication).getDeps().inject(this)
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_home, container, false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.content.visibility = View.GONE
        binding.rvCourses.layoutManager = LinearLayoutManager(context)
        ApiManager(
            ApiRoutes.getCourses,
            apiService,
            CoursesResponse(),
            this
        ).doGETAPICall()
    }

    override fun onSuccess(dataModel: BaseModel?, response: String) {
        if (dataModel is CoursesResponse){
            Log.i("courses response", "$response####")
            binding.progressBar.visibility = View.GONE
            val model = Gson().fromJson(response, CoursesResponse::class.java).data
            if (model?.isEmpty()!!){
                binding.noItemLayout.visibility = View.VISIBLE
            } else {
                binding.content.visibility = View.VISIBLE
                handleCoursesData(model)
            }
        }
    }

    private fun handleCoursesData(list: List<Data>) {
        binding.rvCourses.adapter = context?.let { CourseListAdapter(it, list) }
    }

    override fun onFailure(dataModel: BaseModel?, e: Throwable) {
        super.onFailure(dataModel, e)
        e.printStackTrace()
        Toast.makeText(context, e.localizedMessage, Toast.LENGTH_SHORT).show()
    }

}