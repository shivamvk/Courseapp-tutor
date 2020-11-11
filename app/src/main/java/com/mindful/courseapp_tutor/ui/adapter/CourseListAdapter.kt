package com.mindful.courseapp_tutor.ui.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mindful.courseapp_tutor.databinding.CourseHomeItemLayoutBinding
import com.mindful.courseapp_tutor.ui.activity.CourseDetailsActivity

class CourseListAdapter(val context: Context): RecyclerView.Adapter<CourseListAdapter.ViewHolder>() {


    class ViewHolder(val binding: CourseHomeItemLayoutBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(context: Context){
            binding.container.setOnClickListener{
                context.startActivity(Intent(
                    context, CourseDetailsActivity::class.java
                ))
            }
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
        holder.bind(context)

    override fun getItemCount(): Int = 10
}