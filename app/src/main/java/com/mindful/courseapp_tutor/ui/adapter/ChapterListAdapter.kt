package com.mindful.courseapp_tutor.ui.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mindful.courseapp_tutor.databinding.ChapterItemLayoutBinding
import com.mindful.courseapp_tutor.ui.activity.AddChapterActivity
import com.mindful.courseapp_tutor.ui.activity.LiveVideoTeachActivity
import com.mindful.networklibrary.model.chapters.Data
import java.text.SimpleDateFormat
import java.util.*

class ChapterListAdapter(
    val context: Context,
    val data: List<Data>,
    val courseType: String,
    val courseId: String
): RecyclerView.Adapter<ChapterListAdapter.ViewHolder>() {

    class ViewHolder(val binding: ChapterItemLayoutBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(context: Context, data: Data, courseType: String, courseId: String) {
            binding.tvName.text = data.name
            binding.edit.setOnClickListener {
                context.startActivity(
                    Intent(
                    context,
                    AddChapterActivity::class.java
                ).putExtra("courseType", courseType)
                        .putExtra("courseId", courseId)
                        .putExtra("chapter", data)
                )
            }
            if (courseType == "Recorded") {
                binding.tvLinkDate.text = data.video
                binding.startLive.visibility = View.GONE
            } else {
                val iso = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
                val date: Date? = gmtToLocalDate(iso.parse(data.dateTime))
                val month = SimpleDateFormat("dd MMMM hh:mm a").format(date)
                val day = SimpleDateFormat("EEEE").format(date)
                binding.tvLinkDate.text = "${day}, ${month}"
                binding.startLive.setOnClickListener {
                    context.startActivity(Intent(
                        context, LiveVideoTeachActivity::class.java
                    ).putExtra("chapterId", data._id)
                        .putExtra("courseId", courseId))
                }
            }
        }
        fun gmtToLocalDate(date: Date): Date? {
            val timeZone = Calendar.getInstance().timeZone.id
            return Date(
                date.time + TimeZone.getTimeZone(timeZone).getOffset(date.time)
            )
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ChapterItemLayoutBinding.inflate(
            LayoutInflater.from(parent.context),
            parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) =
        holder.bind(context, data[position], courseType, courseId)

    override fun getItemCount(): Int = data.size
}