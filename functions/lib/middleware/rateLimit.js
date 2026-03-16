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
exports.checkRateLimit = checkRateLimit;
const admin = __importStar(require("firebase-admin"));
const https_1 = require("firebase-functions/v2/https");
const MAX_REQUESTS_PER_MINUTE = 10;
async function checkRateLimit(uid) {
    const now = Date.now();
    const windowStart = now - 60000;
    const ref = admin.firestore().doc(`ratelimits/${uid}`);
    await admin.firestore().runTransaction(async (txn) => {
        var _a, _b;
        const doc = await txn.get(ref);
        const timestamps = (_b = (_a = doc.data()) === null || _a === void 0 ? void 0 : _a.timestamps) !== null && _b !== void 0 ? _b : [];
        const recent = timestamps.filter((t) => t > windowStart);
        if (recent.length >= MAX_REQUESTS_PER_MINUTE) {
            throw new https_1.HttpsError("resource-exhausted", "Rate limit exceeded");
        }
        recent.push(now);
        txn.set(ref, { timestamps: recent, updatedAt: now });
    });
}
//# sourceMappingURL=rateLimit.js.map