package com.example.matafmobile;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class Register_ahli extends AppCompatActivity {

    public static final String TAG = "Register_ahli";
    Spinner stateDD, jenis_ic, keturunanPilihan;
    private DatePickerDialog datePickerDialog;
    private Button btnDate;
    RadioGroup radioGroup;
    RadioButton radioButton;
    EditText nama, alamat, poskod, bandar, telBim, telPej, ic, email, pass, cPass, lainKeturunan;
    Button btnRegis;
    ProgressBar progressBar_regisAhli;

    FirebaseAuth fAuth = FirebaseAuth.getInstance();
    FirebaseFirestore fStore = FirebaseFirestore.getInstance();
    FirebaseDatabase database = FirebaseDatabase.getInstance();

    //hold Special
    String ahli_tabung_ID;
    String hState = "PILIH";
    String selectedIC = "PILIH";
    String seletedKeturunan = "PILIH";
    String uType = "AHLI";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_ahli);

        nama = findViewById(R.id.namaResAhli);
        alamat = findViewById(R.id.alamatResAhli);
        poskod = findViewById(R.id.poskodResAhli);
        bandar = findViewById(R.id.bandarResAhli);
        telBim = findViewById(R.id.telBimResAhli);
        telPej = findViewById(R.id.telPejResAhli);
        ic = findViewById(R.id.icResAhli);
        lainKeturunan = findViewById(R.id.keturunanResAhli);
        email = findViewById(R.id.emailResAhli);
        pass = findViewById(R.id.passwordResAhli);
        cPass = findViewById(R.id.cPasswordResAhli);
        btnRegis = findViewById(R.id.btnRegisterResAhli);
        progressBar_regisAhli = findViewById(R.id.progressBar_regisAhli);

        radioGroup = findViewById(R.id.radioGroup);

        initDatePicker();
        btnDate = findViewById(R.id.datePickerBtn);
        btnDate.setText(getTodayDate());

        stateDD = findViewById(R.id.stateDD);
        jenis_ic = findViewById(R.id.jenis_ic_registerAhli);
        keturunanPilihan = findViewById(R.id.pilihanKeturunan_registerAhli);

        final ArrayAdapter<String> myAdapterState = new ArrayAdapter<String>(Register_ahli.this,
                android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.names));
        myAdapterState.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        stateDD.setAdapter(myAdapterState);

        final ArrayAdapter<String> myAdapterIC = new ArrayAdapter<String>(Register_ahli.this,
                android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.jenis_ic));
        myAdapterIC.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        jenis_ic.setAdapter(myAdapterIC);

        final ArrayAdapter<String> myAdapterKeturunan = new ArrayAdapter<String>(Register_ahli.this,
                android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.keturunan));
        myAdapterKeturunan.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        keturunanPilihan.setAdapter(myAdapterKeturunan);

        stateDD.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if (parent.getItemAtPosition(position).equals("PILIH")){
                    //error
                    Toast.makeText(Register_ahli.this, "Sila Lengkapkan Untuk Mendaftar", Toast.LENGTH_SHORT).show();
                }else{
                    hState = parent.getItemAtPosition(position).toString().trim();
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        jenis_ic.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                selectedIC = parent.getItemAtPosition(position).toString().trim();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        keturunanPilihan.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                seletedKeturunan = parent.getItemAtPosition(position).toString().trim();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        btnRegis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //hold value : hName = hold Name
                final String hNama = nama.getText().toString().trim();
                final String hAlamat = alamat.getText().toString().trim();
                final String hPoskod = poskod.getText().toString().trim();
                final String hBandar = bandar.getText().toString().trim();
                final String hTelBim = telBim.getText().toString().trim();
                final String hTelPej = telPej.getText().toString().trim();
                final String hIc = ic.getText().toString().trim();
                final String hLainKeturunan = lainKeturunan.getText().toString().trim();
                final String hEmail = email.getText().toString().trim();
                final String hPass = pass.getText().toString().trim();
                final String hCPass = cPass.getText().toString().trim();

                //special hold Text
                final String hBirthDate = btnDate.getText().toString().trim();

                int radioID = radioGroup.getCheckedRadioButtonId();
                radioButton = findViewById(radioID);
                final String hJantina = radioButton.getText().toString().trim();

                if(TextUtils.isEmpty(hNama)) {
                    nama.setError("Sila Masukkan Nama");
                    return;
                }

                if(TextUtils.isEmpty(hAlamat)) {
                    alamat.setError("Sila Masukkan Alamat");
                    return;
                }

                if(TextUtils.isEmpty(hPoskod)) {
                    poskod.setError("Sila Masukkan Poskod");
                    return;
                }


                if(TextUtils.isEmpty(hBandar)) {
                    bandar.setError("Sila Masukkan Bandar");
                    return;
                }

                if(validCellPhone(hTelBim) == false) {
                    telBim.setError("Sila Masukkan Nombor Tel. Bimbit");
                    return;
                }

                if(validCellPhone(hTelPej) == false) {
                    telPej.setError("Sila Masukkan Nombor Tel. Pejabat");
                    return;
                }

                if(TextUtils.isEmpty(hIc)) {
                    ic.setError("Sila Masukkan IC");
                    return;
                }

                if(isValidEmail(hEmail) == false){
                    email.setError("Sila Masukkan Email dan Sah");
                    return;
                }

                if(TextUtils.isEmpty(hPass)) {
                    pass.setError("Sila Masukkan Password");
                    return;
                }

                if(pass.length() < 6) {
                    pass.setError("Masukkan Password minimum 6 huruf/angka");
                    return;
                }

                if(TextUtils.isEmpty(hCPass)) {
                    cPass.setError("Sila Masukkan Confirm Password");
                    return;
                }

                if(cPass.length() < 6) {
                    cPass.setError("Masukkan Password minimum 6 huruf/angka");
                    return;
                }

                if(hState.equalsIgnoreCase("PILIH")){
                    Toast.makeText(Register_ahli.this, "Sila Pilih Negeri", Toast.LENGTH_SHORT).show();
                    return;
                }

                if(selectedIC.equalsIgnoreCase("PILIH")){
                    Toast.makeText(Register_ahli.this, "Sila Pilih IC", Toast.LENGTH_SHORT).show();
                    return;
                }

                if(seletedKeturunan.equalsIgnoreCase("PILIH")){
                    Toast.makeText(Register_ahli.this, "Sila Pilih Keturunan", Toast.LENGTH_SHORT).show();
                    return;
                }

                if(!hPass.equals(hCPass)){
                    Toast.makeText(Register_ahli.this, "Password Tidak Selari", Toast.LENGTH_SHORT).show();
                    return;
                }

                progressBar_regisAhli.setVisibility(View.VISIBLE);

                fAuth.createUserWithEmailAndPassword(hEmail, hPass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){

                            FirebaseUser fUser = fAuth.getCurrentUser();
                            fUser.sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(Register_ahli.this, "Verification Email Has been Sent", Toast.LENGTH_SHORT).show();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.d(TAG, "onFailure: Email not sent"+ e.getMessage());
                                }
                            });

                            Toast.makeText(Register_ahli.this, "Ahli Berjaya Berdaftar", Toast.LENGTH_SHORT).show();
                            ahli_tabung_ID = fAuth.getCurrentUser().getUid();
                            DocumentReference documentReference = fStore.collection("ahli").document(ahli_tabung_ID);
                            Map<String,Object> user = new HashMap<>();
                            user.put("ahli_name",hNama);
                            user.put("ahli_email",hEmail);
                            user.put("ahli_phone", hTelBim);
                            user.put("uType", uType);
                            documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.d(TAG, "onSuccess: user profile is created for "+ ahli_tabung_ID);
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.d(TAG, "onFailure "+ e.toString());
                                }
                            });
                            DatabaseReference myRef = database.getReference("ahli_info");
                            Ahli_DB_Helper ahli_data = new Ahli_DB_Helper(hEmail, hNama, hAlamat, hPoskod, hBandar,
                                    hState, hTelBim, hTelPej, selectedIC, hIc, hBirthDate, hJantina, seletedKeturunan, hLainKeturunan );
                            myRef.child(ahli_tabung_ID).setValue(ahli_data);

                            startActivity(new Intent(getApplicationContext(), MainActivity_ahli.class));
                        }else{
                            Toast.makeText(Register_ahli.this, "Error!" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            progressBar_regisAhli.setVisibility(View.GONE);
                        }
                    }
                });

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

    public void checkButton(View view){
        int radioID = radioGroup.getCheckedRadioButtonId();
        radioButton = findViewById(radioID);

        Toast.makeText(this, radioButton.getText(), Toast.LENGTH_SHORT).show();
    }

    private String getTodayDate() {
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        month = month + 1;
        int day = cal.get(Calendar.DAY_OF_MONTH);
        return makeDateString(day, month, year);
    }

    private void initDatePicker() {
        DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                month = month + 1;
                String date = makeDateString(day, month, year);
                btnDate.setText(date);
            }
        };

        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);

        int style = AlertDialog.THEME_HOLO_LIGHT;
        datePickerDialog = new DatePickerDialog(this, style, dateSetListener, year, month, day);
        datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
    }

    private String makeDateString(int day, int month, int year) {
        return getMonthFormat(month) + " " + day + " " + year;

    }

    private String getMonthFormat(int month) {
        if (month == 1){
            return "JAN";
        }
        if (month == 2){
            return "FEB";
        }
        if (month == 3){
            return "MAC";
        }
        if (month == 4){
            return "APR";
        }
        if (month == 5){
            return "MEI";
        }
        if (month == 6){
            return "JUN";
        }
        if (month == 7){
            return "JUL";
        }
        if (month == 8){
            return "OGOS";
        }
        if (month == 9){
            return "SEPT";
        }
        if (month == 10){
            return "OKT";
        }
        if (month == 11){
            return "NOV";
        }
        if (month == 12){
            return "DEC";
        }

        return "JAN";
    }

    public void openDatePicker(View view){
        datePickerDialog.show();
    }
}