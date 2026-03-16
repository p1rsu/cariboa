package com.cariboa.app.data.repository

import com.cariboa.app.data.remote.CloudFunctionsClient
import com.cariboa.app.data.remote.dto.FindHiddenGemsRequest
import com.cariboa.app.domain.model.HiddenGem
import com.cariboa.app.domain.model.TravelInterest
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HiddenGemRepository @Inject constructor(
    private val cloudFunctions: CloudFunctionsClient,
) {
    suspend fun findHiddenGems(destination: String, categories: List<String>? = null): List<HiddenGem> {
        val response = cloudFunctions.findHiddenGems(FindHiddenGemsRequest(destination, categories))
        return response.gems.map { g ->
            HiddenGem(
                name = g.name,
                placeId = g.placeId,
                description = g.description,
                category = runCatching { TravelInterest.valueOf(g.category.uppercase()) }.getOrDefault(TravelInterest.CULTURE),
                aiReason = g.reason,
            )
        }
    }
}
