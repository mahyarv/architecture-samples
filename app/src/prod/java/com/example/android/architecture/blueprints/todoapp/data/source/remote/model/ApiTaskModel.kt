package com.example.android.architecture.blueprints.todoapp.data.source.remote.model

data class ApiTaskModel(
        val id: String?,
        val user: String?,
        val title: String?,
        val description: String?,
        val completed: Boolean?,
        val priority: String?
)
