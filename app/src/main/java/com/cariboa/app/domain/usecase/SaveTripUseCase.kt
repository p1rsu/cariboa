package com.cariboa.app.domain.usecase

import com.cariboa.app.data.repository.TripRepository
import com.cariboa.app.domain.model.Trip
import com.cariboa.app.domain.model.TripStatus
import javax.inject.Inject

class SaveTripUseCase @Inject constructor(
    private val tripRepository: TripRepository,
) {
    suspend operator fun invoke(trip: Trip) {
        tripRepository.saveTrip(trip.copy(status = TripStatus.SAVED, updatedAt = System.currentTimeMillis()))
    }
}
