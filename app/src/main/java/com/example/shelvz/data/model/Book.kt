package com.example.shelvz.data.model

/*

 */


import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class Book(
    @PrimaryKey val id: Int,
    val name: String,
    val summary: String
)