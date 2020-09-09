package com.example.android.architecture.blueprints.todoapp.data.source.remote.rest

import com.example.android.architecture.blueprints.todoapp.data.source.remote.model.ApiTaskModel
import retrofit2.Call
import retrofit2.http.*

/**
 * API Docs: https://tunein-android-challenge.herokuapp.com/docs/api.html
 * This API allows us to sync the local Tasks to the Cloud on Heroku
 */
interface TasksApi {

    /**
     * Gets all tasks belonging to a user with userId
     */
    @GET("/todo/users/{userid}/tasks")
    suspend fun getAllTasks(@Path("userid") userId: String): List<ApiTaskModel>

    /**
     * Adds a list of task(s) for the given userId
     */
    @PUT("/todo/users/{userid}/tasks")
    suspend fun addTask(@Path("userid") userId: String, @Body tasks: List<ApiTaskModel>)

    /**
     * Deletes a task with the given taskId for the given userId
     */
    @DELETE("/todo/users/{userid}/tasks/{taskid}")
    suspend fun deleteTask(@Path("userid") userId: String, @Path("taskid") taskId: String)

}