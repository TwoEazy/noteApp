// app/src/main/java/com/noteapp/ui/screen/CreateNoteScreen.kt
package com.noteapp.ui.screen

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.noteapp.navigation.Navigator
import com.noteapp.ui.theme.BlackGoldColors
import com.noteapp.ui.theme.CustomShapes
import com.noteapp.viewmodel.CreateNoteUiState
import com.noteapp.viewmodel.CreateNoteViewModel
import kotlinx.coroutines.delay

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

    // Animation and focus states
    var animationStarted by remember { mutableStateOf(false) }
    val titleFocusRequester = remember { FocusRequester() }
    val contentFocusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current

    val animatedProgress by animateFloatAsState(
        targetValue = if (animationStarted) 1f else 0f,
        animationSpec = tween(durationMillis = 800, easing = FastOutSlowInEasing),
        label = "create_note_animation"
    )

    // Load note data
    LaunchedEffect(noteId) {
        if (noteId > 0) {
            viewModel.loadNote(noteId)
        }
        delay(200)
        animationStarted = true
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

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        BlackGoldColors.DeepBlack,
                        BlackGoldColors.CharcoalBlack.copy(alpha = 0.8f),
                        BlackGoldColors.DeepBlack
                    )
                )
            )
    ) {
        Scaffold(
            containerColor = BlackGoldColors.DeepBlack.copy(alpha = 0.0f),
            topBar = {
                TopAppBar(
                    title = {
                        AnimatedVisibility(
                            visible = animationStarted,
                            enter = slideInHorizontally(
                                initialOffsetX = { -300 },
                                animationSpec = tween(600, easing = FastOutSlowInEasing)
                            ) + fadeIn()
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    Icons.Default.Edit,
                                    contentDescription = null,
                                    tint = BlackGoldColors.Gold,
                                    modifier = Modifier
                                        .size(24.dp)
                                        .padding(end = 8.dp)
                                )
                                Text(
                                    text = if (noteId > 0) "Edit Note" else "New Note",
                                    style = MaterialTheme.typography.titleLarge,
                                    color = BlackGoldColors.Gold
                                )
                            }
                        }
                    },
                    navigationIcon = {
                        AnimatedVisibility(
                            visible = animationStarted,
                            enter = slideInHorizontally(
                                initialOffsetX = { -200 },
                                animationSpec = tween(600, delayMillis = 100, easing = FastOutSlowInEasing)
                            ) + fadeIn()
                        ) {
                            IconButton(
                                onClick = { navigator.navigateUp() },
                                colors = IconButtonDefaults.iconButtonColors(
                                    contentColor = BlackGoldColors.Gold
                                )
                            ) {
                                Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                            }
                        }
                    },
                    actions = {
                        AnimatedVisibility(
                            visible = animationStarted,
                            enter = slideInHorizontally(
                                initialOffsetX = { 200 },
                                animationSpec = tween(600, delayMillis = 200, easing = FastOutSlowInEasing)
                            ) + fadeIn()
                        ) {
                            IconButton(
                                onClick = {
                                    viewModel.saveNote(title, content)
                                    keyboardController?.hide()
                                },
                                enabled = title.isNotBlank() || content.isNotBlank(),
                                colors = IconButtonDefaults.iconButtonColors(
                                    contentColor = if (title.isNotBlank() || content.isNotBlank())
                                        BlackGoldColors.Gold else BlackGoldColors.MediumGray,
                                    containerColor = if (title.isNotBlank() || content.isNotBlank())
                                        BlackGoldColors.Gold.copy(alpha = 0.1f) else BlackGoldColors.DarkGray.copy(alpha = 0.1f)
                                )
                            ) {
                                Icon(
                                    Icons.Default.Save,
                                    contentDescription = "Save Note",
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = BlackGoldColors.CharcoalBlack.copy(alpha = 0.9f)
                    )
                )
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState())
                    .alpha(animatedProgress)
            ) {
                // Title Section
                AnimatedVisibility(
                    visible = animationStarted,
                    enter = slideInVertically(
                        initialOffsetY = { 200 },
                        animationSpec = tween(700, delayMillis = 300, easing = FastOutSlowInEasing)
                    ) + fadeIn()
                ) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        shape = CustomShapes.cardShape,
                        colors = CardDefaults.cardColors(
                            containerColor = BlackGoldColors.CharcoalBlack.copy(alpha = 0.8f)
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                        border = CardDefaults.outlinedCardBorder().copy(
                            brush = Brush.linearGradient(
                                colors = listOf(
                                    BlackGoldColors.Gold.copy(alpha = 0.3f),
                                    BlackGoldColors.DarkGold.copy(alpha = 0.1f)
                                )
                            ),
                            width = 1.dp
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(20.dp)
                        ) {
                            Text(
                                text = "Title",
                                style = MaterialTheme.typography.labelLarge,
                                color = BlackGoldColors.Gold.copy(alpha = 0.8f),
                                modifier = Modifier.padding(bottom = 8.dp)
                            )

                            OutlinedTextField(
                                value = title,
                                onValueChange = { title = it },
                                placeholder = {
                                    Text(
                                        "Enter note title...",
                                        color = BlackGoldColors.LightGold.copy(alpha = 0.5f)
                                    )
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .focusRequester(titleFocusRequester),
                                singleLine = true,
                                shape = CustomShapes.textFieldShape,
                                // Fixed: Simplified colors specification
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = BlackGoldColors.Gold,
                                    unfocusedBorderColor = BlackGoldColors.MediumGray,
                                    focusedTextColor = BlackGoldColors.LightGold,
                                    unfocusedTextColor = BlackGoldColors.LightGold,
                                    cursorColor = BlackGoldColors.Gold,
                                    focusedLabelColor = BlackGoldColors.Gold,
                                    unfocusedLabelColor = BlackGoldColors.LightGold.copy(alpha = 0.7f)
                                ),
                                textStyle = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.SemiBold
                                )
                            )
                        }
                    }
                }

                // Content Section
                AnimatedVisibility(
                    visible = animationStarted,
                    enter = slideInVertically(
                        initialOffsetY = { 300 },
                        animationSpec = tween(700, delayMillis = 400, easing = FastOutSlowInEasing)
                    ) + fadeIn()
                ) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .padding(horizontal = 16.dp)
                            .padding(bottom = 16.dp),
                        shape = CustomShapes.cardShape,
                        colors = CardDefaults.cardColors(
                            containerColor = BlackGoldColors.CharcoalBlack.copy(alpha = 0.8f)
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                        border = CardDefaults.outlinedCardBorder().copy(
                            brush = Brush.linearGradient(
                                colors = listOf(
                                    BlackGoldColors.Gold.copy(alpha = 0.3f),
                                    BlackGoldColors.DarkGold.copy(alpha = 0.1f)
                                )
                            ),
                            width = 1.dp
                        )
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(20.dp)
                        ) {
                            Text(
                                text = "Content",
                                style = MaterialTheme.typography.labelLarge,
                                color = BlackGoldColors.Gold.copy(alpha = 0.8f),
                                modifier = Modifier.padding(bottom = 8.dp)
                            )

                            OutlinedTextField(
                                value = content,
                                onValueChange = { content = it },
                                placeholder = {
                                    Text(
                                        "Start writing your thoughts...",
                                        color = BlackGoldColors.LightGold.copy(alpha = 0.5f)
                                    )
                                },
                                modifier = Modifier
                                    .fillMaxSize()
                                    .focusRequester(contentFocusRequester),
                                shape = CustomShapes.textFieldShape,
                                // Fixed: Simplified colors specification
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = BlackGoldColors.Gold,
                                    unfocusedBorderColor = BlackGoldColors.MediumGray,
                                    focusedTextColor = BlackGoldColors.LightGold,
                                    unfocusedTextColor = BlackGoldColors.LightGold,
                                    cursorColor = BlackGoldColors.Gold,
                                    focusedLabelColor = BlackGoldColors.Gold,
                                    unfocusedLabelColor = BlackGoldColors.LightGold.copy(alpha = 0.7f)
                                ),
                                textStyle = MaterialTheme.typography.bodyLarge,
                                maxLines = Int.MAX_VALUE
                            )
                        }
                    }
                }
            }
        }

        // Loading State Overlay
        if (uiState is CreateNoteUiState.Saving) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(BlackGoldColors.DeepBlack.copy(alpha = 0.7f)),
                contentAlignment = Alignment.Center
            ) {
                Card(
                    shape = CustomShapes.cardShape,
                    colors = CardDefaults.cardColors(
                        containerColor = BlackGoldColors.CharcoalBlack
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(48.dp),
                            color = BlackGoldColors.Gold,
                            strokeWidth = 4.dp
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Saving your note...",
                            style = MaterialTheme.typography.bodyLarge,
                            color = BlackGoldColors.LightGold
                        )
                    }
                }
            }
        }

        // Error State
        if (uiState is CreateNoteUiState.Error) {
            AnimatedVisibility(
                visible = true,
                enter = slideInVertically(
                    initialOffsetY = { 100 },
                    animationSpec = tween(300)
                ) + fadeIn(),
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(16.dp)
            ) {
                Card(
                    shape = CustomShapes.cardShape,
                    colors = CardDefaults.cardColors(
                        containerColor = BlackGoldColors.ErrorRed.copy(alpha = 0.9f)
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.Edit,
                            contentDescription = null,
                            tint = BlackGoldColors.White,
                            modifier = Modifier.padding(end = 12.dp)
                        )
                        Column {
                            Text(
                                text = "Save Failed",
                                color = BlackGoldColors.White,
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = (uiState as CreateNoteUiState.Error).message,
                                color = BlackGoldColors.White.copy(alpha = 0.9f),
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        TextButton(
                            onClick = { viewModel.saveNote(title, content) },
                            colors = ButtonDefaults.textButtonColors(
                                contentColor = BlackGoldColors.White
                            )
                        ) {
                            Text("Retry")
                        }
                    }
                }
            }
        }
    }

    // Auto-focus title field for new notes
    LaunchedEffect(animationStarted, noteId) {
        if (animationStarted && noteId == 0 && title.isEmpty()) {
            delay(600)
            titleFocusRequester.requestFocus()
        }
    }
}