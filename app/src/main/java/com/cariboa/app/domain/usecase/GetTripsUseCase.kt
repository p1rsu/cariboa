package com.cariboa.app.domain.usecase

import com.cariboa.app.data.repository.TripRepository
import com.cariboa.app.domain.model.Trip
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetTripsUseCase @Inject constructor(
    private val tripRepository: TripRepository,
) {
    operator fun invoke(): Flow<List<Trip>> = tripRepository.getLocalTrips()
}
