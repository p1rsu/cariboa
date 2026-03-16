package com.cariboa.app.domain.model

import org.junit.Assert.*
import org.junit.Test

class UsageInfoTest {
    @Test
    fun freshUsageAllowsAllActions() {
        val usage = UsageInfo()
        assertTrue(usage.canGenerateItinerary())
        assertTrue(usage.canSearchHiddenGems())
        assertTrue(usage.canSearchHotels())
    }

    @Test
    fun exhaustedItineraryLimitBlocksGeneration() {
        val usage = UsageInfo(itinerariesGenerated = 1)
        assertFalse(usage.canGenerateItinerary())
    }

    @Test
    fun exhaustedHotelLimitBlocksSearch() {
        val usage = UsageInfo(hotelSearches = 3)
        assertFalse(usage.canSearchHotels())
    }

    @Test
    fun exhaustedHiddenGemLimitBlocksSearch() {
        val usage = UsageInfo(hiddenGemSearches = 2)
        assertFalse(usage.canSearchHiddenGems())
    }

    @Test
    fun partialUsageStillAllows() {
        val usage = UsageInfo(itinerariesGenerated = 0, hiddenGemSearches = 1, hotelSearches = 2)
        assertTrue(usage.canGenerateItinerary())
        assertTrue(usage.canSearchHiddenGems())
        assertTrue(usage.canSearchHotels())
    }
}
