package com.cariboa.app.domain.model

data class Activity(
    val time: String,
    val title: String,
    val description: String,
    val lat: Double?,
    val lng: Double?,
    val type: TravelInterest,
    val placeId: String?,
)
