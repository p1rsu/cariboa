package com.cariboa.app.domain.model

data class HiddenGem(
    val name: String,
    val placeId: String?,
    val description: String,
    val category: TravelInterest,
    val aiReason: String?,
)
