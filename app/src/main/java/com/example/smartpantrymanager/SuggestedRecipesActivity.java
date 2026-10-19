package com.example.smartpantrymanager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class SuggestedRecipesActivity extends AppCompatActivity {

    private RecipeAdapter adapter;
    private TextView textNoMatch;
    private DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_suggested);
        setTitle("Suggested Recipes");

        db = new DatabaseHelper(this);
        textNoMatch = findViewById(R.id.textNoMatch);
        RecyclerView recyclerView = findViewById(R.id.recyclerRecipes);

        adapter = new RecipeAdapter(recipe -> {
            Intent intent = new Intent(this, RecipeDetailActivity.class);
            intent.putExtra("recipe_id", recipe.getId());
            startActivity(intent);
        });
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Run the strict-matching rule against the current pantry
        List<Recipe> suggested = RecipeMatcher.getSuggestedRecipes(
                db.getAllRecipes(), db.getAllPantryItems());
        adapter.setRecipes(suggested);
        textNoMatch.setVisibility(suggested.isEmpty() ? View.VISIBLE : View.GONE);
    }
}