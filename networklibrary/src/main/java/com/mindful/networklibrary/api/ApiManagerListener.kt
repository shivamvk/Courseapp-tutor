package com.mindful.networklibrary.api

import com.mindful.networklibrary.model.BaseModel

interface ApiManagerListener {
    fun onSuccess(dataModel: BaseModel?, response: String)
    fun onFailure(dataModel: BaseModel?, e: Throwable) {}
}