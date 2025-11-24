package com.hacktheweb.taskmaster.ui.screens

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.hacktheweb.taskmaster.model.Priority
import com.hacktheweb.taskmaster.viewmodel.TaskViewModel
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTaskScreen(
    viewModel: TaskViewModel,
    onNavigateBack: () -> Unit
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var priority by remember { mutableStateOf(Priority.MEDIUM) }
    val dateRangePickerState = rememberDatePickerState()
    val scrollState = rememberScrollState()
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Add New Task") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
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
                DatePicker(
                    colors = DatePickerDefaults.colors(containerColor = CardDefaults.cardColors().containerColor),
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
                    if (title.isNotBlank()) {
                        val instant = Instant.ofEpochMilli(dateRangePickerState.selectedDateMillis!!)
                        val localDate = instant.atZone(ZoneId.systemDefault()).toLocalDate()
                        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
                        val dueDate = localDate.format(formatter)
                        viewModel.addTask(title, description, priority, dueDate)
                        onNavigateBack()
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Add Task")
            }
            Spacer(Modifier.height(20.dp))
        }
    }
}