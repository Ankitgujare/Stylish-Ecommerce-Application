package com.example.stylistshoppingapplication.data.local.repository

import android.content.Context
import com.example.stylistshoppingapplication.data.local.database.ProfileDatabase
import com.example.stylistshoppingapplication.data.local.model.ProfileEntity
import kotlinx.coroutines.flow.Flow

class ProfileRepository(context: Context) {
    private val profileDao = ProfileDatabase.getDatabase(context).profileDao()

    fun getProfileById(id: String): Flow<ProfileEntity?> {
        return profileDao.getProfileById(id)
    }

    suspend fun insertProfile(profile: ProfileEntity) {
        profileDao.insertProfile(profile)
    }

    suspend fun updateProfile(profile: ProfileEntity) {
        profileDao.updateProfile(profile)
    }

    suspend fun deleteProfile(id: String) {
        profileDao.deleteProfile(id)
    }
}