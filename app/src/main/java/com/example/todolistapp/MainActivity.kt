package com.example.todolistapp

import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import com.example.todolistapp.model.Task
import com.example.todolistapp.ui.theme.TodoListAppTheme
import com.example.todolistapp.viewmodel.TaskViewModel
import java.io.File
import java.io.FileOutputStream
import java.util.*

class MainActivity : ComponentActivity() {
    private val taskViewModel: TaskViewModel by viewModels()
    private lateinit var cameraLauncher: ActivityResultLauncher<Void?>
    private var capturedImage: Bitmap? = null
    private var currentTaskId: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Sprawdzenie i poproszenie o uprawnienie do kamery
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.CAMERA), 2)
        }

        // Inicjalizacja launchera kamery
        cameraLauncher = registerForActivityResult(ActivityResultContracts.TakePicturePreview()) { bitmap ->
            if (bitmap != null) {
                capturedImage = bitmap
                Log.d("MainActivity", "Zdjęcie zostało zrobione")
                currentTaskId?.let { taskId ->
                    saveImageAndAttachToTask(taskId, bitmap)
                }
            } else {
                Log.d("MainActivity", "Nie udało się zrobić zdjęcia.")
            }
        }

        setContent {
            TodoListAppTheme {
                Scaffold(
                    floatingActionButton = {
                        FloatingActionButton(onClick = {
                            // Dodanie nowego zadania
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
                    TaskList(
                        taskViewModel = taskViewModel,
                        modifier = Modifier.padding(innerPadding),
                        onTaskEdit = { updatedTask ->
                            taskViewModel.updateTask(updatedTask)
                        },
                        onTaskDelete = { task ->
                            taskViewModel.removeTask(task.id)
                        },
                        onAddPhoto = { taskId ->
                            currentTaskId = taskId
                            dispatchTakePictureIntent()
                        }
                    )
                }
            }
        }
    }

    private fun dispatchTakePictureIntent() {
        Log.d("MainActivity", "Wywołanie dispatchTakePictureIntent")
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            cameraLauncher.launch(null)
        } else {
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.CAMERA), 2)
        }
    }

    private fun saveImageAndAttachToTask(taskId: Int, bitmap: Bitmap) {
        val file = File(filesDir, "$taskId.jpg")
        FileOutputStream(file).use { out ->
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out)
        }
        taskViewModel.updateTaskPhotoPath(taskId, file.absolutePath)
    }
}

@Composable
fun TaskList(
    taskViewModel: TaskViewModel,
    modifier: Modifier = Modifier,
    onTaskEdit: (Task) -> Unit,
    onTaskDelete: (Task) -> Unit,
    onAddPhoto: (Int) -> Unit
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
                },
                onAddPhoto = onAddPhoto
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
            },
            onAddPhoto = { onAddPhoto(taskToEdit!!.id) }
        )
    }
}

@Composable
fun TaskItem(task: Task, onEdit: (Task) -> Unit, onDelete: (Task) -> Unit, onAddPhoto: (Int) -> Unit) {
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
        Button(onClick = { onAddPhoto(task.id) }) {
            Text("Dodaj zdjęcie")
        }
        task.photoPath?.let {
            Spacer(modifier = Modifier.height(8.dp))
            Image(bitmap = BitmapFactory.decodeFile(it).asImageBitmap(), contentDescription = "Zdjęcie")
        }
        Divider()
    }
}

@Composable
fun EditTaskDialog(
    task: Task,
    onDismiss: () -> Unit,
    onSave: (Task) -> Unit,
    onAddPhoto: () -> Unit
) {
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
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = onAddPhoto) {
                    Text("Dodaj zdjęcie")
                }
                // Wyświetlanie zdjęcia, jeśli zostało zrobione
                task.photoPath?.let {
                    Spacer(modifier = Modifier.height(16.dp))
                    Image(bitmap = BitmapFactory.decodeFile(it).asImageBitmap(), contentDescription = "Zdjęcie", modifier = Modifier.size(128.dp))
                }
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
            onTaskDelete = {},
            onAddPhoto = {}
        )
    }
}
