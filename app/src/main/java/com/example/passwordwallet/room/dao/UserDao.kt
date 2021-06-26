package com.example.passwordwallet.room.dao

import androidx.room.*
import com.example.passwordwallet.room.entities.User

@Dao
interface UserDao {
    @Insert
    fun insertUser(user: User)

    @Delete
    fun deleteUser(user: User)

    @Query("SELECT * FROM User")
    fun getUser(): User

    @Query("DELETE FROM User")
    fun deleteUserTable()

    @Update
    fun updateUser(user: User)
}