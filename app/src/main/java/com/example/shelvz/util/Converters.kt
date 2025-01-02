package com.example.shelvz.util

import androidx.room.TypeConverter
import java.time.LocalDate

class Converters {

    // Convert List<String> to a single String, separating items by a delimiter
    @TypeConverter
    fun fromStringList(value: List<String>): String {
        return value.joinToString(separator = "|")
    }

    // Convert a single String back into a List<String>, splitting by the same delimiter
    @TypeConverter
    fun toStringList(value: String): List<String> {
        return if (value.isEmpty()) emptyList() else value.split("|")
    }

    @TypeConverter
    fun fromLocalDate(date: LocalDate): String {
        return date.toString() // Convert LocalDate to String
    }

    @TypeConverter
    fun toLocalDate(dateString: String): LocalDate {
        return LocalDate.parse(dateString) // Convert String to LocalDate
    }
}