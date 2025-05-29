package com.noteapp.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.noteapp.navigation.Navigator
import com.noteapp.viewmodel.CreateNoteUiState
import com.noteapp.viewmodel.CreateNoteViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateNoteScreen(
    viewModel: CreateNoteViewModel,
    navigator: Navigator,
    noteId: Int = 0
) {
    var title by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }
    val uiState by viewModel.uiState.collectAsState()
    val currentNote by viewModel.currentNote.collectAsState()

    LaunchedEffect(noteId) {
        if (noteId > 0) {
            viewModel.loadNote(noteId)
        }
    }

    LaunchedEffect(currentNote) {
        currentNote?.let {
            title = it.title
            content = it.content
        }
    }

    LaunchedEffect(uiState) {
        if (uiState is CreateNoteUiState.Success) {
            navigator.navigateUp()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        if (noteId > 0) "Edit Note" else "New Note",
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navigator.navigateUp() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(
                        onClick = { viewModel.saveNote(title, content) },
                        enabled = title.isNotBlank() || content.isNotBlank()
                    ) {
                        Icon(Icons.Default.Check, contentDescription = "Save")
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Title") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = content,
                    onValueChange = { content = it },
                    label = { Text("Content") },
                    modifier = Modifier.fillMaxSize(),
                    placeholder = { Text("Start typing your note...") }
                )
            }

            if (uiState is CreateNoteUiState.Saving) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            if (uiState is CreateNoteUiState.Error) {
                Snackbar(
                    modifier = Modifier.align(Alignment.BottomCenter),
                    action = {
                        TextButton(onClick = { /* Retry */ }) {
                            Text("Retry")
                        }
                    }
                ) {
                    Text((uiState as CreateNoteUiState.Error).message)
                }
            }
        }
    }
}