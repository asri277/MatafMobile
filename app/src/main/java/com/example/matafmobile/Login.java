package com.example.matafmobile;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

public class Login extends AppCompatActivity {

    EditText uEmail, uPassword;
    CheckBox remember;
    Button btnLogin;
    TextView uForgPass;
    ProgressBar progressBar;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    String userID, checkerType;

    private static final String SHARED_PREFS = "sharedPrefs";
    private static final String TEXT = "text";
    private static final String REMEMBER = "remember";

    private String tempEmail;
    private boolean checkOnOff;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        uEmail = findViewById(R.id.email);
        uPassword = findViewById(R.id.password);
        progressBar = findViewById(R.id.progressBar);
        btnLogin = findViewById(R.id.btnLogin);
        remember = findViewById(R.id.chkRemember);
        uForgPass = findViewById(R.id.cmdForgPass);

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();

        progressBar.setVisibility(View.GONE);

        remember.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(buttonView.isChecked()){
                    saveData();
                }else if(!buttonView.isChecked()){
                    resetData();
                }
            }
        });

        loadData();
        updateViews();

        FirebaseAuth.getInstance().signOut();//logout form firebase

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = uEmail.getText().toString().trim();
                String password = uPassword.getText().toString().trim();

                if(TextUtils.isEmpty(email)) {
                    uEmail.setError("Sila Masukkan Email");
                    return;
                }

                if(TextUtils.isEmpty(password)){
                    uPassword.setError("Sila Masukkan Password");
                    return;
                }

                if(password.length() < 6) {
                    uPassword.setError("Masukkan Password minimum 6 huruf/angka");
                    return;
                }

                progressBar.setVisibility(View.VISIBLE);

                //Intent intent = new Intent(Login.this, MainActivity.class);
                //startActivity(intent);
                //Authenticate the user

                fAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull final Task<AuthResult> task) {

                        if (task.isSuccessful()){

                            userID = fAuth.getCurrentUser().getUid();
                            DocumentReference documentReference = fStore.collection("staff").document(userID);
                            documentReference.addSnapshotListener(Login.this, new EventListener<DocumentSnapshot>() {
                                @Override
                                public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                                    checkerType = documentSnapshot.getString("uType");
                                    if (checkerType.equals("STAFF")){

                                        Toast.makeText(Login.this, "Login Berjaya!", Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(Login.this, MainActivity.class));
                                        finish();


                                    }else{
                                        progressBar.setVisibility(View.GONE);
                                        FirebaseAuth.getInstance().signOut();//logout form firebase
                                        Toast.makeText(Login.this, "Bukan Staff Berdaftar!", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });

                        }else{
                            Toast.makeText(Login.this, "Connection Lost!" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.GONE);
                        }
                    }
                });
            }
        });

        uForgPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final EditText resetEmail = new EditText(v.getContext());
                final AlertDialog.Builder passwordResetDialog = new AlertDialog.Builder(v.getContext());
                passwordResetDialog.setTitle("Reset Password?");
                passwordResetDialog.setMessage("Enter Your Email To Received Reset Link.");
                passwordResetDialog.setView(resetEmail);

                passwordResetDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //extract the email and send reset link
                        String currEmail = resetEmail.getText().toString();
                        if (TextUtils.isEmpty(currEmail)){
                            Toast.makeText(Login.this, "Sila Masukkan Email", Toast.LENGTH_SHORT).show();
                        }else{
                            fAuth.sendPasswordResetEmail(currEmail).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(Login.this, "Reset Link Sent To Your Email", Toast.LENGTH_SHORT).show();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(Login.this, "Error! Reset Link is Not Sent OR Not Verify yet OR "+e.getMessage(), Toast.LENGTH_LONG).show();
                                }
                            });
                        }
                    }
                });

                passwordResetDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //Close the dialog
                    }
                });

                passwordResetDialog.create().show();
            }
        });
    }


    private void resetData(){
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putString(TEXT, "");
        editor.putBoolean(REMEMBER, false);
        editor.apply();
    }

    private void saveData(){
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putString(TEXT, uEmail.getText().toString());
        editor.putBoolean(REMEMBER, remember.isChecked());

        editor.apply();
    }

    private void loadData(){
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        tempEmail = sharedPreferences.getString(TEXT, "");
        checkOnOff = sharedPreferences.getBoolean(REMEMBER, false);
    }

    private void updateViews(){
        uEmail.setText(tempEmail);
        remember.setChecked(checkOnOff);
    }
}

