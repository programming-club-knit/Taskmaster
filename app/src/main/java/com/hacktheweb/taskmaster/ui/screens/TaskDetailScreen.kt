package com.hacktheweb.taskmaster.ui.screens

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hacktheweb.taskmaster.model.Priority
import com.hacktheweb.taskmaster.viewmodel.TaskViewModel
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskDetailScreen(
    viewModel: TaskViewModel,
    taskId: Int,
    onNavigateBack: () -> Unit
) {
    val task = viewModel.getTaskById(taskId)
    var isEditing by remember { mutableStateOf(false) }

    if (task == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = androidx.compose.ui.Alignment.Center) {
            Text("Task not found")
        }
        return
    }

    var title by remember { mutableStateOf(task.title) }
    var description by remember { mutableStateOf(task.description) }
    var priority by remember { mutableStateOf(task.priority) }
    var showEditDialog by remember{mutableStateOf(false)}
    val scrollState = rememberScrollState()
    val split = task.dueDate.split("-")
    val localDate = LocalDate.of(split[0].toInt(), split[1].toInt(), split[2].toInt())
    val millis = localDate.atStartOfDay().toInstant(ZoneOffset.UTC).toEpochMilli()
    val dateRangePickerState = rememberDatePickerState(millis)
//    dateRangePickerState.selectedDateMillis oo bhaaiii
    if (showEditDialog){
        EditDeleteDialog ("Do you really want to Edit?",{showEditDialog=false}){
            isEditing = !isEditing
        }
    }
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Task Details") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { showEditDialog=true }) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp).verticalScroll(scrollState),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (isEditing) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Task Title") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3
                )
                Card {
                    DatePicker(colors = DatePickerDefaults.colors(containerColor = CardDefaults.cardColors().containerColor),
                        state = dateRangePickerState,
                        title = {
                            Text(
                                text = "Select date range"
                            )
                        },
                        showModeToggle = false,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(500.dp)
                            .padding(16.dp)
                    )
                }

                Text("Priority", style = MaterialTheme.typography.titleMedium)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Priority.values().forEach { p ->
                        FilterChip(
                            selected = priority == p,
                            onClick = { priority = p },
                            label = { Text(p.name) }
                        )
                    }
                }

                Button(
                    onClick = {
                        val instant = Instant.ofEpochMilli(dateRangePickerState.selectedDateMillis!!)
                        val localDate = instant.atZone(ZoneId.systemDefault()).toLocalDate()
                        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
                        val dueDate = localDate.format(formatter)
                        viewModel.updateTask(taskId, title, description, priority, dueDate)
                        isEditing=false
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Save Changes")
                }
                Spacer(Modifier.height(20.dp))
            } else {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = task.title,
                            style = MaterialTheme.typography.headlineMedium
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = task.description,
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("Priority: ${task.priority}", style = MaterialTheme.typography.bodyMedium)
                        Text("Due Date: ${task.dueDate}", style = MaterialTheme.typography.bodyMedium)
                        Text(
                            "Status: ${if (task.isCompleted) "Completed" else "Pending"}",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        }
    }
}
