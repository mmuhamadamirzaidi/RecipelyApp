package com.mmuhamadamirzaidi.recipelyapp.Modules;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.mmuhamadamirzaidi.recipelyapp.Common.Common;
import com.mmuhamadamirzaidi.recipelyapp.Interface.ItemClickListener;
import com.mmuhamadamirzaidi.recipelyapp.Model.Category;
import com.mmuhamadamirzaidi.recipelyapp.R;
import com.mmuhamadamirzaidi.recipelyapp.ViewHolder.CategoryViewHolder;
import com.squareup.picasso.Picasso;

public class CategoryActivity extends AppCompatActivity {

    DatabaseReference category;

    RecyclerView recycler_category;
    RecyclerView.LayoutManager layoutManager;

    FirebaseRecyclerAdapter<Category, CategoryViewHolder> adapter;

    SwipeRefreshLayout swipe_layout_category;

    ImageView category_back, category_add;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);

        // Swipe Layout
        swipe_layout_category = (SwipeRefreshLayout) findViewById(R.id.swipe_layout_category);
        swipe_layout_category.setColorSchemeResources(R.color.colorPrimaryDark);

        // Init Firebase
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        category = database.getReference("Category");

        // Init Resources
        category_back = findViewById(R.id.category_back);
        category_add = findViewById(R.id.category_add);

        category_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent categoryIntent = new Intent(CategoryActivity.this, AddCategoryActivity.class);
//                startActivity(categoryIntent);
                Toast.makeText(CategoryActivity.this, "Add Category Button", Toast.LENGTH_SHORT).show();
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
//                        Intent product_id = new Intent(CategoryActivity.this, RecipeDetailsActivity.class);
//                        product_id.putExtra("categoryId", adapter.getRef(position).getKey());
//                        startActivity(product_id);

                        Toast.makeText(CategoryActivity.this, "Key: "+adapter.getRef(position).getKey(), Toast.LENGTH_SHORT).show();
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
}
