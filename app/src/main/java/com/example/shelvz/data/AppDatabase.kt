package com.example.shelvz.data
import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.shelvz.data.dao.BookDao
import com.example.shelvz.data.dao.FileDao
import com.example.shelvz.data.dao.MediaDao
import com.example.shelvz.data.dao.MovieDao
import com.example.shelvz.data.dao.ReviewDao
import com.example.shelvz.data.dao.UserDao
import com.example.shelvz.data.model.Book
import com.example.shelvz.data.model.UserFile
import com.example.shelvz.data.model.Media
import com.example.shelvz.data.model.Movie
import com.example.shelvz.data.model.Review
import com.example.shelvz.data.model.User
import com.example.shelvz.util.Converters
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Inject
import javax.inject.Singleton


@Database(entities = [User::class, Media::class, Book::class, Movie::class, Review::class, UserFile::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun mediaDao(): MediaDao
    abstract fun bookDao(): BookDao
    abstract fun movieDao(): MovieDao
    abstract fun reviewDao(): ReviewDao
    abstract fun fileDao(): FileDao


//    companion object {
//        val MIGRATION_1_2 = object : Migration(1, 2) {
//            override fun migrate(db: SupportSQLiteDatabase) {
//                db.execSQL("ALTER TABLE User ADD COLUMN isLoggedIn INTEGER NOT NULL DEFAULT 0")
//            }
//        }
//    }


    /**
     * Provides the singleton instance of AppDatabase.
     */
    @Module
    @InstallIn(SingletonComponent::class)
    object DatabaseProvider {
        @Provides
        @Singleton
        fun getDatabase(@ApplicationContext context: Context): AppDatabase {
            return Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                "app_database"
            )
//                .addMigrations(AppDatabase.MIGRATION_1_2)
                .fallbackToDestructiveMigration()
                .build()
        }



        @Provides
        fun provideUserDao(database: AppDatabase): UserDao {
            return database.userDao()
        }
        @Provides
        fun provideMediaDao(database: AppDatabase): MediaDao {
            return database.mediaDao()
        }
        @Provides
        fun provideMovieDao(database: AppDatabase): MovieDao {
            return database.movieDao()
        }
        @Provides
        fun provideBookDao(database: AppDatabase): BookDao {
            return database.bookDao()
        }
        @Provides
        fun provideReviewDao(database: AppDatabase): ReviewDao {
            return database.reviewDao()
        }
        @Provides
        fun provideFileDao(database: AppDatabase): FileDao {
            return database.fileDao()      }

    }


}

