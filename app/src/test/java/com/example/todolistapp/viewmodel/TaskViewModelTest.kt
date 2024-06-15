package com.example.todolistapp.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.example.todolistapp.model.Task
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import java.util.Date
import org.junit.Assert.assertEquals
import org.mockito.Mockito.times

@ExperimentalCoroutinesApi
class TaskViewModelTest {

    @get:Rule
    var rule: TestRule = InstantTaskExecutorRule()

    private val testDispatcher = StandardTestDispatcher()
    private val taskViewModel = TaskViewModel()

    @Test
    fun addTask_updatesLiveData() = runTest(testDispatcher) {
        val observer = mock<Observer<List<Task>>>()
        taskViewModel.tasks.observeForever(observer)

        val task = Task(1, "Test Task", "Description", Date(), false, null)
        taskViewModel.addTask(task)

        val captor = argumentCaptor<List<Task>>()
        verify(observer, times(2)).onChanged(captor.capture()) // Expecting 2 calls: one for initial, one for add
        assertEquals(listOf(task), captor.lastValue)
    }

    @Test
    fun removeTask_updatesLiveData() = runTest(testDispatcher) {
        val observer = mock<Observer<List<Task>>>()
        taskViewModel.tasks.observeForever(observer)

        val task = Task(1, "Test Task", "Description", Date(), false, null)
        taskViewModel.addTask(task)
        taskViewModel.removeTask(task.id)

        val captor = argumentCaptor<List<Task>>()
        verify(observer, times(3)).onChanged(captor.capture()) // Expecting 3 calls: initial, add, remove

        // Capture the arguments passed to onChanged
        val allValues = captor.allValues

        // First call should be the initial empty list
        assertEquals(emptyList<Task>(), allValues[0])

        // Second call should be the list with one task
        assertEquals(listOf(task), allValues[1])

        // Third call should be the empty list again after removal
        assertEquals(emptyList<Task>(), allValues[2])
    }

    @Test
    fun updateTask_updatesLiveData() = runTest(testDispatcher) {
        val observer = mock<Observer<List<Task>>>()
        taskViewModel.tasks.observeForever(observer)

        val task = Task(1, "Test Task", "Description", Date(), false, null)
        taskViewModel.addTask(task)
        val updatedTask = task.copy(title = "Updated Task")
        taskViewModel.updateTask(updatedTask)

        val captor = argumentCaptor<List<Task>>()
        verify(observer, times(3)).onChanged(captor.capture()) // subskrypcja, dodanie, aktualizacja
        assertEquals(listOf(updatedTask), captor.lastValue)
    }

    @Test
    fun updateTaskPhotoPath_updatesLiveData() = runTest(testDispatcher) {
        val observer = mock<Observer<List<Task>>>()
        taskViewModel.tasks.observeForever(observer)

        val task = Task(1, "Test Task", "Description", Date(), false, null)
        taskViewModel.addTask(task)
        taskViewModel.updateTaskPhotoPath(task.id, "new/photo/path")

        val updatedTask = task.copy(photoPath = "new/photo/path")
        val captor = argumentCaptor<List<Task>>()
        verify(observer, times(3)).onChanged(captor.capture()) // subskrypcja, dodanie, aktualizacja
        assertEquals(listOf(updatedTask), captor.lastValue)
    }
}
