package com.mindful.networklibrary.model

import java.io.Serializable

data class UtilModel(
    var status: Int? = 200,
    var message: String? = "",
    var errors: Boolean? = false
): BaseModel, Serializable