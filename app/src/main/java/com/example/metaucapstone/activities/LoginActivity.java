package com.example.metaucapstone;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import com.example.metaucapstone.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LoginActivity extends AppCompatActivity {

    public static final String TAG = "LoginActivity";
    public static final String DEFAULT_PROFILE_PIC = "https://firebasestorage.googleapis.com/v0/b/metau-capstone-145a4.appspot.com/o/ProfilePics%2F34AD2.jpeg?alt=media&token=ef7bea72-5852-44b3-b757-60dfd3989bd4";
    public static final int MIN_PASSWORD_LENGTH = 6;

    private InputMethodManager imm;
    private ConstraintLayout clLogin;
    private EditText etUsername;
    private EditText etPassword;
    private Button btnLogin;
    private Button btnSignup;

    private FirebaseAuth auth;

    boolean signingIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        clLogin = findViewById(R.id.clLogin);
        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        btnSignup = findViewById(R.id.btnSignUp);

        auth = FirebaseAuth.getInstance();

        btnLogin.setOnClickListener(btnLoginClicked);
        btnSignup.setOnClickListener(btnSignupClicked);
    }

    private View.OnClickListener btnLoginClicked = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            imm.hideSoftInputFromWindow(clLogin.getWindowToken(), 0);
            if (!checkInput()) {
                return;
            };
            String username = etUsername.getText().toString();
            String password = etPassword.getText().toString();
            login(username, password);

        }
    };

    private View.OnClickListener btnSignupClicked = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            imm.hideSoftInputFromWindow(clLogin.getWindowToken(), 0);
            if (!checkInput()) {
                return;
            }
            String username = etUsername.getText().toString();
            String password = etPassword.getText().toString();
            signup(username, password);

        }
    };

    @Override
    protected void onStart() {
        super.onStart();
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            startActivity(new Intent(LoginActivity.this, MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK));
            finish();
        }
    }

    private OnCompleteListener authRequestComplete = new OnCompleteListener<AuthResult>() {
        @Override
        public void onComplete(@NonNull Task task) {
            if (task.isSuccessful()) {
                if (signingIn) {
                    User user = new User(auth.getCurrentUser().getEmail());
                    storeUser(user);
                }
                new Cache(LoginActivity.this).initCache();
                startActivity(new Intent(LoginActivity.this, MainActivity.class));
            } else if (task.getException().toString().contains("already in use")) {
                Snackbar.make(clLogin, R.string.error_email_in_use,
                        Snackbar.LENGTH_LONG).show();
            } else if (task.getException().toString().contains("password is invalid")) {
                Snackbar.make(clLogin, R.string.error_incorrect_password,
                        Snackbar.LENGTH_LONG).show();
            } else {
                Snackbar.make(clLogin, R.string.error_snackbar,
                        Snackbar.LENGTH_LONG).show();
            }
        }
    };

    private void login(String username, String password) {
        signingIn = false;
        auth.signInWithEmailAndPassword(username, password).addOnCompleteListener(authRequestComplete);
    }

    private void signup(String username, String password) {
        signingIn = true;
        auth.createUserWithEmailAndPassword(username, password).addOnCompleteListener(authRequestComplete);
    }

    private void storeUser(User user) {
        String email = etUsername.getText().toString();
        String displayName = email.substring(0, email.indexOf("@"));
        user.setDisplayName(displayName);
        user.setBio(getString(R.string.new_user_bio));
        FirebaseDatabase.getInstance().getReference()
                .child("Usernames").child(auth.getCurrentUser().getUid())
                .setValue(displayName);
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                .child("Users").child(auth.getCurrentUser().getUid());
        reference.child("Object").setValue(user);
        reference.child("ProfilePic").setValue(DEFAULT_PROFILE_PIC);
    }


    private boolean checkInput() {
        Pattern validEmail = Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);
        Matcher matcher = validEmail.matcher(etUsername.getText().toString());
        if (!matcher.find()) {
            Snackbar.make(clLogin, R.string.feedback_email, Snackbar.LENGTH_LONG).show();
            return false;
        } else if (etUsername.getText().toString().isEmpty() || etPassword.getText().toString().isEmpty()) {
            Snackbar.make(clLogin, R.string.feedback_username_and_password,
                    Snackbar.LENGTH_LONG).show();
            return false;
        } else if (etPassword.getText().toString().length() < MIN_PASSWORD_LENGTH) {
            Snackbar.make(clLogin, R.string.feedback_longer_password,
                    Snackbar.LENGTH_LONG).show();
            return false;
        }
        return true;
    }
}