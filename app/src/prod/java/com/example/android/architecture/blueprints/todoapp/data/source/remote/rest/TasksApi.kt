package com.example.android.architecture.blueprints.todoapp.data.source.remote.rest

import com.example.android.architecture.blueprints.todoapp.data.source.remote.model.ApiTaskModel
import retrofit2.Call
import retrofit2.http.*

interface TasksApi {

    @GET("/todo/users/{userid}/tasks")
    suspend fun getAllTasks(@Path("userid") userId: String): List<ApiTaskModel>

    @PUT("/todo/users/{userid}/tasks")
    suspend fun addTask(@Path("userid") userId: String, @Body tasks: List<ApiTaskModel>)

    @DELETE("/todo/users/{userid}/tasks/{taskid}")
    suspend fun deleteTask(@Path("userid") userId: String, @Path("taskid") taskId: String)

}