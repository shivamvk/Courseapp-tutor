package com.mindful.courseapp_tutor.ui.activity

import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.mindful.courseapp_tutor.R
import com.mindful.courseapp_tutor.databinding.ActivityAddNotesBinding
import com.mindful.courseapp_tutor.di.CourseTutorApplication
import com.mindful.networklibrary.api.ApiManager
import com.mindful.networklibrary.api.ApiManagerListener
import com.mindful.networklibrary.api.ApiRoutes.addNote
import com.mindful.networklibrary.api.ApiService
import com.mindful.networklibrary.model.BaseModel
import com.mindful.networklibrary.model.UtilModel
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import java.util.*
import javax.inject.Inject


class AddNotesActivity : AppCompatActivity(), ApiManagerListener {
    lateinit var binding: ActivityAddNotesBinding
    var chapterId: String? = ""
    var fileUri: String? = ""

    @Inject
    lateinit var apiService: ApiService
    var file: File? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (application as CourseTutorApplication).getDeps().inject(this)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_add_notes)
        supportActionBar!!.hide()
        chapterId = intent.getStringExtra("chapterId")
        binding.btSave.setOnClickListener { handleSaveClick() }
        binding.addNotes.setOnClickListener {
            val mimeTypes = arrayOf(
                "application/msword",
                "application/vnd.openxmlformats-officedocument.wordprocessingml.document",  // .doc & .docx
                "application/vnd.ms-powerpoint",
                "application/vnd.openxmlformats-officedocument.presentationml.presentation",  // .ppt & .pptx
                "application/vnd.ms-excel",
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",  // .xls & .xlsx
                "text/plain",
                "application/pdf"
            )
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.addCategory(Intent.CATEGORY_OPENABLE)
            intent.type = "*/*"
            intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes)
            startActivityForResult(
                intent, 948
            )
        }
    }

    fun getFileName(uri: Uri): String? {
        var result: String? = null
        if (uri.scheme == "content") {
            val cursor: Cursor? = contentResolver.query(uri, null, null, null, null)
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME))
                }
            } finally {
                cursor?.close()
            }
        }
        if (result == null) {
            result = uri.path
            val cut = result!!.lastIndexOf('/')
            if (cut != -1) {
                result = result.substring(cut + 1)
            }
        }
        return result
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 948 && resultCode == RESULT_OK) {
            try {
                val `in` = contentResolver.openInputStream(data!!.data!!)
                Log.i("selected file", getFileName(data?.data!!) + "csfcs")
                file = File.createTempFile("fsff", "." + getFileName(data?.data!!)?.split(".")!![1])
                val out: OutputStream = FileOutputStream(file)
                val buf = ByteArray(1024)
                var len: Int
                while (`in`!!.read(buf).also { len = it } > 0) {
                    out.write(buf, 0, len)
                }
                out.close()
                `in`.close()
                binding!!.tvFileName.visibility = View.VISIBLE
                binding!!.tvFileName.text = data.data!!.path
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    private fun handleSaveClick() {
        if (validateInputs()) {
            binding!!.content.visibility = View.GONE
            binding!!.progressBar.visibility = View.VISIBLE
            val params: MutableList<MultipartBody.Part> = ArrayList()
            val filep: MultipartBody.Part = MultipartBody.Part.createFormData(
                "file",
                file!!.name,
                file!!.asRequestBody("*/*".toMediaTypeOrNull())
            )
            val titlep: MultipartBody.Part = MultipartBody.Part.createFormData(
                "title", binding!!.etTitle.editText!!
                    .text.toString()
            )
            val descriptionp: MultipartBody.Part = MultipartBody.Part.createFormData(
                "description", binding!!.etDescription.editText!!
                    .text.toString()
            )
            val chapterp: MultipartBody.Part = MultipartBody.Part.createFormData(
                "chapter",
                chapterId!!
            )
            params.add(filep)
            params.add(titlep)
            params.add(descriptionp)
            params.add(chapterp)
            Log.i("add note req", file.toString())
            ApiManager(
                addNote,
                apiService!!,
                UtilModel(),
                this
            ).doPostAPIMultiPartCall(params)
        }
    }

    private fun validateInputs(): Boolean {
        if (binding!!.etTitle.editText!!.text.toString().isEmpty()
            || binding!!.etDescription.editText!!.text.toString().isEmpty()
            || file == null
        ) {
            Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }

    override fun onSuccess(dataModel: BaseModel?, response: String) {
        Log.i("add note", response)
        setResult(RESULT_OK)
        finish()
    }

    override fun onFailure(dataModel: BaseModel?, e: Throwable) {
        e.printStackTrace()
        Toast.makeText(this, e.localizedMessage, Toast.LENGTH_SHORT).show()
        binding!!.content.visibility = View.VISIBLE
        binding!!.progressBar.visibility = View.GONE
    }

    companion object {
        fun getRealPathFromUri(ctx: Context, uri: Uri?): String {
            val filePathColumn = arrayOf(MediaStore.Files.FileColumns.DATA)
            val cursor = ctx.contentResolver.query(
                uri!!, filePathColumn,
                null, null, null
            )
            var picturePath = ""
            if (cursor != null) {
                cursor.moveToFirst()
                val columnIndex = cursor.getColumnIndex(filePathColumn[0])
                picturePath = cursor.getString(columnIndex)
                Log.e("", "picturePath : $picturePath")
                cursor.close()
            }
            return picturePath
        }
    }
}