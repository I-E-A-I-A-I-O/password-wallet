package com.example.passwordwallet.room.converters

import androidx.room.TypeConverter
import java.util.*

class UUIDConverter {
    @TypeConverter
    fun fromString(value: String): UUID {
        return UUID.fromString(value)
    }

    @TypeConverter
    fun uuidToString(value: UUID): String {
        return value.toString()
    }
}