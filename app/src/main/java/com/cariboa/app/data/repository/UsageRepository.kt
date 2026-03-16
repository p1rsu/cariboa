package com.cariboa.app.data.repository

import com.cariboa.app.domain.model.UsageInfo
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UsageRepository @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val authRepository: AuthRepository,
) {
    suspend fun getUsage(): UsageInfo {
        val uid = authRepository.currentUser?.uid ?: return UsageInfo()
        val doc = firestore.document("users/$uid/usage/lifetime").get().await()
        if (!doc.exists()) return UsageInfo()
        return UsageInfo(
            itinerariesGenerated = doc.getLong("itinerariesGenerated")?.toInt() ?: 0,
            hiddenGemSearches = doc.getLong("hiddenGemSearches")?.toInt() ?: 0,
            hotelSearches = doc.getLong("hotelSearches")?.toInt() ?: 0,
        )
    }
}
