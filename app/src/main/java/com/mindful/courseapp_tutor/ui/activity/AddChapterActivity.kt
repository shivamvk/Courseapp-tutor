package com.mindful.courseapp_tutor.ui.activity

import android.app.Activity
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.util.Util
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.mindful.courseapp_tutor.R
import com.mindful.courseapp_tutor.databinding.ActivityAddChapterBinding
import com.mindful.courseapp_tutor.di.CourseTutorApplication
import com.mindful.courseapp_tutor.ui.adapter.NotesListAdapter
import com.mindful.networklibrary.api.ApiManager
import com.mindful.networklibrary.api.ApiManagerListener
import com.mindful.networklibrary.api.ApiRoutes
import com.mindful.networklibrary.api.ApiService
import com.mindful.networklibrary.model.BaseModel
import com.mindful.networklibrary.model.UtilModel
import com.mindful.networklibrary.model.chapters.Data
import com.mindful.networklibrary.model.notes.NotesResponse
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

class AddChapterActivity : AppCompatActivity(), ApiManagerListener {

    lateinit var binding: ActivityAddChapterBinding
    var calendar = Calendar.getInstance()
    var displayTime: String = ""
    var courseType: String = ""
    var courseId: String = ""
    var selectedDate: String = ""
    @Inject lateinit var apiService: ApiService
    lateinit var chapter: Data
    var editing: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (application as CourseTutorApplication).getDeps().inject(this)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_add_chapter)
        supportActionBar?.hide()
        courseType = intent.getStringExtra("courseType")!!
        courseId = intent.getStringExtra("courseId")!!
        if (intent.getSerializableExtra("chapter") != null) {
            chapter = intent.getSerializableExtra("chapter") as Data
            editing = true
            initEditFlow()
        } else {
            binding.addNotesSection.visibility = View.GONE
        }
        if (courseType == "Live"){
            binding.etVideoLink.visibility = View.GONE
            binding.tvDateTime.setOnClickListener {
                pickDate()
            }
        } else {
            binding.tvDateTime.visibility = View.GONE
            binding.dateTimeHeading.visibility = View.GONE
        }

        binding.btSave.setOnClickListener {
            handleSaveClick()
        }

        binding.addNotes.setOnClickListener {
            startActivity(
                Intent(this, AddNotesActivity::class.java).putExtra("chapterId", chapter._id)
            )
        }
    }

    override fun onResume() {
        super.onResume()
        if (intent.getSerializableExtra("chapter") != null){
            Log.i("fetching notes", chapter._id)
            ApiManager(
                ApiRoutes.getNotes(chapter._id),
                apiService,
                NotesResponse(),
                this
            ).doGETAPICall()
        }
    }

    private fun initEditFlow() {
        binding.etName.editText?.setText(chapter.name)
        binding.etDetails.editText?.setText(chapter.details)
        binding.switchActive.isChecked = chapter.active
        if (courseType == "Recorded") {
            binding.etVideoLink.editText?.setText(chapter.video)
        } else {
            val iso = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
            val date: Date? = gmtToLocalDate(iso.parse(chapter.dateTime))
            val month = SimpleDateFormat("dd MMMM hh:mm a").format(date)
            val day = SimpleDateFormat("EEEE").format(date)
            binding.tvDateTime.text = "${day}, ${month}"
            selectedDate = chapter.dateTime
        }
        binding.addNotesSection.visibility = View.VISIBLE
    }

    fun gmtToLocalDate(date: Date): Date? {
        val timeZone = Calendar.getInstance().timeZone.id
        return Date(
            date.time + TimeZone.getTimeZone(timeZone).getOffset(date.time)
        )
    }

    private fun handleSaveClick() {
        if (validateInputs()){
            binding.btSave.visibility = View.GONE
            binding.progressBar.visibility = View.VISIBLE
            var jsonObject = JsonObject().apply {
                addProperty("salon", courseId)
                addProperty("details", binding.etDetails.editText?.text.toString())
                addProperty("name", binding.etName.editText?.text.toString())
                addProperty("active", binding.switchActive.isChecked)
            }
            if (courseType == "Live"){
                jsonObject.addProperty("dateTime", selectedDate)
            } else {
                jsonObject.addProperty("video", binding.etVideoLink.editText?.text.toString())
            }
            if (!editing) {
                ApiManager(
                    ApiRoutes.addChapter,
                    apiService,
                    UtilModel(),
                    this
                ).doPOSTAPICall(jsonObject)
            } else {
                ApiManager(
                    ApiRoutes.updateChapter(chapter._id),
                    apiService,
                    UtilModel(),
                    this
                ).doPUTAPICall(jsonObject)
            }
        }
    }

    private fun validateInputs(): Boolean {
        if (binding.etName.editText?.text.toString().isEmpty() ||
                binding.etDetails.editText?.text.toString().isEmpty()){
            Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show()
            return false
        }
        if (courseType == "Live" && binding.tvDateTime.text == "Select"){
            Toast.makeText(this, "Please select a valid date and time for your live session", Toast.LENGTH_SHORT).show()
            return false
        }
        if (courseType == "Recorded" && binding.etVideoLink.editText?.text.isNullOrEmpty()) {
            Toast.makeText(this, "Please provide a valid video link", Toast.LENGTH_SHORT).show()
            return false
        }
        return true
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
                var isosdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm'Z'")
                isosdf.timeZone = TimeZone.getTimeZone("UTC")
                selectedDate = isosdf.format(calendar.time)
                binding.tvDateTime.text = displayTime
            },
            calendar.get(Calendar.HOUR_OF_DAY),
            calendar.get(Calendar.MINUTE),
            false
        )
        dialog.show()
    }

    override fun onSuccess(dataModel: BaseModel?, response: String) {
        if (dataModel is UtilModel){
            Log.i("add chapter response", response)
            finish()
        } else if (dataModel is NotesResponse) {
            var data = Gson().fromJson(response, NotesResponse::class.java).data
            binding.rvNotes.layoutManager = LinearLayoutManager(this)
            binding.rvNotes.adapter = NotesListAdapter(this, data!!)
        }
    }

    override fun onFailure(dataModel: BaseModel?, e: Throwable) {
        super.onFailure(dataModel, e)
        e.printStackTrace()
        Toast.makeText(this, e.localizedMessage, Toast.LENGTH_SHORT).show()
    }
}