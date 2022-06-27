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

    private InputMethodManager imm;
    private ConstraintLayout clLogin;
    private EditText etUsername;
    private EditText etPassword;
    private Button btnLogin;
    private Button btnSignup;

    private FirebaseAuth auth;

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

        btnLogin.setOnClickListener(new View.OnClickListener() {
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
        });

        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imm.hideSoftInputFromWindow(clLogin.getWindowToken(), 0);
                if (!checkInput()) {
                    return;
                }
                String username = etUsername.getText().toString();
                String password = etPassword.getText().toString();
                if (userExists(username)) {
                    Snackbar.make(clLogin, "That email is already registered", Snackbar.LENGTH_LONG);
                    return;
                }
                signup(username, password);
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            startActivity(new Intent(LoginActivity.this, MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK));
            finish();
        }
    }

    private boolean userExists(String username) {
        return false;
    }

    private void signup(String username, String password) {
        auth.createUserWithEmailAndPassword(username, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            User user = new User(auth.getCurrentUser().getEmail());
                            storeUser(user);
                            startActivity(new Intent(LoginActivity.this, MainActivity.class));
                            finish();
                        } else if (task.getException().toString().contains("already in use")) {
                            Snackbar.make(clLogin, "That email is already in use",
                                    Snackbar.LENGTH_LONG).show();
                        } else {
                            Log.i(TAG, task.getResult().toString());
                            Snackbar.make(clLogin, "Sorry, there was an error signing up",
                                    Snackbar.LENGTH_LONG).show();
                            }
                        }
                });
    }

    private void storeUser(User user) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                .child("Users").child(auth.getCurrentUser().getUid());
        reference.setValue(user);
    }

    private void login(String username, String password) {
        auth.signInWithEmailAndPassword(username, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            startActivity(new Intent(LoginActivity.this, MainActivity.class));
                            finish();
                        } else if (task.getException().toString().contains("password is invalid")) {
                            Snackbar.make(clLogin, "Sorry, the password is incorrect",
                                    Snackbar.LENGTH_LONG).show();
                        } else {
                            Snackbar.make(clLogin, "Sorry, there was an error logging in",
                                    Snackbar.LENGTH_LONG).show();

                        }
                    }
                });
    }

    private boolean checkInput() {
        Pattern validEmail = Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);
        Matcher matcher = validEmail.matcher(etUsername.getText().toString());
        if (!matcher.find()) {
            Snackbar.make(clLogin, "Please enter a valid email", Snackbar.LENGTH_LONG).show();
            return false;
        } else if (etUsername.getText().toString().isEmpty() || etPassword.getText().toString().isEmpty()) {
            Snackbar.make(clLogin, "Please enter a username AND password",
                    Snackbar.LENGTH_LONG).show();
            return false;
        } else if (etPassword.getText().toString().length() < 6) {
            Snackbar.make(clLogin, "Please create a longer password",
                    Snackbar.LENGTH_LONG).show();
            return false;
        }
        return true;
    }
}