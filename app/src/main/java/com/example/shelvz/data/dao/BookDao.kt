package com.example.shelvz.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.shelvz.data.model.Book
import java.util.UUID

@Dao
interface BookDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBook(book: Book): Long

    @Delete
    suspend fun deleteBook(book: Book)

    @Query("SELECT * FROM books WHERE mediaId = :mediaId")
    suspend fun getBookById(mediaId: UUID): Book

    @Query("SELECT m.summary \n" +
            "    FROM Media m \n" +
            "    INNER JOIN books b ON m.mediaId = b.mediaId\n" +
            "    WHERE b.mediaId = :mediaId")
    suspend fun getBookSummaryById(mediaId: UUID): String

    @Query("""
    SELECT b.*, 
           m.title AS mediaTitle, m.summary AS summary, 
           m.releaseDate AS releaseDate, m.mediaType AS mediaType, 
           m.averageRating AS averageRating, m.thumbnailPath AS thumbnailPath
    FROM Media m
    INNER JOIN books b ON m.mediaId = b.mediaId
    WHERE b.subject = :subject
""")
    suspend fun getBooksBySubject(subject: String): List<Book>
}

