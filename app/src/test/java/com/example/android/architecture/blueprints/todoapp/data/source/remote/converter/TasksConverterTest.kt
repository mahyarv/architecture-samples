package com.example.android.architecture.blueprints.todoapp.data.source.remote.converter

import com.example.android.architecture.blueprints.todoapp.data.Priority
import com.example.android.architecture.blueprints.todoapp.data.Task
import com.example.android.architecture.blueprints.todoapp.data.source.remote.model.ApiTaskModel
import org.junit.Assert.*
import org.junit.Test

class TasksConverterTest {

    @Test
    fun testConvertGoodApiTaskModel_success() {
        val apiTaskModel = ApiTaskModel("task-id",
                "user-id", "Title1", "Desc1",
                false, "low")

        val task = TasksConverter.convertApiTaskToLocalTaskModel(apiTaskModel)

        assertNotNull(task)
        assertEquals(task?.title, apiTaskModel.title)
        assertEquals(task?.id, apiTaskModel.id)
        assertEquals(task?.description, apiTaskModel.description)
        assertEquals(task?.isCompleted, apiTaskModel.completed)
        assertEquals(task?.priority, Priority.LOW)
    }

    @Test
    fun testConvertBadTaskApiModel_null() {
        val apiTaskModel = ApiTaskModel("task-id",
                "user-id", "Title1", "Desc1",
                false, null)

        val task = TasksConverter.convertApiTaskToLocalTaskModel(apiTaskModel)

        assertNull(task)
    }

    @Test
    fun convertCompletedIncludedLocalTaskModel() {
        val task = Task("Title1", "Desc1", true)
        val userId = "userid"

        val apiTaskModel = TasksConverter.convertLocalTaskToApiTaskModel(task, userId)

        assertNotNull(apiTaskModel)
        assertEquals(task.title, apiTaskModel.title)
        assertEquals(task.id, apiTaskModel.id)
        assertEquals(task.description, apiTaskModel.description)
        assertEquals(task.isCompleted, apiTaskModel.completed)
        assertNotNull(apiTaskModel.completed)
        apiTaskModel.completed?.let { assertTrue(it) }
        assertEquals(task.priority, Priority.LOW)
    }

    @Test
    fun convertCompletedNotIncludedLocalTaskModel() {
        val task = Task("Title1", "Desc1")
        val userId = "userid"

        val apiTaskModel = TasksConverter.convertLocalTaskToApiTaskModel(task, userId)

        assertNotNull(apiTaskModel)
        assertEquals(task.title, apiTaskModel.title)
        assertEquals(task.id, apiTaskModel.id)
        assertEquals(task.description, apiTaskModel.description)
        assertEquals(task.isCompleted, apiTaskModel.completed)
        assertNotNull(apiTaskModel.completed)
        apiTaskModel.completed?.let { assertFalse(it) }
        assertEquals(task.priority, Priority.LOW)
    }
}