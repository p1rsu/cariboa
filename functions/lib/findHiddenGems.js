"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
exports.findHiddenGems = void 0;
const https_1 = require("firebase-functions/v2/https");
const generative_ai_1 = require("@google/generative-ai");
const auth_1 = require("./middleware/auth");
const usageCheck_1 = require("./middleware/usageCheck");
const rateLimit_1 = require("./middleware/rateLimit");
const hiddenGemsPrompt_1 = require("./prompts/hiddenGemsPrompt");
const genAI = new generative_ai_1.GoogleGenerativeAI(process.env.GEMINI_API_KEY || "placeholder");
exports.findHiddenGems = (0, https_1.onCall)({ maxInstances: 10 }, async (request) => {
    const uid = (0, auth_1.verifyAuth)(request);
    await (0, rateLimit_1.checkRateLimit)(uid);
    await (0, usageCheck_1.checkUsage)(uid, "hiddenGemSearches");
    const { destination, interests, budgetLevel } = request.data;
    if (!destination) {
        throw new https_1.HttpsError("invalid-argument", "destination is required");
    }
    const prompt = (0, hiddenGemsPrompt_1.buildHiddenGemsPrompt)({
        destination,
        interests: interests !== null && interests !== void 0 ? interests : [],
        budgetLevel: budgetLevel !== null && budgetLevel !== void 0 ? budgetLevel : "medium",
    });
    const model = genAI.getGenerativeModel({ model: "gemini-2.0-flash" });
    const result = await model.generateContent(prompt);
    const text = result.response.text();
    const jsonMatch = text.match(/\{[\s\S]*\}/);
    if (!jsonMatch)
        throw new https_1.HttpsError("internal", "Invalid AI response");
    const parsed = JSON.parse(jsonMatch[0]);
    if (!parsed.hiddenGems || !Array.isArray(parsed.hiddenGems)) {
        throw new https_1.HttpsError("internal", "Missing hiddenGems in response");
    }
    await (0, usageCheck_1.incrementUsage)(uid, "hiddenGemSearches");
    return parsed;
});
//# sourceMappingURL=findHiddenGems.js.map