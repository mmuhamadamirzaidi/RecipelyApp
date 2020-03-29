package com.mmuhamadamirzaidi.recipelyapp.Modules;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.LinearLayout;

import com.mmuhamadamirzaidi.recipelyapp.Modules.Category.CategoryActivity;
import com.mmuhamadamirzaidi.recipelyapp.R;

public class MainActivity extends AppCompatActivity {

    LinearLayout main_category, main_about;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Init Resources
        main_category = findViewById(R.id.main_category);
        main_about = findViewById(R.id.main_about);

        main_category.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent menuIntent = new Intent(MainActivity.this, CategoryActivity.class);
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
