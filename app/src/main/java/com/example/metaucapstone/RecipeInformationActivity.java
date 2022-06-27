package com.example.metaucapstone;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.parceler.Parcels;

public class RecipeInformationActivity extends AppCompatActivity {

    public static final String TAG = "RecipeInformationActivity";

    TextView tvTitle;
    TextView tvSummary;
    ImageView ivRecipeInfo;
    ProgressBar pbRecipeInfo;
    FloatingActionButton fabSave;
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
        tvSummary = findViewById(R.id.tvSummary);
        ivRecipeInfo = findViewById(R.id.ivRecipeInfo);
        fabSave = findViewById(R.id.fabSave);

        pbRecipeInfo = findViewById(R.id.pbRecipeInfo);
        tvTitle.setText(recipe.getName());
        pbRecipeInfo.setVisibility(ProgressBar.VISIBLE);
        Spoonacular.GetRecipeInfo(recipe, this);

        fabSave.setVisibility(View.GONE);
        fabSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveRecipe();
            }
        });
    }

    private void saveRecipe() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                .child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                user.saveRecipe(recipe);
                reference.setValue(user);
                Log.i(TAG, "recipe saved");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
//        reference.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                for (DataSnapshot userSnapshot : snapshot.getChildren()) {
//                    Log.i(TAG, userSnapshot.getKey() + " | " + FirebaseAuth.getInstance().getCurrentUser().getUid());
//                    if (userSnapshot.getKey().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
//                        User user = userSnapshot.getValue(User.class);
//                        user.saveRecipe(recipe);
//                        reference.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(user)
//                                .addOnCompleteListener(new OnCompleteListener<Void>() {
//                                    @Override
//                                    public void onComplete(@NonNull Task<Void> task) {
//                                        Log.i(TAG, "User recipe saved!");
//                                    }
//                                });
//                        Log.i(TAG, user.getSaved().toString());
//                    }
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) { }
//        });
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
                startActivity(new Intent(RecipeInformationActivity.this, LoginActivity.class));
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}