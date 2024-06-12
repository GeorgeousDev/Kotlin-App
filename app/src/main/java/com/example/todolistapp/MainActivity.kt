package com.example.todolistapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.todolistapp.ui.theme.TodoListAppTheme
import com.example.todolistapp.viewmodel.TaskViewModel

class MainActivity : ComponentActivity() {
    // Uzyskanie referencji do ViewModel
    private val taskViewModel: TaskViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TodoListAppTheme {
                // Scaffold definiuje podstawową strukturę ekranu
                Scaffold(
                    // Przycisk dodawania nowego zadania
                    floatingActionButton = {
                        FloatingActionButton(onClick = {
                            val newTask = Task(
                                id = UUID.randomUUID().hashCode(),
                                title = "Nowe Zadanie",
                                description = "Opis nowego zadania",
                                dueDate = Date(),
                                isCompleted = false
                            )
                            taskViewModel.addTask(newTask)
                        }) {
                            Text("+")
                        }
                    },
                    modifier = Modifier.fillMaxSize()
                ) { innerPadding ->
                    // Wyświetlenie listy zadań
                    TaskList(
                        taskViewModel = taskViewModel,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}
