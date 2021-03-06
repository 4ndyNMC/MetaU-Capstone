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
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.example.metaucapstone.MainActivity;
import com.example.metaucapstone.models.Recipe;
import com.example.metaucapstone.models.User;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.parceler.Parcels;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class RecipeInformationActivity extends AppCompatActivity {

    public static final String TAG = "RecipeInformationActivity";

    ConstraintLayout clRecipeInfo;
    TextView tvTitle;
    TextView tvSummary;
    TextView tvSteps;
    ImageView ivRecipeInfo;
    ProgressBar pbRecipeInfo;
    FloatingActionButton fabSave;
    LottieAnimationView lotRecipeInfo;
    DatabaseHelper db;
    Recipe recipe;

    String currentUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
    boolean saved;
    float x1, x2, y1, y2;

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
        tvSteps = findViewById(R.id.tvSteps);
        ivRecipeInfo = findViewById(R.id.ivRecipeInfo);
        fabSave = findViewById(R.id.fabSave);
        lotRecipeInfo = findViewById(R.id.lotRecipeInfo);
        db = new DatabaseHelper(this);

        pbRecipeInfo = findViewById(R.id.pbRecipeInfo);
        tvTitle.setText(recipe.getName());
        lotRecipeInfo.setVisibility(ProgressBar.VISIBLE);
        pbRecipeInfo.setVisibility(ProgressBar.VISIBLE);
        Spoonacular.GetRecipeInfo(recipe, this);

        if (db.hasRecipe(recipe.getId())) {
            ivRecipeInfo.setVisibility(View.GONE);
            setViews(recipe.getName(), recipe.getSummary(), null, recipe.getSteps());
        }

        FirebaseDatabase.getInstance().getReference()
                .child("Recipes").child(recipe.getId()).child("Users")
                .addListenerForSingleValueEvent(checkIfSaved);

        fabSave.setVisibility(View.GONE);
        fabSave.setOnClickListener(fabSaveClicked);
    }

    @Override
    protected void onResume() {
        super.onResume();
        ((AppContext)getApplication()).setIsAppRunning(true, currentUid);
    }

    @Override
    protected void onPause() {
        super.onPause();
        ((AppContext)getApplication()).setIsAppRunning(false, currentUid);
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

    @Override
    public boolean onTouchEvent(MotionEvent touchEvent){
        switch(touchEvent.getAction()){
            case MotionEvent.ACTION_DOWN:
                x1 = touchEvent.getX();
                y1 = touchEvent.getY();
                break;
            case MotionEvent.ACTION_UP:
                x2 = touchEvent.getX();
                y2 = touchEvent.getY();
                if (x1 < x2) finish();
                break;
        }
        return false;
    }

    private View.OnClickListener fabSaveClicked = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            try {
                if (saved) unsaveRecipe();
                else saveRecipe();
            } catch (IOException e) {
                Log.e(TAG, e.toString());
            }
        }
    };

    private ValueEventListener checkIfSaved = new ValueEventListener() {
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
        public void onCancelled(@NonNull DatabaseError error) {

        }
    };

    private void unsaveRecipe() {
        saved = false;
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference recipeReference = FirebaseDatabase.getInstance().getReference()
                .child("Recipes").child(recipe.getId());
        DatabaseReference userReference = FirebaseDatabase.getInstance().getReference()
                .child("Users").child(uid);

        recipeReference.child("Users").child(uid).removeValue();
        recipeReference.child("Object").child("users").child(uid).removeValue();

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
        db.deleteRecipe(recipe.getId());
        fabSave.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_sticky_note_2_24));
    }

    private void saveRecipe() throws IOException {
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
                    recipeFromDb.setCuisines(recipe.getCuisines());
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
                userReference.child("Recipes").child(recipe.getId()).setValue(true);
                for (Recipe.Cuisine cuisine : recipe.getCuisines()) {
                    if (snapshot.child("Counts").hasChild(cuisine.name())) {
                        userReference.child("Counts").child(cuisine.name())
                                .setValue(snapshot.child("Counts").child(cuisine.name()).getValue(Integer.class));
                    } else {
                        userReference.child("Counts").child(cuisine.name()).setValue(1);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });
        db.insertRecipe(recipe.getId(), recipe);
        fabSave.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_close_24));
    }

    public void loadDataIntoUI(Recipe recipe) {
        setViews(recipe.getName(), recipe.getSummary(), recipe.getImageUrl(), recipe.getSteps());
        lotRecipeInfo.setVisibility(View.GONE);
    }

    private void setViews(String title, String summary, Object img, List<String> steps) {
        if (summary != null) tvSummary.setText(Html.fromHtml(summary, Html.FROM_HTML_MODE_COMPACT));
        tvTitle.setText(title);
        Glide.with(this)
                .load(img)
                .transform(new CenterCrop(), new RoundedCorners(25))
                .override(360, 160)
                .into(ivRecipeInfo);
        StringBuilder stepList = new StringBuilder();
        for (int i = 0; i < steps.size(); i++) {
            stepList.append("\nStep " + (i + 1) + ": " + steps.get(i) + "\n");
        }
        for (int i = 0; i < steps.size(); i++) {
            stepList.append("\n");
        }
        tvSteps.setText(stepList.toString());
        ivRecipeInfo.setVisibility(View.VISIBLE);
        pbRecipeInfo.setVisibility(ProgressBar.GONE);
        fabSave.setVisibility(View.VISIBLE);
    }
}