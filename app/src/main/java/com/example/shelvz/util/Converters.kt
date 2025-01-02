package com.example.shelvz.util

import androidx.room.TypeConverter
import java.time.LocalDate
import java.util.UUID

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

    // Convert the map to a single string: "UUID1:Review1|UUID2:Review2|..."
    @TypeConverter
    fun fromReviewsMap(map: Map<UUID, String?>): String {
        return map.entries.joinToString(separator = "|") { entry ->
            "${entry.key}:${entry.value ?: ""}" // Handle null reviews as empty strings
        }
    }

    // Convert the string back to a map
    @TypeConverter
    fun toReviewsMap(value: String): Map<UUID, String?> {
        if (value.isEmpty()) return emptyMap()
        return value.split("|").associate {
            val parts = it.split(":")
            val uuid = UUID.fromString(parts[0]) // First part is the UUID
            val review = if (parts.size > 1 && parts[1].isNotEmpty()) parts[1] else null // Handle empty or null reviews
            uuid to review
        }
    }
}