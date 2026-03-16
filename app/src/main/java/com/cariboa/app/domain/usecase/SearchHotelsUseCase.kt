package com.cariboa.app.domain.usecase

import com.cariboa.app.data.repository.HotelRepository
import com.cariboa.app.domain.model.Hotel
import javax.inject.Inject

class SearchHotelsUseCase @Inject constructor(
    private val repository: HotelRepository,
) {
    suspend operator fun invoke(
        destination: String,
        checkIn: String,
        checkOut: String,
        priceLevel: Int? = null,
        minRating: Double? = null,
    ): List<Hotel> {
        return repository.searchHotels(destination, checkIn, checkOut, priceLevel, minRating)
    }
}
