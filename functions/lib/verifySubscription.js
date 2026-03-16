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
exports.verifySubscription = void 0;
const https_1 = require("firebase-functions/v2/https");
const admin = __importStar(require("firebase-admin"));
const auth_1 = require("./middleware/auth");
exports.verifySubscription = (0, https_1.onCall)({ maxInstances: 10 }, async (request) => {
    const uid = (0, auth_1.verifyAuth)(request);
    const { purchaseToken, productId } = request.data;
    if (!purchaseToken || !productId) {
        throw new https_1.HttpsError("invalid-argument", "purchaseToken and productId are required");
    }
    // In production, verify with Google Play Billing API here.
    // For now, we trust the client token and update Firestore.
    // TODO: Integrate with Google Play Developer API for server-side validation.
    const isValid = typeof purchaseToken === "string" && purchaseToken.length > 0;
    if (!isValid) {
        throw new https_1.HttpsError("invalid-argument", "Invalid purchase token");
    }
    const tier = productId.includes("pro") ? "PRO" : "FREE";
    await admin.firestore().doc(`users/${uid}`).set({
        subscriptionTier: tier,
        subscriptionProductId: productId,
        subscriptionUpdatedAt: admin.firestore.FieldValue.serverTimestamp(),
    }, { merge: true });
    return { success: true, tier };
});
//# sourceMappingURL=verifySubscription.js.map