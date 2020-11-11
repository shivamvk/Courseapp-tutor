package com.mindful.courseapp_tutor.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mindful.courseapp_tutor.databinding.ChapterItemLayoutBinding

class ChapterListAdapter(val context: Context): RecyclerView.Adapter<ChapterListAdapter.ViewHolder>() {

    class ViewHolder(val binding: ChapterItemLayoutBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(){

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
        holder.bind()

    override fun getItemCount(): Int = 10
}