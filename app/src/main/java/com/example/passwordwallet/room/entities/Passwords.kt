package com.example.passwordwallet.room.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity
data class Passwords(
    @PrimaryKey val id: UUID = UUID.randomUUID(),
    @ColumnInfo(name="description") val description: String,
    @ColumnInfo(name="password") val password: String
)
