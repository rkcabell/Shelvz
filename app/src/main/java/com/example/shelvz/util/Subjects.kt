package com.example.shelvz.util

object Subjects {

    // List of primary subjects
    val primarySubjects = listOf(
        "Arts", "Animals", "Fiction", "Science & Mathematics", "Business & Finance",
        "Children's", "History", "Health & Wellness", "Biography", "Social Sciences",
        "Places", "Textbooks"
    )

    // Aliases map: Key is the alias, value is the primary subject
    private val subjectAliases = mapOf(
        // Arts
        "Architecture" to "Arts",
        "Art Instruction" to "Arts",
        "Art History" to "Arts",
        "Dance" to "Arts",
        "Design" to "Arts",
        "Fashion" to "Arts",
        "Film" to "Arts",
        "Graphic Design" to "Arts",
        "Music" to "Arts",
        "Music Theory" to "Arts",
        "Painting" to "Arts",
        "Photography" to "Arts",

        // Animals
        "Bears" to "Animals",
        "Cats" to "Animals",
        "Kittens" to "Animals",
        "Dogs" to "Animals",
        "Puppies" to "Animals",

        // Fiction
        "Fantasy" to "Fiction",
        "Historical Fiction" to "Fiction",
        "Horror" to "Fiction",
        "Humor" to "Fiction",
        "Literature" to "Fiction",
        "Magic" to "Fiction",
        "Mystery and detective stories" to "Fiction",
        "Plays" to "Fiction",
        "Poetry" to "Fiction",
        "Romance" to "Fiction",
        "Science Fiction" to "Fiction",
        "Short Stories" to "Fiction",
        "Thriller" to "Fiction",
        "Young Adult" to "Fiction",

        // Science & Mathematics
        "Biology" to "Science & Mathematics",
        "Chemistry" to "Science & Mathematics",
        "Mathematics" to "Science & Mathematics",
        "Physics" to "Science & Mathematics",
        "Programming" to "Science & Mathematics",

        // Business & Finance
        "Management" to "Business & Finance",
        "Entrepreneurship" to "Business & Finance",
        "Business Economics" to "Business & Finance",
        "Business Success" to "Business & Finance",
        "Finance" to "Business & Finance",

        // Children's
        "Kids Books" to "Children's",
        "Stories in Rhyme" to "Children's",
        "Baby Books" to "Children's",
        "Bedtime Books" to "Children's",
        "Picture Books" to "Children's",

        // History
        "Ancient Civilization" to "History",
        "Archaeology" to "History",
        "Anthropology" to "History",
        "World War II" to "History",
        "Social Life and Customs" to "History",

        // Health & Wellness
        "Cooking" to "Health & Wellness",
        "Cookbooks" to "Health & Wellness",
        "Mental Health" to "Health & Wellness",
        "Exercise" to "Health & Wellness",
        "Nutrition" to "Health & Wellness",
        "Self-help" to "Health & Wellness",

        // Biography
        "Autobiographies" to "Biography",
        "History" to "Biography",
        "Politics and Government" to "Biography",
        "World War II" to "Biography",
        "Women" to "Biography",
        "Kings and Rulers" to "Biography",
        "Composers" to "Biography",
        "Artists" to "Biography",

        // Social Sciences
        "Anthropology" to "Social Sciences",
        "Religion" to "Social Sciences",
        "Political Science" to "Social Sciences",
        "Psychology" to "Social Sciences",

        // Places
        "Brazil" to "Places",
        "India" to "Places",
        "Indonesia" to "Places",
        "United States" to "Places",

        // Textbooks
        "History" to "Textbooks",
        "Mathematics" to "Textbooks",
        "Geography" to "Textbooks",
        "Psychology" to "Textbooks",
        "Algebra" to "Textbooks",
        "Education" to "Textbooks",
        "Business & Economics" to "Textbooks",
        "Science" to "Textbooks",
        "Chemistry" to "Textbooks",
        "English Language" to "Textbooks",
        "Physics" to "Textbooks",
        "Computer Science" to "Textbooks"
    )

    // Resolve a subject or alias to its primary subject
    fun resolveSubject(input: String): String {
        return subjectAliases[input] ?: input // Return the alias mapping or the input itself
    }
}
