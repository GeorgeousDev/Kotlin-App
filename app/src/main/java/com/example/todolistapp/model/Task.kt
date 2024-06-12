package com.example.todolistapp.model

import java.util.Date

// Klasa danych reprezentująca zadanie
data class Task(
    val id: Int,
    val title: String,
    val description: String,
    val dueDate: Date,
    val isCompleted: Boolean
)
