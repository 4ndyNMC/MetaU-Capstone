package com.example.metaucapstone;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.metaucapstone.models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ProfileFragment extends Fragment {

    ProgressBar pbProfile;
    TextView tvDisplayName;
    TextView tvBio;
    ImageView ivProfilePic;

    public ProfileFragment() { }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        pbProfile = view.findViewById(R.id.pbProfile);
        tvDisplayName = view.findViewById(R.id.tvProfileDisplayName);
        tvBio = view.findViewById(R.id.tvProfileBio);
        ivProfilePic = view.findViewById(R.id.ivProfile);

        pbProfile.setVisibility(View.VISIBLE);
        setViews();
    }

    private void setViews() {
        FirebaseDatabase.getInstance().getReference()
                .child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        User user = snapshot.child("Object").getValue(User.class);
                        tvDisplayName.setText(user.getDisplayName());
                        tvBio.setText(user.getBio());
                        Glide.with(ProfileFragment.this)
                                .load(snapshot.child("ProfilePic").getValue(String.class))
                                .circleCrop()
                                .into(ivProfilePic);
                        pbProfile.setVisibility(View.GONE);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) { }
                });
    }
}