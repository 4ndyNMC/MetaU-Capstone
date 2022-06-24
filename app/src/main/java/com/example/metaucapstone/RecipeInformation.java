package com.example.metaucapstone;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.google.firebase.auth.FirebaseAuth;

import org.parceler.Parcels;

public class RecipeInformation extends AppCompatActivity {

    TextView tvTitle;
    ImageView ivRecipeInfo;
    ProgressBar pbRecipeInfo;
    Recipe recipe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_information);

        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(R.drawable.ic_baseline_image_24);

        recipe = Parcels.unwrap(getIntent().getParcelableExtra("recipe"));
        tvTitle = findViewById(R.id.tvRecipeInfoName);
        ivRecipeInfo = findViewById(R.id.ivRecipeInfo);

        pbRecipeInfo = findViewById(R.id.pbRecipeInfo);
        tvTitle.setText(recipe.getName());
        pbRecipeInfo.setVisibility(ProgressBar.VISIBLE);
        Spoonacular.GetRecipeInfo(recipe, this);
    }

    public void loadDataIntoUI(Recipe recipe) {
        tvTitle.setText(recipe.getName());
        Glide.with(this)
                .load(recipe.getImageUrl())
                .transform(new CenterCrop(), new RoundedCorners(25))
                .override(360, 160)
                .into(ivRecipeInfo);
        pbRecipeInfo.setVisibility(ProgressBar.GONE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_top, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.btnLogout:
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(RecipeInformation.this, LoginActivity.class));
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}