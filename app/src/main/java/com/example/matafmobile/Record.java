package com.example.matafmobile;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class Record extends AppCompatActivity {
    DBHelper myDB;
    TextView staffID, staffName;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    String userID;
    EditText searchInput;
    Button btnFile;

    String fileName = "Record_Bacaan/bacaan";
    StringBuffer buffer = new StringBuffer();
    String theContent;

    private ArrayList<Record_list> recordList;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);

        myDB = new DBHelper(this);
        staffID = findViewById(R.id.staffID_record);
        staffName = findViewById(R.id.staffName_record);
        searchInput = findViewById(R.id.searchInput_record);
        recyclerView = findViewById(R.id.allRecordView_record);
        btnFile = findViewById(R.id.btnFile_record);

        recordList = new ArrayList<>();
        final Record_list_adapter adapter = new Record_list_adapter(recordList);

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();

        userID = fAuth.getCurrentUser().getUid();

        staffID.setText(userID);
        DocumentReference documentReference = fStore.collection("users").document(userID);
        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                staffName.setText(documentSnapshot.getString("fName"));
            }
        });

        String num1, num2, num3, num4, num5, num6;
        Cursor res = myDB.getAllData();
        if (res.getCount() == 0){
            Toast.makeText(Record.this, "Tiada Data Dijumpai!", Toast.LENGTH_SHORT).show();
            return;
        }


        while (res.moveToNext()){

            num1 = res.getString(0);
            num2 = res.getString(1);
            num3 = res.getString(2);
            num4 = res.getString(3);
            num5 = res.getString(4);
            num6 = res.getString(5);
            recordList.add(new Record_list(num1, num2, num3, num4, num5, num6));

            buffer.append("Number : " + num1 + "\n");
            buffer.append("Staff ID : " + num2 + "\n");
            buffer.append("Staff Name : " + num3 + "\n");
            buffer.append("Location : " + num4 + "\n");
            buffer.append("Date : " + num5 + "\n");
            buffer.append("Time : " + num6 + "\n\n");
            theContent = buffer.toString();
        }

        //show All data
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);

        searchInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                filter(s.toString());
            }

            private void filter(String text) {
                ArrayList<Record_list> filteredList = new ArrayList<>();

                for (Record_list itemList : recordList) {
                    if (itemList.getLocation().toLowerCase().contains(text.toLowerCase())){
                        filteredList.add(itemList);
                    }
                }

                adapter.filterList(filteredList);
            }
        });

        if (ActivityCompat.checkSelfPermission(Record.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
        PackageManager.PERMISSION_GRANTED){
            CreateFolder(fileName, theContent);
        }else{
            //kalau x grant permission minta
            ActivityCompat.requestPermissions(Record.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    100);
        }

        btnFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //get name from Declaration
                Uri uri = Uri.parse(Environment.getExternalStorageDirectory() + "/" + fileName + "/");
                //open file Manager
                startActivity(new Intent(Intent.ACTION_GET_CONTENT).setDataAndType(uri, "*/*"));
            }
        });


    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        //check condition
        if (requestCode == 100 && (grantResults.length > 0) && (grantResults[0] == PackageManager.PERMISSION_GRANTED)){
            //lepas granted boleh create folder
            CreateFolder(fileName, theContent);
        }else{
            Toast.makeText(this, "Permission Denied!", Toast.LENGTH_SHORT).show();
        }
    }

    private void CreateFolder(String filename, String content) {
        String filePath = filename + ".txt";
        //initialize File & create file
        File folder = new File(Environment.getExternalStorageDirectory().getAbsolutePath(), "Record_Bacaan");
        File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath(), filePath);
        //Check Condition
        // if not exits its create one or replace data

        if (!folder.exists()){
            folder.mkdirs();
        }

        try {
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(content.getBytes());
            fos.close();
            Toast.makeText(this, "file Saved!", Toast.LENGTH_SHORT).show();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Toast.makeText(this, "file not found", Toast.LENGTH_SHORT).show();
        } catch (IOException e){
            e.printStackTrace();
            Toast.makeText(this, "Error Saving", Toast.LENGTH_SHORT).show();
        }


    }

}