package com.example.matafmobile;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

public class MainActivity_ahli extends AppCompatActivity {

    Button btnLogOut;
    ImageButton btnProfile, btnQRGenerator;

    private long backPressedTime;
    private Toast backToast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_ahli);

        btnLogOut = findViewById(R.id.main_ahli_logout);
        btnProfile = findViewById(R.id.profile_ahli);
        btnQRGenerator = findViewById(R.id.cmd_qr_generatorAhli);

        btnQRGenerator.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity_ahli.this, QR_generator.class));
            }
        });

        btnLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();//logout form firebase
                startActivity(new Intent(MainActivity_ahli.this, Login_ahli.class));
                finish();
            }
        });

        btnProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity_ahli.this, Profile_ahli.class));
            }
        });
    }

    @Override//press again to exit
    public void onBackPressed() {
        if(backPressedTime + 2000 > System.currentTimeMillis()){
            backToast.cancel();
            super.onBackPressed();
            return;
        }else{
            backToast = Toast.makeText(this, "Press back again to Logout", Toast.LENGTH_SHORT);
            backToast.show();
        }
        backPressedTime = System.currentTimeMillis();
    }
}