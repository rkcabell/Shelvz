package com.example.shelvz.ui.user
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shelvz.data.model.User
import com.example.shelvz.data.repository.UserRepository
import kotlinx.coroutines.launch
import java.util.UUID

/*
Stores and manages the ModelUiState.
Fetches or updates data via the ModelRepository.
Processes UI events and updates the state reactively.
 */

class UserViewModel(private val userRepository: UserRepository) : ViewModel() {

    // LiveData to observe user bio
    private val _userBio = MutableLiveData<String>()
    val userBio: LiveData<String> get() = _userBio

    // LiveData to observe rated media
    private val _ratedMedia = MutableLiveData<Map<UUID, Float>>()
    val ratedMedia: LiveData<Map<UUID, Float>> get() = _ratedMedia

    // LiveData to observe reviewed media
    private val _userReviews = MutableLiveData<Map<UUID, String?>>()
    val userReviews: LiveData<Map<UUID, String?>> get() = _userReviews

    // Insert a new user
    fun insertUser(user: User) {
        viewModelScope.launch {
            userRepository.insertUser(user)
        }
    }

    // Delete a user
    fun deleteUser(user: User) {
        viewModelScope.launch {
            userRepository.deleteUser(user)
        }
    }

    // Update user bio
    fun updateBio(userId: UUID, newBio: String) {
        viewModelScope.launch {
            userRepository.updateBio(userId, newBio)
            _userBio.postValue(newBio) // Update LiveData after the operation
        }
    }

    // Get user bio
    fun loadUserBio(userId: UUID) {
        viewModelScope.launch {
            val bio = userRepository.getUserBio(userId)
            _userBio.postValue(bio.bio) // Update LiveData with fetched bio
        }
    }

    // Get rated media
    fun loadRatedMedia(userId: UUID) {
        viewModelScope.launch {
            val ratings = userRepository.getRatedMedia(userId)
            _ratedMedia.postValue(ratings) // Update LiveData with fetched ratings
        }
    }

    // Add or update a media rating
    fun addOrUpdateRating(userId: UUID, mediaId: UUID, rating: Float?) {
        viewModelScope.launch {
            val currentRatings = _ratedMedia.value?.toMutableMap() ?: mutableMapOf()
            if (rating != null) {
                currentRatings[mediaId] = rating
            } else {
                // Remove the rating if it's null
                currentRatings.remove(mediaId)
            }

            // Update in repository and LiveData
            userRepository.updateRatedMedia(userId, currentRatings)
            _ratedMedia.postValue(currentRatings)
        }
    }

    // Add or update a media rating
    fun addOrUpdateReview(userId: UUID, mediaId: UUID, review: String?) {
        viewModelScope.launch {
            val currentReviews = _userReviews.value?.toMutableMap() ?: mutableMapOf()

            if (review != null) {
                currentReviews[mediaId] = review
            } else {
                // Remove the review if it's null
                currentReviews.remove(mediaId)
            }

            // Update in repository and LiveData
            userRepository.updateReviewedMedia(userId, currentReviews)
            _userReviews.postValue(currentReviews)
        }
    }
}
