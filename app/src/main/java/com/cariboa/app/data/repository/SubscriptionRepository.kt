package com.cariboa.app.data.repository

import com.cariboa.app.domain.model.SubscriptionTier
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SubscriptionRepository @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository,
) {
    suspend fun getSubscriptionStatus(): SubscriptionTier {
        val user = userRepository.getUser()
        return user?.subscriptionTier ?: SubscriptionTier.TRIAL
    }

    suspend fun checkAndUpdateSubscriptionStatus() {
        val user = userRepository.getUser() ?: return
        if (user.subscriptionTier != SubscriptionTier.PRO) return
        val now = System.currentTimeMillis()
        val expiry = user.subscriptionExpiry ?: return
        val gracePeriod = 3 * 24 * 60 * 60 * 1000L
        if (now > expiry + gracePeriod) {
            val uid = authRepository.currentUser?.uid ?: return
            firestore.document("users/$uid").update("subscriptionTier", "TRIAL").await()
        }
    }
}
