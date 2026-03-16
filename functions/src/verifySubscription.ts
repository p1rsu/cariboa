import { onCall, HttpsError } from "firebase-functions/v2/https";
import * as admin from "firebase-admin";
import { verifyAuth } from "./middleware/auth";

export const verifySubscription = onCall({ maxInstances: 10 }, async (request) => {
  const uid = verifyAuth(request);

  const { purchaseToken, productId } = request.data;

  if (!purchaseToken || !productId) {
    throw new HttpsError("invalid-argument", "purchaseToken and productId are required");
  }

  // In production, verify with Google Play Billing API here.
  // For now, we trust the client token and update Firestore.
  // TODO: Integrate with Google Play Developer API for server-side validation.

  const isValid = typeof purchaseToken === "string" && purchaseToken.length > 0;

  if (!isValid) {
    throw new HttpsError("invalid-argument", "Invalid purchase token");
  }

  const tier = productId.includes("pro") ? "PRO" : "FREE";

  await admin.firestore().doc(`users/${uid}`).set(
    {
      subscriptionTier: tier,
      subscriptionProductId: productId,
      subscriptionUpdatedAt: admin.firestore.FieldValue.serverTimestamp(),
    },
    { merge: true }
  );

  return { success: true, tier };
});
