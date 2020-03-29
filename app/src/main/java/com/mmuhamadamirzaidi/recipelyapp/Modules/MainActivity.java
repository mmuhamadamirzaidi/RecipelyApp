package com.mmuhamadamirzaidi.recipelyapp.Modules;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.mmuhamadamirzaidi.recipelyapp.R;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity {

    CircleImageView main_category, main_bookmark, main_about;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Init Resources
        main_category = findViewById(R.id.main_category);
        main_bookmark = findViewById(R.id.main_bookmark);
        main_about = findViewById(R.id.main_about);

        main_category.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent menuIntent = new Intent(MainActivity.this, CategoryActivity.class);
                startActivity(menuIntent);
            }
        });

        main_bookmark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent menuIntent = new Intent(MainActivity.this, BookmarkActivity.class);
                startActivity(menuIntent);
            }
        });

        main_about.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent menuIntent = new Intent(MainActivity.this, AboutActivity.class);
                startActivity(menuIntent);
            }
        });
    }
}
