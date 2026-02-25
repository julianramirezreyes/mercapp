# Mercapp

Mercapp is an **offline-first shopping list** app built with **Kotlin Multiplatform** and **Compose Multiplatform**.

- **Primary language**: Kotlin
- **UI**: Compose Multiplatform (Material 3)
- **Local DB**: SQLDelight (SQLite)
- **Backend**: Supabase (PostgREST + Auth)

## Features

- **Offline-first**: create/edit establishments and products without connectivity.
- **Sync**: automatic push/pull sync to Supabase with basic conflict handling.
- **Shopping list**: products can be toggled in/out of the list per establishment.
- **Share**: copy shopping list content for easy sharing (e.g. WhatsApp).
- **Session persistence**: keep auth session locally and refresh when needed.

## Project Structure

- **`composeApp/`**
  - Compose Multiplatform application (Android + Desktop + Web targets).
  - Main UI lives in `composeApp/src/commonMain/kotlin`.
- **`shared/`**
  - Shared Kotlin Multiplatform code (domain, data, sync, repositories, SQLDelight schema).
- **`iosApp/`**
  - iOS entry point (Xcode project).
- **`server/`**
  - Ktor server module (optional / experimental).

## Requirements

- **JDK 17** (recommended)
- Android Studio / IntelliJ IDEA (for Android + KMP tooling)
- Xcode (only for iOS)

## Build & Run

### Android

- **Debug APK**

```bash
./gradlew :composeApp:assembleDebug
```

- **Install Debug on device (ADB)**

```bash
./gradlew :composeApp:installDebug
```

### Desktop (JVM)

```bash
./gradlew :composeApp:run
```

### Web

- **Wasm (recommended)**

```bash
./gradlew :composeApp:wasmJsBrowserDevelopmentRun
```

- **JS (legacy)**

```bash
./gradlew :composeApp:jsBrowserDevelopmentRun
```

### iOS

Open `iosApp/` in Xcode and run from there.

## Create a Shareable Release APK (Signed)

`assembleRelease` may generate an **unsigned** APK by default. To share/install a release APK you must **sign it**.

### 1) Create a keystore (one-time)

```bash
keytool -genkeypair -v \
  -keystore mercapp-release.keystore \
  -alias mercapp \
  -keyalg RSA -keysize 2048 -validity 10000
```

### 2) Add signing properties (recommended)

Add to `~/.gradle/gradle.properties`:

```properties
RELEASE_STORE_FILE=/absolute/path/to/mercapp-release.keystore
RELEASE_STORE_PASSWORD=your_password
RELEASE_KEY_ALIAS=mercapp
RELEASE_KEY_PASSWORD=your_password
```

### 3) Build release

```bash
./gradlew :composeApp:assembleRelease
```

APK output:

- `composeApp/build/outputs/apk/release/`

## Troubleshooting

- **Android Studio shows Gradle version errors but `./gradlew build` succeeds**
  - Ensure the IDE is configured to use the **Gradle Wrapper**.
  - Sync Gradle and, if needed, invalidate caches.

## Development

- Formatting and style follow Kotlin/Compose conventions.
- Prefer small, focused commits.

## License

Private project (add a license if you plan to open source it).