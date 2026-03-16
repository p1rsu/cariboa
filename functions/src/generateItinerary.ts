import { onCall } from "firebase-functions/v2/https";
import { GoogleGenerativeAI } from "@google/generative-ai";
import { verifyAuth } from "./middleware/auth";
import { checkUsage, incrementUsage } from "./middleware/usageCheck";
import { checkRateLimit } from "./middleware/rateLimit";
import { buildItineraryPrompt } from "./prompts/itineraryPrompt";

const genAI = new GoogleGenerativeAI(process.env.GEMINI_API_KEY || "placeholder");

export const generateItinerary = onCall({ maxInstances: 10 }, async (request) => {
  const uid = verifyAuth(request);
  await checkRateLimit(uid);
  await checkUsage(uid, "itinerariesGenerated");

  const { destination, startDate, endDate, travelers, interests, budgetLevel } = request.data;
  const prompt = buildItineraryPrompt({ destination, startDate, endDate, travelers, interests, budgetLevel });

  const model = genAI.getGenerativeModel({ model: "gemini-2.0-flash" });
  const result = await model.generateContent(prompt);
  const text = result.response.text();

  const jsonMatch = text.match(/\{[\s\S]*\}/);
  if (!jsonMatch) throw new Error("Invalid AI response");
  const parsed = JSON.parse(jsonMatch[0]);
  if (!parsed.days || !Array.isArray(parsed.days)) throw new Error("Missing days");

  await incrementUsage(uid, "itinerariesGenerated");
  return parsed;
});
