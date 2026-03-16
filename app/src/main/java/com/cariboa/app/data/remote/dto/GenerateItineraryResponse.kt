package com.cariboa.app.data.remote.dto

data class ItineraryDayDto(val day: Int, val activities: List<ActivityDto>)
data class ActivityDto(val time: String, val title: String, val description: String, val type: String, val placeName: String?, val placeId: String?, val lat: Double?, val lng: Double?, val rating: Double?, val photoRef: String?)
data class HotelDto(val name: String, val placeId: String?, val priceLevel: Int?, val rating: Double?, val photoRef: String?)
data class HiddenGemDto(val name: String, val placeId: String?, val description: String, val category: String, val reason: String?)

data class GenerateItineraryResponse(
    val days: List<ItineraryDayDto>,
    val hotelSuggestions: List<HotelDto>,
    val hiddenGems: List<HiddenGemDto>,
)
