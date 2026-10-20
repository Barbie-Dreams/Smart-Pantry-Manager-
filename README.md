# Smart Pantry Manager

A Java Android app that helps reduce food waste. The user tracks the
ingredients they already have at home, and the app suggests only the recipes
they can cook right now, with no shopping trip needed.

Built for Mobile App Development 700 (Richfield), Faculty of Information Technology.

## Features
- Pantry management: add, edit and delete ingredients (name, quantity, unit, optional expiry date)
- Pantry list screen (RecyclerView with a custom adapter)
- 19 pre-loaded recipes, seeded on first run
- Suggested Recipes screen using the strict-matching rule
- Recipe Detail screen with ingredients and method
- Settings screen with an expiring-soon alerts toggle
- Input validation and an empty-state message when no recipes match

## Strict-matching rule
A recipe is suggested only if every ingredient it needs is in the pantry in at
least the required quantity. If even one ingredient is missing or short, the
recipe is excluded. Matching ignores case and simple plurals ("Tomatoes" matches
"tomato") and converts units (kg to g, l to ml).

## Database: SQLite
I chose SQLite (SQLiteOpenHelper) because it runs locally on the device, needs
no internet or backend, and keeps data after the app is closed. The database has
three tables: `pantry_items`, `recipes` and `recipe_ingredients`.

## Setup and run
1. Install Android Studio and clone this repository.
2. Open the project folder in Android Studio and let Gradle sync.
3. Create an emulator (Device Manager) or connect an Android phone with USB debugging on.
4. Press Run on the `app` configuration.

Requirements: Java, minimum SDK 24.

## Author
Cheryl-Jane T Gutura
