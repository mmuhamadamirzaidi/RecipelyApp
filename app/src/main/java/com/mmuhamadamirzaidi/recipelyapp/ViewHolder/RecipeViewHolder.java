package com.mmuhamadamirzaidi.recipelyapp.ViewHolder;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.mmuhamadamirzaidi.recipelyapp.Interface.ItemClickListener;
import com.mmuhamadamirzaidi.recipelyapp.R;

public class RecipeViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public ImageView recipe_image, recipe_bookmark;
    public TextView recipe_name;

    private ItemClickListener itemClickListener;

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public RecipeViewHolder(@NonNull View itemView) {
        super(itemView);

        recipe_image = (ImageView) itemView.findViewById(R.id.recipe_image);
        recipe_bookmark = (ImageView) itemView.findViewById(R.id.recipe_bookmark);

        recipe_name = (TextView) itemView.findViewById(R.id.recipe_name);

        itemView.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        itemClickListener.onClick(v, getAdapterPosition(), false);
    }
}
