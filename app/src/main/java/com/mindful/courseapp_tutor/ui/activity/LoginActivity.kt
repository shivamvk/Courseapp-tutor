package com.mindful.courseapp_tutor.ui.activity

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.mindful.courseapp_tutor.R
import com.mindful.courseapp_tutor.databinding.ActivityLoginBinding
import com.mindful.courseapp_tutor.di.CourseTutorApplication
import com.mindful.networklibrary.api.ApiManager
import com.mindful.networklibrary.api.ApiManagerListener
import com.mindful.networklibrary.api.ApiRoutes
import com.mindful.networklibrary.api.ApiService
import com.mindful.networklibrary.model.BaseModel
import com.mindful.networklibrary.model.UtilModel
import com.mindful.networklibrary.model.login.Data
import com.mindful.networklibrary.model.login.LoginResponse
import com.mindful.networklibrary.sharedprefs.PreferencesHelper
import javax.inject.Inject
import com.mindful.networklibrary.sharedprefs.PreferencesHelper.set

class LoginActivity : AppCompatActivity(), ApiManagerListener {

    lateinit var binding: ActivityLoginBinding
    @Inject lateinit var apiService: ApiService
    @Inject lateinit var prefs: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (application as CourseTutorApplication).getDeps().inject(this)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login)
        supportActionBar?.hide()
        binding.progressBar.visibility = View.GONE
        binding.btSignIn.setOnClickListener {
            handleSigninClick()
        }
    }

    private fun handleSigninClick() {
        if (validateInputs()){
            binding.btSignIn.visibility = View.GONE
            binding.progressBar.visibility = View.VISIBLE
            ApiManager(
                ApiRoutes.login,
                apiService,
                LoginResponse(),
                this
            ).doPOSTAPICall(JsonObject()
                .apply {
                    addProperty("email", binding.etEmail.editText?.text.toString())
                    addProperty("password", binding.etPassword.editText?.text.toString())
                })
        }
    }

    private fun validateInputs(): Boolean {
        if (binding.etEmail.editText?.text.toString().isEmpty() ||
                binding.etPassword.editText?.text.toString().isEmpty()){
            Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }

    override fun onSuccess(dataModel: BaseModel?, response: String) {
        if (dataModel is LoginResponse){
            Log.i("login response", response)
            binding.btSignIn.visibility = View.VISIBLE
            binding.progressBar.visibility = View.GONE
            val model = Gson().fromJson(response, LoginResponse::class.java)
            if (model.errors!!){
                Toast.makeText(this, model.message, Toast.LENGTH_SHORT).show()
            } else {
                handleLoginSuccess(model.data!!)
            }
        }
    }

    private fun handleLoginSuccess(user: Data) {
        prefs[PreferencesHelper.SharedPrefKeys.USER_TOKEN.toString()] = user.token
        prefs[PreferencesHelper.SharedPrefKeys.USER_ID.toString()] = user.user._id
        prefs[PreferencesHelper.SharedPrefKeys.USER_EMAIL.toString()] = user.user.email
        prefs[PreferencesHelper.SharedPrefKeys.USER_NAME.toString()] = user.user.full_name
        prefs[PreferencesHelper.SharedPrefKeys.USER_PHONE.toString()] = user.user.mobile_number
        prefs[PreferencesHelper.SharedPrefKeys.USER_PROFILE_PICTURE.toString()] = user.user.profile_picture
        prefs[PreferencesHelper.SharedPrefKeys.USER_ROLE.toString()] = user.user.role
        //Dagger is init again here because token is refreshed
        (application as CourseTutorApplication).initDagger()
        startActivity(
            Intent(
            this, MainActivity::class.java
        )
        )
    }
}