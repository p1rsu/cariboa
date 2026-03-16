package com.cariboa.app.domain.usecase

import com.cariboa.app.data.repository.TripRepository
import javax.inject.Inject

class DeleteTripUseCase @Inject constructor(
    private val tripRepository: TripRepository,
) {
    suspend operator fun invoke(tripId: String) {
        tripRepository.deleteTrip(tripId)
    }
}
