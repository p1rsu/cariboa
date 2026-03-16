"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
exports.searchHotels = void 0;
const https_1 = require("firebase-functions/v2/https");
const generative_ai_1 = require("@google/generative-ai");
const auth_1 = require("./middleware/auth");
const usageCheck_1 = require("./middleware/usageCheck");
const rateLimit_1 = require("./middleware/rateLimit");
const genAI = new generative_ai_1.GoogleGenerativeAI(process.env.GEMINI_API_KEY || "placeholder");
exports.searchHotels = (0, https_1.onCall)({ maxInstances: 10 }, async (request) => {
    const uid = (0, auth_1.verifyAuth)(request);
    await (0, rateLimit_1.checkRateLimit)(uid);
    await (0, usageCheck_1.checkUsage)(uid, "hotelSearches");
    const { destination, checkIn, checkOut, travelers, budgetLevel, suggestions } = request.data;
    if (!destination) {
        throw new https_1.HttpsError("invalid-argument", "destination is required");
    }
    const suggestionContext = (suggestions === null || suggestions === void 0 ? void 0 : suggestions.length)
        ? `Preferred areas/hotels from itinerary: ${suggestions.map((s) => s.name).join(", ")}`
        : "";
    const prompt = `You are a hotel search AI. Find suitable hotels for travelers.

Destination: ${destination}
Check-in: ${checkIn}
Check-out: ${checkOut}
Travelers: ${travelers !== null && travelers !== void 0 ? travelers : 2}
Budget: ${budgetLevel !== null && budgetLevel !== void 0 ? budgetLevel : "medium"}
${suggestionContext}

Respond with ONLY valid JSON:
{
  "hotels": [
    {
      "name": "Hotel name",
      "area": "Neighborhood or area",
      "description": "Brief description",
      "priceRange": "$/$$/$$$",
      "rating": 4.5,
      "amenities": ["WiFi", "Pool"],
      "highlights": "What makes it special"
    }
  ]
}

Include 4-6 hotels matching the budget level. Prioritize location and value.`;
    const model = genAI.getGenerativeModel({ model: "gemini-2.0-flash" });
    const result = await model.generateContent(prompt);
    const text = result.response.text();
    const jsonMatch = text.match(/\{[\s\S]*\}/);
    if (!jsonMatch)
        throw new https_1.HttpsError("internal", "Invalid AI response");
    const parsed = JSON.parse(jsonMatch[0]);
    if (!parsed.hotels || !Array.isArray(parsed.hotels)) {
        throw new https_1.HttpsError("internal", "Missing hotels in response");
    }
    await (0, usageCheck_1.incrementUsage)(uid, "hotelSearches");
    return parsed;
});
//# sourceMappingURL=searchHotels.js.map