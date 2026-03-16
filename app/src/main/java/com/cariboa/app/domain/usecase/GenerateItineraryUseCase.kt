package com.cariboa.app.domain.usecase

import com.cariboa.app.data.remote.CloudFunctionsClient
import com.cariboa.app.data.remote.dto.GenerateItineraryRequest
import com.cariboa.app.data.repository.TripRepository
import com.cariboa.app.domain.model.*
import javax.inject.Inject

class GenerateItineraryUseCase @Inject constructor(
    private val cloudFunctions: CloudFunctionsClient,
    private val tripRepository: TripRepository,
) {
    suspend operator fun invoke(request: GenerateItineraryRequest): Trip {
        val response = cloudFunctions.generateItinerary(request)
        val trip = Trip(
            destination = request.destination,
            startDate = 0L, // Will be set properly by caller
            endDate = 0L,
            travelers = request.travelers,
            interests = request.interests.mapNotNull { runCatching { TravelInterest.valueOf(it.uppercase()) }.getOrNull() },
            budgetLevel = BudgetLevel.valueOf(request.budgetLevel.uppercase()),
            itinerary = response.days.map { day ->
                ItineraryDay(day = day.day, activities = day.activities.map { a ->
                    Activity(time = a.time, title = a.title, description = a.description,
                        lat = a.lat, lng = a.lng,
                        type = runCatching { TravelInterest.valueOf(a.type.uppercase()) }.getOrDefault(TravelInterest.CULTURE),
                        placeId = a.placeId)
                })
            },
            hotels = response.hotelSuggestions.map { h ->
                Hotel(name = h.name, placeId = h.placeId, priceLevel = h.priceLevel ?: 0,
                    rating = h.rating ?: 0.0, photoRef = h.photoRef)
            },
            hiddenGems = response.hiddenGems.map { g ->
                HiddenGem(name = g.name, placeId = g.placeId, description = g.description,
                    category = runCatching { TravelInterest.valueOf(g.category.uppercase()) }.getOrDefault(TravelInterest.CULTURE),
                    aiReason = g.reason)
            },
            status = TripStatus.DRAFT,
        )
        tripRepository.saveTrip(trip)
        return trip
    }
}
