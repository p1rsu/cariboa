package com.cariboa.app.domain.model

data class UsageInfo(
    val itinerariesGenerated: Int = 0,
    val hiddenGemSearches: Int = 0,
    val hotelSearches: Int = 0,
) {
    companion object {
        const val TRIAL_ITINERARY_LIMIT = 1
        const val TRIAL_HIDDEN_GEM_LIMIT = 2
        const val TRIAL_HOTEL_LIMIT = 3
    }

    fun canGenerateItinerary() = itinerariesGenerated < TRIAL_ITINERARY_LIMIT
    fun canSearchHiddenGems() = hiddenGemSearches < TRIAL_HIDDEN_GEM_LIMIT
    fun canSearchHotels() = hotelSearches < TRIAL_HOTEL_LIMIT
}
