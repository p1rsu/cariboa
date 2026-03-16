package com.cariboa.app.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.cariboa.app.data.repository.TripRepository
import com.cariboa.app.domain.model.TripStatus
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class TripStatusWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val tripRepository: TripRepository,
) : CoroutineWorker(context, params) {
    override suspend fun doWork(): Result {
        val now = System.currentTimeMillis()
        val trips = tripRepository.fetchTripsFromFirestore()
        trips.forEach { trip ->
            when {
                trip.status == TripStatus.SAVED && now >= trip.startDate ->
                    tripRepository.updateTripStatus(trip.id, TripStatus.ACTIVE)
                trip.status == TripStatus.ACTIVE && now > trip.endDate ->
                    tripRepository.updateTripStatus(trip.id, TripStatus.COMPLETED)
            }
        }
        return Result.success()
    }
}
