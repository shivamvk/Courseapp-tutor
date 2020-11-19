package com.mindful.networklibrary.api

object ApiRoutes {
    fun getChaptersByCourseId(_id: String): String = "service/bysalon/${_id}"
    fun updateChapter(id: String): String = "service/update/${id}"
    fun getNotes(id: String): String = "note/bychapter/${id}"
    val addChapter: String = "service/create"
    val getCourses: String = "salon"
    val login: String = "auth/login"
    val addNote: String = "note"
}