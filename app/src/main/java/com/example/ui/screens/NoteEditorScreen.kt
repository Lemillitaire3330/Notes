package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Password
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.Sync
import androidx.compose.material.icons.rounded.SyncDisabled
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.components.ChangePasswordDialog
import com.example.ui.components.DeleteNotePasswordDialog
import com.example.ui.components.NoteColorPicker
import com.example.ui.theme.NoteBackgroundTintsDark
import com.example.ui.theme.NoteBackgroundTintsLight
import com.example.ui.theme.NoteColorsDark
import com.example.ui.theme.NoteColorsLight
import com.example.ui.viewmodel.NoteViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteEditorScreen(
    viewModel: NoteViewModel,
    modifier: Modifier = Modifier
) {
    val title by viewModel.editorTitle.collectAsStateWithLifecycle()
    val content by viewModel.editorContent.collectAsStateWithLifecycle()
    val hint by viewModel.editorPasswordHint.collectAsStateWithLifecycle()
    val colorIndex by viewModel.editorColorIndex.collectAsStateWithLifecycle()
    val isAutoSave by viewModel.editorIsAutoSave.collectAsStateWithLifecycle()
    val saveStatus by viewModel.editorSaveStatus.collectAsStateWithLifecycle()
    val currentNote by viewModel.currentNote.collectAsStateWithLifecycle()

    var showMenu by remember { mutableStateOf(false) }
    var showChangePasswordDialog by remember { mutableStateOf(false) }
    var showColorPicker by remember { mutableStateOf(false) }
    var showDeleteConfirmDialog by remember { mutableStateOf(false) }

    val isDark = isSystemInDarkTheme()
    val bgTints = if (isDark) NoteBackgroundTintsDark else NoteBackgroundTintsLight
    val accentColors = if (isDark) NoteColorsDark else NoteColorsLight
    val currentAccent = accentColors[colorIndex.coerceIn(0, accentColors.size - 1)]
    val currentBg = bgTints[colorIndex.coerceIn(0, bgTints.size - 1)]

    // Word and character count computation
    val wordCount by remember(content) {
        derivedStateOf {
            if (content.isBlank()) 0
            else content.trim().split("\\s+".toRegex()).size
        }
    }
    val charCount by remember(content) {
        derivedStateOf { content.length }
    }

    // Intercept back button to safely persist and lock
    BackHandler {
        viewModel.lockAndExitEditor()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(
                        onClick = { viewModel.lockAndExitEditor() },
                        modifier = Modifier.testTag("editor_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Enregistrer et quitter"
                        )
                    }
                },
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = currentAccent.copy(alpha = 0.2f),
                            modifier = Modifier.size(28.dp)
                        ) {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier.fillMaxSize()
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.Lock,
                                    contentDescription = null,
                                    tint = currentAccent,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                        Text(
                            text = if (title.isBlank()) "Note protégée" else title,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1
                        )
                    }
                },
                actions = {
                    // Manual Save button
                    IconButton(
                        onClick = { viewModel.saveCurrentNoteManually() },
                        modifier = Modifier.testTag("manual_save_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Save,
                            contentDescription = "Enregistrer manuellement",
                            tint = currentAccent
                        )
                    }

                    // Lock button (immediately lock and exit)
                    IconButton(
                        onClick = { viewModel.lockAndExitEditor() },
                        modifier = Modifier.testTag("lock_note_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = "Verrouiller et quitter",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    // More options menu
                    Box {
                        IconButton(
                            onClick = { showMenu = true },
                            modifier = Modifier.testTag("editor_more_options")
                        ) {
                            Icon(
                                imageVector = Icons.Default.MoreVert,
                                contentDescription = "Options supplémentaires"
                            )
                        }

                        DropdownMenu(
                            expanded = showMenu,
                            onDismissRequest = { showMenu = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("Modifier le mot de passe") },
                                leadingIcon = { Icon(Icons.Default.Password, contentDescription = null) },
                                onClick = {
                                    showMenu = false
                                    showChangePasswordDialog = true
                                },
                                modifier = Modifier.testTag("menu_change_password")
                            )
                            DropdownMenuItem(
                                text = { Text(if (showColorPicker) "Masquer la palette" else "Changer la couleur") },
                                leadingIcon = { Icon(Icons.Default.Palette, contentDescription = null) },
                                onClick = {
                                    showMenu = false
                                    showColorPicker = !showColorPicker
                                },
                                modifier = Modifier.testTag("menu_change_color")
                            )
                            DropdownMenuItem(
                                text = { Text("Supprimer la note", color = MaterialTheme.colorScheme.error) },
                                leadingIcon = {
                                    Icon(
                                        Icons.Default.Delete,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.error
                                    )
                                },
                                onClick = {
                                    showMenu = false
                                    showDeleteConfirmDialog = true
                                },
                                modifier = Modifier.testTag("menu_delete_note")
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        bottomBar = {
            Surface(
                color = MaterialTheme.colorScheme.surface,
                shadowElevation = 8.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .imePadding()
            ) {
                Column {
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = "$wordCount mot(s)",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "•",
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "$charCount caractère(s)",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Button(
                            onClick = { viewModel.saveCurrentNoteManually() },
                            colors = ButtonDefaults.buttonColors(containerColor = currentAccent),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.testTag("footer_save_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Enregistrer")
                        }
                    }
                }
            }
        },
        modifier = modifier
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(currentBg.copy(alpha = 0.25f))
        ) {
            // Auto-Save Control Strip
            Surface(
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(
                            imageVector = if (isAutoSave) Icons.Rounded.Sync else Icons.Rounded.SyncDisabled,
                            contentDescription = null,
                            tint = if (isAutoSave) currentAccent else MaterialTheme.colorScheme.outline,
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            text = if (isAutoSave) saveStatus else "Sauvegarde auto désactivée",
                            style = MaterialTheme.typography.labelMedium,
                            color = if (isAutoSave) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.outline,
                            maxLines = 1
                        )
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "Auto-save",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Medium
                        )
                        Switch(
                            checked = isAutoSave,
                            onCheckedChange = { viewModel.toggleAutoSave(it) },
                            colors = SwitchDefaults.colors(checkedThumbColor = currentAccent),
                            modifier = Modifier.testTag("editor_autosave_switch")
                        )
                    }
                }
            }

            // Optional Color picker
            AnimatedVisibility(
                visible = showColorPicker,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Surface(
                    color = MaterialTheme.colorScheme.surface,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp)
                ) {
                    NoteColorPicker(
                        selectedIndex = colorIndex,
                        onColorSelected = { viewModel.updateNoteColor(it) }
                    )
                }
            }

            // Scrollable Note Writing Canvas
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp, vertical = 16.dp)
            ) {
                // Title Field
                BasicTextField(
                    value = title,
                    onValueChange = { viewModel.onTitleChange(it) },
                    textStyle = TextStyle(
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    ),
                    cursorBrush = SolidColor(currentAccent),
                    decorationBox = { innerTextField ->
                        Box {
                            if (title.isEmpty()) {
                                Text(
                                    text = "Titre de la note...",
                                    style = TextStyle(
                                        fontSize = 24.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                                    )
                                )
                            }
                            innerTextField()
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("editor_title_input")
                )

                Spacer(modifier = Modifier.height(12.dp))
                HorizontalDivider(color = currentAccent.copy(alpha = 0.3f), thickness = 1.dp)
                Spacer(modifier = Modifier.height(16.dp))

                // Content Field
                BasicTextField(
                    value = content,
                    onValueChange = { viewModel.onContentChange(it) },
                    textStyle = TextStyle(
                        fontSize = 16.sp,
                        lineHeight = 24.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    ),
                    cursorBrush = SolidColor(currentAccent),
                    decorationBox = { innerTextField ->
                        Box(modifier = Modifier.fillMaxWidth()) {
                            if (content.isEmpty()) {
                                Text(
                                    text = "Commencez à écrire votre note sécurisée ici...\nLe texte est protégé par votre mot de passe et sauvegardé selon vos préférences.",
                                    style = TextStyle(
                                        fontSize = 16.sp,
                                        lineHeight = 24.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                                    )
                                )
                            }
                            innerTextField()
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(500.dp)
                        .testTag("editor_content_input")
                )
            }
        }
    }

    // Change Password Dialog
    if (showChangePasswordDialog) {
        ChangePasswordDialog(
            currentHint = hint,
            onDismiss = { showChangePasswordDialog = false },
            onConfirm = { newPass, newHint ->
                viewModel.updateNotePassword(newPass, newHint)
                showChangePasswordDialog = false
            }
        )
    }

    // Delete Confirmation with Password Dialog
    if (showDeleteConfirmDialog) {
        currentNote?.let { note ->
            DeleteNotePasswordDialog(
                note = note,
                onDismiss = { showDeleteConfirmDialog = false },
                onConfirmDelete = { password ->
                    if (viewModel.verifyPassword(note, password)) {
                        viewModel.deleteCurrentNote()
                        showDeleteConfirmDialog = false
                        true
                    } else {
                        false
                    }
                }
            )
        }
    }
}
