# FarePlan SA

> **Travel Smarter. Spend Clearer.**

A combined travel-booking and financial-management Android app built for the South African traveller. FarePlan SA merges travel search (flights, hotels) with real-time budget tracking, giving users a single tool to plan a trip that fits their wallet.

![FarePlan SA Logo](docs/logo.png)

---

## 📱 Demo Video

Watch the full demonstration on YouTube:

🎥 **[FarePlan SA — App Prototype Demonstration](https://youtu.be/YOUR_VIDEO_ID)**

*(Replace `YOUR_VIDEO_ID` with the unlisted YouTube link once uploaded.)*

---

## 🎯 Purpose of the App

The contemporary digital travel landscape is fragmented. Booking platforms like Booking.com and KAYAK handle reservations but ignore budgeting. Budgeting apps like Spendee track expenses but don't help you book. FarePlan SA bridges this gap by becoming a **financial-first travel planner**.

The app's core innovation is **Dynamic Budget Tethering** — the ability to filter search results based on the user's remaining trip budget. Users set a budget once, and every flight, hotel, or expense they add recalculates their remaining funds and adjusts recommendations accordingly.

The name **FarePlan SA** captures the mission:
- **"Fare"** — the transactional aspect (flights, hotels, cars)
- **"Plan"** — the strategic, financial orchestration
- **"SA"** — South African heritage with isiZulu and Afrikaans support

---

## ✨ Features

### Core Features
- **User Authentication** — Firebase Auth (email/password + Google), with bcrypt-hashed passwords stored securely
- **Onboarding** — First-time setup: pick home currency and preferred language
- **Trip Planning** — Create trips with destination, dates, and total budget
- **Dynamic Budget Tethering** — Search results filtered by remaining budget
- **Itinerary Builder** — Add, edit, tick off, and delete itinerary items (with timeline view)
- **Expense Tracking** — Log expenses (Airbnb, tours, food) with category + paid status
- **Real-Time Budget Alerts** — Traffic-light system (green < 70%, amber 70–89%, red ≥ 90%)
- **Push Notifications** — Firebase Cloud Messaging at 80/90/100% budget thresholds
- **Multi-Currency** — Live ZAR conversion via ExchangeRate-API (6-hour cache)
- **Multi-Language** — Full English, isiZulu, and Afrikaans support
- **South African Number/Date Formatting** — `R 1 500,00` and `DD/MM/YYYY`

### Screens
- Splash, Onboarding, Login, Sign Up
- Dashboard (Financial Overview)
- Create Trip, Trip Detail (Itinerary + Expenses)
- Add Itinerary Item, Add Expense
- Search (Flights/Hotels), Settings

---

## 🏗️ Architecture

┌─────────────────────────────────────────────────────────────────┐
│ MOBILE CLIENT │
│ (Kotlin + Material 3) │
└────────────────────────────────┬────────────────────────────────┘
│
│ HTTPS / SDK
▼
┌─────────────────────────────────────────────────────────────────┐
│ FIREBASE │
│ │
│ ┌──────────────┐ ┌──────────────┐ ┌───────────────────────┐ │
│ │ Authentication│ │ Firestore │ │ Cloud Messaging │ │
│ │ (bcrypt) │ │ (NoSQL DB) │ │ (Push notifications) │ │
│ └──────────────┘ └──────────────┘ └───────────────────────┘ │
└─────────────────────────────────────────────────────────────────┘
│
│ REST (Retrofit)
▼
┌─────────────────────────────────────────────────────────────────┐
│ EXTERNAL APIS │
│ │
│ • ExchangeRate-API (currency conversion) │
│ • Sky Scrapper / RapidAPI (flights + hotels — mock) │
│ • OpenStreetMap OSRM (geocoding + routing) │
└─────────────────────────────────────────────────────────────────┘


### Tech Stack

| Layer | Technology |
|-------|-----------|
| **Language** | Kotlin |
| **UI** | Material 3, AndroidX, ViewBinding, RecyclerView |
| **Database** | Firebase Firestore (NoSQL, offline persistence) |
| **Auth** | Firebase Authentication (email, Google OAuth) |
| **Push** | Firebase Cloud Messaging |
| **Currency** | ExchangeRate-API (Retrofit + Gson) |
| **Image Loading** | (Optional: Glide) |
| **Localization** | Android Resources (`values/`, `values-zu/`, `values-af/`) |
| **Testing** | JUnit 4, GitHub Actions CI |
| **Version Control** | Git + GitHub |

---

## 📂 Project Structure
FarePlanSA/
├── .github/
│ └── workflows/
│ └── android.yml # GitHub Actions CI
├── app/
│ ├── google-services.json # Firebase config (gitignored)
│ ├── build.gradle.kts
│ └── src/
│ ├── main/
│ │ ├── java/com/example/fareplansa/
│ │ │ ├── MainActivity.kt # Router
│ │ │ ├── SplashActivity.kt # Splash screen
│ │ │ ├── OnboardingActivity.kt # First-time setup
│ │ │ ├── LoginActivity.kt # Sign-in
│ │ │ ├── SignUpActivity.kt # Registration
│ │ │ ├── DashboardActivity.kt # Home
│ │ │ ├── CreateTripActivity.kt # New trip form
│ │ │ ├── TripDetailActivity.kt # Trip detail
│ │ │ ├── AddItineraryItemActivity.kt # Add/edit itinerary
│ │ │ ├── AddExpenseActivity.kt # Add expense
│ │ │ ├── SearchActivity.kt # Search
│ │ │ ├── SettingsActivity.kt # Language + currency
│ │ │ ├── AuthErrorMapper.kt # Firebase error mapper
│ │ │ ├── BudgetAlertHelper.kt # Traffic-light logic
│ │ │ ├── CurrencyRepository.kt # ExchangeRate-API
│ │ │ ├── CurrencyConverter.kt # Conversion utility
│ │ │ ├── NavigationHelper.kt # Bottom nav + drawer
│ │ │ ├── LocaleHelper.kt # Language switching
│ │ │ ├── FarePlanMessagingService.kt # FCM service
│ │ │ ├── Expense.kt # Data classes
│ │ │ ├── ItineraryItem.kt
│ │ │ ├── Trip.kt
│ │ │ └── UserProfile.kt
│ │ ├── res/
│ │ │ ├── layout/ # All activity & item layouts
│ │ │ ├── drawable/ # Vector icons, backgrounds
│ │ │ ├── values/ # strings.xml, colors.xml, styles.xml, themes.xml
│ │ │ ├── values-zu/ # isiZulu translations
│ │ │ ├── values-af/ # Afrikaans translations
│ │ │ ├── color/ # State selectors
│ │ │ └── menu/ # Bottom nav menu
│ │ └── AndroidManifest.xml
│ └── test/java/com/example/fareplansa/
│ ├── BudgetAlertHelperTest.kt
│ └── CurrencyConverterTest.kt
├── functions/ # Firebase Cloud Functions (optional)
│ └── index.js
├── docs/ # Screenshots + images
│ ├── logo.png
│ ├── architecture.png
│ └── screenshots/
├── .gitignore
├── build.gradle.kts
├── settings.gradle.kts
├── firebase.json
└── README.md


---

## 🚀 Getting Started

### Prerequisites
- **Android Studio** Hedgehog (2023.1.1) or later
- **JDK** 17
- **Android SDK** 34 or later
- A **Firebase project** with Firestore + Authentication enabled
- An **ExchangeRate-API** account (free tier)

### Setup Steps

**1. Clone the repository**
```bash
git clone https://github.com/ST10456229/FarePlan_SA.git
cd FarePlan_SA
2. Add google-services.json

Download the config from your Firebase Console

Place it at app/google-services.json

(This file is gitignored for security)

3. Add your ExchangeRate-API key

Get a free key from exchangerate-api.com

Open CurrencyRepository.kt

Replace "YOUR_API_KEY_HERE" with your actual key

4. Open in Android Studio

File → Open → select FarePlanSA

Let Gradle sync complete

5. Build and run

Connect a physical device or start an emulator

Click the green ▶ Run button

🧪 Automated Testing
Unit Tests
Two test suites cover the core business logic:

BudgetAlertHelperTest.kt — Verifies:

Spend percentage calculations (0%, 50%, 100%, capped at 100%)

Traffic-light colour transitions (green → amber → red)

Edge cases (zero budget, over-budget)

CurrencyConverterTest.kt — Verifies:

Same-currency conversion (identity)

USD → ZAR and ZAR → USD conversion

Unknown currency handling (returns null)

Run Tests Locally
bash
./gradlew test
GitHub Actions CI
Every push to master automatically:

Sets up JDK 17

Restores the Firebase config from an encrypted secret

Runs all unit tests

Builds a debug APK

View results: Actions tab

🌍 REST API Integration
API	Purpose	Where Used	Hosted
Firebase Firestore	Users, trips, itinerary, expenses	All screens	Google Cloud
Firebase Authentication	Registration + login (bcrypt)	Login, SignUp	Google Cloud
ExchangeRate-API	Currency conversion	AddExpense	exchangerate-api.com
Firebase Cloud Messaging	Budget alert push	Trip Detail	Google Cloud
Sky Scrapper (RapidAPI)	Flight/hotel search (mock data)	Search screen	rapidapi.com
OpenStreetMap OSRM	Geocoding + distance	Future enhancement	project-osrm.org
Database Schema (Firestore)
text
users/{userId}
├── userId: String
├── displayName: String
├── email: String
├── homeCurrency: String  ("ZAR")
├── language: String      ("en" | "zu" | "af")
├── fcmToken: String      (Cloud Messaging token)
├── createdAt: Timestamp
│
├── trips/{tripId}
│   ├── destination: String
│   ├── startDate: Timestamp
│   ├── endDate: Timestamp
│   ├── totalBudget: Number
│   ├── remainingBudget: Number
│   ├── dailyBurnRate: Number
│   ├── currency: String
│   ├── isComplete: Boolean
│   │
│   ├── itinerary/{itemId}
│   │   ├── title: String
│   │   ├── notes: String
│   │   ├── date: Timestamp
│   │   ├── time: String
│   │   ├── isDone: Boolean
│   │   └── linkedExpenseId: String?
│   │
│   └── expenses/{expenseId}
│       ├── category: String
│       ├── vendor: String
│       ├── description: String
│       ├── costInZAR: Number
│       ├── isPaid: Boolean
│       └── date: Timestamp
🌐 Localization
The app fully supports three languages via Android's resource system:

Language	Folder	Coverage
English	values/	100%
isiZulu	values-zu/	100%
Afrikaans	values-af/	100%
Users switch language from the Settings screen; the app restarts to apply the change.

South African formatting:

Dates: DD/MM/YYYY

Numbers: R 1 500,00 (space for thousands, comma for decimals)

🤖 AI Tools Used
AI tools (Claude, ChatGPT) were used as productivity aids for:

Code scaffolding — RecyclerView adapters, Material layouts, Firebase CRUD boilerplate

Debugging — Diagnosing Firebase Auth KTX deprecation, Gradle compileSdk mismatch, RecyclerView inflation errors

Translations — Drafting isiZulu and Afrikaans strings (not validated by native speakers)

Design assets — SVG vector path data for icons

Documentation — Structuring this README

What AI did NOT do:

All architectural decisions (data model, navigation, feature prioritisation)

All Firebase configuration and API key setup

All unit test design and cases

Every AI-generated function was reviewed line-by-line before integration

Full disclosure: see AI_USAGE.md (optional — add if you want to keep the README shorter)

📸 Screenshots
Screen	Preview
Onboarding	https://docs/screenshots/onboarding.png
Login	https://docs/screenshots/login.png
Dashboard	https://docs/screenshots/dashboard.png
Create Trip	https://docs/screenshots/create_trip.png
Trip Detail	https://docs/screenshots/trip_detail.png
Add Expense	https://docs/screenshots/add_expense.png
Search	https://docs/screenshots/search.png
Settings	https://docs/screenshots/settings.png
(Replace with actual screenshots — see "Screenshot Guide" below.)

🎥 Recording the Demo Video
Requirements (per the assignment):

Show the app running on a physical phone

Voice-over explaining each feature

Show: register, login, settings, trip creation, itinerary, expenses, budget alerts, search

Show Firebase Console with encrypted user data

Show Logcat with the ExchangeRate-API network call

Suggested tool:

Windows: OBS Studio or Xbox Game Bar (Win + G)

Android: built-in screen recorder

Upload to YouTube as Unlisted → link in this README

📋 Version Control with Git
Repository: github.com/ST10456229/FarePlan_SA

Commits: Regular commits throughout development tracking feature progress, refactors, and bug fixes.

Branch strategy: master (single-branch, student project)

Ignored files:

app/google-services.json — Firebase config (contains API keys)

build/, .gradle/, .idea/ — Build outputs and IDE config

*.jks, *.keystore — Signing keys

CI/CD:

GitHub Actions runs on every push

Unit tests + debug APK build

Firebase config restored securely from a repository secret

📄 License
Student project — © 2026 Sphumelele Khuzwayo (ST10456229)

👤 Author
Sphumelele Khuzwayo

Student Number: ST10456229

GitHub: @ST10456229

🙏 Acknowledgements
Firebase — Authentication, Firestore, Cloud Messaging

ExchangeRate-API — Live currency conversion

RapidAPI — Flight and hotel search data

Material Design 3 — UI components and theming

AndroidX — Supporting libraries

Last updated: September 2026

text

---

## 🖼️ What to Add to the `docs/` Folder

The README references images. Create these:

### 1. Create the `docs` folder

In Project view:
1. Right-click project root → **New → Directory** → name it `docs`
2. Inside `docs`, create another folder: `screenshots`

### 2. Add your assets

Copy these into the folders:

| Path | What to Put There |
|------|------------------|
| `docs/logo.png` | Your FarePlan SA logo |
| `docs/architecture.png` | *(optional)* Diagram of your architecture — or delete this line from README if you don't have it |
| `docs/screenshots/onboarding.png` | Screenshot of the Onboarding screen |
| `docs/screenshots/login.png` | Screenshot of the Login screen |
| `docs/screenshots/dashboard.png` | Screenshot of the Dashboard |
| `docs/screenshots/create_trip.png` | Screenshot of Create Trip |
| `docs/screenshots/trip_detail.png` | Screenshot of Trip Detail |
| `docs/screenshots/add_expense.png` | Screenshot of Add Expense |
| `docs/screenshots/search.png` | Screenshot of Search |
| `docs/screenshots/settings.png` | Screenshot of Settings |

**How to take screenshots:**
- Run the app on your emulator/phone
- Press **Ctrl + S** (emulator) or the phone's power+volume-down
- Pull the PNGs from `Pictures/Screenshots` on the emulator or the phone's screenshot folder

---

## 📝 Customize Before Committing

Before you commit, replace these placeholders:

| Placeholder | Replace With |
|-------------|-------------|
| `https://youtu.be/YOUR_VIDEO_ID` | The actual unlisted YouTube link |
| `ST10456229` | *(already correct if this is your GitHub username)* |
| `@ST10456229` | *(already correct)* |

---

## 🚀 Push It

Once the README is saved and images are in place:

```bash
git add README.md docs/
git commit -m "Add README with full project documentation"
git push
Then go to your GitHub repo — the README will render automatically on the main page.

📋 What the README Covers (vs. Assignment Requirements)
Assignment Requirement	Where It's Covered in README
Purpose of the app	🎯 Purpose of the App section
Design considerations	🏗️ Architecture + 🌐 Localization sections
GitHub utilisation	📋 Version Control with Git section
GitHub Actions utilisation	🧪 Automated Testing + CI link
Images	📸 Screenshots section
Video link	📱 Demo Video section
AI write-up	🤖 AI Tools Used section
Everything is covered. ✅
