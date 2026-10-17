package com.example.smartpantrymanager;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.content.ContentValues;
import android.database.Cursor;
import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "pantry.db";
    private static final int DB_VERSION = 2;

    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE pantry_items (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "name TEXT NOT NULL, " +
                "quantity REAL NOT NULL, " +
                "unit TEXT NOT NULL, " +
                "expiry_date TEXT)");

        db.execSQL("CREATE TABLE recipes (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "name TEXT NOT NULL, " +
                "steps TEXT NOT NULL)");

        db.execSQL("CREATE TABLE recipe_ingredients (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "recipe_id INTEGER NOT NULL, " +
                "ingredient_name TEXT NOT NULL, " +
                "quantity REAL NOT NULL, " +
                "unit TEXT NOT NULL, " +
                "FOREIGN KEY(recipe_id) REFERENCES recipes(id))");

        seedRecipes(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS recipe_ingredients");
        db.execSQL("DROP TABLE IF EXISTS recipes");
        db.execSQL("DROP TABLE IF EXISTS pantry_items");
        onCreate(db);
    }

    // CREATE
    public long addPantryItem(String name, double quantity, String unit, String expiryDate) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("name", name);
        values.put("quantity", quantity);
        values.put("unit", unit);
        values.put("expiry_date", expiryDate);
        return db.insert("pantry_items", null, values);
    }

    // READ
    public List<PantryItem> getAllPantryItems() {
        List<PantryItem> items = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM pantry_items ORDER BY name", null);
        while (cursor.moveToNext()) {
            items.add(new PantryItem(
                    cursor.getInt(0),
                    cursor.getString(1),
                    cursor.getDouble(2),
                    cursor.getString(3),
                    cursor.getString(4)));
        }
        cursor.close();
        return items;
    }

    // UPDATE
    public int updatePantryItem(int id, String name, double quantity, String unit, String expiryDate) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("name", name);
        values.put("quantity", quantity);
        values.put("unit", unit);
        values.put("expiry_date", expiryDate);
        return db.update("pantry_items", values, "id = ?", new String[]{String.valueOf(id)});
    }

    // DELETE
    public int deletePantryItem(int id) {
        SQLiteDatabase db = getWritableDatabase();
        return db.delete("pantry_items", "id = ?", new String[]{String.valueOf(id)});
    }

    // Reads every recipe together with its ingredient rows
    public List<Recipe> getAllRecipes() {
        List<Recipe> recipes = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor rc = db.rawQuery("SELECT id, name, steps FROM recipes ORDER BY name", null);
        while (rc.moveToNext()) {
            int id = rc.getInt(0);
            Recipe recipe = new Recipe(id, rc.getString(1), rc.getString(2));

            Cursor ic = db.rawQuery(
                    "SELECT ingredient_name, quantity, unit FROM recipe_ingredients WHERE recipe_id = ?",
                    new String[]{String.valueOf(id)});
            while (ic.moveToNext()) {
                recipe.addIngredient(new RecipeIngredient(ic.getString(0), ic.getDouble(1), ic.getString(2)));
            }
            ic.close();
            recipes.add(recipe);
        }
        rc.close();
        return recipes;
    }

    // Ingredient format: "name|quantity|unit"
// Names are lowercase and singular so matching stays simple.
    private void addRecipe(SQLiteDatabase db, String name, String steps, String... ingredients) {
        ContentValues values = new ContentValues();
        values.put("name", name);
        values.put("steps", steps);
        long recipeId = db.insert("recipes", null, values);

        for (String ing : ingredients) {
            String[] p = ing.split("\\|");
            ContentValues iv = new ContentValues();
            iv.put("recipe_id", recipeId);
            iv.put("ingredient_name", p[0]);
            iv.put("quantity", Double.parseDouble(p[1]));
            iv.put("unit", p[2]);
            db.insert("recipe_ingredients", null, iv);
        }
    }

    private void seedRecipes(SQLiteDatabase db) {
        addRecipe(db, "Scrambled Eggs",
                "1. Beat the eggs with salt.\n2. Melt butter in a pan.\n3. Pour in eggs and stir gently until set.",
                "egg|3|pcs", "butter|10|g", "salt|2|g");

        addRecipe(db, "Tomato Omelette",
                "1. Chop the tomato and onion.\n2. Fry them in oil until soft.\n3. Pour in beaten eggs and cook until firm.",
                "egg|2|pcs", "tomato|1|pcs", "onion|1|pcs", "oil|15|ml", "salt|2|g");

        addRecipe(db, "Sadza with Tomato Relish",
                "1. Cook the maize meal in boiling water into thick sadza.\n2. Fry onion in oil, add chopped tomato and salt.\n3. Simmer into a relish and serve with the sadza.",
                "maize meal|300|g", "tomato|2|pcs", "onion|1|pcs", "oil|30|ml", "salt|3|g");

        addRecipe(db, "Egg Fried Rice",
                "1. Cook the rice and let it cool.\n2. Scramble the eggs in hot oil with chopped onion.\n3. Add rice and salt, and fry for 3 minutes.",
                "rice|200|g", "egg|2|pcs", "onion|1|pcs", "oil|30|ml", "salt|3|g");

        addRecipe(db, "Tomato Rice",
                "1. Fry onion in oil, add chopped tomatoes until soft.\n2. Add rice and salt, stir well.\n3. Add water and cook until the rice is tender.",
                "rice|200|g", "tomato|2|pcs", "onion|1|pcs", "oil|30|ml", "salt|3|g");

        addRecipe(db, "Cabbage Stew",
                "1. Fry onion in oil.\n2. Add chopped tomato and shredded cabbage.\n3. Season with salt and cook on low heat for 10 minutes.",
                "cabbage|300|g", "tomato|1|pcs", "onion|1|pcs", "oil|20|ml", "salt|3|g");

        addRecipe(db, "Peanut Butter Sandwich",
                "1. Spread peanut butter on one slice of bread.\n2. Cover with the second slice.",
                "bread|2|pcs", "peanut butter|30|g");

        addRecipe(db, "French Toast",
                "1. Whisk eggs, milk and sugar.\n2. Dip each slice of bread in the mixture.\n3. Fry in butter until golden on both sides.",
                "bread|4|pcs", "egg|2|pcs", "milk|100|ml", "sugar|10|g", "butter|10|g");

        addRecipe(db, "Pancakes",
                "1. Mix flour, egg, milk and sugar into a smooth batter.\n2. Heat oil in a pan.\n3. Pour in batter and cook each side until golden.",
                "flour|200|g", "egg|1|pcs", "milk|250|ml", "sugar|20|g", "oil|15|ml");

        addRecipe(db, "Mashed Potatoes",
                "1. Peel and boil the potatoes until soft.\n2. Drain and mash with butter, milk and salt.",
                "potato|500|g", "butter|20|g", "milk|50|ml", "salt|3|g");

        addRecipe(db, "Fried Potatoes",
                "1. Peel and slice the potatoes.\n2. Fry in hot oil until golden and crisp.\n3. Sprinkle with salt.",
                "potato|500|g", "oil|60|ml", "salt|3|g");

        addRecipe(db, "Bean Stew",
                "1. Boil the beans until tender.\n2. Fry onion in oil, add chopped tomatoes.\n3. Add the beans and salt, and simmer for 15 minutes.",
                "bean|250|g", "onion|1|pcs", "tomato|2|pcs", "oil|30|ml", "salt|3|g");

        addRecipe(db, "Spinach in Peanut Butter",
                "1. Fry onion and tomato in oil.\n2. Add chopped spinach and cook until wilted.\n3. Stir in peanut butter and salt, and simmer for 5 minutes.",
                "spinach|300|g", "peanut butter|40|g", "onion|1|pcs", "tomato|1|pcs", "oil|15|ml", "salt|3|g");

        addRecipe(db, "Chicken and Rice",
                "1. Brown the chicken pieces in oil with onion.\n2. Add rice, salt and water.\n3. Cover and cook until the rice and chicken are done.",
                "chicken|500|g", "rice|200|g", "onion|1|pcs", "oil|30|ml", "salt|4|g");

        addRecipe(db, "Pasta with Tomato Sauce",
                "1. Boil the pasta until soft and drain.\n2. Fry onion in oil, add chopped tomatoes and salt.\n3. Simmer into a sauce and mix with the pasta.",
                "pasta|250|g", "tomato|3|pcs", "onion|1|pcs", "oil|30|ml", "salt|3|g");

        addRecipe(db, "Banana Pancakes",
                "1. Mash the bananas.\n2. Mix with egg and flour into a batter.\n3. Fry spoonfuls in a pan until golden.",
                "banana|2|pcs", "egg|1|pcs", "flour|100|g");

        addRecipe(db, "Rice Pudding",
                "1. Simmer rice in milk on low heat, stirring often.\n2. Add sugar when the rice is soft.\n3. Cook until thick and creamy.",
                "rice|100|g", "milk|500|ml", "sugar|40|g");

        addRecipe(db, "Egg Sandwich",
                "1. Fry the eggs in butter.\n2. Place between two slices of bread.",
                "bread|2|pcs", "egg|2|pcs", "butter|10|g");

        addRecipe(db, "Chicken and Potato Stew",
                "1. Brown the chicken in oil with onion.\n2. Add chopped tomatoes, potatoes and salt.\n3. Add water and simmer until the potatoes are soft.",
                "chicken|500|g", "potato|400|g", "onion|1|pcs", "tomato|2|pcs", "oil|30|ml", "salt|4|g");
    }
}

