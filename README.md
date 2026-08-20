# HabitTrack 🌱

A simple and polished Android habit tracker built with Java and XML.

## Features

- Add habits
- Prevent duplicate habits
- Mark habits as completed today
- Undo habit completion
- Track current streaks
- View weekly progress
- Delete habits
- Undo deletion
- Persistent local storage
- Light mode
- Dark mode
- Responsive user interface

## Tech Stack

- Java
- XML
- Android Studio
- Android SDK
- SharedPreferences
- Material Design

## How It Works

HabitTrack stores habit data locally on the Android device.

Each habit contains:

- A unique ID
- A habit name
- A list of completed dates

The application uses this information to calculate current streaks and weekly progress.

## Persistence

Habit data remains available after closing or restarting the application because it is stored locally on the device.

## Installation

Download the latest APK from the Releases section and install it on an Android device.

## Project Structure

```text
HabitTrack/
├── app/
│   └── src/
│       └── main/
│           ├── java/
│           ├── res/
│           └── AndroidManifest.xml
├── gradle/
├── README.md
├── build.gradle
└── settings.gradle
