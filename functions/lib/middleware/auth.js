"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
exports.verifyAuth = verifyAuth;
const https_1 = require("firebase-functions/v2/https");
function verifyAuth(request) {
    if (!request.auth) {
        throw new https_1.HttpsError("unauthenticated", "Must be signed in");
    }
    return request.auth.uid;
}
//# sourceMappingURL=auth.js.map