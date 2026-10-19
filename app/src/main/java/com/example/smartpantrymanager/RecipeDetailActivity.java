package com.example.smartpantrymanager;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class RecipeDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_detail);

        int recipeId = getIntent().getIntExtra("recipe_id", -1);
        DatabaseHelper db = new DatabaseHelper(this);

        Recipe found = null;
        for (Recipe r : db.getAllRecipes()) {
            if (r.getId() == recipeId) {
                found = r;
                break;
            }
        }
        if (found == null) {
            finish();
            return;
        }

        setTitle(found.getName());
        ((TextView) findViewById(R.id.textDetailName)).setText(found.getName());
        ((TextView) findViewById(R.id.textDetailSteps)).setText(found.getSteps());

        StringBuilder sb = new StringBuilder();
        for (RecipeIngredient ing : found.getIngredients()) {
            double q = ing.getQuantity();
            String qty = (q == Math.floor(q)) ? String.valueOf((int) q) : String.valueOf(q);
            sb.append("• ").append(qty).append(" ").append(ing.getUnit())
                    .append(" ").append(ing.getName()).append("\n");
        }
        ((TextView) findViewById(R.id.textDetailIngredients)).setText(sb.toString().trim());
    }
}