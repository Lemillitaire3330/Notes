package com.example.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface SecureNoteDao {
    @Query("SELECT * FROM secure_notes ORDER BY isPinned DESC, updatedAt DESC")
    fun getAllNotes(): Flow<List<SecureNote>>

    @Query("SELECT * FROM secure_notes WHERE id = :id")
    fun getNoteById(id: Long): Flow<SecureNote?>

    @Query("SELECT * FROM secure_notes WHERE id = :id LIMIT 1")
    suspend fun getNoteByIdOnce(id: Long): SecureNote?

    @Query("SELECT * FROM secure_notes WHERE title LIKE '%' || :query || '%' ORDER BY isPinned DESC, updatedAt DESC")
    fun searchNotes(query: String): Flow<List<SecureNote>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNote(note: SecureNote): Long

    @Update
    suspend fun updateNote(note: SecureNote)

    @Delete
    suspend fun deleteNote(note: SecureNote)

    @Query("DELETE FROM secure_notes WHERE id = :id")
    suspend fun deleteNoteById(id: Long)

    @Query("UPDATE secure_notes SET isPinned = :isPinned WHERE id = :id")
    suspend fun updatePinStatus(id: Long, isPinned: Boolean)
}
