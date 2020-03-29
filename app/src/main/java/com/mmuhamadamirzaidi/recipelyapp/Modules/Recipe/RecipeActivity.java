package com.mmuhamadamirzaidi.recipelyapp.Modules.Recipe;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.mancj.materialsearchbar.MaterialSearchBar;
import com.mmuhamadamirzaidi.recipelyapp.Common.Common;
import com.mmuhamadamirzaidi.recipelyapp.Interface.ItemClickListener;
import com.mmuhamadamirzaidi.recipelyapp.Model.Recipe;
import com.mmuhamadamirzaidi.recipelyapp.Modules.Category.AddCategoryActivity;
import com.mmuhamadamirzaidi.recipelyapp.Modules.Category.CategoryActivity;
import com.mmuhamadamirzaidi.recipelyapp.R;
import com.mmuhamadamirzaidi.recipelyapp.SQLite.Database;
import com.mmuhamadamirzaidi.recipelyapp.ViewHolder.RecipeViewHolder;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class RecipeActivity extends AppCompatActivity {

    FirebaseDatabase database;
    DatabaseReference recipe;

    String categoryId = "";

    RecyclerView recycler_recipe;
    RecyclerView.LayoutManager layoutManager;

    FirebaseRecyclerAdapter<Recipe, RecipeViewHolder> adapter;
    FirebaseRecyclerAdapter<Recipe, RecipeViewHolder> searchAdapter;

    List<String> suggestionList = new ArrayList<>();

    MaterialSearchBar recipe_list_search_bar;

    SwipeRefreshLayout swipe_layout_recipe_list;

    // Bookmark
    Database bookmarkDB;

    ImageView recipe_add;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe);

        // Init Firebase
        database = FirebaseDatabase.getInstance();
        recipe = database.getReference("Recipe");

        // Local Bookmark Database
        bookmarkDB = new Database(this);

        // Init Resources
        recipe_add = findViewById(R.id.recipe_add);

        recipe_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent menuIntent = new Intent(RecipeActivity.this, AddRecipeActivity.class);
                menuIntent.putExtra("categoryId", categoryId);
                startActivity(menuIntent);
            }
        });

        swipe_layout_recipe_list = (SwipeRefreshLayout) findViewById(R.id.swipe_layout_recipe_list);
        swipe_layout_recipe_list.setColorSchemeResources(R.color.colorPrimaryDark);

        swipe_layout_recipe_list.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Get CategoryId intent
                if (getIntent() != null) {
                    categoryId = getIntent().getStringExtra("categoryId");
                }
                if (!categoryId.isEmpty() && categoryId != null) {

                    if (Common.isConnectedToInternet(getBaseContext())) {
                        loadRecipe(categoryId);
                    } else {
                        Toast.makeText(RecipeActivity.this, "Please check Internet connection!", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        swipe_layout_recipe_list.post(new Runnable() {
            @Override
            public void run() {
                // Get CategoryId intent
                if (getIntent() != null) {
                    categoryId = getIntent().getStringExtra("categoryId");
                }
                if (!categoryId.isEmpty() && categoryId != null) {

                    if (Common.isConnectedToInternet(getBaseContext())) {
                        loadRecipe(categoryId);
                    } else {
                        Toast.makeText(RecipeActivity.this, "Please check Internet connection!", Toast.LENGTH_SHORT).show();
                    }
                }

                // Search

                recipe_list_search_bar = (MaterialSearchBar) findViewById(R.id.recipe_list_search_bar);
                recipe_list_search_bar.setHint("Search the recipes...");

                loadSuggestion();

                recipe_list_search_bar.addTextChangeListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {

                        //When search bar typing
                        List<String> suggest = new ArrayList<String>();
                        for (String search : suggestionList) {
                            if (search.toLowerCase().contains(recipe_list_search_bar.getText().toLowerCase().trim()))
                                suggest.add(search);
                        }
                        recipe_list_search_bar.setLastSuggestions(suggest);

                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                    }
                });
                recipe_list_search_bar.setOnSearchActionListener(new MaterialSearchBar.OnSearchActionListener() {
                    @Override
                    public void onSearchStateChanged(boolean enabled) {
                        //When search bar closed, restore original list
                        if (!enabled)
                            recycler_recipe.setAdapter(adapter);
                    }

                    @Override
                    public void onSearchConfirmed(CharSequence text) {
                        //When finish. show result
                        startSearch(text);
                    }

                    @Override
                    public void onButtonClicked(int buttonCode) {

                    }
                });
            }
        });

        // Load category
        recycler_recipe = (RecyclerView) findViewById(R.id.recycler_recipe);
        layoutManager = new LinearLayoutManager(this);
        recycler_recipe.setLayoutManager(layoutManager);
    }

    private void startSearch(CharSequence text) {

        //Create query by name
        Query searchByName = recipe.orderByChild("recipeName").equalTo(text.toString().trim());

        FirebaseRecyclerOptions<Recipe> recipeOptions = new FirebaseRecyclerOptions.Builder<Recipe>()
                .setQuery(searchByName, Recipe.class)
                .build();

        searchAdapter = new FirebaseRecyclerAdapter<Recipe, RecipeViewHolder>(recipeOptions) {
            @Override
            protected void onBindViewHolder(@NonNull RecipeViewHolder viewHolder, int position, @NonNull Recipe model) {

                viewHolder.recipe_name.setText(model.getRecipeName());

                Picasso.with(getBaseContext()).load(model.getRecipeImage())
                        .into(viewHolder.recipe_image);

                final Recipe local = model;

                viewHolder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {

                        Intent recipe_detail = new Intent(RecipeActivity.this, RecipeDetailsActivity.class);
                        recipe_detail.putExtra("recipeId", searchAdapter.getRef(position).getKey());
                        startActivity(recipe_detail);
                    }
                });
            }

            @NonNull
            @Override
            public RecipeViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

                View itemView = LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.item_recipe, viewGroup, false);
                return new RecipeViewHolder(itemView);
            }
        };
        searchAdapter.startListening();
        recycler_recipe.setAdapter(searchAdapter);
    }

    private void loadSuggestion() {

        recipe.orderByChild("categoryId").equalTo(categoryId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    Recipe item = dataSnapshot1.getValue(Recipe.class);
                    suggestionList.add(item.getRecipeName());
                }

                recipe_list_search_bar.setLastSuggestions(suggestionList);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void loadRecipe(String categoryId) {

        Query searchByName = recipe.orderByChild("categoryId").equalTo(categoryId);

        FirebaseRecyclerOptions<Recipe> recipeOptions = new FirebaseRecyclerOptions.Builder<Recipe>()
                .setQuery(searchByName, Recipe.class)
                .build();

        adapter = new FirebaseRecyclerAdapter<Recipe, RecipeViewHolder>(recipeOptions) {
            @Override
            protected void onBindViewHolder(@NonNull final RecipeViewHolder viewHolder, final int position, @NonNull final Recipe model) {

                viewHolder.recipe_name.setText(model.getRecipeName());

                Picasso.with(getBaseContext()).load(model.getRecipeImage()).into(viewHolder.recipe_image);

                //Add bookmark
                if (bookmarkDB.currentBookmark(adapter.getRef(position).getKey()))
                    viewHolder.recipe_bookmark.setImageResource(R.drawable.ic_bookmark_primary_dark_24dp);

                //Remove bookmark
                viewHolder.recipe_bookmark.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!bookmarkDB.currentBookmark(adapter.getRef(position).getKey())) {
                            bookmarkDB.addToBookmark(adapter.getRef(position).getKey());
                            viewHolder.recipe_bookmark.setImageResource(R.drawable.ic_bookmark_primary_dark_24dp);
                            Toast.makeText(RecipeActivity.this, model.getRecipeName() + " added to bookmark!", Toast.LENGTH_SHORT).show();
                        } else {
                            bookmarkDB.clearBookmark(adapter.getRef(position).getKey());
                            viewHolder.recipe_bookmark.setImageResource(R.drawable.ic_bookmark_border_primary_dark_24dp);
                            Toast.makeText(RecipeActivity.this, model.getRecipeName() + " removed from bookmark!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

                final Recipe clickItem = model;
                viewHolder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {

                        Intent recipe_detail = new Intent(RecipeActivity.this, RecipeDetailsActivity.class);
                        recipe_detail.putExtra("recipeId", adapter.getRef(position).getKey());
                        startActivity(recipe_detail);
                    }
                });
            }

            @NonNull
            @Override
            public RecipeViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                View itemView = LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.item_recipe, viewGroup, false);
                return new RecipeViewHolder(itemView);
            }
        };

        adapter.startListening();
        recycler_recipe.setAdapter(adapter);
        swipe_layout_recipe_list.setRefreshing(false);
    }

    @Override
    protected void onResume() {
        super.onResume();

        //Fix back button not display Recipe
        if (adapter != null) {
            adapter.startListening();
        }

        if (searchAdapter != null){
            searchAdapter.startListening();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
        if (searchAdapter != null) {
            searchAdapter.stopListening();
        }
    }
}
