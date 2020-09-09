package com.example.android.architecture.blueprints.todoapp.data.source.remote.model

data class ApiTaskModel(
        val id: String?,
        val user: String?,
        val title: String?,
        val description: String?,
        val completed: Boolean?,
        val priority: String?
)

//{
//    "idInternal" : 1,
//    "id" : "123",
//    "user" : "user1",
//    "title" : "Spring cleaning",
//    "description" : "Remember to scrub the bathroom floors.",
//    "completed" : false,
//    "priority" : "HIGH"
//}