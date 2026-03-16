"use strict";
var __createBinding = (this && this.__createBinding) || (Object.create ? (function(o, m, k, k2) {
    if (k2 === undefined) k2 = k;
    var desc = Object.getOwnPropertyDescriptor(m, k);
    if (!desc || ("get" in desc ? !m.__esModule : desc.writable || desc.configurable)) {
      desc = { enumerable: true, get: function() { return m[k]; } };
    }
    Object.defineProperty(o, k2, desc);
}) : (function(o, m, k, k2) {
    if (k2 === undefined) k2 = k;
    o[k2] = m[k];
}));
var __setModuleDefault = (this && this.__setModuleDefault) || (Object.create ? (function(o, v) {
    Object.defineProperty(o, "default", { enumerable: true, value: v });
}) : function(o, v) {
    o["default"] = v;
});
var __importStar = (this && this.__importStar) || (function () {
    var ownKeys = function(o) {
        ownKeys = Object.getOwnPropertyNames || function (o) {
            var ar = [];
            for (var k in o) if (Object.prototype.hasOwnProperty.call(o, k)) ar[ar.length] = k;
            return ar;
        };
        return ownKeys(o);
    };
    return function (mod) {
        if (mod && mod.__esModule) return mod;
        var result = {};
        if (mod != null) for (var k = ownKeys(mod), i = 0; i < k.length; i++) if (k[i] !== "default") __createBinding(result, mod, k[i]);
        __setModuleDefault(result, mod);
        return result;
    };
})();
Object.defineProperty(exports, "__esModule", { value: true });
exports.checkUsage = checkUsage;
exports.incrementUsage = incrementUsage;
const admin = __importStar(require("firebase-admin"));
const https_1 = require("firebase-functions/v2/https");
const TRIAL_LIMITS = {
    itinerariesGenerated: 1,
    hiddenGemSearches: 2,
    hotelSearches: 3,
};
async function checkUsage(uid, field) {
    var _a, _b, _c, _d;
    const userDoc = await admin.firestore().doc(`users/${uid}`).get();
    const tier = (_a = userDoc.data()) === null || _a === void 0 ? void 0 : _a.subscriptionTier;
    if (tier === "PRO")
        return;
    const usageDoc = await admin.firestore().doc(`users/${uid}/usage/lifetime`).get();
    const currentUsage = (_c = (_b = usageDoc.data()) === null || _b === void 0 ? void 0 : _b[field]) !== null && _c !== void 0 ? _c : 0;
    if (currentUsage >= ((_d = TRIAL_LIMITS[field]) !== null && _d !== void 0 ? _d : 0)) {
        throw new https_1.HttpsError("resource-exhausted", "Trial limit reached");
    }
}
async function incrementUsage(uid, field) {
    const ref = admin.firestore().doc(`users/${uid}/usage/lifetime`);
    await ref.set({ [field]: admin.firestore.FieldValue.increment(1) }, { merge: true });
}
//# sourceMappingURL=usageCheck.js.map