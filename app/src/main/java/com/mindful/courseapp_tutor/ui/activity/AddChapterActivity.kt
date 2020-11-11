package com.mindful.courseapp_tutor.ui.activity

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.mindful.courseapp_tutor.R
import com.mindful.courseapp_tutor.databinding.ActivityAddChapterBinding
import java.text.SimpleDateFormat
import java.util.*

class AddChapterActivity : AppCompatActivity() {

    lateinit var binding: ActivityAddChapterBinding
    var calendar = Calendar.getInstance()
    var displayTime: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_add_chapter)
        supportActionBar?.hide()
        binding.tvDateTime.setOnClickListener {
            pickDate()
        }
    }

    private fun pickDate() {
        var dialog = DatePickerDialog(
            this,
            DatePickerDialog.OnDateSetListener { view, year, month, day ->
                //TODO make an iso from these
                calendar.set(Calendar.YEAR, year)
                calendar.set(Calendar.MONTH, month)
                calendar.set(Calendar.DAY_OF_MONTH, day)
                displayTime = SimpleDateFormat("dd MMMM").format(calendar.time)
                pickTime()
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        dialog.show()
    }

    private fun pickTime() {
        var dialog = TimePickerDialog(
            this,
            TimePickerDialog.OnTimeSetListener { timePicker, hour, minute ->
                //TODO make an iso from these
                calendar.set(Calendar.HOUR_OF_DAY, hour)
                calendar.set(Calendar.MINUTE, minute)
                displayTime += ", " +  SimpleDateFormat("hh:mm a").format(calendar.time)
                binding.tvDateTime.text = displayTime
            },
            calendar.get(Calendar.HOUR_OF_DAY),
            calendar.get(Calendar.MINUTE),
            false
        )
        dialog.show()
    }
}