package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.rounded.EnhancedEncryption
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.SecureNote
import com.example.ui.components.CreateNoteDialog
import com.example.ui.components.DeleteNotePasswordDialog
import com.example.ui.components.NoteCard
import com.example.ui.components.UnlockDialog
import com.example.ui.strings.AppStrings
import com.example.ui.viewmodel.NoteViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotesListScreen(
    viewModel: NoteViewModel,
    modifier: Modifier = Modifier
) {
    val notes by viewModel.notesList.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val unlockedNoteIds by viewModel.unlockedNoteIds.collectAsStateWithLifecycle()
    val currentLanguage by viewModel.settingsManager.language.collectAsStateWithLifecycle()

    val strings = remember(currentLanguage) { AppStrings(currentLanguage) }

    var showCreateDialog by remember { mutableStateOf(false) }
    var noteToUnlock by remember { mutableStateOf<SecureNote?>(null) }
    var noteToDelete by remember { mutableStateOf<SecureNote?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.primaryContainer,
                            modifier = Modifier.size(36.dp)
                        ) {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier.fillMaxSize()
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.EnhancedEncryption,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                        }
                        Column {
                            Text(
                                text = strings.appTitle,
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = if (notes.isEmpty()) strings.appSubtitleEmpty else strings.appSubtitleCount(notes.size),
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                },
                actions = {
                    // Hamburger / 3-dots button to open Settings
                    IconButton(
                        onClick = { viewModel.openSettings() },
                        modifier = Modifier.testTag("settings_menu_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = strings.settings
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { showCreateDialog = true },
                icon = { Icon(Icons.Default.Add, contentDescription = null) },
                text = { Text(strings.newNoteFab) },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.testTag("create_note_fab")
            )
        },
        modifier = modifier
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { viewModel.onSearchQueryChanged(it) },
                placeholder = { Text(strings.searchPlaceholder) },
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Sentences,
                    imeAction = ImeAction.Search
                ),
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = strings.searchPlaceholder,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                },
                trailingIcon = {
                    AnimatedVisibility(
                        visible = searchQuery.isNotEmpty(),
                        enter = fadeIn(),
                        exit = fadeOut()
                    ) {
                        IconButton(
                            onClick = { viewModel.onSearchQueryChanged("") },
                            modifier = Modifier.testTag("clear_search_button")
                        ) {
                            Icon(Icons.Default.Clear, contentDescription = strings.clearSearch)
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                    focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                    unfocusedBorderColor = Color.Transparent,
                    focusedBorderColor = MaterialTheme.colorScheme.primary
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .testTag("search_notes_input")
            )

            // Content List or Empty State
            if (notes.isEmpty()) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp)
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            modifier = Modifier.size(80.dp)
                        ) {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier.fillMaxSize()
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.Lock,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(44.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = if (searchQuery.isNotBlank()) strings.noSearchResultsTitle else strings.emptyVaultTitle,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = if (searchQuery.isNotBlank())
                                strings.noSearchResults(searchQuery)
                            else
                                strings.emptyVaultMessage,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center,
                            fontSize = 14.sp
                        )
                    }
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 88.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(notes, key = { it.id }) { note ->
                        val isUnlocked = unlockedNoteIds.contains(note.id)
                        NoteCard(
                            note = note,
                            isUnlocked = isUnlocked,
                            strings = strings,
                            onClick = {
                                if (isUnlocked) {
                                    viewModel.openUnlockedNote(note)
                                } else {
                                    noteToUnlock = note
                                }
                            },
                            onTogglePin = { viewModel.togglePin(note) },
                            onDelete = { noteToDelete = note }
                        )
                    }
                }
            }
        }
    }

    // Unlock Dialog
    noteToUnlock?.let { note ->
        UnlockDialog(
            note = note,
            strings = strings,
            onDismiss = { noteToUnlock = null },
            onUnlock = { password ->
                val success = viewModel.unlockAndOpenNote(note, password)
                if (success) {
                    noteToUnlock = null
                }
                success
            }
        )
    }

    // Create Note Dialog
    if (showCreateDialog) {
        CreateNoteDialog(
            strings = strings,
            onDismiss = { showCreateDialog = false },
            onCreate = { title, password, hint, colorIndex, isAutoSave ->
                viewModel.createAndOpenNote(title, password, hint, colorIndex, isAutoSave)
                showCreateDialog = false
            }
        )
    }

    // Delete Confirmation with Password Dialog
    noteToDelete?.let { note ->
        DeleteNotePasswordDialog(
            note = note,
            strings = strings,
            onDismiss = { noteToDelete = null },
            onConfirmDelete = { password ->
                if (viewModel.verifyPassword(note, password)) {
                    viewModel.deleteNote(note)
                    noteToDelete = null
                    true
                } else {
                    false
                }
            }
        )
    }
}
