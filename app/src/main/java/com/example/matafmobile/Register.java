package com.example.matafmobile;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.telephony.PhoneNumberUtils;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
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
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class Register extends AppCompatActivity {

    public static final String TAG = "TAG";
    EditText uName, uEmail, uPassword, uPhone;
    Button uRegisterBtn;
    TextView uLoginBtn;
    FirebaseAuth fAuth;
    ProgressBar progressBar;
    FirebaseFirestore fStore;
    String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        uName = findViewById(R.id.name);
        uEmail = findViewById(R.id.email_ahli);
        uPassword = findViewById(R.id.password_ahli);
        uPhone = findViewById(R.id.phoneNum);
        uRegisterBtn = findViewById(R.id.btnLogin_ahli);
        uLoginBtn = findViewById(R.id.linkLogin);

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        progressBar = findViewById(R.id.progressBar);

        progressBar.setVisibility(View.GONE);

        uRegisterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String email = uEmail.getText().toString().trim();
                String password = uPassword.getText().toString().trim();
                final String fullName = uName.getText().toString();
                final String phoneNum = uPhone.getText().toString();
                final String userType = "STAFF";

                if(TextUtils.isEmpty(fullName)){
                    uName.setError("Sila Masukkan Nama");
                    return;
                }

                if(isValidEmail(email) == false){
                    uEmail.setError("Sila Masukkan Email dan Sah");
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

                if(validCellPhone(phoneNum) == false){
                    uPhone.setError("Sila Masukkan Nombor telefon");
                    return;
                }

                progressBar.setVisibility(View.VISIBLE);

                //Register the user into firebase

                fAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){

                            // Sent Verification Link
                            FirebaseUser fUser = fAuth.getCurrentUser();
                            fUser.sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(Register.this, "Verification Email Has been Sent", Toast.LENGTH_SHORT).show();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.d(TAG, "onFailure: Email not sent"+ e.getMessage());
                                }
                            });

                            Toast.makeText(Register.this, "Pengguna Berjaya Berdaftar", Toast.LENGTH_SHORT).show();
                            userID = fAuth.getCurrentUser().getUid();
                            DocumentReference documentReference = fStore.collection("staff").document(userID);
                            Map<String,Object> user = new HashMap<>();
                            user.put("fName",fullName);
                            user.put("email",email);
                            user.put("phoneNum",phoneNum);
                            user.put("uType", userType);
                            documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.d(TAG, "onSuccess: user profile is created for "+ userID);
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.d(TAG, "onFailure "+ e.toString());
                                }
                            });
                            startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        }else{
                            Toast.makeText(Register.this, "Error!" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.GONE);
                        }
                    }
                });

            }
        });

        uLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),Login.class));
                finish();
            }
        });


    }

    public static boolean isValidEmail(CharSequence target) {
        return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }

    public boolean validCellPhone(String number)
    {
        return (!TextUtils.isEmpty(number) && android.util.Patterns.PHONE.matcher(number).matches());
    }

}