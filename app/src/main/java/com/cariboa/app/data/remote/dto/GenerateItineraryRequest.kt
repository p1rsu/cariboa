package com.cariboa.app.data.remote.dto

data class GenerateItineraryRequest(
    val destination: String,
    val startDate: String,
    val endDate: String,
    val travelers: Int,
    val interests: List<String>,
    val budgetLevel: String,
)
