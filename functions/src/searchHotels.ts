import { onCall, HttpsError } from "firebase-functions/v2/https";
import { GoogleGenerativeAI } from "@google/generative-ai";
import { verifyAuth } from "./middleware/auth";
import { checkUsage, incrementUsage } from "./middleware/usageCheck";
import { checkRateLimit } from "./middleware/rateLimit";

const genAI = new GoogleGenerativeAI(process.env.GEMINI_API_KEY || "placeholder");

export const searchHotels = onCall({ maxInstances: 10 }, async (request) => {
  const uid = verifyAuth(request);
  await checkRateLimit(uid);
  await checkUsage(uid, "hotelSearches");

  const { destination, checkIn, checkOut, travelers, budgetLevel, suggestions } = request.data;

  if (!destination) {
    throw new HttpsError("invalid-argument", "destination is required");
  }

  const suggestionContext = suggestions?.length
    ? `Preferred areas/hotels from itinerary: ${suggestions.map((s: any) => s.name).join(", ")}`
    : "";

  const prompt = `You are a hotel search AI. Find suitable hotels for travelers.

Destination: ${destination}
Check-in: ${checkIn}
Check-out: ${checkOut}
Travelers: ${travelers ?? 2}
Budget: ${budgetLevel ?? "medium"}
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
  if (!jsonMatch) throw new HttpsError("internal", "Invalid AI response");
  const parsed = JSON.parse(jsonMatch[0]);
  if (!parsed.hotels || !Array.isArray(parsed.hotels)) {
    throw new HttpsError("internal", "Missing hotels in response");
  }

  await incrementUsage(uid, "hotelSearches");
  return parsed;
});
