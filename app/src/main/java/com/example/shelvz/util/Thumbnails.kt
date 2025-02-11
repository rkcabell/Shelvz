package com.example.shelvz.util

import com.example.shelvz.R

object Thumbnails {
    private val thumbnailMap = mapOf(
        "Arts" to R.drawable.thumbnail_arts,
        "Animals" to R.drawable.thumbnail_animals,
        "Fiction" to R.drawable.thumbnail_fiction,
        "Science & Mathematics" to R.drawable.thumbnail_science,
        "Business & Finance" to R.drawable.thumbnail_business,
        "Children's" to R.drawable.thumbnail_children,
        "History" to R.drawable.thumbnail_history,
        "Health & Wellness" to R.drawable.thumbnail_health,
        "Biography" to R.drawable.thumbnail_biography,
        "Social Sciences" to R.drawable.thumbnail_social_sciences,
        "Places" to R.drawable.thumbnail_places,
        "Textbooks" to R.drawable.thumbnail_textbooks
    )

    private val defaultThumbnail = R.drawable.thumbnail_default

    fun getThumbnail(subject: String): Int {
        return thumbnailMap[subject] ?: defaultThumbnail
    }
}