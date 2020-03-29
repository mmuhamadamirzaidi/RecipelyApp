package com.mmuhamadamirzaidi.recipelyapp.Modules.Recipe;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.mmuhamadamirzaidi.recipelyapp.Common.Common;
import com.mmuhamadamirzaidi.recipelyapp.Model.Recipe;
import com.mmuhamadamirzaidi.recipelyapp.Modules.MainActivity;
import com.mmuhamadamirzaidi.recipelyapp.R;

import java.util.UUID;

import dmax.dialog.SpotsDialog;

public class AddRecipeActivity extends AppCompatActivity {

    ImageView add_recipe_save, add_recipe_image;

    EditText add_recipe_name, add_recipe_serves, add_recipe_ingredients, add_recipe_steps;

    Recipe newRecipe;

    Uri saveUri;

    private AlertDialog dialog;

    FirebaseDatabase database;
    DatabaseReference recipe;

    FirebaseStorage storage;
    StorageReference storageReference;

    String categoryId="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_recipe);

        // Get Category Id
        Intent intent = getIntent();
        categoryId = intent.getStringExtra("categoryId");

        // Custom Dialog
        dialog = new SpotsDialog.Builder().setContext(AddRecipeActivity.this).setTheme(R.style.Upload).build();

        // Init Firebase
        database = FirebaseDatabase.getInstance();
        recipe = database.getReference("Recipe");

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        add_recipe_save = findViewById(R.id.add_recipe_save);
        add_recipe_image = findViewById(R.id.add_recipe_image);

        add_recipe_name = findViewById(R.id.add_recipe_name);
        add_recipe_serves = findViewById(R.id.add_recipe_serves);
        add_recipe_ingredients = findViewById(R.id.add_recipe_ingredients);
        add_recipe_steps = findViewById(R.id.add_recipe_steps);

        // Select Image
        add_recipe_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addImage();
            }
        });

        // Save All Recipe Information
        add_recipe_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadImage();
            }
        });
    }

    private void addImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Image"), Common.PICK_IMAGE_REQUEST);
    }

    private void uploadImage() {
        dialog.show();

        final String add_recipe_name_ongoing = add_recipe_name.getText().toString().trim();

        if (saveUri != null){

            String imageName = UUID.randomUUID().toString();
            final StorageReference imageFolder = storageReference.child("products/"+imageName);

            if (add_recipe_name_ongoing.length() > 0) {
                imageFolder.putFile(saveUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        dialog.dismiss();

                        Toast.makeText(AddRecipeActivity.this, "Image uploaded!", Toast.LENGTH_SHORT).show();
                        imageFolder.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                dialog.dismiss();

                                newRecipe = new Recipe();

                                newRecipe.setRecipeImage(uri.toString());

                                newRecipe.setRecipeName(add_recipe_name.getText().toString().trim());
                                newRecipe.setRecipeServes(add_recipe_serves.getText().toString().trim());
                                newRecipe.setRecipeIngredients(add_recipe_ingredients.getText().toString().trim());
                                newRecipe.setRecipeSteps(add_recipe_steps.getText().toString().trim());
                                newRecipe.setCategoryId(categoryId);

                                recipe.push().setValue(newRecipe);
                                Toast.makeText(AddRecipeActivity.this, newRecipe.getRecipeName() + " recipe was added!", Toast.LENGTH_SHORT).show();

                                SendUserToMainActivity();
                            }
                        });
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        dialog.dismiss();

                        Toast.makeText(AddRecipeActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
            else {
                dialog.dismiss();
                Toast.makeText(AddRecipeActivity.this, "Please input recipe name!", Toast.LENGTH_SHORT).show();
            }
        }
        else {
            dialog.dismiss();
            Toast.makeText(AddRecipeActivity.this, "Please select recipe image!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Common.PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            saveUri = data.getData();
        }
    }

    private void SendUserToMainActivity() {
        Intent mainIntent = new Intent(AddRecipeActivity.this, MainActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
        finish();
    }
}
