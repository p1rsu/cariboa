"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
exports.buildItineraryPrompt = buildItineraryPrompt;
function buildItineraryPrompt(params) {
    return `You are a travel planning AI. Generate a detailed day-by-day itinerary.

Destination: ${params.destination}
Dates: ${params.startDate} to ${params.endDate}
Travelers: ${params.travelers}
Interests: ${params.interests.join(", ")}
Budget: ${params.budgetLevel}

Respond with ONLY valid JSON:
{
  "days": [{ "day": 1, "activities": [{ "time": "09:00", "title": "Name", "description": "Brief", "type": "culture", "placeName": "Searchable name" }] }],
  "hotelSuggestions": [{ "name": "Hotel", "area": "Area" }],
  "hiddenGems": [{ "name": "Place", "description": "Why special", "category": "food", "reason": "Why recommend" }]
}

Include 3-5 activities/day, 2-3 hotels, 3-5 hidden gems. Prioritize interests. Match ${params.budgetLevel} budget.`;
}
//# sourceMappingURL=itineraryPrompt.js.map