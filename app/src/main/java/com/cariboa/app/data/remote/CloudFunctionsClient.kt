package com.cariboa.app.data.remote

import com.cariboa.app.data.remote.dto.*
import com.google.firebase.functions.FirebaseFunctions
import com.google.gson.Gson
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CloudFunctionsClient @Inject constructor(
    private val functions: FirebaseFunctions,
    private val gson: Gson,
) {
    suspend fun generateItinerary(request: GenerateItineraryRequest): GenerateItineraryResponse {
        val payload = gson.fromJson(gson.toJson(request), HashMap::class.java)
        val result = functions.getHttpsCallable("generateItinerary").call(payload).await()
        return gson.fromJson(gson.toJson(result.getData()), GenerateItineraryResponse::class.java)
    }

    suspend fun searchHotels(request: SearchHotelsRequest): SearchHotelsResponse {
        val payload = gson.fromJson(gson.toJson(request), HashMap::class.java)
        val result = functions.getHttpsCallable("searchHotels").call(payload).await()
        return gson.fromJson(gson.toJson(result.getData()), SearchHotelsResponse::class.java)
    }

    suspend fun findHiddenGems(request: FindHiddenGemsRequest): FindHiddenGemsResponse {
        val payload = gson.fromJson(gson.toJson(request), HashMap::class.java)
        val result = functions.getHttpsCallable("findHiddenGems").call(payload).await()
        return gson.fromJson(gson.toJson(result.getData()), FindHiddenGemsResponse::class.java)
    }
}
