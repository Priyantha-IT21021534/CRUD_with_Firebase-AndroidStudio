package com.example.labs_firebase;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {
    EditText edtID, edtName, edtAddress, edtTelNo;
    Button btnSave, btnShow, btnUpdate, btnDelete, btnClear;
    Student student;

    final DatabaseReference DB = FirebaseDatabase.getInstance().getReference().child("Student");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        edtID = findViewById(R.id.edt_id);
        edtName = findViewById(R.id.edt_name);
        edtAddress = findViewById(R.id.edt_address);
        edtTelNo = findViewById(R.id.edt_contactNumber);

        btnSave = findViewById(R.id.btn_save);
        btnShow = findViewById(R.id.btn_show);
        btnUpdate = findViewById(R.id.btn_update);
        btnDelete = findViewById(R.id.btn_delete);
        btnClear = findViewById(R.id.btn_clear);

        student = new Student();
    }

    private void clearInputs(){
        edtID.setText("");
        edtName.setText("");
        edtAddress.setText("");
        edtTelNo.setText("");
    }

    private int enterDataToDB(){
        int success = 0;
        try {
            if (TextUtils.isEmpty(edtID.getText().toString()))
                Toast.makeText(getApplicationContext(), "Please Enter ID", Toast.LENGTH_SHORT).show();
            else if (TextUtils.isEmpty(edtName.getText().toString()))
                Toast.makeText(getApplicationContext(), "Please Enter Name", Toast.LENGTH_SHORT).show();
            else if (TextUtils.isEmpty(edtAddress.getText().toString()))
                Toast.makeText(getApplicationContext(), "Please Enter Address", Toast.LENGTH_SHORT).show();
            else if (TextUtils.isEmpty(edtTelNo.getText().toString()))
                Toast.makeText(getApplicationContext(), "Please Enter Contact Number", Toast.LENGTH_SHORT).show();
            else {
                student.setId(edtID.getText().toString().trim());
                student.setName(edtName.getText().toString().trim());
                student.setAddress(edtAddress.getText().toString().trim());
                student.setTelNo(Integer.parseInt(edtTelNo.getText().toString().trim()));

                DB.child(student.getId()).setValue(student);

                success = 1;
            }
        }catch (NumberFormatException e){
            Toast.makeText(getApplicationContext(), "Invalid Contact Number", Toast.LENGTH_SHORT).show();
        }

        return success;
    }

    public void onClickClear(View view){
        clearInputs();
    }

    public void onClickSave(View view){
        if(enterDataToDB() == 1){
            Toast.makeText(getApplicationContext(), "Student saved successfully", Toast.LENGTH_SHORT).show();
            clearInputs();
            edtID.requestFocus();
        }
    }

    public void onClickShow(View view){
        String id = edtID.getText().toString();
        DatabaseReference dbChild = DB.child(id);

        if(TextUtils.isEmpty(id))
            Toast.makeText(getApplicationContext(), "Please Enter ID", Toast.LENGTH_SHORT).show();
        else{
            dbChild.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.hasChildren()){
                        edtName.setText(snapshot.child("name").getValue().toString());
                        edtAddress.setText(snapshot.child("address").getValue().toString());
                        edtTelNo.setText(snapshot.child("telNo").getValue().toString());
                    }
                    else{
                        Toast.makeText(getApplicationContext(), "No data with given ID", Toast.LENGTH_SHORT).show();
                        clearInputs();
                        edtID.setText(id);
                        edtID.setSelection(edtID.getText().length());
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {}
            });
        }
    }

    public void onClickUpdate(View view){
        DB.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.hasChild(edtID.getText().toString())){
                    if(enterDataToDB() == 1){
                        Toast.makeText(getApplicationContext(), "Student updated successfully", Toast.LENGTH_SHORT).show();
                    }
                }
                else{
                    Toast.makeText(getApplicationContext(), "Source ID not found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });



    }

    public void onClickDelete(View view){
        DB.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String id = edtID.getText().toString();

                if(TextUtils.isEmpty(id)) {
                    Toast.makeText(getApplicationContext(), "Please Enter ID", Toast.LENGTH_SHORT).show();
                }
                else if(snapshot.hasChild(id)){
                    DB.child(id).removeValue();
                    clearInputs();
                    Toast.makeText(getApplicationContext(), "Student Removed from database", Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(getApplicationContext(), "Source ID not found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }
}