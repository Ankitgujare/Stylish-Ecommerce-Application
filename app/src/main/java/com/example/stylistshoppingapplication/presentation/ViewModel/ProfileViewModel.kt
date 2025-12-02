package com.example.stylistshoppingapplication.presentation.ViewModel

import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.stylistshoppingapplication.data.local.model.ProfileEntity
import com.example.stylistshoppingapplication.data.local.repository.ProfileRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

data class ProfileUiState(
    val displayName: String = "",
    val email: String = "",
    val photoUrl: String? = null,
    val pincode: String = "",
    val address: String = "",
    val city: String = "",
    val state: String = "",
    val country: String = "",
    val bankAccount: String = "",
    val accountHolder: String = "",
    val ifscCode: String = "",
    val isLoading: Boolean = false,
    val isUploading: Boolean = false,
    val error: String? = null,
    val isSaved: Boolean = false
)

class ProfileViewModel(private val context: Context) : ViewModel() {
    private val repository = ProfileRepository(context)
    private val auth = FirebaseAuth.getInstance()
    private val storage = FirebaseStorage.getInstance()

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {
        loadProfile()
    }

    private fun loadProfile() {
        val user = auth.currentUser
        if (user != null) {
            viewModelScope.launch {
                _uiState.value = _uiState.value.copy(isLoading = true)
                try {
                    // Try to get from local DB first
                    val localProfile = repository.getProfileById(user.uid).firstOrNull()
                    
                    if (localProfile != null) {
                        _uiState.value = _uiState.value.copy(
                            displayName = localProfile.name,
                            email = localProfile.email,
                            photoUrl = localProfile.photoUrl ?: user.photoUrl?.toString(),
                            pincode = localProfile.pincode,
                            address = localProfile.address,
                            city = localProfile.city,
                            state = localProfile.state,
                            country = localProfile.country,
                            bankAccount = localProfile.bankAccount,
                            accountHolder = localProfile.accountHolder,
                            ifscCode = localProfile.ifscCode,
                            isLoading = false
                        )
                    } else {
                        // Fallback to Firebase Auth data
                        _uiState.value = _uiState.value.copy(
                            displayName = user.displayName ?: "",
                            email = user.email ?: "",
                            photoUrl = user.photoUrl?.toString(),
                            isLoading = false
                        )
                    }
                } catch (e: Exception) {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = "Failed to load profile: ${e.message}"
                    )
                }
            }
        }
    }

    fun updateField(field: String, value: String) {
        _uiState.value = when (field) {
            "displayName" -> _uiState.value.copy(displayName = value)
            "email" -> _uiState.value.copy(email = value)
            "pincode" -> _uiState.value.copy(pincode = value)
            "address" -> _uiState.value.copy(address = value)
            "city" -> _uiState.value.copy(city = value)
            "state" -> _uiState.value.copy(state = value)
            "country" -> _uiState.value.copy(country = value)
            "bankAccount" -> _uiState.value.copy(bankAccount = value)
            "accountHolder" -> _uiState.value.copy(accountHolder = value)
            "ifscCode" -> _uiState.value.copy(ifscCode = value)
            else -> _uiState.value
        }
    }

    fun saveProfile() {
        val user = auth.currentUser ?: return
        val state = _uiState.value

        viewModelScope.launch {
            _uiState.value = state.copy(isLoading = true, isSaved = false)
            try {
                // 1. Update Firebase Profile (Display Name)
                if (state.displayName != user.displayName) {
                    val profileUpdates = UserProfileChangeRequest.Builder()
                        .setDisplayName(state.displayName)
                        .build()
                    user.updateProfile(profileUpdates).await()
                }

                // 2. Save to Local DB
                val profileEntity = ProfileEntity(
                    id = user.uid,
                    name = state.displayName,
                    email = state.email,
                    photoUrl = state.photoUrl,
                    pincode = state.pincode,
                    address = state.address,
                    city = state.city,
                    state = state.state,
                    country = state.country,
                    bankAccount = state.bankAccount,
                    accountHolder = state.accountHolder,
                    ifscCode = state.ifscCode
                )
                repository.insertProfile(profileEntity)

                _uiState.value = state.copy(isLoading = false, isSaved = true)
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Profile Saved Successfully", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                _uiState.value = state.copy(isLoading = false, error = e.message)
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Failed to save: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    fun uploadImage(uri: Uri) {
        val user = auth.currentUser ?: return
        
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isUploading = true)
            
            try {
                val inputStream = context.contentResolver.openInputStream(uri)
                    ?: throw Exception("Could not open input stream")
                
                val bytes = inputStream.use { it.readBytes() }
                
                if (bytes.isEmpty()) {
                    throw Exception("File is empty")
                }

                // Create directory if it doesn't exist
                val imagesDir = java.io.File(context.filesDir, "profile_images")
                if (!imagesDir.exists()) {
                    imagesDir.mkdirs()
                }

                // Create local file
                val localFile = java.io.File(imagesDir, "${user.uid}.jpg")
                localFile.writeBytes(bytes)
                
                val localPath = localFile.absolutePath
                android.util.Log.d("ProfileViewModel", "Image saved locally to: $localPath")

                // Update Local State
                _uiState.value = _uiState.value.copy(photoUrl = localPath)

                // Save to DB immediately to persist the path
                saveProfile()
                
                _uiState.value = _uiState.value.copy(isUploading = false)
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Image Saved Successfully", Toast.LENGTH_SHORT).show()
                }
                
            } catch (e: Exception) {
                e.printStackTrace()
                android.util.Log.e("ProfileViewModel", "Error saving image: ${e.message}", e)
                
                _uiState.value = _uiState.value.copy(isUploading = false, error = "Failed to save image: ${e.message}")
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Failed to save image", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
    
    fun resetSavedState() {
        _uiState.value = _uiState.value.copy(isSaved = false)
    }
}
