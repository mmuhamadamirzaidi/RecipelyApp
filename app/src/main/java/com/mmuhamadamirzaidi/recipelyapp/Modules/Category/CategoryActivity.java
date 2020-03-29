package com.mmuhamadamirzaidi.recipelyapp.Modules.Category;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.mmuhamadamirzaidi.recipelyapp.Common.Common;
import com.mmuhamadamirzaidi.recipelyapp.Interface.ItemClickListener;
import com.mmuhamadamirzaidi.recipelyapp.Model.Category;
import com.mmuhamadamirzaidi.recipelyapp.Modules.Recipe.RecipeActivity;
import com.mmuhamadamirzaidi.recipelyapp.R;
import com.mmuhamadamirzaidi.recipelyapp.ViewHolder.CategoryViewHolder;
import com.squareup.picasso.Picasso;

import java.util.UUID;

import dmax.dialog.SpotsDialog;

public class CategoryActivity extends AppCompatActivity {

    DatabaseReference category;

    FirebaseStorage storage;
    StorageReference storageReference;

    RecyclerView recycler_category;
    RecyclerView.LayoutManager layoutManager;

    FirebaseRecyclerAdapter<Category, CategoryViewHolder> adapter;

    SwipeRefreshLayout swipe_layout_category;

    ImageView category_add;

    AlertDialog dialog;
    Dialog updateDialog, updateLoadingDialog;

    Button button_update, button_cancel;

    EditText add_category_name;
    ImageView add_image_category, select_image;

    Uri saveUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);

        // Custom Dialog
        updateLoadingDialog = new SpotsDialog.Builder().setContext(CategoryActivity.this).setTheme(R.style.Update).build();
        dialog = new SpotsDialog.Builder().setContext(CategoryActivity.this).setTheme(R.style.Upload).build();

        // Swipe Layout
        swipe_layout_category = (SwipeRefreshLayout) findViewById(R.id.swipe_layout_category);
        swipe_layout_category.setColorSchemeResources(R.color.colorPrimaryDark);

        // Init Firebase
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        category = database.getReference("Category");

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        // Init Resources
        category_add = findViewById(R.id.category_add);

        category_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent menuIntent = new Intent(CategoryActivity.this, AddCategoryActivity.class);
                startActivity(menuIntent);
            }
        });

        swipe_layout_category.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (Common.isConnectedToInternet(getBaseContext())){
                    loadCategory();
                }
                else{
                    Toast.makeText(getBaseContext(), "Please check Internet connection!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Default, load for first time
        swipe_layout_category.post(new Runnable() {
            @Override
            public void run() {
                if (Common.isConnectedToInternet(getBaseContext())){
                    loadCategory();
                }
                else{
                    Toast.makeText(getBaseContext(), "Please check Internet connection!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Load category
        recycler_category = (RecyclerView) findViewById(R.id.recycler_category);
        layoutManager = new LinearLayoutManager(this);
        recycler_category.setLayoutManager(layoutManager);
    }

    private void loadCategory() {

        FirebaseRecyclerOptions<Category> options = new FirebaseRecyclerOptions.Builder<Category>()
                .setQuery(category, Category.class)
                .build();

        adapter = new FirebaseRecyclerAdapter<Category, CategoryViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull CategoryViewHolder viewHolder, int position, @NonNull Category model) {

                viewHolder.category_name.setText(model.getName());

                Picasso.with(getBaseContext()).load(model.getImage()).into(viewHolder.category_image);

                final Category clickItem = model;
                viewHolder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {

                        // Get CategoryId and send to new activity
                        Intent product_id = new Intent(CategoryActivity.this, RecipeActivity.class);
                        product_id.putExtra("categoryId", adapter.getRef(position).getKey());
                        startActivity(product_id);
                    }
                });
            }

            @NonNull
            @Override
            public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                View itemView = LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.item_category, viewGroup, false);
                return new CategoryViewHolder(itemView);
            }
        };
        adapter.startListening();
        recycler_category.setAdapter(adapter);
        swipe_layout_category.setRefreshing(false);
    }

    @Override
    protected void onResume() {
        super.onResume();

        //Fix back button not display Category
        if (adapter != null){
            adapter.startListening();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {

        if (item.getTitle().equals(Common.UPDATE)) {

            showUpdateDialog(adapter.getRef(item.getOrder()).getKey(), adapter.getItem(item.getOrder()));
        } else if (item.getTitle().equals(Common.DELETE)) {

            deleteCategory(adapter.getRef(item.getOrder()).getKey());
        }

        return super.onContextItemSelected(item);
    }

    private void deleteCategory(String key) {
        category.child(key).removeValue();
    }

    private void showUpdateDialog(final String key, final Category item) {

        updateDialog = new Dialog(CategoryActivity.this);
        updateDialog.setCanceledOnTouchOutside(false);
        updateDialog.setContentView(R.layout.dialog_activity_update_category);

        add_image_category = updateDialog.findViewById(R.id.add_image_category);

        add_category_name = updateDialog.findViewById(R.id.add_category_name);

        select_image = updateDialog.findViewById(R.id.select_image);

        button_update = updateDialog.findViewById(R.id.button_update);
        button_cancel = updateDialog.findViewById(R.id.button_cancel);

        // Set Original Value
        add_category_name.setText(item.getName());

        select_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addImage();
            }
        });

        add_image_category.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadNewImage(item);
            }
        });

        button_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                updateLoadingDialog.show();
                item.setName(add_category_name.getText().toString().trim());
                category.child(key).setValue(item);

                updateLoadingDialog.dismiss();
                updateDialog.cancel();
                Toast.makeText(CategoryActivity.this, item.getName() + " category was updated!", Toast.LENGTH_SHORT).show();
            }
        });

        button_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateDialog.cancel();
            }
        });

        updateDialog.show();

    }

    private void uploadNewImage(final Category item) {
        dialog.show();

        if (saveUri != null) {

            String imageName = UUID.randomUUID().toString();
            final StorageReference imageFolder = storageReference.child("categories/" + imageName);
            imageFolder.putFile(saveUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    dialog.dismiss();

                    Toast.makeText(CategoryActivity.this, "Image uploaded!", Toast.LENGTH_SHORT).show();
                    imageFolder.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            dialog.dismiss();

                            // Update new image info and upload link
                            item.setImage(uri.toString());
                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    dialog.dismiss();

                    updateDialog.cancel();
                    Toast.makeText(CategoryActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            dialog.dismiss();
            Toast.makeText(CategoryActivity.this, "Please select category image!", Toast.LENGTH_SHORT).show();
        }
    }

    private void addImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Image"), Common.PICK_IMAGE_REQUEST);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Common.PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            saveUri = data.getData();
        }
    }
}
