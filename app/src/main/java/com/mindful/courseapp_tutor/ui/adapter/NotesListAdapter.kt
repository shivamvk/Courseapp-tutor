package com.mindful.courseapp_tutor.ui.adapter

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import android.webkit.MimeTypeMap
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.mindful.courseapp_tutor.databinding.NotesItemLayoutBinding
import com.mindful.networklibrary.BuildConfig
import com.mindful.networklibrary.model.notes.Data


class NotesListAdapter(val context: Context, val data: List<Data>): RecyclerView.Adapter<NotesListAdapter.ViewHolder>() {

    class ViewHolder(val binding: NotesItemLayoutBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(data: Data, context: Context) {
            binding.tvTitle.text = data.title
            binding.tvDescription.text = data.description
            binding.view.setOnClickListener {
                context.startActivity(getIntent(BuildConfig.AWSURL + data.file))
            }
        }

        fun getIntent(url: String): Intent{
            var intent: Intent? = null
            var ext = url.substring(url.lastIndexOf("."))
            if (ext == ".pdf") {
                intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            } else if (ext == ".doc" || ext == ".docx"){
                intent = Intent()
                intent.setAction(Intent.ACTION_VIEW)
                intent.setDataAndType(Uri.parse(url), "application/msword")
            }
            return intent!!
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = NotesItemLayoutBinding.inflate(
            LayoutInflater.from(parent.context),
            parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) =
        holder.bind(data[position], context)

    override fun getItemCount(): Int = data.size
}