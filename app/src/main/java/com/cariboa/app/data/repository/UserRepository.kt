package com.cariboa.app.data.repository

import com.cariboa.app.domain.model.*
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepository @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val authRepository: AuthRepository,
) {
    private fun userDoc() = firestore.collection("users")
        .document(authRepository.currentUser?.uid ?: throw IllegalStateException("Not authenticated"))

    suspend fun getUser(): User? {
        val doc = userDoc().get().await()
        if (!doc.exists()) return null
        return doc.toUser()
    }

    suspend fun createUser(user: User) {
        userDoc().set(user.toMap(), SetOptions.merge()).await()
    }

    suspend fun updateInterests(interests: List<TravelInterest>) {
        userDoc().update("interests", interests.map { it.name }).await()
    }

    suspend fun deleteUserData() {
        val uid = authRepository.currentUser?.uid ?: return
        val trips = firestore.collection("users/$uid/trips").get().await()
        trips.documents.forEach { it.reference.delete().await() }
        val usage = firestore.collection("users/$uid/usage").get().await()
        usage.documents.forEach { it.reference.delete().await() }
        userDoc().delete().await()
    }

    private fun com.google.firebase.firestore.DocumentSnapshot.toUser(): User = User(
        uid = id,
        email = getString("email") ?: "",
        displayName = getString("displayName") ?: "",
        photoUrl = getString("photoUrl"),
        subscriptionTier = SubscriptionTier.valueOf(getString("subscriptionTier") ?: "TRIAL"),
        subscriptionExpiry = getLong("subscriptionExpiry"),
        interests = (get("interests") as? List<*>)
            ?.mapNotNull { runCatching { TravelInterest.valueOf(it as String) }.getOrNull() }
            ?: emptyList(),
        createdAt = getLong("createdAt") ?: System.currentTimeMillis(),
        lastActive = getLong("lastActive") ?: System.currentTimeMillis(),
        schemaVersion = getLong("schemaVersion")?.toInt() ?: 1,
    )

    private fun User.toMap() = mapOf(
        "email" to email,
        "displayName" to displayName,
        "photoUrl" to photoUrl,
        "subscriptionTier" to subscriptionTier.name,
        "subscriptionExpiry" to subscriptionExpiry,
        "interests" to interests.map { it.name },
        "createdAt" to createdAt,
        "lastActive" to lastActive,
        "schemaVersion" to schemaVersion,
    )
}
