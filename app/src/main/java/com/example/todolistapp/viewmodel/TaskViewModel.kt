package com.example.todolistapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.todolistapp.model.Task

class TaskViewModel : ViewModel() {
    private val _tasks = MutableLiveData<List<Task>>(emptyList())
    val tasks: LiveData<List<Task>> = _tasks

    fun addTask(task: Task) {
        _tasks.value = _tasks.value?.plus(task)
    }

    fun updateTask(updatedTask: Task) {
        _tasks.value = _tasks.value?.map { task ->
            if (task.id == updatedTask.id) updatedTask else task
        }
    }

    fun removeTask(taskId: Int) {
        _tasks.value = _tasks.value?.filter { it.id != taskId }
    }

    fun updateTaskPhotoPath(taskId: Int, photoPath: String) {
        _tasks.value = _tasks.value?.map { task ->
            if (task.id == taskId) task.copy(photoPath = photoPath) else task
        }
    }
}
