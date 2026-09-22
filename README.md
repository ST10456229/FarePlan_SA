# FarePlan SA

> **Travel Smarter. Spend Clearer.**

A combined travel-booking and financial-management Android app built for the South African traveller. FarePlan SA merges travel search (flights, hotels) with real-time budget tracking, giving users a single tool to plan a trip that fits their wallet.

<p align="center">
  <img width="120" alt="FarePlan SA Logo" src="https://github.com/user-attachments/assets/9870ac5f-8acd-4e26-a8c6-c2245d50550d" />
</p>

---

## 📱 Demo Video

Watch the full demonstration on YouTube:

<p align="center">
  🎥 <a href="https://youtu.be/YOUR_VIDEO_ID"><strong>FarePlan SA — App Prototype Demonstration</strong></a>
</p>

> Replace `YOUR_VIDEO_ID` with the unlisted YouTube link once uploaded.

---

## 🎯 Purpose of the App

The contemporary digital travel landscape is fragmented. Booking platforms like Booking.com and KAYAK handle reservations but ignore budgeting. Budgeting apps like Spendee track expenses but don't help you book. FarePlan SA bridges this gap by becoming a **financial-first travel planner**.

The app's core innovation is **Dynamic Budget Tethering** — the ability to filter search results based on the user's remaining trip budget. Users set a budget once, and every flight, hotel, or expense they add recalculates their remaining funds and adjusts recommendations accordingly.

The name **FarePlan SA** captures the mission:

* **"Fare"** — the transactional aspect (flights, hotels, cars)
* **"Plan"** — the strategic, financial orchestration
* **"SA"** — South African heritage with isiZulu and Afrikaans support

---

## ✨ Features

### Core Features

* **User Authentication** — Firebase Auth (email/password + Google), with bcrypt-hashed passwords stored securely
* **Onboarding** — First-time setup: pick home currency and preferred language
* **Trip Planning** — Create trips with destination, dates, and total budget
* **Dynamic Budget Tethering** — Search results filtered by remaining budget
* **Itinerary Builder** — Add, edit, tick off, and delete itinerary items with timeline view
* **Expense Tracking** — Log expenses such as accommodation, tours, and food with category and paid status
* **Real-Time Budget Alerts** — Traffic-light system:

  * 🟢 Green: `< 70%`
  * 🟠 Amber: `70–89%`
  * 🔴 Red: `≥ 90%`
* **Push Notifications** — Firebase Cloud Messaging at 80%, 90%, and 100% budget thresholds
* **Multi-Currency** — Live ZAR conversion via ExchangeRate-API with a 6-hour cache
* **Multi-Language** — Full English, isiZulu, and Afrikaans support
* **South African Number/Date Formatting** — `R 1 500,00` and `DD/MM/YYYY`

---

# 📸 Application Screens

## 🚀 Splash & Onboarding

<table>
<tr>
<td align="center">
<strong>Splash Screen</strong><br><br>
<img width="300" alt="FarePlan SA Splash Screen" src="https://github.com/user-attachments/assets/0a846eb2-2b1f-4644-a294-ab61b5ce7add" />
</td>

<td align="center">
<strong>Onboarding</strong><br><br>
<img width="300" alt="FarePlan SA Onboarding Screen" src="https://github.com/user-attachments/assets/db04bf92-097e-49a5-9710-0e5945ae6530" />
</td>
</tr>
</table>

---

## 🔐 Authentication

<table>
<tr>
<td align="center">
<strong>Login</strong><br><br>
<img width="300" alt="FarePlan SA Login Screen" src="https://github.com/user-attachments/assets/c0329598-c3b2-47b6-9f40-80cb5d2511b3" />
</td>

<td align="center">
<strong>Sign Up</strong><br><br>
<img width="300" alt="FarePlan SA Sign Up Screen" src="https://github.com/user-attachments/assets/a56267de-31f4-48c6-b5e5-4f0f40de26f9" />
</td>
</tr>
</table>

---

## 💰 Dashboard & Trip Planning

<table>
<tr>
<td align="center">
<strong>Dashboard</strong><br>
<sub>Financial Overview</sub><br><br>
<img width="300" alt="FarePlan SA Dashboard" src="https://github.com/user-attachments/assets/28ecffda-23f6-4690-bbec-3d1332dacc29" />
</td>

<td align="center">
<strong>Create Trip</strong><br><br>
<img width="300" alt="FarePlan SA Create Trip" src="https://github.com/user-attachments/assets/95f6da75-4291-429c-a02a-f8829ad237cc" />
</td>
</tr>
</table>

---

## 🗓️ Trip Details & Itinerary

<table>
<tr>
<td align="center">
<strong>Trip Detail</strong><br>
<sub>Itinerary + Expenses</sub><br><br>
<img width="300" alt="FarePlan SA Trip Detail" src="https://github.com/user-attachments/assets/34cbabc8-5614-4795-bdc7-ae6f5dab02c6" />
</td>

<td align="center">
<strong>Trip Detail</strong><br>
<sub>Additional View</sub><br><br>
<img width="300" alt="FarePlan SA Trip Detail Additional View" src="https://github.com/user-attachments/assets/79fb793a-a29a-46c4-9985-fedf19679ba8" />
</td>
</tr>

<tr>
<td align="center">
<strong>Add Itinerary Item</strong><br><br>
<img width="300" alt="FarePlan SA Add Itinerary Item" src="https://github.com/user-attachments/assets/5136fe7a-f201-4725-b5fc-3f01103ea0d3" />
</td>

<td align="center">
<strong>Add Expense</strong><br><br>
<img width="300" alt="FarePlan SA Add Expense" src="https://github.com/user-attachments/assets/c38698d1-bac7-4590-ab29-42853888df11" />
</td>
</tr>
</table>

---

## ✈️🔎 Travel Search

FarePlan SA provides flight and hotel search functionality through its integrated travel APIs.

<table>
<tr>
<td align="center">
<strong>Search</strong><br>
<sub>Travel Search</sub><br><br>
<img width="300" alt="FarePlan SA Search Screen" src="https://github.com/user-attachments/assets/70bdbdf0-1be9-4b4d-a4a7-c5afcb07b52e" />
</td>

<td align="center">
<strong>Flight Search</strong><br><br>
<img width="300" alt="FarePlan SA Flight Search" src="https://github.com/user-attachments/assets/bdd39c22-9a11-4257-8f11-d699f4e4f861" />
</td>

<td align="center">
<strong>Hotel Search</strong><br><br>
<img width="300" alt="FarePlan SA Hotel Search" src="https://github.com/user-attachments/assets/03c7912d-2911-45f4-9acb-dc40a3edced3" />
</td>
</tr>
</table>

---

## ⚙️ Settings

<table>
<tr>
<td align="center">
<strong>Settings</strong><br><br>
<img width="300" alt="FarePlan SA Settings" src="https://github.com/user-attachments/assets/60fba34d-4c9a-4661-96ab-d7a3a6fedaaa" />
</td>

<td align="center">
<strong>Language Settings</strong><br><br>
<img width="300" alt="FarePlan SA Language Settings" src="https://github.com/user-attachments/assets/3994ee75-1aec-48b6-aa6c-cd05f09808aa" />
</td>
</tr>
</table>

---

## 🧭 Application Flow

The primary application flow can be summarised as:

```text
Splash
   │
   ▼
Onboarding
   │
   ▼
Login / Sign Up
   │
   ▼
Dashboard
   │
   ├───────────────┐
   ▼               ▼
Create Trip      Settings
   │
   ▼
Trip Detail
   │
   ├───────────────┐
   ▼               ▼
Itinerary       Expenses
   │               │
   └───────┬───────┘
           ▼
     Budget Tracking
           │
           ▼
    Budget Alerts
           │
           ▼
      Travel Search
     ┌─────┴─────┐
     ▼           ▼
  Flights      Hotels
```

---

## 🛠️ Technologies Used

* **Kotlin**
* **Android SDK**
* **AndroidX**
* **Material Design 3**
* **Firebase Authentication**
* **Firebase Firestore**
* **Firebase Cloud Messaging**
* **ExchangeRate-API**
* **RapidAPI / Sky Scrapper**
* **OpenStreetMap**
* **OSRM**
* **Git & GitHub**
* **GitHub Actions**

---

## 🧪 Testing

The application includes automated unit tests covering:

* Budget percentage calculations
* Budget alert thresholds
* Zero-budget handling
* Over-budget scenarios
* Currency conversion
* Same-currency conversion
* USD ↔ ZAR conversion
* Unsupported currencies

Run the tests locally with:

```bash
./gradlew test
```

---

## ⚙️ CI/CD

GitHub Actions automatically runs whenever changes are pushed to the `master` branch.

```text
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
```

---

## 🤖 AI-Assisted Development

AI tools, including **ChatGPT** and **Claude**, were used as productivity aids during development.

AI assistance included:

* Code scaffolding
* Debugging
* Initial translation drafts
* Design asset generation
* Documentation formatting

The developer remained responsible for:

* Application architecture
* Database design
* Navigation structure
* Feature prioritisation
* Firebase configuration
* API configuration
* Unit-test design
* Feature implementation
* Integration

Every AI-generated function was reviewed before being integrated into the project.

> **Note:** AI-generated translations were not independently validated by native speakers.

For further information, see `AI_USAGE.md`.

---

## 🔐 Security

Sensitive and generated files are excluded from version control through `.gitignore`.

```text
app/google-services.json
build/
.gradle/
.idea/
*.jks
*.keystore
```

`google-services.json` is not committed to the repository because it contains Firebase project configuration and API-related information.

For CI/CD, the Firebase configuration is restored through an encrypted GitHub repository secret.

---

## 📁 Project Structure

```text
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
│       ├── splash.png
│       ├── onboarding.png
│       ├── login.png
│       ├── signup.png
│       ├── dashboard.png
│       ├── create_trip.png
│       ├── trip_detail.png
│       ├── add_itinerary.png
│       ├── add_expense.png
│       ├── search.png
│       └── settings.png
│
├── AI_USAGE.md
├── README.md
└── ...
```

---

## 📋 Assignment Requirements Coverage

| Requirement             | Covered In              |
| ----------------------- | ----------------------- |
| Application purpose     | Purpose of the App      |
| Application features    | Features                |
| Application screenshots | Application Screens     |
| Design considerations   | Application Flow        |
| Database design         | Database documentation  |
| API integration         | Technologies Used       |
| Automated testing       | Testing                 |
| GitHub utilisation      | Version Control         |
| GitHub Actions          | CI/CD                   |
| AI usage disclosure     | AI-Assisted Development |
| Demonstration video     | Demo Video              |

---

## 📄 License

**Student Project**

© 2026 Sphumelele Khuzwayo
**Student Number:** ST10456229

This project was developed for academic purposes.

---

## 👤 Author

**Sphumelele Khuzwayo**

Student Number: `ST10456229`

GitHub: `@ST10456229`

---

## 🙏 Acknowledgements

Special thanks to the technologies and services used throughout the development of FarePlan SA:

* **Firebase** — Authentication, Firestore and Cloud Messaging
* **ExchangeRate-API** — Live currency conversion
* **RapidAPI** — Flight and hotel search data
* **Material Design 3** — UI components and theming
* **AndroidX** — Android supporting libraries
* **OpenStreetMap / OSRM** — Geocoding and distance services

---

<p align="center">
  <strong>FarePlan SA</strong><br>
  Travel Smarter. Spend Clearer. ✈️
</p>

<p align="center">
  Last updated: September 2026
</p>

---

## 🏗️ Architecture

FarePlan SA follows a layered Android architecture where the mobile application communicates with Firebase services and external REST APIs.

```text
┌───────────────────────────────────────────────────────────────┐
│                        MOBILE CLIENT                          │
│                    Kotlin + Material 3                       │
│                                                               │
│  Activities • ViewBinding • RecyclerView • AndroidX          │
└───────────────────────────────┬───────────────────────────────┘
                                │
                         HTTPS / Firebase SDK
                                │
                                ▼
┌───────────────────────────────────────────────────────────────┐
│                           FIREBASE                            │
│                                                               │
│   ┌─────────────────┐  ┌─────────────────┐  ┌──────────────┐ │
│   │ Authentication  │  │    Firestore    │  │ Cloud        │ │
│   │                 │  │                 │  │ Messaging    │ │
│   │ Email / Google  │  │     NoSQL DB    │  │              │ │
│   └─────────────────┘  └─────────────────┘  └──────────────┘ │
│                                                               │
│                 Google Cloud Infrastructure                   │
└───────────────────────────────┬───────────────────────────────┘
                                │
                           REST / Retrofit
                                │
                                ▼
┌───────────────────────────────────────────────────────────────┐
│                       EXTERNAL SERVICES                       │
│                                                               │
│  • ExchangeRate-API                                           │
│    Live currency conversion                                   │
│                                                               │
│  • Sky Scrapper / RapidAPI                                    │
│    Flight and hotel search                                    │
│                                                               │
│  • OpenStreetMap / OSRM                                       │
│    Geocoding and routing                                      │
└───────────────────────────────────────────────────────────────┘
```

### Architecture Overview

| Component                    | Responsibility                                   |
| ---------------------------- | ------------------------------------------------ |
| **Mobile Client**            | Android application interface and business logic |
| **Firebase Authentication**  | User registration and authentication             |
| **Cloud Firestore**          | Stores users, trips, itineraries and expenses    |
| **Firebase Cloud Messaging** | Budget alert push notifications                  |
| **ExchangeRate-API**         | Live currency conversion                         |
| **Sky Scrapper / RapidAPI**  | Flight and hotel search                          |
| **OpenStreetMap / OSRM**     | Geocoding and routing services                   |
| **Retrofit**                 | HTTP communication with external REST APIs       |

---

## 🛠️ Tech Stack

| Layer                   | Technology                                                |
| ----------------------- | --------------------------------------------------------- |
| **Language**            | Kotlin                                                    |
| **UI**                  | Material 3, AndroidX, ViewBinding, RecyclerView           |
| **Database**            | Firebase Firestore (NoSQL, offline persistence)           |
| **Authentication**      | Firebase Authentication (Email/Password, Google OAuth)    |
| **Push Notifications**  | Firebase Cloud Messaging                                  |
| **Currency Conversion** | ExchangeRate-API (Retrofit + Gson)                        |
| **Image Loading**       | Glide *(optional)*                                        |
| **Localization**        | Android Resources (`values/`, `values-zu/`, `values-af/`) |
| **Testing**             | JUnit 4, GitHub Actions CI                                |
| **Version Control**     | Git + GitHub                                              |

---

# 📂 Project Structure

```text
FarePlan_SA/
│
├── .github/
│   └── workflows/
│       └── android.yml                 # GitHub Actions CI
│
├── app/
│   ├── google-services.json             # Firebase config (gitignored)
│   ├── build.gradle.kts
│   │
│   └── src/
│       ├── main/
│       │   ├── java/com/example/fareplansa/
│       │   │
│       │   │   ├── MainActivity.kt                    # Router
│       │   │   ├── SplashActivity.kt                  # Splash screen
│       │   │   ├── OnboardingActivity.kt              # First-time setup
│       │   │   ├── LoginActivity.kt                   # Sign-in
│       │   │   ├── SignUpActivity.kt                  # Registration
│       │   │   ├── DashboardActivity.kt               # Home
│       │   │   ├── CreateTripActivity.kt              # New trip form
│       │   │   ├── TripDetailActivity.kt              # Trip details
│       │   │   ├── AddItineraryItemActivity.kt        # Add/edit itinerary
│       │   │   ├── AddExpenseActivity.kt              # Add expense
│       │   │   ├── SearchActivity.kt                  # Travel search
│       │   │   ├── SettingsActivity.kt                # Language + currency
│       │   │   │
│       │   │   ├── AuthErrorMapper.kt                 # Firebase errors
│       │   │   ├── BudgetAlertHelper.kt               # Budget logic
│       │   │   ├── CurrencyRepository.kt              # ExchangeRate-API
│       │   │   ├── CurrencyConverter.kt               # Conversion utility
│       │   │   ├── NavigationHelper.kt                # Navigation
│       │   │   ├── LocaleHelper.kt                    # Language switching
│       │   │   ├── FarePlanMessagingService.kt        # FCM service
│       │   │   │
│       │   │   ├── Expense.kt                          # Data model
│       │   │   ├── ItineraryItem.kt                    # Data model
│       │   │   ├── Trip.kt                             # Data model
│       │   │   └── UserProfile.kt                      # Data model
│       │   │
│       │   ├── res/
│       │   │   ├── layout/                             # Activity/item layouts
│       │   │   ├── drawable/                           # Icons/backgrounds
│       │   │   ├── values/                             # Strings/themes/styles
│       │   │   ├── values-zu/                          # isiZulu translations
│       │   │   ├── values-af/                          # Afrikaans translations
│       │   │   ├── color/                              # State selectors
│       │   │   └── menu/                               # Navigation menus
│       │   │
│       │   └── AndroidManifest.xml
│       │
│       └── test/
│           └── java/com/example/fareplansa/
│               ├── BudgetAlertHelperTest.kt
│               └── CurrencyConverterTest.kt
│
├── functions/                             # Firebase Cloud Functions
│   └── index.js
│
├── docs/
│   ├── logo.png
│   ├── architecture.png
│   └── screenshots/
│
├── .gitignore
├── build.gradle.kts
├── settings.gradle.kts
├── firebase.json
├── AI_USAGE.md
└── README.md
```

---

# 🚀 Getting Started

Follow these steps to set up FarePlan SA locally.

## Prerequisites

Before running the project, make sure you have:

* **Android Studio** Hedgehog (2023.1.1) or later
* **JDK 17**
* **Android SDK 34** or later
* A **Firebase project** with:

  * Firebase Authentication enabled
  * Cloud Firestore enabled
  * Firebase Cloud Messaging configured
* An **ExchangeRate-API** account

---

## 1. Clone the Repository

Clone the project from GitHub:

```bash
git clone https://github.com/ST10456229/FarePlan_SA.git
cd FarePlan_SA
```

---

## 2. Add `google-services.json`

Download your Firebase configuration file from the **Firebase Console**.

Place the file here:

```text
app/google-services.json
```

> ⚠️ **Security:** `google-services.json` is excluded from Git through `.gitignore` and should not be committed to the repository.

---

## 3. Configure ExchangeRate-API

Create an account with ExchangeRate-API and obtain an API key.

Open:

```text
CurrencyRepository.kt
```

Replace the API key placeholder with your own key:

```kotlin
"YOUR_API_KEY_HERE"
```

> ⚠️ Do not commit private API keys or other secrets to GitHub.

---

## 4. Open the Project in Android Studio

1. Launch **Android Studio**.
2. Select **File → Open**.
3. Select the cloned `FarePlan_SA` project directory.
4. Allow Gradle to sync.
5. Wait for the project dependencies to finish downloading.

---

## 5. Build and Run

Connect a physical Android device or start an Android emulator.

Then:

1. Select the desired device.
2. Click the green **▶ Run** button.
3. Wait for the application to build and install.
4. Launch **FarePlan SA**.

---

## 🧪 Run Tests

To run the unit tests from the terminal:

```bash
./gradlew test
```

Or use Android Studio:

**Gradle → Tasks → verification → test**

---

## ⚙️ Build the Debug APK

To manually build the debug APK:

```bash
./gradlew assembleDebug
```

The generated APK can be found under:

```text
app/build/outputs/apk/debug/
```

---

## 🔄 GitHub Actions

The project includes a GitHub Actions workflow located at:

```text
.github/workflows/android.yml
```

The workflow automatically:

1. Sets up JDK 17
2. Restores Firebase configuration
3. Runs unit tests
4. Builds the debug APK
5. Reports the build/test results

The workflow is triggered when changes are pushed to the `master` branch.

---

## 🔐 Environment & Security Notes

The following files should **not** be committed to the repository:

```text
google-services.json
*.jks
*.keystore
local.properties
```

API keys, Firebase configuration, signing keys and other credentials should be stored securely.

For CI/CD, required Firebase configuration is restored using encrypted **GitHub repository secrets**.

---

## 📌 Development Notes

FarePlan SA is a student-developed Android application created as part of an academic software development project.

The application combines:

* Travel planning
* Flight and hotel searching
* Itinerary management
* Expense tracking
* Budget monitoring
* Currency conversion
* Push notifications
* Multi-language support

The architecture is designed around an Android mobile client connected to Firebase cloud services and external REST APIs.

---

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
