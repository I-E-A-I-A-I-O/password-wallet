package com.example.passwordwallet.room.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity
data class User(
    @PrimaryKey val id: UUID = UUID.randomUUID(),
    @ColumnInfo(name="name") val name: String,
    @ColumnInfo(name="email") val email: String,
    @ColumnInfo(name="access_token") val token: String
)
