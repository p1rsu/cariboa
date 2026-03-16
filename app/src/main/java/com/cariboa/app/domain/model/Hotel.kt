package com.cariboa.app.domain.model

data class Hotel(
    val name: String,
    val placeId: String?,
    val priceLevel: Int,
    val rating: Double,
    val photoRef: String?,
)
