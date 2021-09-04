package com.example.matafmobile;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.budiyev.android.codescanner.CodeScanner;
import com.budiyev.android.codescanner.CodeScannerView;
import com.budiyev.android.codescanner.DecodeCallback;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.zxing.Result;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class Scanner extends AppCompatActivity {
    CodeScanner codeScanner;
    CodeScannerView scanView;
    TextView resultData;
    DBHelper myDB;
    Button cmdVerified;

    FirebaseAuth fAuth;
    FirebaseFirestore fStore;

    String userName;
    String userID;
    String location;

    Calendar calendar;
    String currentDate;

    SimpleDateFormat format;
    String currentTime;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanner);

        myDB = new DBHelper(this);
        scanView = findViewById(R.id.scanView);
        codeScanner = new CodeScanner(this, scanView);
        resultData = findViewById(R.id.resultQR);
        cmdVerified = findViewById(R.id.btn_verified);

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();

        calendar = Calendar.getInstance(); //Date
        format = new SimpleDateFormat("HH:mm:ss");

        userID = fAuth.getCurrentUser().getUid();
        DocumentReference documentReference = fStore.collection("users").document(userID);
        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                userName = documentSnapshot.getString("fName");
            }
        });
        currentDate = DateFormat.getDateInstance().format(calendar.getTime());
        //        currentDate = DateFormat.getDateInstance(DateFormat.FULL).format(calendar.getTime()); untuk full view for date
        currentTime = format.format(calendar.getTime());


        codeScanner.setDecodeCallback(new DecodeCallback() {
            @Override
            public void onDecoded(@NonNull final Result result) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        resultData.setText(result.getText());
                        location = result.getText();
                        boolean isInserted = myDB.insertData(userID, userName, location, currentDate, currentTime);
                        if (isInserted){
                            Toast.makeText(Scanner.this, "Data Recorded!", Toast.LENGTH_SHORT).show();
                            cmdVerified.setVisibility(View.VISIBLE);
                        }else{
                            Toast.makeText(Scanner.this, "Data is Failed to Record!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

        cmdVerified.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Scanner.this, Verified_staff.class));
                finish();
            }
        });


//       codeScanner.startPreview();
//        kalau nk click on listener

    }


    @Override
    protected void onResume() {
        super.onResume();
        requestForCamera();
    }

    private void requestForCamera() {
        Dexter.withActivity(this).withPermission(Manifest.permission.CAMERA).withListener(new PermissionListener() {
            @Override
            public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                codeScanner.startPreview();
            }

            @Override
            public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {
                Toast.makeText(Scanner.this, "Camera Permission is Required", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                permissionToken.continuePermissionRequest();
            }
        }).check();
    }


}