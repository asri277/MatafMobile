package com.example.matafmobile;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

public class Verified_staff extends AppCompatActivity {

    ImageView verified_staff_img;
    Button cmdDone;
    DBHelper myDB;
    TextView num, staffID, staffName, location, date, time;

    FirebaseAuth fAuth = FirebaseAuth.getInstance();
    StorageReference storageReference = FirebaseStorage.getInstance().getReference();//in setting

    String userID = fAuth.getCurrentUser().getUid();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verified_staff);

        myDB = new DBHelper(Verified_staff.this);
        verified_staff_img = findViewById(R.id.verified_staff_image);
        cmdDone = findViewById(R.id.btnDone_verified_staff);
        num = findViewById(R.id.num_verified);
        staffID = findViewById(R.id.staffID_verified);
        staffName = findViewById(R.id.staffName_verified);
        location = findViewById(R.id.location_verified);
        date = findViewById(R.id.date_verified);
        time = findViewById(R.id.time_verified);

        Cursor res = myDB.getAllData();
        if (res.getCount() == 0){
            Toast.makeText(Verified_staff.this, "Tiada Data Dijumpai!", Toast.LENGTH_SHORT).show();
            return;
        }

        if(res.moveToLast()){
            num.setText("No. :\n" + res.getString(0));
            staffID.setText("Staff ID :\n" + res.getString(1));
            staffName.setText("Staff Name :\n" + res.getString(2));
            location.setText("Location :\n" + res.getString(3));
            date.setText("Date :\n" + res.getString(4));
            time.setText("Time :\n" + res.getString(5));
            Toast.makeText(Verified_staff.this, "Verified!", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(Verified_staff.this, "Error Verified!", Toast.LENGTH_SHORT).show();
        }



        StorageReference profileRef = storageReference.child("user/"+userID+"/profile.jpg"); //in main
        profileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri).into(verified_staff_img);
            }
        });

        cmdDone.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.Q)
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Verified_staff.this, Scanner.class));
                finish();
            }
        });
    }
}