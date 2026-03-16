package com.cariboa.app.data.repository

import com.cariboa.app.data.remote.CloudFunctionsClient
import com.cariboa.app.data.remote.dto.SearchHotelsRequest
import com.cariboa.app.domain.model.Hotel
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HotelRepository @Inject constructor(
    private val cloudFunctions: CloudFunctionsClient,
) {
    suspend fun searchHotels(destination: String, checkIn: String, checkOut: String,
                             priceLevel: Int? = null, minRating: Double? = null): List<Hotel> {
        val response = cloudFunctions.searchHotels(SearchHotelsRequest(destination, checkIn, checkOut, priceLevel, minRating))
        return response.hotels.map { h ->
            Hotel(name = h.name, placeId = h.placeId, priceLevel = h.priceLevel ?: 0,
                rating = h.rating ?: 0.0, photoRef = h.photoRef)
        }
    }
}
