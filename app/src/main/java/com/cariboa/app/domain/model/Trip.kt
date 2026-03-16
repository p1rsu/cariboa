package com.cariboa.app.domain.model

data class Trip(
    val id: String = "",
    val destination: String,
    val startDate: Long,
    val endDate: Long,
    val travelers: Int,
    val interests: List<TravelInterest>,
    val budgetLevel: BudgetLevel,
    val itinerary: List<ItineraryDay> = emptyList(),
    val hotels: List<Hotel> = emptyList(),
    val hiddenGems: List<HiddenGem> = emptyList(),
    val status: TripStatus = TripStatus.DRAFT,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val schemaVersion: Int = 1,
)
