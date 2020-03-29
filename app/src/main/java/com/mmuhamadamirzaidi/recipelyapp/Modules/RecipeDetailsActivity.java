package com.mmuhamadamirzaidi.recipelyapp.Modules;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mmuhamadamirzaidi.recipelyapp.Common.Common;
import com.mmuhamadamirzaidi.recipelyapp.Model.Recipe;
import com.mmuhamadamirzaidi.recipelyapp.R;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class RecipeDetailsActivity extends AppCompatActivity {

    FirebaseDatabase database;
    DatabaseReference recipe;

    ImageView recipe_details_image;

    TextView recipe_details_name, recipe_details_id, recipe_details_serves, recipe_details_ingredients, recipe_details_steps;

    RatingBar detail_rating_bar;

    String recipeId="";

    Recipe currentRecipe;

    String time = new SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_details);

        // Init Firebase
        database = FirebaseDatabase.getInstance();
        recipe = database.getReference("Recipe");

        // Init view
        recipe_details_image = (ImageView) findViewById(R.id.recipe_details_image);

        recipe_details_name = (TextView) findViewById(R.id.recipe_details_name);
        recipe_details_id = (TextView) findViewById(R.id.recipe_details_id);
        recipe_details_serves = (TextView) findViewById(R.id.recipe_details_serves);
        recipe_details_ingredients = (TextView) findViewById(R.id.recipe_details_ingredients);
        recipe_details_steps = (TextView) findViewById(R.id.recipe_details_steps);

        // Get recipeId intent
        if (getIntent() != null){
            recipeId = getIntent().getStringExtra("recipeId");
        }
        if (!recipeId.isEmpty() && recipeId != null){

            if (Common.isConnectedToInternet(this)){
                loadRecipeDetails(recipeId);
            }
            else{
                Toast.makeText(RecipeDetailsActivity.this, "Please check Internet connection!", Toast.LENGTH_SHORT).show();
            }

        }
    }

    private void loadRecipeDetails(final String recipeId) {
        recipe.child(recipeId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                currentRecipe = dataSnapshot.getValue(Recipe.class);

                // Set image
                Picasso.with(getBaseContext()).load(currentRecipe.getRecipeImage()).into(recipe_details_image);

                recipe_details_name.setText(currentRecipe.getRecipeName());
                recipe_details_id.setText(recipeId);
                recipe_details_serves.setText(currentRecipe.getRecipeServes());
                recipe_details_ingredients.setText(currentRecipe.getRecipeIngredients());
                recipe_details_steps.setText(currentRecipe.getRecipeSteps());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
