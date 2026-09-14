package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.crypto.CryptoUtils
import com.example.data.NoteRepository
import com.example.data.SecureNote
import com.example.data.settings.SettingsManager
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class Screen {
    LIST,
    EDITOR,
    SETTINGS
}

class NoteViewModel(
    private val repository: NoteRepository,
    val settingsManager: SettingsManager
) : ViewModel() {

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

    private val _isSaving = MutableStateFlow(false)
    val isSaving: StateFlow<Boolean> = _isSaving.asStateFlow()

    private val _hasUnsavedChanges = MutableStateFlow(false)
    val hasUnsavedChanges: StateFlow<Boolean> = _hasUnsavedChanges.asStateFlow()

    private val _lastSavedTime = MutableStateFlow<Long?>(null)
    val lastSavedTime: StateFlow<Long?> = _lastSavedTime.asStateFlow()

    private val _unlockedNoteIds = MutableStateFlow<Set<Long>>(emptySet())
    val unlockedNoteIds: StateFlow<Set<Long>> = _unlockedNoteIds.asStateFlow()

    private val unlockedPasswords = mutableMapOf<Long, String>()

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
                    val currentClearContent = editorContent.value
                    saveNoteToDb(title, currentClearContent)
                }
            }
        }
    }

    fun openSettings() {
        _currentScreen.value = Screen.SETTINGS
    }

    fun closeSettings() {
        _currentScreen.value = Screen.LIST
    }

    fun onSearchQueryChanged(query: String) {
        searchQuery.value = query
    }

    fun verifyPassword(note: SecureNote, enteredPass: String): Boolean {
        return CryptoUtils.verifyPassword(enteredPass, note.password)
    }

    fun unlockAndOpenNote(note: SecureNote, enteredPass: String): Boolean {
        if (!verifyPassword(note, enteredPass)) {
            return false
        }
        val decryptedContent = try {
            CryptoUtils.decrypt(note.content, enteredPass)
        } catch (e: Exception) {
            return false
        }
        _unlockedNoteIds.value = _unlockedNoteIds.value + note.id
        unlockedPasswords[note.id] = enteredPass
        openNoteInEditor(note, decryptedContent, enteredPass)
        return true
    }

    fun openUnlockedNote(note: SecureNote) {
        val cachedPass = unlockedPasswords[note.id]
        if (cachedPass != null) {
            val decryptedContent = try {
                CryptoUtils.decrypt(note.content, cachedPass)
            } catch (e: Exception) {
                note.content
            }
            openNoteInEditor(note, decryptedContent, cachedPass)
        }
    }

    private fun openNoteInEditor(note: SecureNote, decryptedContent: String, clearPassword: String) {
        _currentNote.value = note
        editorTitle.value = note.title
        editorContent.value = decryptedContent
        editorPassword.value = clearPassword
        editorPasswordHint.value = note.passwordHint
        editorColorIndex.value = note.colorIndex
        editorIsAutoSave.value = note.isAutoSaveEnabled
        _lastSavedTime.value = note.updatedAt
        _hasUnsavedChanges.value = false
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
            val cleanTitle = if (title.isBlank()) "Note" else title.trim()
            val now = System.currentTimeMillis()
            val encryptedContent = CryptoUtils.encrypt("", password)
            val hashedPassword = CryptoUtils.hashPassword(password)
            val newNote = SecureNote(
                title = cleanTitle,
                content = encryptedContent,
                password = hashedPassword,
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
            unlockedPasswords[generatedId] = password
            openNoteInEditor(createdNote, "", password)
        }
    }

    fun onTitleChange(newTitle: String) {
        editorTitle.value = newTitle
        if (!editorIsAutoSave.value) {
            _hasUnsavedChanges.value = true
        }
    }

    fun onContentChange(newContent: String) {
        editorContent.value = newContent
        if (!editorIsAutoSave.value) {
            _hasUnsavedChanges.value = true
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
        val pass = editorPassword.value
        viewModelScope.launch {
            _isSaving.value = true
            val now = System.currentTimeMillis()
            val cleanTitle = if (title.isBlank()) "Note" else title.trim()
            val encryptedContent = if (pass.isNotEmpty()) {
                CryptoUtils.encrypt(content, pass)
            } else {
                content
            }
            val passwordField = if (pass.isNotEmpty() && !activeNote.password.startsWith(CryptoUtils.PREFIX_HASH)) {
                CryptoUtils.hashPassword(pass)
            } else {
                activeNote.password
            }
            val updated = activeNote.copy(
                title = cleanTitle,
                content = encryptedContent,
                password = passwordField,
                passwordHint = editorPasswordHint.value,
                colorIndex = editorColorIndex.value,
                isAutoSaveEnabled = editorIsAutoSave.value,
                updatedAt = now
            )
            repository.updateNote(updated)
            _currentNote.value = updated
            _lastSavedTime.value = now
            _hasUnsavedChanges.value = false
            _isSaving.value = false
        }
    }

    fun updateNotePassword(newPassword: String, hint: String) {
        val activeNote = _currentNote.value ?: return
        editorPassword.value = newPassword
        editorPasswordHint.value = hint
        unlockedPasswords[activeNote.id] = newPassword
        val now = System.currentTimeMillis()
        val encryptedContent = CryptoUtils.encrypt(editorContent.value, newPassword)
        val newPasswordHash = CryptoUtils.hashPassword(newPassword)
        val updated = activeNote.copy(
            content = encryptedContent,
            password = newPasswordHash,
            passwordHint = hint.trim(),
            updatedAt = now
        )
        _currentNote.value = updated
        viewModelScope.launch {
            repository.updateNote(updated)
            _lastSavedTime.value = now
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
            unlockedPasswords.remove(note.id)
            if (_currentNote.value?.id == note.id) {
                editorTitle.value = ""
                editorContent.value = ""
                editorPassword.value = ""
                editorPasswordHint.value = ""
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
        val pass = editorPassword.value
        if (activeNote != null && pass.isNotEmpty()) {
            val cleanTitle = if (editorTitle.value.isBlank()) "Note" else editorTitle.value.trim()
            val now = System.currentTimeMillis()
            val encryptedContent = CryptoUtils.encrypt(editorContent.value, pass)
            val passwordField = if (!activeNote.password.startsWith(CryptoUtils.PREFIX_HASH)) {
                CryptoUtils.hashPassword(pass)
            } else {
                activeNote.password
            }
            val updated = activeNote.copy(
                title = cleanTitle,
                content = encryptedContent,
                password = passwordField,
                passwordHint = editorPasswordHint.value,
                colorIndex = editorColorIndex.value,
                isAutoSaveEnabled = editorIsAutoSave.value,
                updatedAt = now
            )
            viewModelScope.launch {
                repository.updateNote(updated)
            }
            _unlockedNoteIds.value = _unlockedNoteIds.value - activeNote.id
            unlockedPasswords.remove(activeNote.id)
        }
        editorTitle.value = ""
        editorContent.value = ""
        editorPassword.value = ""
        editorPasswordHint.value = ""
        _currentNote.value = null
        _currentScreen.value = Screen.LIST
    }

    fun lockNote(noteId: Long) {
        _unlockedNoteIds.value = _unlockedNoteIds.value - noteId
        unlockedPasswords.remove(noteId)
    }
}

class NoteViewModelFactory(
    private val repository: NoteRepository,
    private val settingsManager: SettingsManager
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(NoteViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return NoteViewModel(repository, settingsManager) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
