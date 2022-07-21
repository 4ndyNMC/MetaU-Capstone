package com.example.metaucapstone;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.util.Timer;
import java.util.TimerTask;

public class ProfileFragment extends Fragment {

    public static final String TAG = "ProfileFragment";
    public static final long TIMEOUT_LENGTH = 3000L;

    InputMethodManager imm;
    ProgressBar pbProfile;
    TextView tvDisplayName;
    TextView tvBio;
    ImageView ivProfilePic;
    Button btnFollow;
    Button btnSaved;
    DatabaseHelper db;

    User user;
    Timer timer;
    String uid;
    String currentUid;
    String key;
    String profilePicUrl;
    boolean following;
    boolean otherProfile;
    boolean[] gotResult = new boolean[1];

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
        db = new DatabaseHelper(getContext());

        // TODO added onCancelled
        btnFollow.setOnClickListener(followClicked);
        btnSaved.setOnClickListener(savedClicked);

        getUser();
    }

    @Override
    public void onResume() {
        super.onResume();
        getUser();
    }

    @Override
    public void onStop() {
        super.onStop();
        FirebaseDatabase.getInstance().getReference()
                .child("Users").child(key)
                .removeEventListener(getProfileFromNetwork);
        timer.cancel();
    }

    private final View.OnClickListener followClicked = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String currentUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
            DatabaseReference userFollowing = FirebaseDatabase.getInstance().getReference()
                    .child("Users").child(currentUid).child("Following").child(uid);
            DatabaseReference userFollowers = FirebaseDatabase.getInstance().getReference()
                    .child("Users").child(uid).child("Followers").child(currentUid);
            if (following) {
                following = false;
                try {
                    db.insertFriend(key, profilePicUrl, user);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                userFollowing.setValue(true);
                userFollowers.setValue(true);
                btnFollow.setText(R.string.unfollow);
            }
            else {
                following = true;
                db.deleteFriend(key);
                userFollowing.removeValue();
                userFollowers.removeValue();
                btnFollow.setText(R.string.follow);
            }
        }
    };

    private final View.OnClickListener savedClicked = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Fragment fragment = new SavedFragment(uid);
            getParentFragmentManager().beginTransaction().replace(R.id.flContainer, fragment).commit();
        }
    };

    private final ValueEventListener getProfileFromNetwork = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
            gotResult[0] = true;
            user = snapshot.child("Object").getValue(User.class);
            profilePicUrl = snapshot.child("ProfilePic").getValue(String.class);
            if (isVisible()) {
                setViews(user, profilePicUrl);
                if (otherProfile) {
                    btnFollow.setVisibility(View.VISIBLE);
                    btnSaved.setVisibility(View.VISIBLE);
                    if (snapshot.hasChild("Followers/" + currentUid)) {
                        following = false;
                        btnFollow.setText(R.string.unfollow);
                    } else {
                        following = true;
                        btnFollow.setText(R.string.follow);
                    }
                }
            }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) { }
    };

    private void getProfileFromDb() throws IOException, ClassNotFoundException {
        if (!db.hasFriend(key)) return;
        Cursor profileData = db.getFriendsData(key);
        profileData.moveToNext();
        byte[] serializedUser = profileData.getBlob(2);
        ByteArrayInputStream bis = new ByteArrayInputStream(serializedUser);
        ObjectInput in = new ObjectInputStream(bis);
        user = (User) in.readObject();
        ((com.example.metaucapstone.MainActivity) getContext()).runOnUiThread(() -> {
            try {
                setViews(user, db.getDefaultPfp());
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        });
    }

    private void setViews(User user, Object profilePic) {
        tvDisplayName.setText(user.getDisplayName());
        tvBio.setText(user.getBio());
        Glide.with(ProfileFragment.this)
                .load(profilePic)
                .circleCrop()
                .into(ivProfilePic);
        pbProfile.setVisibility(View.GONE);
    }

    private void getUser() {
        gotResult[0] = false;
        otherProfile = uid != null;
        currentUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        key = otherProfile ? uid : currentUid;

        imm.hideSoftInputFromWindow(this.getView().getWindowToken(), 0);
        pbProfile.setVisibility(View.VISIBLE);
        btnFollow.setVisibility(View.GONE);
        btnSaved.setVisibility(View.GONE);

        FirebaseDatabase.getInstance().getReference()
                .child("Users").child(key)
                .addListenerForSingleValueEvent(getProfileFromNetwork);
        timer = new Timer();
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                timer.cancel();
                if (!gotResult[0]) {
                    try {
                        getProfileFromDb();
                    } catch (IOException | ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        timer.schedule(timerTask, TIMEOUT_LENGTH);
    }
}