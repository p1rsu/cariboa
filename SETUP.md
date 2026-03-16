# Cariboa - Setup Guide

## Prerequisites

- Android Studio (Ladybug or newer)
- JDK 17+
- Node.js 18+ and npm
- Firebase CLI (`npm install -g firebase-tools`)
- Android device or emulator (API 26+)

---

## 1. Clone the Repository

```bash
git clone https://github.com/p1rsu/cariboa.git
cd cariboa
```

## 2. Create a Firebase Project

1. Go to [console.firebase.google.com](https://console.firebase.google.com)
2. Click **Add project** and name it `cariboa`
3. Disable Google Analytics (optional, can enable later)
4. Click **Create project**

## 3. Register the Android App

1. In the Firebase console, click **Add app** and select **Android**
2. Enter package name: `com.cariboa.app`
3. Enter app nickname: `Cariboa`
4. Click **Register app**
5. Download `google-services.json`
6. Replace `app/google-services.json` with the downloaded file

## 4. Enable Firebase Authentication

1. In Firebase console, go to **Authentication** > **Get started**
2. Enable **Google** sign-in provider
   - Requires SHA-1 fingerprint (see step below)
3. Enable **Email/Password** sign-in provider

### Get SHA-1 Fingerprint

```bash
./gradlew signingReport
```

Copy the **SHA-1** from the `debug` variant, then:

1. Go to Firebase console > **Project settings** > **Your apps**
2. Click **Add fingerprint** and paste the SHA-1

## 5. Create Firestore Database

1. In Firebase console, go to **Firestore Database** > **Create database**
2. Select **Start in test mode**
3. Choose a region close to you
4. Click **Create**

## 6. Deploy Firestore Security Rules

```bash
firebase login
firebase init firestore
# Select "Use an existing project" > choose your project
# Accept default rules file (firestore.rules already exists)

firebase deploy --only firestore:rules
```

## 7. Set Up API Keys

### Gemini API Key

1. Go to [aistudio.google.com/apikey](https://aistudio.google.com/app/apikey)
2. Click **Create API key**
3. Copy the key

### Google Places API Key

1. Go to [console.cloud.google.com](https://console.cloud.google.com)
2. Select the project Firebase created
3. Go to **APIs & Services** > **Library**
4. Search and enable **Places API (New)**
5. Go to **APIs & Services** > **Credentials** > **Create Credentials** > **API Key**
6. Copy the key

### Save Keys

Create `functions/.env`:

```
GEMINI_API_KEY=your_gemini_key_here
PLACES_API_KEY=your_places_key_here
```

## 8. Deploy Cloud Functions

```bash
cd functions
npm install
npm run build
cd ..
firebase deploy --only functions
```

## 9. Set Up Local Properties

Create `local.properties` in the project root (if not already present):

```
sdk.dir=C:\\Users\\YOUR_USERNAME\\AppData\\Local\\Android\\Sdk
```

Replace `YOUR_USERNAME` with your Windows username.

## 10. Build and Run

### Option A: Android Studio

1. Open Android Studio
2. Select **Open** and choose the `cariboa` directory
3. Wait for Gradle sync to complete
4. Connect a device or start an emulator
5. Click **Run** (green play button)

### Option B: Command Line

```bash
# Build
./gradlew assembleDebug

# Install on connected device
./gradlew installDebug
```

---

## App Flow

1. **Splash** - Boa waving with Cariboa logo
2. **Onboarding** - Swipe through 4 welcome pages, select travel interests
3. **Sign In** - Google or email/password
4. **Home** - Tap "Plan a Trip"
5. **Trip Wizard** - Enter destination, dates, interests, budget > Generate
6. **Itinerary** - View day-by-day plan with activities, hotels, hidden gems > Save
7. **Hidden Gems** tab - Search AI-curated local spots
8. **My Trips** tab - View and manage saved trips
9. **Paywall** - Appears after trial limits (1 itinerary, 2 gem searches, 3 hotel searches)

---

## Trial vs Pro

| Feature | Trial | Pro ($4.99/mo or $39.99/yr) |
|---|---|---|
| Itineraries | 1 | Unlimited |
| Hidden gem searches | 2 | Unlimited |
| Hotel searches | 3 | Unlimited |
| Saved trips | 1 | Unlimited |
| Hidden gem details | Top 3, no AI reasoning | Full results + AI explanations |
| Offline access | No | Yes |

---

## Troubleshooting

| Issue | Fix |
|---|---|
| Google Sign-In fails | Verify SHA-1 fingerprint in Firebase console |
| Cloud Functions error | Check `functions/.env` has valid API keys |
| Build fails after replacing google-services.json | Run `./gradlew clean assembleDebug` |
| "No internet" in emulator | Check emulator network settings |
| `app.androidTest` configuration not supported | Click the run config dropdown > select `app` instead of `app.androidTest`. If `app` is missing: Edit Configurations > + > Android App > set Module to `Cariboa.app.main` |
| Gradle sync fails | File > Invalidate Caches and Restart in Android Studio |
| Room migration error | Uninstall the app and reinstall |

---

## Project Structure

```
cariboa/
  app/                          # Android app
    src/main/java/com/cariboa/app/
      data/                     # Repositories, Room, Firebase, DTOs
      domain/                   # Models and use cases
      navigation/               # Screen routes and NavGraph
      ui/                       # Compose screens and components
      worker/                   # WorkManager jobs
    src/main/assets/lottie/     # Boa animation files
  functions/                    # Firebase Cloud Functions (TypeScript)
    src/
      generateItinerary.ts      # Gemini AI itinerary generation
      searchHotels.ts           # Google Places hotel search
      findHiddenGems.ts         # AI + Places hidden gem discovery
      verifySubscription.ts     # Play Billing verification
      middleware/               # Auth, rate limiting, usage checks
  firestore.rules               # Firestore security rules
  docs/superpowers/             # Design spec and implementation plan
```
