package com.example.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Password
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
fun ChangePasswordDialog(
    currentHint: String,
    strings: AppStrings = AppStrings(AppLanguage.ENGLISH),
    onDismiss: () -> Unit,
    onConfirm: (newPassword: String, newHint: String) -> Unit
) {
    var newPassword by remember { mutableStateOf("") }
    var newHint by remember { mutableStateOf(currentHint) }
    var isError by remember { mutableStateOf(false) }

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
                        imageVector = Icons.Default.Password,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(32.dp)
                    )
                }
            }
        },
        title = {
            Text(
                text = strings.changePasswordTitle,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = strings.changePasswordSubtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                PasswordField(
                    value = newPassword,
                    onValueChange = {
                        newPassword = it
                        if (isError) isError = false
                    },
                    label = strings.newPasswordLabel,
                    isError = isError && newPassword.trim().isEmpty(),
                    errorMessage = strings.passwordRequiredError,
                    testTag = "change_password_input"
                )

                OutlinedTextField(
                    value = newHint,
                    onValueChange = { newHint = it },
                    label = { Text(strings.hintOptionalLabel) },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("change_hint_input")
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (newPassword.trim().isEmpty()) {
                        isError = true
                    } else {
                        onConfirm(newPassword, newHint)
                    }
                },
                modifier = Modifier.testTag("change_password_confirm_button")
            ) {
                Text(strings.updatePasswordButton)
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = onDismiss,
                modifier = Modifier.testTag("change_password_cancel_button")
            ) {
                Text(strings.cancelButton)
            }
        }
    )
}
