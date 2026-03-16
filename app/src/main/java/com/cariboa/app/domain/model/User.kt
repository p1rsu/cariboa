package com.cariboa.app.domain.model

data class User(
    val uid: String,
    val email: String,
    val displayName: String,
    val photoUrl: String?,
    val subscriptionTier: SubscriptionTier,
    val subscriptionExpiry: Long?,
    val interests: List<TravelInterest>,
    val createdAt: Long = System.currentTimeMillis(),
    val lastActive: Long = System.currentTimeMillis(),
    val schemaVersion: Int = 1,
)
