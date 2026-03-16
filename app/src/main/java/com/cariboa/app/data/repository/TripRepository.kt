package com.cariboa.app.data.repository

import com.cariboa.app.data.local.dao.TripDao
import com.cariboa.app.data.local.mapper.TripMapper.toDomain
import com.cariboa.app.data.local.mapper.TripMapper.toEntity
import com.cariboa.app.domain.model.*
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TripRepository @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val authRepository: AuthRepository,
    private val tripDao: TripDao,
) {
    private fun tripsCollection() = firestore
        .collection("users/${authRepository.currentUser?.uid}/trips")

    fun getLocalTrips(): Flow<List<Trip>> =
        tripDao.getAllTrips().map { entities -> entities.map { it.toDomain() } }

    suspend fun saveTrip(trip: Trip) {
        val data = trip.toFirestoreMap()
        if (trip.id.isEmpty()) {
            val ref = tripsCollection().add(data).await()
            tripDao.upsertTrip(trip.copy(id = ref.id).toEntity())
        } else {
            tripsCollection().document(trip.id).set(data).await()
            tripDao.upsertTrip(trip.toEntity())
        }
    }

    suspend fun deleteTrip(tripId: String) {
        tripsCollection().document(tripId).delete().await()
        tripDao.deleteTrip(tripId)
    }

    suspend fun updateTripStatus(tripId: String, status: TripStatus) {
        tripsCollection().document(tripId).update("status", status.name).await()
    }

    suspend fun fetchTripsFromFirestore(): List<Trip> {
        val docs = tripsCollection().get().await()
        return docs.mapNotNull { doc ->
            try {
                Trip(
                    id = doc.id,
                    destination = doc.getString("destination") ?: "",
                    startDate = doc.getLong("startDate") ?: 0,
                    endDate = doc.getLong("endDate") ?: 0,
                    travelers = doc.getLong("travelers")?.toInt() ?: 1,
                    interests = emptyList(),
                    budgetLevel = BudgetLevel.valueOf(doc.getString("budgetLevel") ?: "MODERATE"),
                    status = TripStatus.valueOf(doc.getString("status") ?: "DRAFT"),
                    createdAt = doc.getLong("createdAt") ?: 0,
                    updatedAt = doc.getLong("updatedAt") ?: 0,
                )
            } catch (e: Exception) { null }
        }
    }

    private fun Trip.toFirestoreMap() = mapOf(
        "destination" to destination,
        "startDate" to startDate,
        "endDate" to endDate,
        "travelers" to travelers,
        "interests" to interests.map { it.name },
        "budgetLevel" to budgetLevel.name,
        "status" to status.name,
        "createdAt" to createdAt,
        "updatedAt" to System.currentTimeMillis(),
        "schemaVersion" to schemaVersion,
    )
}
