package com.mindful.courseapp_tutor.ui.activity

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import com.mindful.courseapp_tutor.R
import com.mindful.courseapp_tutor.databinding.ActivityLiveVideoTeachBinding
import io.agora.rtc.Constants
import io.agora.rtc.IRtcEngineEventHandler
import io.agora.rtc.RtcEngine
import io.agora.rtc.video.VideoCanvas
import io.agora.rtm.*

class LiveVideoTeachActivity: AppCompatActivity(), RtmClientListener,
    RtmCallEventListener {

    private val PERMISSION_ALL_REQUEST_CODE: Int = 706
    lateinit var binding: ActivityLiveVideoTeachBinding
    private var rtcEngine: RtcEngine? = null
    private var rtmClient: RtmClient? = null
    private var rtmCallManager: RtmCallManager? = null
    private var localInvitation: LocalInvitation? = null
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
        binding = DataBindingUtil.setContentView(this, R.layout.activity_live_video_teach)
        Log.i("live session starting", "activity launched")
        supportActionBar?.hide()
        init()
    }
    // For devices running Android 10.0 or later, you also need to add the following permission:

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
        initializeAgoraEngine()
        setupLocalVideo()
        joinChannel()
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
        rtcEngine!!.joinChannel(null, "asdfghjkl", "Extra Optional data", 0)
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