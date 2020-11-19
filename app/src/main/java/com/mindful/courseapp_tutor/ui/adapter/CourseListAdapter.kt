package com.mindful.courseapp_tutor.ui.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.mindful.courseapp_tutor.databinding.CourseHomeItemLayoutBinding
import com.mindful.courseapp_tutor.ui.activity.CourseDetailsActivity
import com.mindful.networklibrary.BuildConfig
import com.mindful.networklibrary.model.courses.Data

class CourseListAdapter(val context: Context, val data: List<Data>): RecyclerView.Adapter<CourseListAdapter.ViewHolder>() {


    class ViewHolder(val binding: CourseHomeItemLayoutBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(context: Context, data: Data){
            binding.container.setOnClickListener{
                context.startActivity(Intent(
                    context, CourseDetailsActivity::class.java
                ).putExtra("course", data))
            }
            binding.title.text = data.name
            binding.category.text = data.category.name
            Glide.with(context)
                .load(BuildConfig.AWSURL + data.image)
                .into(binding.image)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = CourseHomeItemLayoutBinding.inflate(
            LayoutInflater.from(parent.context),
            parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) =
        holder.bind(context, data[position])

    override fun getItemCount(): Int = data.size
}