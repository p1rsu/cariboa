package com.cariboa.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "trips")
data class TripEntity(
    @PrimaryKey val id: String,
    val destination: String,
    val startDate: Long,
    val endDate: Long,
    val travelers: Int,
    val interests: String,
    val budgetLevel: String,
    val itinerary: String,
    val hotels: String,
    val hiddenGems: String,
    val status: String,
    val createdAt: Long,
    val updatedAt: Long,
    val schemaVersion: Int = 1,
)
