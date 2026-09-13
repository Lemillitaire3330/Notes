package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.NoteRepository
import com.example.data.SecureNote
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

enum class Screen {
    LIST,
    EDITOR
}

class NoteViewModel(private val repository: NoteRepository) : ViewModel() {

    val searchQuery = MutableStateFlow("")

    val notesList: StateFlow<List<SecureNote>> = searchQuery
        .flatMapLatest { query ->
            repository.searchNotes(query)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private val _currentScreen = MutableStateFlow(Screen.LIST)
    val currentScreen: StateFlow<Screen> = _currentScreen.asStateFlow()

    private val _currentNote = MutableStateFlow<SecureNote?>(null)
    val currentNote: StateFlow<SecureNote?> = _currentNote.asStateFlow()

    // Editor state
    val editorTitle = MutableStateFlow("")
    val editorContent = MutableStateFlow("")
    val editorPassword = MutableStateFlow("")
    val editorPasswordHint = MutableStateFlow("")
    val editorColorIndex = MutableStateFlow(0)
    val editorIsAutoSave = MutableStateFlow(true)

    private val _editorSaveStatus = MutableStateFlow("Prêt")
    val editorSaveStatus: StateFlow<String> = _editorSaveStatus.asStateFlow()

    private val _lastSavedTime = MutableStateFlow<Long?>(null)
    val lastSavedTime: StateFlow<Long?> = _lastSavedTime.asStateFlow()

    private val _unlockedNoteIds = MutableStateFlow<Set<Long>>(emptySet())
    val unlockedNoteIds: StateFlow<Set<Long>> = _unlockedNoteIds.asStateFlow()

    private var autoSaveJob: Job? = null
    private val timeFormatter = SimpleDateFormat("HH:mm:ss", Locale.getDefault())

    init {
        // Observe text changes for debounced auto-save
        viewModelScope.launch {
            combine(editorTitle, editorContent) { title, content ->
                Pair(title, content)
            }
            .debounce(600)
            .collect { (title, content) ->
                val activeNote = _currentNote.value
                if (activeNote != null && editorIsAutoSave.value && _currentScreen.value == Screen.EDITOR) {
                    val hasChanged = title != activeNote.title || content != activeNote.content
                    if (hasChanged) {
                        saveNoteToDb(title, content)
                    }
                }
            }
        }
    }

    fun onSearchQueryChanged(query: String) {
        searchQuery.value = query
    }

    fun verifyPassword(note: SecureNote, enteredPass: String): Boolean {
        return note.password == enteredPass
    }

    fun unlockAndOpenNote(note: SecureNote, enteredPass: String): Boolean {
        if (verifyPassword(note, enteredPass)) {
            _unlockedNoteIds.value = _unlockedNoteIds.value + note.id
            openNoteInEditor(note)
            return true
        }
        return false
    }

    fun openUnlockedNote(note: SecureNote) {
        if (_unlockedNoteIds.value.contains(note.id)) {
            openNoteInEditor(note)
        }
    }

    private fun openNoteInEditor(note: SecureNote) {
        _currentNote.value = note
        editorTitle.value = note.title
        editorContent.value = note.content
        editorPassword.value = note.password
        editorPasswordHint.value = note.passwordHint
        editorColorIndex.value = note.colorIndex
        editorIsAutoSave.value = note.isAutoSaveEnabled
        _lastSavedTime.value = note.updatedAt
        _editorSaveStatus.value = "Enregistré à ${timeFormatter.format(Date(note.updatedAt))}"
        _currentScreen.value = Screen.EDITOR
    }

    fun createAndOpenNote(
        title: String,
        password: String,
        hint: String,
        colorIndex: Int,
        isAutoSave: Boolean
    ) {
        viewModelScope.launch {
            val cleanTitle = if (title.isBlank()) "Nouvelle note" else title.trim()
            val now = System.currentTimeMillis()
            val newNote = SecureNote(
                title = cleanTitle,
                content = "",
                password = password,
                passwordHint = hint.trim(),
                colorIndex = colorIndex,
                isPinned = false,
                isAutoSaveEnabled = isAutoSave,
                createdAt = now,
                updatedAt = now
            )
            val generatedId = repository.insertNote(newNote)
            val createdNote = newNote.copy(id = generatedId)
            _unlockedNoteIds.value = _unlockedNoteIds.value + generatedId
            openNoteInEditor(createdNote)
        }
    }

    fun onTitleChange(newTitle: String) {
        editorTitle.value = newTitle
        if (!editorIsAutoSave.value) {
            _editorSaveStatus.value = "Modifications non enregistrées"
        }
    }

    fun onContentChange(newContent: String) {
        editorContent.value = newContent
        if (!editorIsAutoSave.value) {
            _editorSaveStatus.value = "Modifications non enregistrées"
        }
    }

    fun toggleAutoSave(enabled: Boolean) {
        editorIsAutoSave.value = enabled
        val activeNote = _currentNote.value ?: return
        val updated = activeNote.copy(isAutoSaveEnabled = enabled, updatedAt = System.currentTimeMillis())
        _currentNote.value = updated
        viewModelScope.launch {
            repository.updateNote(updated)
            if (enabled) {
                saveCurrentNoteManually()
            }
        }
    }

    fun saveCurrentNoteManually() {
        val activeNote = _currentNote.value ?: return
        saveNoteToDb(editorTitle.value, editorContent.value)
    }

    private fun saveNoteToDb(title: String, content: String) {
        val activeNote = _currentNote.value ?: return
        viewModelScope.launch {
            _editorSaveStatus.value = "Enregistrement..."
            val now = System.currentTimeMillis()
            val cleanTitle = if (title.isBlank()) "Sans titre" else title.trim()
            val updated = activeNote.copy(
                title = cleanTitle,
                content = content,
                password = editorPassword.value,
                passwordHint = editorPasswordHint.value,
                colorIndex = editorColorIndex.value,
                isAutoSaveEnabled = editorIsAutoSave.value,
                updatedAt = now
            )
            repository.updateNote(updated)
            _currentNote.value = updated
            _lastSavedTime.value = now
            _editorSaveStatus.value = "Enregistré à ${timeFormatter.format(Date(now))}"
        }
    }

    fun updateNotePassword(newPassword: String, hint: String) {
        val activeNote = _currentNote.value ?: return
        editorPassword.value = newPassword
        editorPasswordHint.value = hint
        val now = System.currentTimeMillis()
        val updated = activeNote.copy(
            password = newPassword,
            passwordHint = hint.trim(),
            updatedAt = now
        )
        _currentNote.value = updated
        viewModelScope.launch {
            repository.updateNote(updated)
            _editorSaveStatus.value = "Mot de passe mis à jour"
        }
    }

    fun updateNoteColor(colorIndex: Int) {
        editorColorIndex.value = colorIndex
        val activeNote = _currentNote.value ?: return
        val updated = activeNote.copy(
            colorIndex = colorIndex,
            updatedAt = System.currentTimeMillis()
        )
        _currentNote.value = updated
        viewModelScope.launch {
            repository.updateNote(updated)
        }
    }

    fun togglePin(note: SecureNote) {
        viewModelScope.launch {
            repository.togglePin(note.id, note.isPinned)
        }
    }

    fun deleteNote(note: SecureNote) {
        viewModelScope.launch {
            repository.deleteNote(note)
            _unlockedNoteIds.value = _unlockedNoteIds.value - note.id
            if (_currentNote.value?.id == note.id) {
                _currentNote.value = null
                _currentScreen.value = Screen.LIST
            }
        }
    }

    fun deleteCurrentNote() {
        val activeNote = _currentNote.value ?: return
        deleteNote(activeNote)
    }

    fun lockAndExitEditor() {
        val activeNote = _currentNote.value
        if (activeNote != null) {
            // Save before exit
            val cleanTitle = if (editorTitle.value.isBlank()) "Sans titre" else editorTitle.value.trim()
            val now = System.currentTimeMillis()
            val updated = activeNote.copy(
                title = cleanTitle,
                content = editorContent.value,
                password = editorPassword.value,
                passwordHint = editorPasswordHint.value,
                colorIndex = editorColorIndex.value,
                isAutoSaveEnabled = editorIsAutoSave.value,
                updatedAt = now
            )
            viewModelScope.launch {
                repository.updateNote(updated)
            }
            // Lock note on exit so password is required again
            _unlockedNoteIds.value = _unlockedNoteIds.value - activeNote.id
        }
        _currentNote.value = null
        _currentScreen.value = Screen.LIST
    }

    fun lockNote(noteId: Long) {
        _unlockedNoteIds.value = _unlockedNoteIds.value - noteId
    }
}

class NoteViewModelFactory(private val repository: NoteRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(NoteViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return NoteViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
