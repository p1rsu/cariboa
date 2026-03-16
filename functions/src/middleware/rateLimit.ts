import * as admin from "firebase-admin";
import { HttpsError } from "firebase-functions/v2/https";

const MAX_REQUESTS_PER_MINUTE = 10;

export async function checkRateLimit(uid: string): Promise<void> {
  const now = Date.now();
  const windowStart = now - 60_000;
  const ref = admin.firestore().doc(`ratelimits/${uid}`);

  await admin.firestore().runTransaction(async (txn) => {
    const doc = await txn.get(ref);
    const timestamps: number[] = doc.data()?.timestamps ?? [];
    const recent = timestamps.filter((t: number) => t > windowStart);

    if (recent.length >= MAX_REQUESTS_PER_MINUTE) {
      throw new HttpsError("resource-exhausted", "Rate limit exceeded");
    }

    recent.push(now);
    txn.set(ref, { timestamps: recent, updatedAt: now });
  });
}
