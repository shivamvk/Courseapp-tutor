package com.mindful.courseapp_tutor.ui.activity

import android.Manifest
import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import com.google.gson.Gson
import com.mindful.courseapp_tutor.R
import com.mindful.courseapp_tutor.databinding.ActivityLiveVideoTeachBinding
import com.mindful.courseapp_tutor.di.CourseTutorApplication
import com.mindful.networklibrary.model.educloudlogin.EduCloudLoginResponse
import com.mindful.networklibrary.sharedprefs.PreferencesHelper
import io.agora.rtc.Constants
import io.agora.rtc.IRtcEngineEventHandler
import io.agora.rtc.RtcEngine
import io.agora.rtc.video.VideoCanvas
import io.agora.rtm.*
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import org.json.JSONObject
import java.io.IOException
import com.mindful.networklibrary.sharedprefs.PreferencesHelper.get
import javax.inject.Inject

class LiveVideoTeachActivity : AppCompatActivity(), RtmClientListener,
    RtmCallEventListener {

    private val PERMISSION_ALL_REQUEST_CODE: Int = 706
    lateinit var binding: ActivityLiveVideoTeachBinding
    var eduCloudToken = ""
    private var rtcEngine: RtcEngine? = null
    private var rtmClient: RtmClient? = null
    private var rtmCallManager: RtmCallManager? = null
    lateinit var chapterId: String
    private var localInvitation: LocalInvitation? = null
    @Inject
    lateinit var prefs: SharedPreferences
    private val rtcEventHandler = object : IRtcEngineEventHandler() {
        override fun onFirstRemoteVideoDecoded(uid: Int, width: Int, height: Int, elapsed: Int) {
            runOnUiThread {
//                setupRemoteVideo(uid)
            }
        }

        override fun onUserOffline(uid: Int, reason: Int) {
//            runOnUiThread { onRemoteUserLeft() }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (application as CourseTutorApplication).getDeps().inject(this)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_live_video_teach)
        Log.i("live session starting", "activity launched")
        supportActionBar?.hide()
        chapterId = intent.getStringExtra("chapterId")!!
        init()
    }
    // For devices running Android 10.0 or later, you also need to add the following permission:

    fun endcall(v: View) {
        finish()
    }

    fun init() {
        val PERMISSIONS = arrayOf(
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.CAMERA
        )
        if (!hasPermissions(this, *PERMISSIONS)) {
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL_REQUEST_CODE)
        } else {
            permissionGrantedInitEverything()
        }
    }

    private fun permissionGrantedInitEverything() {
        initAgoraEngineAndJoinChannel()
    }

    private fun initAgoraEngineAndJoinChannel() {
        joinChannelOnEduCloud()
        loginUserOnEduCloud()
        initializeAgoraEngine()
        setupLocalVideo()
        joinChannel()
    }

    private fun loginUserOnEduCloud() {
        var json = JSONObject("{\n" +
                "  \"userName\": \"${prefs[PreferencesHelper.SharedPrefKeys.USER_NAME.toString(), ""]}\",\n" +
                "  \"role\": \"host\",\n" +
                "  \"streamUuid\": \"${prefs[PreferencesHelper.SharedPrefKeys.USER_NAME.toString(), ""]}\",\n" +
                "  \"publishType\": 0\n" +
                "}")
        val url = "https://api.agora.io/scene/apps/${getString(R.string.agora_app_id)}/v1/rooms/${
            intent.getStringExtra(
                "chapterId"
            )
        }/users/${prefs[PreferencesHelper.SharedPrefKeys.USER_ID.toString(), ""]}/entry"
        var body = RequestBody.create("application/json".toMediaTypeOrNull(), json.toString())
        val request = Request.Builder()
            .post(body)
            .url(url)
            .build()
        val client = OkHttpClient()
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {}
            override fun onResponse(call: Call, response: Response) {
                val res = response.body?.string()
                res?.let { Log.i("agora response", "${url}\n${it}") }
                try {
                    eduCloudToken =
                        Gson().fromJson(res, EduCloudLoginResponse::class.java).data.user.userToken
                    Log.i("educloud", "login success $eduCloudToken")
                } catch (e: java.lang.Exception){
                    e.printStackTrace()
                    Log.i("educloud", "login failed")
                }
            }
        })
    }

    private fun joinChannelOnEduCloud() {
        var json = JSONObject(
            "{\n" +
                    "  \"roomName\": \"${intent.getStringExtra("chapterId")}\",\n" +
                    "  \"roleConfig\": {\n" +
                    "    \"host\": {\n" +
                    "      \"limit\": 1,\n" +
                    "      \"verifyType\": 0,\n" +
                    "      \"subscribe\": 0\n" +
                    "    },\n" +
                    "    \"audience\": {\n" +
                    "      \"limit\": 16,\n" +
                    "      \"verifyType\": 0,\n" +
                    "      \"subscribe\": 0\n" +
                    "    }\n" +
                    "  }\n" +
                    "}"
        )
        runpost(
            "https://api.agora.io/scene/apps/${getString(R.string.agora_app_id)}/v1/rooms/${
                intent.getStringExtra(
                    "chapterId"
                )
            }/config", json
        )
    }

    private fun setupLocalVideo() {
        rtcEngine!!.enableVideo()
        val surfaceView = RtcEngine.CreateRendererView(baseContext)
        surfaceView.setZOrderMediaOverlay(true)
        binding.localVideoViewContainer.addView(surfaceView)
        rtcEngine!!.setupLocalVideo(VideoCanvas(surfaceView, VideoCanvas.RENDER_MODE_FIT, 0))
    }

    //TODO change channel name to appropriate channel coming from backend
    private fun joinChannel() {
        rtcEngine!!.joinChannel(null, intent.getStringExtra("chapterId"), "Extra Optional data", 0)
        rtcEngine!!.setChannelProfile(Constants.CHANNEL_PROFILE_LIVE_BROADCASTING)
        rtcEngine!!.setClientRole(Constants.CLIENT_ROLE_BROADCASTER)
        Log.i("cahnnel join", "success")
    }

    private fun initializeAgoraEngine() {
        try {
            rtcEngine =
                RtcEngine.create(baseContext, getString(R.string.agora_app_id), rtcEventHandler)
//            rtmClient =
//                RtmClient.createInstance(baseContext, getString(R.string.agora_app_id), this)
//            rtmClient?.login(
//                null,
//                "tutor", //TODO Replace this with user id
//                object : ResultCallback<Void> {
//                    override fun onSuccess(p0: Void?) {
//                        Log.e("rtm client login", "${p0.toString()} ########")
//                    }
//
//                    override fun onFailure(p0: ErrorInfo?) {
//                        Log.e("rtm client login", "${p0.toString()} ########")
//                    }
//                })
//            rtmCallManager = rtmClient?.rtmCallManager
//            rtmCallManager?.setEventListener(this)
//            localInvitation =
//                rtmCallManager?.createLocalInvitation(intent.getStringExtra("calleeId"))
//            rtmCallManager?.sendLocalInvitation(localInvitation, object : ResultCallback<Void> {
//                override fun onSuccess(p0: Void?) {
//                    Log.e("rtm client login", "${p0.toString()} ########")
//                }
//
//                override fun onFailure(p0: ErrorInfo?) {
//                    Log.e("rtm client login", "${p0.toString()} ########")
//                }
//            })
        } catch (e: Exception) {
            Log.e("VideoCallActivity", Log.getStackTraceString(e))
            throw RuntimeException(
                "NEED TO check rtc sdk init fatal error:\n" + Log.getStackTraceString(
                    e
                )
            )
        }
    }

    fun run(url: String) {
        val request = Request.Builder()
            .url(url)
            .build()
        val client = OkHttpClient()
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {}
            override fun onResponse(call: Call, response: Response) {
                response.body?.string()?.let { Log.i("agora response", it) }
            }
        })
    }

    fun runput(url: String, json: JSONObject){
        var body = RequestBody.create("application/json".toMediaTypeOrNull(), json.toString())
        val request = Request.Builder()
            .put(body)
            .url(url)
            .build()
        val client = OkHttpClient()
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {}
            override fun onResponse(call: Call, response: Response) {
                response.body?.string()?.let { Log.i("agora response", "${url}\n${it}") }
            }
        })
    }

    fun runpost(url: String, json: JSONObject) {
        var body = RequestBody.create("application/json".toMediaTypeOrNull(), json.toString())
        val request = Request.Builder()
            .post(body)
            .url(url)
            .build()
        val client = OkHttpClient()
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {}
            override fun onResponse(call: Call, response: Response) {
                response.body?.string()?.let { Log.i("agora response", "${url}\n${it}") }
            }
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        rtcEngine!!.leaveChannel()
        RtcEngine.destroy()
        rtcEngine = null
        rtmClient?.logout(null)
    }

    fun hasPermissions(context: Context, vararg permissions: String): Boolean = permissions.all {
        ActivityCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
    }

    override fun onConnectionStateChanged(p0: Int, p1: Int) {

    }

    override fun onMessageReceived(p0: RtmMessage?, p1: String?) {
    }

    override fun onTokenExpired() {
    }

    override fun onPeersOnlineStatusChanged(p0: MutableMap<String, Int>?) {
    }

    override fun onLocalInvitationReceivedByPeer(p0: LocalInvitation?) {
    }

    override fun onLocalInvitationAccepted(p0: LocalInvitation?, p1: String?) {
    }

    override fun onLocalInvitationRefused(p0: LocalInvitation?, p1: String?) {
    }

    override fun onLocalInvitationCanceled(p0: LocalInvitation?) {
    }

    override fun onLocalInvitationFailure(p0: LocalInvitation?, p1: Int) {
    }

    override fun onRemoteInvitationReceived(p0: RemoteInvitation?) {
    }

    override fun onRemoteInvitationAccepted(p0: RemoteInvitation?) {
    }

    override fun onRemoteInvitationRefused(p0: RemoteInvitation?) {
    }

    override fun onRemoteInvitationCanceled(p0: RemoteInvitation?) {
    }

    override fun onRemoteInvitationFailure(p0: RemoteInvitation?, p1: Int) {
    }
}