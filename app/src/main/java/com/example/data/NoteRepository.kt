package com.example.data

import kotlinx.coroutines.flow.Flow

class NoteRepository(private val noteDao: SecureNoteDao) {
    val allNotes: Flow<List<SecureNote>> = noteDao.getAllNotes()

    fun getNoteById(id: Long): Flow<SecureNote?> = noteDao.getNoteById(id)

    suspend fun getNoteByIdOnce(id: Long): SecureNote? = noteDao.getNoteByIdOnce(id)

    fun searchNotes(query: String): Flow<List<SecureNote>> {
        return if (query.isBlank()) {
            noteDao.getAllNotes()
        } else {
            noteDao.searchNotes(query.trim())
        }
    }

    suspend fun insertNote(note: SecureNote): Long = noteDao.insertNote(note)

    suspend fun updateNote(note: SecureNote) = noteDao.updateNote(note)

    suspend fun deleteNote(note: SecureNote) = noteDao.deleteNote(note)

    suspend fun deleteNoteById(id: Long) = noteDao.deleteNoteById(id)

    suspend fun togglePin(id: Long, currentPinState: Boolean) {
        noteDao.updatePinStatus(id, !currentPinState)
    }
}
