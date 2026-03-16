"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
exports.generateItinerary = void 0;
const https_1 = require("firebase-functions/v2/https");
const generative_ai_1 = require("@google/generative-ai");
const auth_1 = require("./middleware/auth");
const usageCheck_1 = require("./middleware/usageCheck");
const rateLimit_1 = require("./middleware/rateLimit");
const itineraryPrompt_1 = require("./prompts/itineraryPrompt");
const genAI = new generative_ai_1.GoogleGenerativeAI(process.env.GEMINI_API_KEY || "placeholder");
exports.generateItinerary = (0, https_1.onCall)({ maxInstances: 10 }, async (request) => {
    const uid = (0, auth_1.verifyAuth)(request);
    await (0, rateLimit_1.checkRateLimit)(uid);
    await (0, usageCheck_1.checkUsage)(uid, "itinerariesGenerated");
    const { destination, startDate, endDate, travelers, interests, budgetLevel } = request.data;
    const prompt = (0, itineraryPrompt_1.buildItineraryPrompt)({ destination, startDate, endDate, travelers, interests, budgetLevel });
    const model = genAI.getGenerativeModel({ model: "gemini-2.0-flash" });
    const result = await model.generateContent(prompt);
    const text = result.response.text();
    const jsonMatch = text.match(/\{[\s\S]*\}/);
    if (!jsonMatch)
        throw new Error("Invalid AI response");
    const parsed = JSON.parse(jsonMatch[0]);
    if (!parsed.days || !Array.isArray(parsed.days))
        throw new Error("Missing days");
    await (0, usageCheck_1.incrementUsage)(uid, "itinerariesGenerated");
    return parsed;
});
//# sourceMappingURL=generateItinerary.js.map