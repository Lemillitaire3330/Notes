package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.AppDatabase
import com.example.data.NoteRepository
import com.example.ui.screens.NoteEditorScreen
import com.example.ui.screens.NotesListScreen
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
        val viewModelFactory = NoteViewModelFactory(repository)

        setContent {
            MyApplicationTheme {
                val viewModel: NoteViewModel = viewModel(factory = viewModelFactory)
                SecureNotesApp(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun SecureNotesApp(viewModel: NoteViewModel) {
    val currentScreen by viewModel.currentScreen.collectAsStateWithLifecycle()

    Surface(modifier = Modifier.fillMaxSize()) {
        Crossfade(
            targetState = currentScreen,
            animationSpec = tween(durationMillis = 250),
            label = "screen_transition"
        ) { screen ->
            when (screen) {
                Screen.LIST -> NotesListScreen(viewModel = viewModel)
                Screen.EDITOR -> NoteEditorScreen(viewModel = viewModel)
            }
        }
    }
}
