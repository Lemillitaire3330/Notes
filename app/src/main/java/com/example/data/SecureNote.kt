package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "secure_notes")
data class SecureNote(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val content: String,
    val password: String,
    val passwordHint: String = "",
    val colorIndex: Int = 0,
    val isPinned: Boolean = false,
    val isAutoSaveEnabled: Boolean = true,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
