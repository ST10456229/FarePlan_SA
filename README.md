# FarePlan SA

> **Travel Smarter. Spend Clearer.**

A combined travel-booking and financial-management Android app built for the South African traveller. FarePlan SA merges travel search (flights, hotels) with real-time budget tracking, giving users a single tool to plan a trip that fits their wallet.

![FarePlan SA Logo](<img width="196" height="196" alt="logo enganced " src="https://github.com/user-attachments/assets/da461c20-04a5-4563-91c1-0a17411badd0" />
)

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
```
🧪 Automated Testing
Unit Tests

Two test suites cover the application's core business logic.

BudgetAlertHelperTest.kt

Verifies:

Spend percentage calculations:
0%
50%
100%
Values capped at 100%
Traffic-light budget status transitions:
🟢 Green
🟠 Amber
🔴 Red
Edge cases:
Zero budget
Over-budget spending
CurrencyConverterTest.kt

Verifies:

Same-currency conversion (identity conversion)
USD → ZAR conversion
ZAR → USD conversion
Unknown currency handling
null returned for unsupported currencies
Running Tests Locally

Run the complete unit test suite using Gradle:

./gradlew test
⚙️ GitHub Actions CI/CD

Every push to the master branch automatically triggers the GitHub Actions workflow.

The CI pipeline:

Sets up JDK 17
Restores the Firebase configuration from an encrypted repository secret
Runs all unit tests
Builds a debug APK

Test and build results can be viewed from the repository's Actions tab.

🌍 REST API & Cloud Service Integration
API / Service	Purpose	Where Used	Hosted By
Firebase Firestore	Users, trips, itineraries and expenses	Throughout the application	Google Cloud
Firebase Authentication	User registration and authentication	Login, Sign Up	Google Cloud
ExchangeRate-API	Live currency conversion	Add Expense	ExchangeRate-API
Firebase Cloud Messaging	Budget alert push notifications	Trip Detail	Google Cloud
Sky Scrapper (RapidAPI)	Flight and hotel search data	Search	RapidAPI
OpenStreetMap / OSRM	Geocoding and distance calculations	Future enhancement	Project OSRM
🗄️ Database Schema

FarePlan SA uses Cloud Firestore as its primary database.

users/{userId}
│
├── userId: String
├── displayName: String
├── email: String
├── homeCurrency: String       ("ZAR")
├── language: String           ("en" | "zu" | "af")
├── fcmToken: String           (Cloud Messaging token)
├── createdAt: Timestamp
│
└── trips/{tripId}
    │
    ├── destination: String
    ├── startDate: Timestamp
    ├── endDate: Timestamp
    ├── totalBudget: Number
    ├── remainingBudget: Number
    ├── dailyBurnRate: Number
    ├── currency: String
    ├── isComplete: Boolean
    │
    ├── itinerary/{itemId}
    │   ├── title: String
    │   ├── notes: String
    │   ├── date: Timestamp
    │   ├── time: String
    │   ├── isDone: Boolean
    │   └── linkedExpenseId: String?
    │
    └── expenses/{expenseId}
        ├── category: String
        ├── vendor: String
        ├── description: String
        ├── costInZAR: Number
        ├── isPaid: Boolean
        └── date: Timestamp
🌐 Localization

FarePlan SA supports three languages through Android's resource system.

Language	Android Resource Folder	Coverage
🇬🇧 English	values/	100%
🇿🇦 isiZulu	values-zu/	100%
🇿🇦 Afrikaans	values-af/	100%

Users can change the application language from the Settings screen. The application restarts to apply the selected language.

South African Formatting

FarePlan SA uses South African-friendly formatting for dates and currency:

Dates: DD/MM/YYYY
Currency: R 1 500,00
Thousands separator: Space
Decimal separator: Comma
🤖 AI Tools Used

AI tools, including Claude and ChatGPT, were used as productivity aids throughout development.

AI-Assisted Tasks
Area	Usage
Code Scaffolding	RecyclerView adapters, Material layouts and Firebase CRUD boilerplate
Debugging	Firebase Auth KTX deprecation, Gradle compileSdk mismatches and RecyclerView inflation errors
Translations	Initial isiZulu and Afrikaans string drafts
Design Assets	SVG vector path data for icons
Documentation	README structure and documentation formatting

Note: AI-generated translations were not independently validated by native speakers.

What AI Did Not Do

The following development decisions and activities were completed by the developer:

Application architecture decisions
Database and data-model design
Navigation structure
Feature prioritisation
Firebase configuration
API key configuration
Unit test design and test cases
Application feature implementation and integration

Every AI-generated function was reviewed line-by-line before being integrated into the project.

For a more detailed breakdown of AI usage, see:

AI_USAGE.md

📸 Screenshots

Screenshots of the main application screens are stored in the docs/screenshots/ directory.

Screen	Preview
Onboarding	docs/screenshots/onboarding.png
Login	docs/screenshots/login.png
Dashboard	docs/screenshots/dashboard.png
Create Trip	docs/screenshots/create_trip.png
Trip Detail	docs/screenshots/trip_detail.png
Add Expense	docs/screenshots/add_expense.png
Search	docs/screenshots/search.png
Settings	docs/screenshots/settings.png

Note: Replace the screenshot paths above with the final screenshots once they have been added to the repository.

🎥 Demo Video

The demonstration video showcases the application's main functionality and development requirements.

Demonstrated Features

The video demonstrates:

User registration
User login
Language settings
Trip creation
Itinerary management
Expense tracking
Budget monitoring
Budget alerts
Flight and hotel search
Firebase integration
Additional Demonstrations

The video also demonstrates:

The application running on a physical Android device
Firebase Console
Firestore user and trip data
Logcat output
ExchangeRate-API network requests
Demo Video

🔗 YouTube: https://youtu.be/YOUR_VIDEO_ID

The video can be uploaded to YouTube as Unlisted and the final URL added above.

📋 Version Control
Repository

github.com/ST10456229/FarePlan_SA

Development was tracked using Git throughout the project.

Git Usage

Regular commits were used to document:

Feature development
Bug fixes
Refactoring
Testing
Configuration changes
Documentation
Branch Strategy

The project uses a single primary branch:

master

This branch structure is used for the student project.

🔐 Security & Ignored Files

Sensitive and generated files are excluded from version control using .gitignore.

app/google-services.json
build/
.gradle/
.idea/
*.jks
*.keystore
Firebase Configuration

google-services.json is not committed to the repository because it contains Firebase project configuration and API-related information.

For CI/CD, the Firebase configuration is restored securely through an encrypted GitHub repository secret.

⚙️ CI/CD Pipeline

GitHub Actions is used to automate testing and application builds.

The pipeline runs automatically on every push to master.

Push to master
      │
      ▼
Set up JDK 17
      │
      ▼
Restore Firebase configuration
      │
      ▼
Run unit tests
      │
      ▼
Build debug APK
      │
      ▼
Report results
📁 Project Documentation Structure

The repository uses the following documentation structure:

FarePlan_SA/
│
├── app/
│   └── ...
│
├── docs/
│   ├── logo.png
│   ├── architecture.png
│   │
│   └── screenshots/
│       ├── onboarding.png
│       ├── login.png
│       ├── dashboard.png
│       ├── create_trip.png
│       ├── trip_detail.png
│       ├── add_expense.png
│       ├── search.png
│       └── settings.png
│
├── AI_USAGE.md
├── README.md
└── ...
🖼️ Documentation Assets

The following assets should be placed inside the docs/ directory.

File	Purpose
docs/logo.png	FarePlan SA application logo
docs/architecture.png	Application architecture diagram
docs/screenshots/onboarding.png	Onboarding screen
docs/screenshots/login.png	Login screen
docs/screenshots/dashboard.png	Dashboard
docs/screenshots/create_trip.png	Create Trip screen
docs/screenshots/trip_detail.png	Trip Detail screen
docs/screenshots/add_expense.png	Add Expense screen
docs/screenshots/search.png	Search screen
docs/screenshots/settings.png	Settings screen
📱 Taking Screenshots

Screenshots can be captured while running the application on an emulator or physical Android device.

Android Emulator

Use:

Ctrl + S
Physical Android Device

Use the device's standard screenshot shortcut:

Power + Volume Down

Screenshots should be saved as PNG files and placed in:

docs/screenshots/
📝 Before Committing

Replace the following placeholders before submitting the final repository:

Placeholder	Action
https://youtu.be/YOUR_VIDEO_ID	Replace with the actual unlisted YouTube URL
Screenshot paths	Ensure all referenced screenshots exist
docs/architecture.png	Add the architecture diagram or remove the reference
AI_USAGE.md	Add the file if detailed AI usage documentation is required
🚀 Committing the README

After adding the README and documentation assets:

git add README.md docs/
git commit -m "Add project documentation"
git push

Once pushed, GitHub will automatically render README.md on the repository's main page.

📋 Assignment Requirements Coverage
Assignment Requirement	README Section
Purpose of the application	Project introduction
Design considerations	Architecture & Localization
GitHub utilisation	Version Control
GitHub Actions utilisation	Automated Testing & CI/CD
Images	Screenshots
Demonstration video	Demo Video
AI usage disclosure	AI Tools Used
Database design	Database Schema
API integration	REST API & Cloud Services
Testing	Automated Testing
📄 License

Student Project

© 2026 Sphumelele Khuzwayo
Student Number: ST10456229

This project was developed for academic purposes.

👤 Author

Sphumelele Khuzwayo

Student Number: ST10456229

GitHub: @ST10456229

🙏 Acknowledgements

Special thanks to the technologies and services used throughout the development of FarePlan SA:

Firebase — Authentication, Firestore and Cloud Messaging
ExchangeRate-API — Live currency conversion
RapidAPI — Flight and hotel search data
Material Design 3 — UI components and theming
AndroidX — Android supporting libraries
OpenStreetMap / OSRM — Geocoding and distance services

<p align="center"> <strong>FarePlan SA</strong><br> Plan smarter. Spend smarter. Travel better. ✈️ </p>

<p align="center"> Last updated: September 2026 </p>

Everything is covered. ✅
