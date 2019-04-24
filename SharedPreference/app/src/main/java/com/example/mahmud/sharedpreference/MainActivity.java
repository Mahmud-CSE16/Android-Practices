package com.example.mahmud.sharedpreference;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText usernameEditText,passwordEditText;
    private Button saveButton,loadButton;
    private TextView textView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        usernameEditText = findViewById(R.id.usernameEditTextId);
        passwordEditText = findViewById(R.id.passwordEditTextId);

        saveButton = findViewById(R.id.saveButtonId);
        loadButton = findViewById(R.id.loadButtonId);
        textView = findViewById(R.id.TextViewId);

        saveButton.setOnClickListener(this);
        loadButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.saveButtonId){
            String username = usernameEditText.getText().toString();
            String password = passwordEditText.getText().toString();

            if(username.equals("")||password.equals("")){
                Toast.makeText(getApplicationContext(),"Please Enter UserName & Password",Toast.LENGTH_SHORT).show();
            }else{
                SharedPreferences sharedPreferences = getSharedPreferences("userDetails",Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("usernameKey",username);
                editor.putString("passwordKey",password);
                editor.commit();
                Toast.makeText(getApplicationContext(),"Data has been save successfully",Toast.LENGTH_SHORT).show();
            }
            usernameEditText.setText("");
            passwordEditText.setText("");

        }else if(v.getId()==R.id.loadButtonId){
            SharedPreferences sharedPreferences = getSharedPreferences("userDetails",Context.MODE_PRIVATE);

            if (sharedPreferences.contains("usernameKey") && sharedPreferences.contains("passwordKey")){
                String username = sharedPreferences.getString("usernameKey","Data Not found");
                String password = sharedPreferences.getString("passwordKey","Data Not found");

                textView.setText(username+"\n"+password);
            }
        }
    }
}
