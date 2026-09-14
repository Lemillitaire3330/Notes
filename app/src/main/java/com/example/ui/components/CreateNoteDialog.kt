package com.example.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.NoteAdd
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.settings.AppLanguage
import com.example.ui.strings.AppStrings

@Composable
fun CreateNoteDialog(
    strings: AppStrings = AppStrings(AppLanguage.ENGLISH),
    onDismiss: () -> Unit,
    onCreate: (title: String, password: String, hint: String, colorIndex: Int, isAutoSave: Boolean) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordHint by remember { mutableStateOf("") }
    var selectedColorIndex by remember { mutableIntStateOf(0) }
    var isAutoSave by remember { mutableStateOf(true) }
    var hasAttemptedSubmit by remember { mutableStateOf(false) }

    val isPasswordEmpty = password.trim().isEmpty()

    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.primaryContainer,
                modifier = Modifier.size(56.dp)
            ) {
                androidx.compose.foundation.layout.Box(
                    modifier = Modifier.padding(12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.NoteAdd,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(32.dp)
                    )
                }
            }
        },
        title = {
            Text(
                text = strings.createNoteTitle,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Title
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text(strings.noteTitleLabel) },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("create_note_title_input")
                )

                // Password with Show/Hide toggle
                PasswordField(
                    value = password,
                    onValueChange = {
                        password = it
                        if (hasAttemptedSubmit) hasAttemptedSubmit = false
                    },
                    label = strings.notePasswordLabel,
                    isError = hasAttemptedSubmit && isPasswordEmpty,
                    errorMessage = strings.passwordRequiredError,
                    testTag = "create_note_password_input"
                )

                // Password hint
                OutlinedTextField(
                    value = passwordHint,
                    onValueChange = { passwordHint = it },
                    label = { Text(strings.hintOptionalLabel) },
                    placeholder = { Text(strings.hintPlaceholder) },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("create_note_hint_input")
                )

                // Auto-save toggle
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = strings.autoSaveLabel,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = strings.autoSaveDesc,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Switch(
                            checked = isAutoSave,
                            onCheckedChange = { isAutoSave = it },
                            modifier = Modifier.testTag("create_note_autosave_switch")
                        )
                    }
                }

                // Color accent picker
                Column {
                    Text(
                        text = strings.noteColorLabel,
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    NoteColorPicker(
                        selectedIndex = selectedColorIndex,
                        onColorSelected = { selectedColorIndex = it }
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (isPasswordEmpty) {
                        hasAttemptedSubmit = true
                    } else {
                        onCreate(title, password, passwordHint, selectedColorIndex, isAutoSave)
                    }
                },
                modifier = Modifier.testTag("confirm_create_note_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.size(6.dp))
                Text(strings.createNoteButton)
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = onDismiss,
                modifier = Modifier.testTag("cancel_create_note_button")
            ) {
                Text(strings.cancelButton)
            }
        }
    )
}
