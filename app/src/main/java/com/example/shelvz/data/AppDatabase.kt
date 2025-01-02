package com.example.shelvz.data
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.shelvz.data.db.BookDao
import com.example.shelvz.data.db.MediaDao
import com.example.shelvz.data.db.MovieDao
import com.example.shelvz.data.db.UserDao
import com.example.shelvz.data.model.Book
import com.example.shelvz.data.model.Media
import com.example.shelvz.data.model.Movie
import com.example.shelvz.data.model.User
import com.example.shelvz.util.Converters

@Database(entities = [User::class, Media::class, Book::class, Movie::class], version = 1)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun mediaDao(): MediaDao
    abstract fun bookDao(): BookDao
    abstract fun movieDao(): MovieDao
}

