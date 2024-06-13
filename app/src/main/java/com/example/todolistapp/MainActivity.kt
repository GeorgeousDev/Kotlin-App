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
@Composable
fun TaskList(taskViewModel: TaskViewModel, modifier: Modifier = Modifier) {
    // Obserwowanie zmian w liście zadań
    val tasks by taskViewModel.tasks.observeAsState(emptyList())

    LazyColumn(modifier = modifier.padding(16.dp)) {
        // Wyświetlenie każdego zadania jako elementu listy
        items(tasks) { task ->
            TaskItem(
                task = task,
                onEdit = { taskViewModel.updateTask(it) },
                onDelete = { taskViewModel.removeTask(it.id) }
            )
        }
    }
}

@Composable
fun TaskItem(task: Task, onEdit: (Task) -> Unit, onDelete: (Task) -> Unit) {
    Column(modifier = Modifier.padding(8.dp)) {
        Text(text = task.title, style = MaterialTheme.typography.titleMedium)
        Text(text = task.description, style = MaterialTheme.typography.bodyMedium)
        Spacer(modifier = Modifier.height(4.dp))
        Row {
            // Przycisk edycji zadania
            Button(onClick = { onEdit(task) }) {
                Text("Edit")
            }
            Spacer(modifier = Modifier.width(8.dp))
            // Przycisk usunięcia zadania
            Button(onClick = { onDelete(task) }) {
                Text("Delete")
            }
        }
        Divider()
    }
}
