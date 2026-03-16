package com.cariboa.app.data.local.mapper

import com.cariboa.app.data.local.entity.TripEntity
import com.cariboa.app.domain.model.*
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

object TripMapper {
    private val gson = Gson()

    fun Trip.toEntity(): TripEntity = TripEntity(
        id = id,
        destination = destination,
        startDate = startDate,
        endDate = endDate,
        travelers = travelers,
        interests = gson.toJson(interests.map { it.name }),
        budgetLevel = budgetLevel.name,
        itinerary = gson.toJson(itinerary),
        hotels = gson.toJson(hotels),
        hiddenGems = gson.toJson(hiddenGems),
        status = status.name,
        createdAt = createdAt,
        updatedAt = updatedAt,
        schemaVersion = schemaVersion,
    )

    fun TripEntity.toDomain(): Trip {
        val interestNames: List<String> = gson.fromJson(interests, object : TypeToken<List<String>>() {}.type)
        val days: List<ItineraryDay> = gson.fromJson(itinerary, object : TypeToken<List<ItineraryDay>>() {}.type) ?: emptyList()
        val hotelList: List<Hotel> = gson.fromJson(hotels, object : TypeToken<List<Hotel>>() {}.type) ?: emptyList()
        val gemList: List<HiddenGem> = gson.fromJson(hiddenGems, object : TypeToken<List<HiddenGem>>() {}.type) ?: emptyList()

        return Trip(
            id = id,
            destination = destination,
            startDate = startDate,
            endDate = endDate,
            travelers = travelers,
            interests = interestNames.mapNotNull { runCatching { TravelInterest.valueOf(it) }.getOrNull() },
            budgetLevel = BudgetLevel.valueOf(budgetLevel),
            itinerary = days,
            hotels = hotelList,
            hiddenGems = gemList,
            status = TripStatus.valueOf(status),
            createdAt = createdAt,
            updatedAt = updatedAt,
            schemaVersion = schemaVersion,
        )
    }
}
