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
import com.example.android.architecture.blueprints.todoapp.data.source.remote.rest.TasksApi

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
        return try {
            val tasks = tasksApi.getAllTasks(USER_ID).mapNotNull { TasksConverter.convertApiTaskToLocalTaskModel(it) }
            Success(tasks)
        } catch (exception: Exception) {
            Error(exception)
        }
    }

    override suspend fun getTask(taskId: String): Result<Task> {
        return try {
            val task = tasksApi.getAllTasks(USER_ID).mapNotNull { TasksConverter.convertApiTaskToLocalTaskModel(it) }.findLast { it.id == taskId }
            return if (task != null) {
                Success(task)
            } else {
                Error(Exception("Task not found"))
            }
        } catch (exception: Exception) {
            Error(exception)
        }
    }


    override suspend fun saveTask(task: Task) {
        try {
            val apiTaskModel = TasksConverter.convertLocalTaskToApiTaskModel(task, USER_ID)
            tasksApi.addTask(USER_ID, listOf(apiTaskModel))
        } catch (exception: Exception) {
            // handle error
        }
    }

    override suspend fun saveTasks(tasks: List<Task>) {
        try {
            val apiTaskModels = tasks.map { TasksConverter.convertLocalTaskToApiTaskModel(it, USER_ID) }
            tasksApi.addTask(USER_ID, apiTaskModels)
        } catch (exception: Exception) {
            // handle error
        }
    }

    override suspend fun completeTask(task: Task) {
        try {
            val apiTaskModel = TasksConverter.convertLocalTaskToApiTaskModel(task, USER_ID, true)
            tasksApi.addTask(USER_ID, listOf(apiTaskModel))
        } catch (exception: Exception) {
            // handle error
        }
    }

    override suspend fun completeTask(taskId: String) {
        // Not required for the remote data source
    }

    override suspend fun activateTask(task: Task) {
        try {
            val apiTaskModel = TasksConverter.convertLocalTaskToApiTaskModel(task, USER_ID, false)
            tasksApi.addTask(USER_ID, listOf(apiTaskModel))
        } catch (exception: Exception) {
            // handle error
        }
    }

    override suspend fun activateTask(taskId: String) {
        // Not required for the remote data source
    }

    override suspend fun clearCompletedTasks() {
        try {
            val tasks = tasksApi.getAllTasks(USER_ID).mapNotNull { TasksConverter.convertApiTaskToLocalTaskModel(it) }
            val taskIdsToDelete = tasks.filter { it.isCompleted }.joinToString { it.id }
            tasksApi.deleteTask(USER_ID, taskIdsToDelete)
        } catch (exception: Exception) {
            // handle error
        }
    }

    override suspend fun deleteAllTasks() {
        try {
            val tasks = tasksApi.getAllTasks(USER_ID).mapNotNull { TasksConverter.convertApiTaskToLocalTaskModel(it) }
            val taskIdsToDelete = tasks.joinToString { it.id }
            tasksApi.deleteTask(USER_ID, taskIdsToDelete)
        } catch (exception: Exception) {
            // handle error
        }

    }

    override suspend fun deleteTask(taskId: String) {
        try {
            tasksApi.deleteTask(USER_ID, taskId)
        } catch (exception: Exception) {
            // handle error
        }
    }
}

/**
 * This is a hardcoded ID that should not be used in prod.
 * Instead, a unique ID should be used based on device and/or user credentials
 */
private const val USER_ID = "user2"