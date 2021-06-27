package com.example.passwordwallet.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.passwordwallet.room.converters.UUIDConverter
import com.example.passwordwallet.room.dao.PasswordsDao
import com.example.passwordwallet.room.dao.UserDao
import com.example.passwordwallet.room.entities.Passwords
import com.example.passwordwallet.room.entities.User

@Database(entities = [User::class, Passwords::class], version = 2)
@TypeConverters(UUIDConverter::class)
abstract class AppDatabase: RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun passwordsDao(): PasswordsDao

    companion object {
        @Volatile private var instance: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return instance ?: synchronized(this) {
                instance ?: buildDatabase(context).also {instance = it}
            }
        }

        private fun buildDatabase(context: Context): AppDatabase {
            return Room.databaseBuilder(context, AppDatabase::class.java, "app-storage").build()
        }
    }
}