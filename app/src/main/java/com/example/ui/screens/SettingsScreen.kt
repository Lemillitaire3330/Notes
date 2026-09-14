package com.example.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.FontDownload
import androidx.compose.material.icons.filled.FormatSize
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.RadioButton
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.settings.AppFontFamily
import com.example.data.settings.AppFontSize
import com.example.data.settings.AppLanguage
import com.example.data.settings.ThemeMode
import com.example.ui.components.RawDatabaseInspectorDialog
import com.example.ui.strings.AppStrings
import com.example.ui.viewmodel.NoteViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: NoteViewModel,
    modifier: Modifier = Modifier
) {
    val language by viewModel.settingsManager.language.collectAsStateWithLifecycle()
    val themeMode by viewModel.settingsManager.themeMode.collectAsStateWithLifecycle()
    val fontFamily by viewModel.settingsManager.fontFamily.collectAsStateWithLifecycle()
    val fontSize by viewModel.settingsManager.fontSize.collectAsStateWithLifecycle()
    val notes by viewModel.notesList.collectAsStateWithLifecycle()

    val strings = remember(language) { AppStrings(language) }
    var showInspectorDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = strings.settingsTitle,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = { viewModel.closeSettings() },
                        modifier = Modifier.testTag("settings_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = strings.backTooltip
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        modifier = modifier
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Language Selection
            SettingsCard(
                icon = Icons.Default.Language,
                title = strings.languageLabel
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    AppLanguage.entries.forEach { langOption ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { viewModel.settingsManager.setLanguage(langOption) }
                                .padding(vertical = 4.dp)
                                .testTag("select_lang_${langOption.code}")
                        ) {
                            RadioButton(
                                selected = language == langOption,
                                onClick = { viewModel.settingsManager.setLanguage(langOption) }
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "${langOption.nativeName} (${langOption.displayName})",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = if (language == langOption) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }
                }
            }

            // Theme Selection
            SettingsCard(
                icon = Icons.Default.ColorLens,
                title = strings.themeLabel
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    ThemeChip(
                        selected = themeMode == ThemeMode.SYSTEM,
                        onClick = { viewModel.settingsManager.setThemeMode(ThemeMode.SYSTEM) },
                        label = strings.themeSystem,
                        modifier = Modifier.weight(1f).testTag("theme_chip_system")
                    )
                    ThemeChip(
                        selected = themeMode == ThemeMode.LIGHT,
                        onClick = { viewModel.settingsManager.setThemeMode(ThemeMode.LIGHT) },
                        label = strings.themeLight,
                        modifier = Modifier.weight(1f).testTag("theme_chip_light")
                    )
                    ThemeChip(
                        selected = themeMode == ThemeMode.DARK,
                        onClick = { viewModel.settingsManager.setThemeMode(ThemeMode.DARK) },
                        label = strings.themeDark,
                        modifier = Modifier.weight(1f).testTag("theme_chip_dark")
                    )
                }
            }

            // Font Style Selection
            SettingsCard(
                icon = Icons.Default.FontDownload,
                title = strings.fontLabel
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    ThemeChip(
                        selected = fontFamily == AppFontFamily.DEFAULT,
                        onClick = { viewModel.settingsManager.setFontFamily(AppFontFamily.DEFAULT) },
                        label = strings.fontDefault,
                        modifier = Modifier.weight(1f).testTag("font_chip_default")
                    )
                    ThemeChip(
                        selected = fontFamily == AppFontFamily.SERIF,
                        onClick = { viewModel.settingsManager.setFontFamily(AppFontFamily.SERIF) },
                        label = strings.fontSerif,
                        modifier = Modifier.weight(1f).testTag("font_chip_serif")
                    )
                    ThemeChip(
                        selected = fontFamily == AppFontFamily.MONOSPACE,
                        onClick = { viewModel.settingsManager.setFontFamily(AppFontFamily.MONOSPACE) },
                        label = strings.fontMonospace,
                        modifier = Modifier.weight(1f).testTag("font_chip_mono")
                    )
                }
            }

            // Font Size Selection
            SettingsCard(
                icon = Icons.Default.FormatSize,
                title = strings.fontSizeLabel
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    ThemeChip(
                        selected = fontSize == AppFontSize.SMALL,
                        onClick = { viewModel.settingsManager.setFontSize(AppFontSize.SMALL) },
                        label = strings.fontSizeSmall,
                        modifier = Modifier.weight(1f).testTag("size_chip_small")
                    )
                    ThemeChip(
                        selected = fontSize == AppFontSize.NORMAL,
                        onClick = { viewModel.settingsManager.setFontSize(AppFontSize.NORMAL) },
                        label = strings.fontSizeNormal,
                        modifier = Modifier.weight(1f).testTag("size_chip_normal")
                    )
                    ThemeChip(
                        selected = fontSize == AppFontSize.LARGE,
                        onClick = { viewModel.settingsManager.setFontSize(AppFontSize.LARGE) },
                        label = strings.fontSizeLarge,
                        modifier = Modifier.weight(1f).testTag("size_chip_large")
                    )
                }
            }

            // Security & Encryption Section with Raw Inspector
            SettingsCard(
                icon = Icons.Default.Security,
                title = strings.sectionSecurity
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = strings.securitySpecs,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = 20.sp
                    )

                    OutlinedButton(
                        onClick = { showInspectorDialog = true },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("inspect_db_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Visibility,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(strings.inspectDbButton)
                    }
                }
            }

            // About & Open Source
            SettingsCard(
                icon = Icons.Default.Info,
                title = strings.sectionAbout
            ) {
                Text(
                    text = strings.aboutLicenseText,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 18.sp
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }

    if (showInspectorDialog) {
        RawDatabaseInspectorDialog(
            notes = notes,
            strings = strings,
            onDismiss = { showInspectorDialog = false }
        )
    }
}

@Composable
private fun SettingsCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    content: @Composable () -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(22.dp)
                )
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            content()
        }
    }
}

@Composable
private fun ThemeChip(
    selected: Boolean,
    onClick: () -> Unit,
    label: String,
    modifier: Modifier = Modifier
) {
    FilterChip(
        selected = selected,
        onClick = onClick,
        label = {
            Text(
                text = label,
                fontSize = 12.sp,
                maxLines = 1
            )
        },
        modifier = modifier
    )
}
