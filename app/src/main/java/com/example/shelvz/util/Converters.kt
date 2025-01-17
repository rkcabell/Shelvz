package com.example.shelvz.util

import androidx.room.TypeConverter
import com.example.shelvz.data.model.File
import com.example.shelvz.data.model.Review
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.UUID

class Converters {
    private val formatter = DateTimeFormatter.ISO_LOCAL_DATE
    private val gson = Gson()
    private val typeListString = object : TypeToken<List<String>>() {}.type
    private val typeListUuid = object : TypeToken<List<UUID>>() {}.type
    private val typeListReview = object : TypeToken<List<Review>>() {}.type

    // 1) UUID <-> String
    @TypeConverter
    fun fromUuid(uuid: UUID?): String? {
        return uuid?.toString()
    }

    @TypeConverter
    fun toUuid(uuidString: String?): UUID? {
        return uuidString?.let { UUID.fromString(it) }
    }

    // 2) LocalDate <-> String
    // ISO-8601 yyyy-mm-dd
    @TypeConverter
    fun fromLocalDate(date: LocalDate?): String? {
        return date?.format(formatter)
    }

    @TypeConverter
    fun toLocalDate(dateString: String?): LocalDate? {
        return dateString?.let { LocalDate.parse(it, formatter) }
    }


    // 3) List<String> <-> JSON
    @TypeConverter
    fun fromStringList(list: List<String>?): String? {
        return gson.toJson(list)
    }

    @TypeConverter
    fun toStringList(json: String?): List<String>? {
        return json?.let {
            gson.fromJson(it, typeListString)
        } ?: emptyList()

    }

    // 4) List<UUID> <-> JSON
    @TypeConverter
    fun fromUuidList(list: List<UUID>?): String? {
        return gson.toJson(list)
    }

    @TypeConverter
    fun toUuidList(json: String?): List<UUID>? {
        return json?.let {
            gson.fromJson(it, typeListUuid)
        } ?: emptyList()
    }

    // 5) List<Review> <-> JSON
    @TypeConverter
    fun fromReviewList(list: List<Review>?): String? {
        return gson.toJson(list)
    }

    @TypeConverter
    fun toReviewList(json: String?): List<Review>? {
        return json?.let {
            gson.fromJson(it, typeListReview)
        } ?: emptyList()
    }

    // 5) List<Review> <-> JSON
    @TypeConverter
    fun fromFileList(file: List<File>): String? {
        return gson.toJson(file)
    }

    @TypeConverter
    fun toFileList(json: String?): List<File> {
        return json?.let {
            gson.fromJson(it, typeListReview)
        } ?: emptyList()
    }

}