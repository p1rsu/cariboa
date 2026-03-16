package com.cariboa.app.domain.usecase

import com.cariboa.app.data.repository.UsageRepository
import com.cariboa.app.data.repository.UserRepository
import com.cariboa.app.domain.model.SubscriptionTier
import javax.inject.Inject

class CheckUsageLimitsUseCase @Inject constructor(
    private val usageRepository: UsageRepository,
    private val userRepository: UserRepository,
) {
    suspend fun canGenerateItinerary(): Boolean {
        val user = userRepository.getUser() ?: return false
        if (user.subscriptionTier == SubscriptionTier.PRO) return true
        return usageRepository.getUsage().canGenerateItinerary()
    }
    suspend fun canSearchHiddenGems(): Boolean {
        val user = userRepository.getUser() ?: return false
        if (user.subscriptionTier == SubscriptionTier.PRO) return true
        return usageRepository.getUsage().canSearchHiddenGems()
    }
    suspend fun canSearchHotels(): Boolean {
        val user = userRepository.getUser() ?: return false
        if (user.subscriptionTier == SubscriptionTier.PRO) return true
        return usageRepository.getUsage().canSearchHotels()
    }
}
