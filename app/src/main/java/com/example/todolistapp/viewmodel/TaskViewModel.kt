package com.example.todolistapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.todolistapp.model.Task
import java.util.*

class TaskViewModel : ViewModel() {
    private val _tasks = MutableLiveData<List<Task>>()
    val tasks: LiveData<List<Task>> = _tasks

    init {
        _tasks.value = listOf(
            Task(1, "Zadanie 1", "Opis zadania 1", Date(), false),
            Task(2, "Zadanie 2", "Opis zadania 2", Date(), false)
        )
    }

    // Dodanie nowego zadania
    fun addTask(task: Task) {
        val currentTasks = _tasks.value ?: listOf()
        _tasks.value = currentTasks + task
    }

    // Aktualizacja istniejącego zadania
    fun updateTask(updatedTask: Task) {
        val currentTasks = _tasks.value ?: return
        _tasks.value = currentTasks.map { task ->
            if (task.id == updatedTask.id) updatedTask else task
        }
    }

    // Oznaczenie zadania jako ukończone
    fun completeTask(taskId: Int) {
        val currentTasks = _tasks.value ?: return
        _tasks.value = currentTasks.map { task ->
            if (task.id == taskId) task.copy(isCompleted = true) else task
        }
    }

    // Usunięcie zadania
    fun removeTask(taskId: Int) {
        val currentTasks = _tasks.value ?: return
        _tasks.value = currentTasks.filter { it.id != taskId }
    }
}
