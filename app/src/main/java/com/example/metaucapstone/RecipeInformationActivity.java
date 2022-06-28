package com.example.metaucapstone;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.example.metaucapstone.models.Recipe;
import com.example.metaucapstone.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

public class RecipeInformationActivity extends AppCompatActivity {

    public static final String TAG = "RecipeInformationActivity";

    ConstraintLayout clRecipeInfo;
    TextView tvTitle;
    TextView tvSummary;
    ImageView ivRecipeInfo;
    ProgressBar pbRecipeInfo;
    FloatingActionButton fabSave;
    Recipe recipe;

    boolean saved;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_information);

        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(R.drawable.ic_baseline_image_24);

        recipe = Parcels.unwrap(getIntent().getParcelableExtra("recipe"));
        clRecipeInfo = findViewById(R.id.clRecipeInfo);
        tvTitle = findViewById(R.id.tvRecipeInfoName);
        tvSummary = findViewById(R.id.tvSummary);
        ivRecipeInfo = findViewById(R.id.ivRecipeInfo);
        fabSave = findViewById(R.id.fabSave);

        pbRecipeInfo = findViewById(R.id.pbRecipeInfo);
        tvTitle.setText(recipe.getName());
        pbRecipeInfo.setVisibility(ProgressBar.VISIBLE);
        Spoonacular.GetRecipeInfo(recipe, this);

        DatabaseReference recipeUsersRef = FirebaseDatabase.getInstance().getReference()
                        .child("Recipes").child(recipe.getId()).child("Users");
        recipeUsersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.hasChild(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                    saved = true;
                    fabSave.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_close_24));
                }
                else {
                    saved = false;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });

        fabSave.setVisibility(View.GONE);
        fabSave.setOnClickListener(fabSaveClicked);
    }

    private View.OnClickListener fabSaveClicked = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (saved) unsaveRecipe();
            else saveRecipe();
        }
    };

    private void unsaveRecipe() {
        saved = false;
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference userReference = FirebaseDatabase.getInstance().getReference()
                .child("Users").child(uid);

        userReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.child("Object").getValue(User.class);
                user.removeRecipe(recipe);
                userReference.child("Object").setValue(user);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });
        userReference.child("Recipes").child(recipe.getId()).removeValue();

        fabSave.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_sticky_note_2_24));
    }

    private void saveRecipe() {
        saved = true;
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference recipeReference = FirebaseDatabase.getInstance().getReference()
                .child("Recipes");
        DatabaseReference userReference = FirebaseDatabase.getInstance().getReference()
                .child("Users").child(uid);

        recipeReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.hasChild(recipe.getId())) {
                    Log.i(TAG, "does not have id");
                    recipe.addUser(uid);
                    recipeReference.child(recipe.getId()).child("Object").setValue(recipe);
                } else {
                    Log.i(TAG, "has id");
                    Recipe recipeFromDb = snapshot.child(recipe.getId()).child("Object")
                            .getValue(Recipe.class);
                    recipeFromDb.addUser(uid);
                    recipeReference.child(recipe.getId()).child("Object").setValue(recipeFromDb);
                }
                recipeReference.child(recipe.getId()).child("Users").child(uid)
                        .setValue(true);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });

        userReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.child("Object").getValue(User.class);
                user.saveRecipe(recipe);
                userReference.child("Object").setValue(user);
                userReference.child("Recipes").child(recipe.getId())
                        .setValue(true);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });
        fabSave.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_close_24));
    }

    public void loadDataIntoUI(Recipe recipe) {
        tvTitle.setText(recipe.getName());
        tvSummary.setText(Html.fromHtml(recipe.getSummary(), Html.FROM_HTML_MODE_COMPACT));
        Glide.with(this)
                .load(recipe.getImageUrl())
                .transform(new CenterCrop(), new RoundedCorners(25))
                .override(360, 160)
                .into(ivRecipeInfo);
        pbRecipeInfo.setVisibility(ProgressBar.GONE);
        fabSave.setVisibility(View.VISIBLE);
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
                startActivity(new Intent(RecipeInformationActivity.this,
                        LoginActivity.class));
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}