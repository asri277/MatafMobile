package com.example.matafmobile;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

public class Profile_ahli extends AppCompatActivity {

    FirebaseAuth fAuth = FirebaseAuth.getInstance();
    StorageReference storageReference = FirebaseStorage.getInstance().getReference();
    FirebaseFirestore fStore = FirebaseFirestore.getInstance();
    FirebaseDatabase database = FirebaseDatabase.getInstance();


    public static final String TAG = "Profile_ahli";
    ImageView gambar_ahli;
    TextView namaAhli, jenisAhli, statusAhli, email_ahli;
    EditText telAhli, telpejAhli, alamatAhli;
    Button btnUpdate_ahli;
    String userID = fAuth.getCurrentUser().getUid();

    DatabaseReference ahliDB = database.getReference("ahli_info").child(userID);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_ahli);

        gambar_ahli = findViewById(R.id.profile_image_ahli);
        namaAhli = findViewById(R.id.fullName_Ahli);
        jenisAhli = findViewById(R.id.uType_ahli);
        telAhli = findViewById(R.id.noTel_ahli);
        telpejAhli = findViewById(R.id.noPej_ahli);
        alamatAhli = findViewById(R.id.alamat_ahli);
        btnUpdate_ahli = findViewById(R.id.cmd_update_ahli);
        statusAhli = findViewById(R.id.status_ahli);
        email_ahli = findViewById(R.id.email_showAhli);

        telAhli.setFocusable(false);
        telpejAhli.setFocusable(false);

        ahliInformation();

        btnUpdate_ahli.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String data1 = telAhli.getText().toString().trim();
                String data2 = telpejAhli.getText().toString().trim();

                if(validCellPhone(data1) == false){
                    telAhli.setError("Sila Masukkan Nombor telefon sah");
                    return;
                }

                if(validCellPhone(data2) == false){
                    telpejAhli.setError("Sila Masukkan Nombor telefon sah");
                    return;
                }

                ahliDB.child("g_noPhone").setValue(data1);
                ahliDB.child("h_noPejabat").setValue(data2);
                ahliInformation();
                Toast.makeText(Profile_ahli.this, "Kemaskini Berjaya!", Toast.LENGTH_SHORT).show();
            }
        });

        StorageReference profileRef = storageReference.child("ahli/"+userID+"/profile.jpg"); //in main
        profileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri).into(gambar_ahli);
            }
        });


        //Change image
        gambar_ahli.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //open galeri
                Intent openGalleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(openGalleryIntent, 1000);
            }
        });

        telAhli.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                telAhli.setFocusableInTouchMode(true);
            }
        });

        telpejAhli.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                telpejAhli.setFocusableInTouchMode(true);
            }
        });

        alamatAhli.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alamatAhli.setFocusableInTouchMode(true);
            }
        });


    }

    public boolean validCellPhone(String number)
    {
        return (!TextUtils.isEmpty(number) && android.util.Patterns.PHONE.matcher(number).matches());
    }

    public void ahliInformation(){

        DocumentReference documentReference = fStore.collection("ahli").document(userID);
        documentReference.addSnapshotListener(Profile_ahli.this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                jenisAhli.setText(documentSnapshot.getString("uType"));
            }
        });

        ahliDB.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                Ahli_DB_Helper user = dataSnapshot.getValue(Ahli_DB_Helper.class);
                email_ahli.setText(user.getA_email());
                namaAhli.setText(user.getB_name().toUpperCase());
                telAhli.setText(user.getG_noPhone());
                telpejAhli.setText(user.getH_noPejabat());
                String fullAddress = user.getC_alamat().toUpperCase() + " " + user.getD_poskod().toUpperCase() + " " + user.getE_bandar().toUpperCase() + " " + user.getF_negeri().toUpperCase();
                alamatAhli.setText(fullAddress);
            }

            @Override
            public void onCancelled(DatabaseError error) {
            // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @androidx.annotation.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1000){
            if(resultCode == Activity.RESULT_OK){
                Uri imageUri = data.getData();
                //profileImage.setImageURI(imageUri);
                uploadImageToFirebase(imageUri);
            }
        }
    }

    //Change Image
    private void uploadImageToFirebase(Uri imageUri) {
        //upload image to firebase storage
        final StorageReference fileRef = storageReference.child("ahli/"+userID+"/profile.jpg");
        fileRef.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Picasso.get().load(uri).into(gambar_ahli);
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(Profile_ahli.this, "Failed to Uploaded", Toast.LENGTH_SHORT).show();
            }
        });
    }
}