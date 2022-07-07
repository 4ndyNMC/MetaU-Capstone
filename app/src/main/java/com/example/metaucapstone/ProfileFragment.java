package com.example.metaucapstone;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.metaucapstone.models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ProfileFragment extends Fragment {

    public static final String TAG = "ProfileFragment";

    InputMethodManager imm;
    ProgressBar pbProfile;
    TextView tvDisplayName;
    TextView tvBio;
    ImageView ivProfilePic;
    Button btnFollow;
    Button btnSaved;

    String uid;

    public ProfileFragment() {
        uid = null;
    }

    public ProfileFragment(String uid) {
        this.uid = uid;
    }

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

        imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        pbProfile = view.findViewById(R.id.pbProfile);
        tvDisplayName = view.findViewById(R.id.tvProfileDisplayName);
        tvBio = view.findViewById(R.id.tvProfileBio);
        ivProfilePic = view.findViewById(R.id.ivProfile);
        btnFollow = view.findViewById(R.id.btnFollow);
        btnSaved = view.findViewById(R.id.btnSaved);

        btnFollow.setOnClickListener(followClicked);

        setViews();
    }

    @Override
    public void onResume() {
        super.onResume();
        setViews();
    }

    private View.OnClickListener followClicked = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String currentUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
            DatabaseReference userFollowing = FirebaseDatabase.getInstance().getReference()
                    .child("Users").child(currentUid).child("Following").child(uid);
            DatabaseReference userFollowers = FirebaseDatabase.getInstance().getReference()
                    .child("Users").child(uid).child("Followers").child(currentUid);
            if (btnFollow.getText().toString().equals("Follow")) {
                userFollowing.setValue(true);
                userFollowers.setValue(true);
                btnFollow.setText(R.string.unfollow);
            }
            else {
                userFollowing.removeValue();
                userFollowers.removeValue();
                btnFollow.setText(R.string.follow);
            }
        }
    };

    private View.OnClickListener savedClicked = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            
        }
    };

    private void setViews() {
        boolean otherProfile = uid != null;
        String currentUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        String key = otherProfile ? uid : currentUid;

        imm.hideSoftInputFromWindow(this.getView().getWindowToken(), 0);
        pbProfile.setVisibility(View.VISIBLE);
        btnFollow.setVisibility(View.GONE);
        btnSaved.setVisibility(View.GONE);

        FirebaseDatabase.getInstance().getReference()
                .child("Users").child(key)
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
                        if (otherProfile) {
                            btnFollow.setVisibility(View.VISIBLE);
                            btnSaved.setVisibility(View.VISIBLE);
                            if (snapshot.hasChild("Followers/" + currentUid)) btnFollow.setText(R.string.unfollow);
                            else btnFollow.setText(R.string.follow);
                        }
                        pbProfile.setVisibility(View.GONE);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) { }
                });
    }
}