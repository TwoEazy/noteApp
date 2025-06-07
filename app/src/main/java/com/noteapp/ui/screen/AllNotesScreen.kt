// app/src/main/java/com/noteapp/ui/screen/AllNotesScreen.kt
package com.noteapp.ui.screen

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Note
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.noteapp.data.model.Note
import com.noteapp.navigation.Navigator
import com.noteapp.navigation.Screens
import com.noteapp.ui.theme.BlackGoldColors
import com.noteapp.ui.theme.CustomShapes
import com.noteapp.viewmodel.AllNotesUiState
import com.noteapp.viewmodel.AllNotesViewModel
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AllNotesScreen(
    viewModel: AllNotesViewModel,
    navigator: Navigator,
    onLogout: () -> Unit
) {
    val notes by viewModel.notes.collectAsState()
    val uiState by viewModel.uiState.collectAsState()

    // Check if user is still logged in
    LaunchedEffect(Unit) {
        if (!viewModel.isUserLoggedIn()) {
            // User is not logged in, navigate to login immediately
            onLogout()
            return@LaunchedEffect
        }
    }

    // Animation states
    var animationStarted by remember { mutableStateOf(false) }
    val fabRotation by animateFloatAsState(
        targetValue = if (animationStarted) 0f else 180f,
        animationSpec = tween(800, easing = FastOutSlowInEasing),
        label = "fab_rotation"
    )

    LaunchedEffect(Unit) {
        delay(300)
        animationStarted = true
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
                                    Icons.Default.Note,
                                    contentDescription = null,
                                    tint = BlackGoldColors.Gold,
                                    modifier = Modifier
                                        .size(28.dp)
                                        .padding(end = 8.dp)
                                )
                                Text(
                                    "My Notes",
                                    style = MaterialTheme.typography.headlineSmall,
                                    color = BlackGoldColors.Gold
                                )
                            }
                        }
                    },
                    actions = {
                        AnimatedVisibility(
                            visible = animationStarted,
                            enter = slideInHorizontally(
                                initialOffsetX = { 300 },
                                animationSpec = tween(600, delayMillis = 200, easing = FastOutSlowInEasing)
                            ) + fadeIn()
                        ) {
                            Row {
                                IconButton(
                                    onClick = { /* TODO: Add search functionality */ },
                                    colors = IconButtonDefaults.iconButtonColors(
                                        contentColor = BlackGoldColors.Gold
                                    )
                                ) {
                                    Icon(Icons.Default.Search, contentDescription = "Search")
                                }
                                IconButton(
                                    onClick = {
                                        // Perform logout
                                        viewModel.logout()
                                        onLogout()
                                    },
                                    colors = IconButtonDefaults.iconButtonColors(
                                        contentColor = BlackGoldColors.Gold
                                    )
                                ) {
                                    Icon(Icons.Default.ExitToApp, contentDescription = "Logout")
                                }
                            }
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = BlackGoldColors.CharcoalBlack.copy(alpha = 0.9f)
                    )
                )
            },
            floatingActionButton = {
                AnimatedVisibility(
                    visible = animationStarted,
                    enter = scaleIn(
                        animationSpec = tween(600, delayMillis = 400, easing = FastOutSlowInEasing)
                    ) + fadeIn()
                ) {
                    FloatingActionButton(
                        onClick = {
                            // Check if user is logged in before creating note
                            if (viewModel.isUserLoggedIn()) {
                                navigator.navigateTo(Screens.CreateNote)
                            } else {
                                onLogout()
                            }
                        },
                        modifier = Modifier
                            .rotate(fabRotation)
                            .size(64.dp),
                        shape = CircleShape,
                        containerColor = BlackGoldColors.Gold,
                        contentColor = BlackGoldColors.DeepBlack,
                        elevation = FloatingActionButtonDefaults.elevation(
                            defaultElevation = 8.dp,
                            pressedElevation = 12.dp
                        )
                    ) {
                        Icon(
                            Icons.Default.Add,
                            contentDescription = "Add Note",
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }
            }
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                when (uiState) {
                    is AllNotesUiState.Loading -> {
                        LoadingContent()
                    }
                    is AllNotesUiState.Empty -> {
                        EmptyNotesContent()
                    }
                    is AllNotesUiState.Success -> {
                        NotesListContent(
                            notes = notes,
                            onNoteClick = { note ->
                                // Check if user is logged in before editing note
                                if (viewModel.isUserLoggedIn()) {
                                    navigator.navigateTo(
                                        Screens.CreateNote,
                                        "noteId" to note.id
                                    )
                                } else {
                                    onLogout()
                                }
                            },
                            onDeleteClick = { note ->
                                if (viewModel.isUserLoggedIn()) {
                                    viewModel.deleteNote(note)
                                } else {
                                    onLogout()
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun LoadingContent() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Card(
            shape = CustomShapes.cardShape,
            colors = CardDefaults.cardColors(
                containerColor = BlackGoldColors.CharcoalBlack.copy(alpha = 0.9f)
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
                    text = "Loading your notes...",
                    style = MaterialTheme.typography.bodyLarge,
                    color = BlackGoldColors.LightGold
                )
            }
        }
    }
}

@Composable
private fun EmptyNotesContent() {
    var animationStarted by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(300)
        animationStarted = true
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        AnimatedVisibility(
            visible = animationStarted,
            enter = slideInVertically(
                initialOffsetY = { 200 },
                animationSpec = tween(800, easing = FastOutSlowInEasing)
            ) + fadeIn()
        ) {
            Card(
                modifier = Modifier.padding(32.dp),
                shape = CustomShapes.cardShape,
                colors = CardDefaults.cardColors(
                    containerColor = BlackGoldColors.CharcoalBlack.copy(alpha = 0.8f)
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Column(
                    modifier = Modifier.padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        Icons.Default.Note,
                        contentDescription = null,
                        tint = BlackGoldColors.Gold.copy(alpha = 0.7f),
                        modifier = Modifier
                            .size(64.dp)
                            .padding(bottom = 16.dp)
                    )
                    Text(
                        text = "No notes yet",
                        style = MaterialTheme.typography.headlineSmall,
                        color = BlackGoldColors.Gold,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    Text(
                        text = "Tap the + button to create your first note",
                        style = MaterialTheme.typography.bodyLarge,
                        color = BlackGoldColors.LightGold.copy(alpha = 0.8f)
                    )
                }
            }
        }
    }
}

@Composable
private fun NotesListContent(
    notes: List<Note>,
    onNoteClick: (Note) -> Unit,
    onDeleteClick: (Note) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        itemsIndexed(notes) { index, note ->
            val animationDelay = (index * 100).coerceAtMost(800)

            AnimatedVisibility(
                visible = true,
                enter = slideInHorizontally(
                    initialOffsetX = { 300 },
                    animationSpec = tween(
                        durationMillis = 600,
                        delayMillis = animationDelay,
                        easing = FastOutSlowInEasing
                    )
                ) + fadeIn(
                    animationSpec = tween(
                        durationMillis = 600,
                        delayMillis = animationDelay
                    )
                )
            ) {
                NoteItem(
                    note = note,
                    onNoteClick = { onNoteClick(note) },
                    onDeleteClick = { onDeleteClick(note) }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteItem(
    note: Note,
    onNoteClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    var isPressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.95f else 1f,
        animationSpec = tween(150),
        label = "note_press_scale"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .scale(scale)
            .clickable(
                onClick = {
                    isPressed = true
                    onNoteClick()
                }
            ),
        shape = CustomShapes.cardShape,
        colors = CardDefaults.cardColors(
            containerColor = BlackGoldColors.CharcoalBlack.copy(alpha = 0.9f)
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 6.dp,
            pressedElevation = 12.dp
        ),
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
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                // Note Title
                Text(
                    text = note.title.ifBlank { "Untitled Note" },
                    style = MaterialTheme.typography.titleLarge,
                    color = BlackGoldColors.Gold,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                // Note Content Preview
                if (note.content.isNotBlank()) {
                    Text(
                        text = note.content.take(100) + if (note.content.length > 100) "..." else "",
                        style = MaterialTheme.typography.bodyMedium,
                        color = BlackGoldColors.LightGold.copy(alpha = 0.8f),
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                }

                // Date and Time
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.Note,
                        contentDescription = null,
                        tint = BlackGoldColors.Gold.copy(alpha = 0.6f),
                        modifier = Modifier
                            .size(16.dp)
                            .padding(end = 4.dp)
                    )
                    Text(
                        text = formatDate(note.updatedAt),
                        style = MaterialTheme.typography.labelMedium,
                        color = BlackGoldColors.LightGold.copy(alpha = 0.6f)
                    )
                }
            }

            // Delete Button
            IconButton(
                onClick = onDeleteClick,
                colors = IconButtonDefaults.iconButtonColors(
                    contentColor = BlackGoldColors.ErrorRed.copy(alpha = 0.8f),
                    containerColor = BlackGoldColors.ErrorRed.copy(alpha = 0.1f)
                ),
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
            ) {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = "Delete Note",
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }

    // Reset press state
    LaunchedEffect(isPressed) {
        if (isPressed) {
            delay(150)
            isPressed = false
        }
    }
}

fun formatDate(timestamp: Long): String {
    val now = System.currentTimeMillis()
    val diff = now - timestamp

    return when {
        diff < 60_000 -> "Just now"
        diff < 3600_000 -> "${diff / 60_000}m ago"
        diff < 86400_000 -> "${diff / 3600_000}h ago"
        diff < 604800_000 -> "${diff / 86400_000}d ago"
        else -> {
            val sdf = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
            sdf.format(Date(timestamp))
        }
    }
}