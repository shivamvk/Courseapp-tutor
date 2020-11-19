package com.mindful.networklibrary.api

import com.google.gson.JsonObject
import com.mindful.networklibrary.model.BaseModel
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import rx.Subscriber
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers

class ApiManager
    (
    private val mUrl: String,
    private val apiService: ApiService,
    private val dataModel: BaseModel?,
    private val apiListener: ApiManagerListener,
) {
    fun doPOSTAPICall(jsonObject: JsonObject) {
        apiService.doPostApiCall(mUrl, jsonObject)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : Subscriber<ResponseBody>() {
                override fun onCompleted() {
                }

                override fun onError(e: Throwable) {
                    apiListener.onFailure(dataModel, e)
                }

                override fun onNext(responseBody: ResponseBody?) {
                    if (responseBody == null) {
                        return
                    }
                    apiListener.onSuccess(dataModel, responseBody.string());
                }
            })
    }

    fun doPostAPIMultiPartCall(params: List<MultipartBody.Part>) {
        apiService.doPostApiMultipartCall(mUrl, params)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : Subscriber<ResponseBody>() {
                override fun onCompleted() {
                }

                override fun onError(e: Throwable) {
                    apiListener.onFailure(dataModel, e)
                }

                override fun onNext(responseBody: ResponseBody?) {
                    if (responseBody == null) {
                        return
                    }
                    apiListener.onSuccess(dataModel, responseBody.string());
                }
            })
    }

    fun doGETAPICall() {
        apiService.doGetApiCall(mUrl)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : Subscriber<ResponseBody?>() {
                override fun onCompleted() {
                }

                override fun onError(e: Throwable) {
                    apiListener.onFailure(dataModel, e)
                }

                override fun onNext(responseBody: ResponseBody?) {
                    if (responseBody == null) {
                        return
                    }
                    apiListener.onSuccess(dataModel, responseBody.string());
                }
            })
    }

    fun doPUTAPICall(jsonObject: JsonObject) {
        apiService.doPutApiCall(mUrl, jsonObject)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : Subscriber<ResponseBody>() {
                override fun onCompleted() {
                }

                override fun onError(e: Throwable) {
                    apiListener.onFailure(dataModel, e)
                }

                override fun onNext(responseBody: ResponseBody?) {
                    if (responseBody == null) {
                        return
                    }
                    apiListener.onSuccess(dataModel, responseBody.string());
                }
            })
    }

}