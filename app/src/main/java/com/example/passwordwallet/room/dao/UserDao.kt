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
    fun getUser(): List<User>

    @Query("DELETE FROM User")
    fun deleteUserTable()

    @Update
    fun updateUser(user: User)

    @Query("UPDATE User SET access_token = :newToken WHERE access_token = :oldToken")
    fun updateAccessToken(newToken: String, oldToken: String)
}