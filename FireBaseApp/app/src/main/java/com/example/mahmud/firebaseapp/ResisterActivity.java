package com.example.mahmud.firebaseapp;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class ResisterActivity extends AppCompatActivity {

    EditText editText3,editText4;

    FirebaseAuth auth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resister);

        editText3 = findViewById(R.id.editText3);
        editText4 = findViewById(R.id.editText4);

        auth = FirebaseAuth.getInstance();
    }

    void createUser(View v){
        String email = editText3.getText().toString();
        String password = editText4.getText().toString();

        if(email.equals("")&&password.equals("")){
            Toast.makeText(getApplicationContext(),"Please Enter Email & password",Toast.LENGTH_SHORT).show();
        }else{
            auth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()){
                        Toast.makeText(getApplicationContext(),"Successfully resistered",Toast.LENGTH_SHORT).show();
                        finish();
                        Intent intent = new Intent(getApplicationContext(),ProfileActivity.class);
                        startActivity(intent);
                    }
                    else {
                        Toast.makeText(getApplicationContext(),"Not resistered successfully",Toast.LENGTH_SHORT).show();

                    }
                }
            });
        }
    }
}
