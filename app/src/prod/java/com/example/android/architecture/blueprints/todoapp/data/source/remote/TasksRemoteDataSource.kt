/*
 * Copyright (C) 2019 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.android.architecture.blueprints.todoapp.data.source.remote

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import com.example.android.architecture.blueprints.todoapp.ServiceLocator
import com.example.android.architecture.blueprints.todoapp.data.Result
import com.example.android.architecture.blueprints.todoapp.data.Result.Error
import com.example.android.architecture.blueprints.todoapp.data.Result.Success
import com.example.android.architecture.blueprints.todoapp.data.Task
import com.example.android.architecture.blueprints.todoapp.data.source.TasksDataSource
import com.example.android.architecture.blueprints.todoapp.data.source.remote.converter.TasksConverter
import com.example.android.architecture.blueprints.todoapp.data.source.remote.model.ApiTaskModel
import com.example.android.architecture.blueprints.todoapp.data.source.remote.rest.TasksApi
import kotlinx.coroutines.delay
import retrofit2.Callback

/**
 * Implementation of the data source that adds a latency simulating network.
 */
class TasksRemoteDataSource(private val tasksApi: TasksApi = ServiceLocator.buildService(TasksApi::class.java)) : TasksDataSource {

    private val observableTasks = MutableLiveData<Result<List<Task>>>()

    override suspend fun refreshTasks() {
        observableTasks.value = getTasks()
    }

    override suspend fun refreshTask(taskId: String) {
        refreshTasks()
    }

    override fun observeTasks(): LiveData<Result<List<Task>>> {
        return observableTasks
    }

    override fun observeTask(taskId: String): LiveData<Result<Task>> {
        return observableTasks.map { tasks ->
            when (tasks) {
                is Result.Loading -> Result.Loading
                is Error -> Error(tasks.exception)
                is Success -> {
                    val task = tasks.data.firstOrNull() { it.id == taskId }
                            ?: return@map Error(Exception("Not found"))
                    Success(task)
                }
            }
        }
    }

    override suspend fun getTasks(): Result<List<Task>> {
        val tasks = tasksApi.getAllTasks(USER_ID).mapNotNull { TasksConverter.convertApiTaskToLocalTaskModel(it) }
        return Success(tasks)
    }

    override suspend fun getTask(taskId: String): Result<Task> {
        // Simulate network by delaying the execution.
//        delay(SERVICE_LATENCY_IN_MILLIS)
//        TASKS_SERVICE_DATA[taskId]?.let {
//            return Success(it)
//        }
        return Error(Exception("Task not found"))
    }


    override suspend fun saveTask(task: Task) {
        val apiTaskModel = TasksConverter.convertLocalTaskToApiTaskModel(task, USER_ID)
        tasksApi.addTask(USER_ID, listOf(apiTaskModel))
    }

    override suspend fun completeTask(task: Task) {
        val apiTaskModel = TasksConverter.convertLocalTaskToApiTaskModel(task, USER_ID, true)
        tasksApi.addTask(USER_ID, listOf(apiTaskModel))
    }

    override suspend fun completeTask(taskId: String) {
        // Not required for the remote data source
    }

    override suspend fun activateTask(task: Task) {
        val apiTaskModel = TasksConverter.convertLocalTaskToApiTaskModel(task, USER_ID, false)
        tasksApi.addTask(USER_ID, listOf(apiTaskModel))
    }

    override suspend fun activateTask(taskId: String) {
        // Not required for the remote data source
    }

    override suspend fun clearCompletedTasks() {
//        TASKS_SERVICE_DATA = TASKS_SERVICE_DATA.filterValues {
//            !it.isCompleted
//        } as LinkedHashMap<String, Task>
    }

    override suspend fun deleteAllTasks() {
        // TASKS_SERVICE_DATA.clear()
        val tasks = tasksApi.getAllTasks(USER_ID).mapNotNull { TasksConverter.convertApiTaskToLocalTaskModel(it) }
        // todo delete all

    }

    override suspend fun deleteTask(taskId: String) {
        tasksApi.deleteTask(USER_ID, taskId)
    }
}

private const val USER_ID = "user2"