import * as admin from "firebase-admin";
import { HttpsError } from "firebase-functions/v2/https";

const TRIAL_LIMITS: Record<string, number> = {
  itinerariesGenerated: 1,
  hiddenGemSearches: 2,
  hotelSearches: 3,
};

export async function checkUsage(uid: string, field: string): Promise<void> {
  const userDoc = await admin.firestore().doc(`users/${uid}`).get();
  const tier = userDoc.data()?.subscriptionTier;
  if (tier === "PRO") return;

  const usageDoc = await admin.firestore().doc(`users/${uid}/usage/lifetime`).get();
  const currentUsage = usageDoc.data()?.[field] ?? 0;

  if (currentUsage >= (TRIAL_LIMITS[field] ?? 0)) {
    throw new HttpsError("resource-exhausted", "Trial limit reached");
  }
}

export async function incrementUsage(uid: string, field: string): Promise<void> {
  const ref = admin.firestore().doc(`users/${uid}/usage/lifetime`);
  await ref.set({ [field]: admin.firestore.FieldValue.increment(1) }, { merge: true });
}
