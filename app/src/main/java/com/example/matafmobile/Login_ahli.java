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

public class Login_ahli extends AppCompatActivity {

    EditText uEmail_ahli, uPassword_ahli;
    CheckBox remember_ahli;
    Button btnLogin_ahli;
    TextView uRegisterBtn_ahli, uForgPass_ahli;
    ProgressBar progressBar_ahli;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    String userID_ahli, checkerType_ahli;

    private static final String SHARED_PREFS = "sharedPrefs";
    private static final String TEXT = "text";
    private static final String REMEMBER = "remember";

    private String tempEmail;
    private boolean checkOnOff;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_ahli);

        uEmail_ahli = findViewById(R.id.email_ahli);
        uPassword_ahli = findViewById(R.id.password_ahli);
        progressBar_ahli = findViewById(R.id.progressBar_ahli);
        btnLogin_ahli = findViewById(R.id.btnLogin_ahli);
        uRegisterBtn_ahli = findViewById(R.id.linkRegister_ahli);
        remember_ahli = findViewById(R.id.chkRemember_ahli);
        uForgPass_ahli = findViewById(R.id.cmdForgPass_ahli);

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();

        progressBar_ahli.setVisibility(View.GONE);

        remember_ahli.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(buttonView.isChecked()){
                    saveData_ahli();
                }else if(!buttonView.isChecked()){
                    resetData_ahli();
                }
            }
        });

        loadData_ahli();
        updateViews_ahli();

        FirebaseAuth.getInstance().signOut();//logout form firebase

        btnLogin_ahli.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = uEmail_ahli.getText().toString().trim();
                String password = uPassword_ahli.getText().toString().trim();

                if(TextUtils.isEmpty(email)) {
                    uEmail_ahli.setError("Sila Masukkan Email");
                    return;
                }

                if(TextUtils.isEmpty(password)){
                    uPassword_ahli.setError("Sila Masukkan Password");
                    return;
                }

                if(password.length() < 6) {
                    uPassword_ahli.setError("Masukkan Password minimum 6 huruf/angka");
                    return;
                }

                progressBar_ahli.setVisibility(View.VISIBLE);

                //Authenticate the user

                fAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull final Task<AuthResult> task) {

                        if (task.isSuccessful()){

                            userID_ahli = fAuth.getCurrentUser().getUid();
                            DocumentReference documentReference = fStore.collection("ahli").document(userID_ahli);
                            documentReference.addSnapshotListener(Login_ahli.this, new EventListener<DocumentSnapshot>() {
                                @Override
                                public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                                    checkerType_ahli = documentSnapshot.getString("uType");
                                    if (checkerType_ahli.equals("AHLI")){

                                        Toast.makeText(Login_ahli.this, "Login Berjaya!", Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(Login_ahli.this, MainActivity_ahli.class));
                                        finish();


                                    }else{
                                        progressBar_ahli.setVisibility(View.GONE);
                                        FirebaseAuth.getInstance().signOut();//logout form firebase
                                        Toast.makeText(Login_ahli.this, "Bukan Ahli Berdaftar!", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });

                        }else{
                            Toast.makeText(Login_ahli.this, "Connection Lost!" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            progressBar_ahli.setVisibility(View.GONE);
                        }
                    }
                });
            }
        });

        uRegisterBtn_ahli.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),Register_ahli.class));
                finish();
            }
        });

        uForgPass_ahli.setOnClickListener(new View.OnClickListener() {
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
                            Toast.makeText(Login_ahli.this, "Sila Masukkan Email", Toast.LENGTH_SHORT).show();
                        }else{
                            fAuth.sendPasswordResetEmail(currEmail).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(Login_ahli.this, "Reset Link Sent To Your Email", Toast.LENGTH_SHORT).show();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(Login_ahli.this, "Error! Reset Link is Not Sent OR Not Verify yet OR "+e.getMessage(), Toast.LENGTH_LONG).show();
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


    private void resetData_ahli(){
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putString(TEXT, "");
        editor.putBoolean(REMEMBER, false);
        editor.apply();
    }

    private void saveData_ahli(){
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putString(TEXT, uEmail_ahli.getText().toString());
        editor.putBoolean(REMEMBER, remember_ahli.isChecked());

        editor.apply();
    }

    private void loadData_ahli(){
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        tempEmail = sharedPreferences.getString(TEXT, "");
        checkOnOff = sharedPreferences.getBoolean(REMEMBER, false);
    }

    private void updateViews_ahli(){
        uEmail_ahli.setText(tempEmail);
        remember_ahli.setChecked(checkOnOff);
    }
}