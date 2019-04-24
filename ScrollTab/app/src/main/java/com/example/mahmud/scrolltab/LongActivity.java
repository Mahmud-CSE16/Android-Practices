package com.example.mahmud.scrolltab;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class LongActivity extends AppCompatActivity {

    private  EditText nameEditText,passwordEditText;
    private Button loginbutton;
    private TextView textView;
    int counter = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_long);

        nameEditText = findViewById(R.id.EditTextNameId);
        passwordEditText = findViewById(R.id.EditTextPasswordId);
        loginbutton = findViewById(R.id.ButtonId);
        textView = findViewById(R.id.TextViewId);
        textView.setText("Number of attempts remaining "+counter+"times");

        loginbutton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String name = nameEditText.getText().toString();
                String password= passwordEditText.getText().toString();

                if(name.equals("Mahmud") && password.equals("1607052")){
                    Intent intent = new Intent(LongActivity.this,MainActivity.class);
                    startActivity(intent);
                }
                else{
                    counter--;
                    textView.setText("Number of attempts remaining "+counter+"times");
                    if(counter==0){
                        loginbutton.setEnabled(false);
                    }
                }

            }
        });
    }
}
