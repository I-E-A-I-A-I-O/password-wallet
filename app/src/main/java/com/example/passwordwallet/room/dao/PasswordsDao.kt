package com.example.passwordwallet.room.dao

import androidx.room.*
import com.example.passwordwallet.room.entities.Passwords
import java.util.*

@Dao
interface PasswordsDao {
    @Insert
    fun insertPassword(password: Passwords)

    @Insert
    fun insertPasswords(vararg passwords: Passwords)

    @Query("SELECT * FROM Passwords")
    fun getPasswords(): List<Passwords>

    @Delete
    fun deletePassword(password: Passwords)

    @Delete
    fun deletePasswords(vararg password: Passwords)

    @Query("SELECT * FROM Passwords WHERE id = :id")
    fun selectById(id: UUID): Passwords

    @Query("DELETE FROM Passwords")
    fun deletePasswordTable()

    @Update
    fun updatePasswordRecord(password: Passwords)

    @Update
    fun updatePasswordRecords(vararg passwords: Passwords)

    @Query("UPDATE Passwords SET id = :newId WHERE id = :oldId")
    fun updateId(newId: UUID, oldId: UUID)

    @Query("DELETE FROM Passwords WHERE id = :id")
    fun deleteById(id: UUID)
}