package com.example.smartpantrymanager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class RecipeMatcher {

    // STRICT RULE: a recipe is suggested only if EVERY ingredient is in the
    // pantry in at least the required quantity. One missing = excluded.
    public static List<Recipe> getSuggestedRecipes(List<Recipe> allRecipes, List<PantryItem> pantry) {
        Map<String, Double> totals = buildPantryTotals(pantry);
        List<Recipe> suggested = new ArrayList<>();
        for (Recipe recipe : allRecipes) {
            if (canMake(recipe, totals)) {
                suggested.add(recipe);
            }
        }
        return suggested;
    }

    // Adds up the pantry by normalized name + base unit, so two "tomato"
    // entries are combined and 0.5 kg counts as 500 g.
    private static Map<String, Double> buildPantryTotals(List<PantryItem> pantry) {
        Map<String, Double> totals = new HashMap<>();
        for (PantryItem item : pantry) {
            String key = makeKey(item.getName(), item.getUnit());
            double qty = toBaseQuantity(item.getQuantity(), item.getUnit());
            Double existing = totals.get(key);
            totals.put(key, (existing == null ? 0 : existing) + qty);
        }
        return totals;
    }

    private static boolean canMake(Recipe recipe, Map<String, Double> totals) {
        for (RecipeIngredient ing : recipe.getIngredients()) {
            Double have = totals.get(makeKey(ing.getName(), ing.getUnit()));
            double need = toBaseQuantity(ing.getQuantity(), ing.getUnit());
            if (have == null || have + 0.0001 < need) {
                return false; // missing or not enough: reject the whole recipe
            }
        }
        return true;
    }

    private static String makeKey(String name, String unit) {
        return normalizeName(name) + "|" + baseUnit(unit);
    }

    // "Tomatoes" -> "tomato", "EGGS" -> "egg", "  Peanut  Butter " -> "peanut butter"
    static String normalizeName(String name) {
        String n = name.trim().toLowerCase(Locale.ROOT).replaceAll("\\s+", " ");
        if (n.endsWith("ies") && n.length() > 4) {
            return n.substring(0, n.length() - 3) + "y";
        }
        if (n.endsWith("oes")) {
            return n.substring(0, n.length() - 2);
        }
        if (n.endsWith("s") && !n.endsWith("ss") && !n.endsWith("us")) {
            return n.substring(0, n.length() - 1);
        }
        return n;
    }

    // Maps different spellings of a unit to one base unit: g, ml or pcs
    static String baseUnit(String unit) {
        String u = unit.trim().toLowerCase(Locale.ROOT);
        switch (u) {
            case "g": case "gram": case "grams":
            case "kg": case "kilogram": case "kilograms":
                return "g";
            case "ml": case "milliliter": case "milliliters":
            case "millilitre": case "millilitres":
            case "l": case "liter": case "liters": case "litre": case "litres":
                return "ml";
            case "pcs": case "pc": case "piece": case "pieces":
            case "whole": case "unit": case "units":
                return "pcs";
            default:
                return u;
        }
    }

    // Converts kg to g and l to ml so quantities compare correctly
    static double toBaseQuantity(double quantity, String unit) {
        switch (unit.trim().toLowerCase(Locale.ROOT)) {
            case "kg": case "kilogram": case "kilograms":
            case "l": case "liter": case "liters": case "litre": case "litres":
                return quantity * 1000;
            default:
                return quantity;
        }
    }
}