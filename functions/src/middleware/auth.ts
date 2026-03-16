import { HttpsError } from "firebase-functions/v2/https";

export function verifyAuth(request: any): string {
  if (!request.auth) {
    throw new HttpsError("unauthenticated", "Must be signed in");
  }
  return request.auth.uid;
}
