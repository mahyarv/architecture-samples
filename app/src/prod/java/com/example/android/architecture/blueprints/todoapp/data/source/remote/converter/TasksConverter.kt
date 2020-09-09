package com.example.android.architecture.blueprints.todoapp.data.source.remote.converter

import com.example.android.architecture.blueprints.todoapp.data.PriorityConverter
import com.example.android.architecture.blueprints.todoapp.data.Task
import com.example.android.architecture.blueprints.todoapp.data.source.remote.model.ApiTaskModel

object TasksConverter {

    private val priorityConverter: PriorityConverter = PriorityConverter()

    fun convertApiTaskToLocalTaskModel(apiTaskModel: ApiTaskModel): Task? {
        return if (apiTaskModel.title != null && apiTaskModel.description != null && apiTaskModel.completed != null && apiTaskModel.priority != null && apiTaskModel.id != null) {
            Task(apiTaskModel.title, apiTaskModel.description, apiTaskModel.completed, priorityConverter.toPriority(apiTaskModel.priority), apiTaskModel.id)
        } else {
            null
        }
    }

    fun convertLocalTaskToApiTaskModel(task: Task, userId: String, completed : Boolean? = null): ApiTaskModel {
        return ApiTaskModel(task.id, userId, task.title, task.description, completed ?: task.isCompleted, task.priority.toString())
    }
}