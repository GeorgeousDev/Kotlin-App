package com.example.todolistapp

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.todolistapp.model.Task
import com.example.todolistapp.ui.theme.TodoListAppTheme
import com.example.todolistapp.viewmodel.TaskViewModel
import java.util.*

class MainActivity : ComponentActivity() {
    private val taskViewModel: TaskViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Sprawdzenie i poproszenie o uprawnienie do powiadomień
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.POST_NOTIFICATIONS), 1)
            }
        }

        // Tworzenie kanału powiadomień
        createNotificationChannel()

        setContent {
            TodoListAppTheme {
                Scaffold(
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

                            // Wyślij powiadomienie o dodaniu nowego zadania
                            sendPushNotification("Dodano zadanie", newTask.title, newTask.id)
                        }) {
                            Text("+")
                        }
                    },
                    modifier = Modifier.fillMaxSize()
                ) { innerPadding ->
                    TaskList(
                        taskViewModel = taskViewModel,
                        modifier = Modifier.padding(innerPadding),
                        onTaskEdit = { updatedTask ->
                            taskViewModel.updateTask(updatedTask)
                            sendPushNotification("Edytowano zadanie", updatedTask.title, updatedTask.id)
                        },
                        onTaskDelete = { task ->
                            taskViewModel.removeTask(task.id)
                            sendPushNotification("Usunięto zadanie", task.title, task.id)
                        }
                    )
                }
            }
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Task Notifications"
            val descriptionText = "Notifications for task updates"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel("task_channel", name, importance).apply {
                description = descriptionText
            }

            val notificationManager: NotificationManager =
                getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun sendPushNotification(title: String, message: String, notificationId: Int) {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
            val builder = NotificationCompat.Builder(this, "task_channel")
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)

            with(NotificationManagerCompat.from(this)) {
                notify(notificationId, builder.build())
            }
        }
    }
}

@Composable
fun TaskList(
    taskViewModel: TaskViewModel,
    modifier: Modifier = Modifier,
    onTaskEdit: (Task) -> Unit,
    onTaskDelete: (Task) -> Unit
) {
    val tasks by taskViewModel.tasks.observeAsState(emptyList())
    var showDialog by remember { mutableStateOf(false) }
    var taskToEdit by remember { mutableStateOf<Task?>(null) }

    LazyColumn(modifier = modifier.padding(16.dp)) {
        items(tasks) { task ->
            TaskItem(
                task = task,
                onEdit = {
                    taskToEdit = it
                    showDialog = true
                },
                onDelete = {
                    onTaskDelete(it)
                }
            )
        }
    }

    if (showDialog && taskToEdit != null) {
        EditTaskDialog(
            task = taskToEdit!!,
            onDismiss = { showDialog = false },
            onSave = { updatedTask ->
                onTaskEdit(updatedTask)
                showDialog = false
            }
        )
    }
}

@Composable
fun TaskItem(task: Task, onEdit: (Task) -> Unit, onDelete: (Task) -> Unit) {
    Column(modifier = Modifier.padding(8.dp)) {
        Text(text = task.title, style = MaterialTheme.typography.titleMedium)
        Text(text = task.description, style = MaterialTheme.typography.bodyMedium)
        Spacer(modifier = Modifier.height(4.dp))
        Row {
            Button(onClick = { onEdit(task) }) {
                Text("Edit")
            }
            Spacer(modifier = Modifier.width(8.dp))
            Button(onClick = { onDelete(task) }) {
                Text("Delete")
            }
        }
        Divider()
    }
}

@Composable
fun EditTaskDialog(task: Task, onDismiss: () -> Unit, onSave: (Task) -> Unit) {
    var title by remember { mutableStateOf(task.title) }
    var description by remember { mutableStateOf(task.description) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = "Edit Task") },
        text = {
            Column {
                TextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Title") }
                )
                TextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description") }
                )
            }
        },
        confirmButton = {
            Button(onClick = {
                onSave(task.copy(title = title, description = description))
            }) {
                Text("Save")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    TodoListAppTheme {
        TaskList(
            taskViewModel = TaskViewModel(),
            onTaskEdit = {},
            onTaskDelete = {}
        )
    }
}
