package com.cariboa.app.data.remote.dto

data class SearchHotelsRequest(
    val destination: String,
    val checkIn: String,
    val checkOut: String,
    val priceLevel: Int? = null,
    val minRating: Double? = null,
)
