package com.mindful.networklibrary.api

import com.google.gson.JsonObject
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.http.*
import rx.Observable

interface ApiService {
    @POST
    fun doPostApiCall(@Url url: String, @Body jsonObject: JsonObject): Observable<ResponseBody>

    @GET
    fun doGetApiCall(@Url url: String): Observable<ResponseBody>

    @Multipart
    @POST
    fun doPostApiMultipartCall(@Url url: String, @Part params: List<MultipartBody.Part>): Observable<ResponseBody>

    @PUT
    fun doPutApiCall(@Url mUrl: String, @Body jsonObject: JsonObject): Observable<ResponseBody>
}