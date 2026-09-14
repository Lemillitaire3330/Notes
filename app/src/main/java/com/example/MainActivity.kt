package com.example

import android.app.Activity
import android.app.LocaleManager
import android.os.Build
import android.os.Bundle
import android.os.LocaleList
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.AppDatabase
import com.example.data.NoteRepository
import com.example.data.settings.AppLanguage
import com.example.data.settings.SettingsManager
import com.example.ui.components.FirstLaunchLanguageDialog
import com.example.ui.screens.NoteEditorScreen
import com.example.ui.screens.NotesListScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.NoteViewModel
import com.example.ui.viewmodel.NoteViewModelFactory
import com.example.ui.viewmodel.Screen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val database = AppDatabase.getInstance(applicationContext)
        val repository = NoteRepository(database.secureNoteDao())
        val settingsManager = SettingsManager(applicationContext)
        val viewModelFactory = NoteViewModelFactory(repository, settingsManager)

        setContent {
            val themeMode by settingsManager.themeMode.collectAsStateWithLifecycle()
            val fontFamily by settingsManager.fontFamily.collectAsStateWithLifecycle()
            val fontSize by settingsManager.fontSize.collectAsStateWithLifecycle()

            MyApplicationTheme(
                themeMode = themeMode,
                fontFamilyChoice = fontFamily,
                fontSizeChoice = fontSize
            ) {
                val viewModel: NoteViewModel = viewModel(factory = viewModelFactory)
                SecureNotesApp(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun SecureNotesApp(viewModel: NoteViewModel) {
    val currentScreen by viewModel.currentScreen.collectAsStateWithLifecycle()
    val hasSeenNotice by viewModel.settingsManager.hasSeenLanguageNotice.collectAsStateWithLifecycle()
    val currentLanguage by viewModel.settingsManager.language.collectAsStateWithLifecycle()
    val context = LocalContext.current

    // Dynamically update window title and system locale according to selected language
    LaunchedEffect(currentLanguage) {
        val titleText = when (currentLanguage) {
            AppLanguage.ENGLISH -> "Secure Note"
            AppLanguage.FRENCH -> "Notes Sécurisées"
            AppLanguage.CHINESE -> "安全便签"
        }
        (context as? Activity)?.title = titleText

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val localeManager = context.getSystemService(LocaleManager::class.java)
            val langTag = when (currentLanguage) {
                AppLanguage.ENGLISH -> "en"
                AppLanguage.FRENCH -> "fr"
                AppLanguage.CHINESE -> "zh"
            }
            localeManager?.applicationLocales = LocaleList.forLanguageTags(langTag)
        }
    }

    Surface(modifier = Modifier.fillMaxSize()) {
        Crossfade(
            targetState = currentScreen,
            animationSpec = tween(durationMillis = 250),
            label = "screen_transition"
        ) { screen ->
            when (screen) {
                Screen.LIST -> NotesListScreen(viewModel = viewModel)
                Screen.EDITOR -> NoteEditorScreen(viewModel = viewModel)
                Screen.SETTINGS -> SettingsScreen(viewModel = viewModel)
            }
        }
    }

    if (!hasSeenNotice) {
        FirstLaunchLanguageDialog(
            currentLanguage = currentLanguage,
            onSelectLanguage = { viewModel.settingsManager.setLanguage(it) },
            onDismiss = { viewModel.settingsManager.setHasSeenLanguageNotice(true) }
        )
    }
}
