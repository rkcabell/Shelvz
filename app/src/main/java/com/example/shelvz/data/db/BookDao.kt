package com.example.shelvz.data.db

import androidx.room.Dao
import androidx.room.Database
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.RoomDatabase
import com.example.shelvz.data.model.Book
import com.example.shelvz.data.model.Media
import com.example.shelvz.data.model.User
import java.util.UUID

@Dao
interface BookDao {

    @Insert
    suspend fun insertBook(book: Book): Any

    @Delete
    suspend fun deleteBook(book: Book): Any

    @Query("SELECT * FROM books WHERE mediaId = :mediaId")
    suspend fun getBookById(mediaId: UUID): Book

    @Query("SELECT m.summary \n" +
            "    FROM Media m \n" +
            "    INNER JOIN books b ON m.mediaId = b.mediaId\n" +
            "    WHERE b.mediaId = :mediaId")
    suspend fun getBookSummaryById(mediaId: UUID): String
}

