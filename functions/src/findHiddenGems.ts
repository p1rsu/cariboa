import { onCall, HttpsError } from "firebase-functions/v2/https";
import { GoogleGenerativeAI } from "@google/generative-ai";
import { verifyAuth } from "./middleware/auth";
import { checkUsage, incrementUsage } from "./middleware/usageCheck";
import { checkRateLimit } from "./middleware/rateLimit";
import { buildHiddenGemsPrompt } from "./prompts/hiddenGemsPrompt";

const genAI = new GoogleGenerativeAI(process.env.GEMINI_API_KEY || "placeholder");

export const findHiddenGems = onCall({ maxInstances: 10 }, async (request) => {
  const uid = verifyAuth(request);
  await checkRateLimit(uid);
  await checkUsage(uid, "hiddenGemSearches");

  const { destination, interests, budgetLevel } = request.data;

  if (!destination) {
    throw new HttpsError("invalid-argument", "destination is required");
  }

  const prompt = buildHiddenGemsPrompt({
    destination,
    interests: interests ?? [],
    budgetLevel: budgetLevel ?? "medium",
  });

  const model = genAI.getGenerativeModel({ model: "gemini-2.0-flash" });
  const result = await model.generateContent(prompt);
  const text = result.response.text();

  const jsonMatch = text.match(/\{[\s\S]*\}/);
  if (!jsonMatch) throw new HttpsError("internal", "Invalid AI response");
  const parsed = JSON.parse(jsonMatch[0]);
  if (!parsed.hiddenGems || !Array.isArray(parsed.hiddenGems)) {
    throw new HttpsError("internal", "Missing hiddenGems in response");
  }

  await incrementUsage(uid, "hiddenGemSearches");
  return parsed;
});
