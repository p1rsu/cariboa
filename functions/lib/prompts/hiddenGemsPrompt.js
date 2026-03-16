"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
exports.buildHiddenGemsPrompt = buildHiddenGemsPrompt;
function buildHiddenGemsPrompt(params) {
    return `You are a local travel expert. Find unique hidden gems and off-the-beaten-path experiences.

Destination: ${params.destination}
Interests: ${params.interests.join(", ")}
Budget: ${params.budgetLevel}

Respond with ONLY valid JSON:
{
  "hiddenGems": [
    {
      "name": "Place name",
      "description": "What makes it special",
      "category": "food|culture|nature|adventure|shopping",
      "reason": "Why a traveler should visit",
      "bestTime": "Best time to visit",
      "localTip": "Insider tip"
    }
  ]
}

Include 5-8 hidden gems. Focus on lesser-known spots that match the interests. Avoid generic tourist traps. Match ${params.budgetLevel} budget level.`;
}
//# sourceMappingURL=hiddenGemsPrompt.js.map