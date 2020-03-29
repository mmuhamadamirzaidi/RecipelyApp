package com.mmuhamadamirzaidi.recipelyapp.Modules.Category;

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
import com.mmuhamadamirzaidi.recipelyapp.Model.Category;
import com.mmuhamadamirzaidi.recipelyapp.Modules.MainActivity;
import com.mmuhamadamirzaidi.recipelyapp.R;

import java.util.UUID;

import dmax.dialog.SpotsDialog;

public class AddCategoryActivity extends AppCompatActivity {

    FirebaseDatabase database;
    DatabaseReference category;

    FirebaseStorage storage;
    StorageReference storageReference;

    EditText add_category_name;
    ImageView add_category_save, add_category_image;

    Category newCategory;

    Uri saveUri;
    private final int PICK_IMAGE_REQUEST = 71;

    private AlertDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_category);

        // Custom Dialog
        dialog = new SpotsDialog.Builder().setContext(AddCategoryActivity.this).setTheme(R.style.Upload).build();

        add_category_name = (EditText) findViewById(R.id.add_category_name);

        add_category_save = (ImageView) findViewById(R.id.add_category_save);
        add_category_image = (ImageView) findViewById(R.id.add_category_image);

        // Init Firebase
        database = FirebaseDatabase.getInstance();
        category = database.getReference("Category");

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        // Select Category Image
        add_category_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addImage();
            }
        });

        // Upload Image
        add_category_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadImage();
            }
        });
    }

    private void uploadImage() {
        dialog.show();

        final String add_category_name_ongoing = add_category_name.getText().toString().trim();

        if (saveUri != null){

            String imageName = UUID.randomUUID().toString();
            final StorageReference imageFolder = storageReference.child("categories/"+imageName);

            if (add_category_name_ongoing.length() > 0) {
                imageFolder.putFile(saveUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        dialog.dismiss();

                        Toast.makeText(AddCategoryActivity.this, "Image uploaded!", Toast.LENGTH_SHORT).show();
                        imageFolder.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                dialog.dismiss();

                                //Set value for newCategory if image uploaded
                                newCategory = new Category(add_category_name_ongoing, uri.toString());

                                category.push().setValue(newCategory);
                                Toast.makeText(AddCategoryActivity.this, newCategory.getName() + " category was added!", Toast.LENGTH_SHORT).show();

                                SendUserToMainActivity();
                            }
                        });
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        dialog.dismiss();

                        Toast.makeText(AddCategoryActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
            else {
                dialog.dismiss();
                Toast.makeText(AddCategoryActivity.this, "Please input category name!", Toast.LENGTH_SHORT).show();
            }
        }
        else {
            dialog.dismiss();
            Toast.makeText(AddCategoryActivity.this, "Please select category image!", Toast.LENGTH_SHORT).show();
        }
    }

    private void addImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Image"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null){
            saveUri = data.getData();
        }
    }

    private void SendUserToMainActivity() {
        Intent mainIntent = new Intent(AddCategoryActivity.this, MainActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
        finish();
    }
}
