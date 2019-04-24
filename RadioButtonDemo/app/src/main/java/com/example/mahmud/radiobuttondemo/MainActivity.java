package com.example.mahmud.radiobuttondemo;

import android.annotation.SuppressLint;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private RadioGroup radioGroup;
    private Button showbutton;
    private RadioButton radioButton;
    private TextView resultText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        radioGroup = findViewById(R.id.radioGroupId);
        showbutton = findViewById(R.id.buttonId);
        resultText = findViewById(R.id.textId);

        showbutton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick (View v){
                    try {
                        int selectId = radioGroup.getCheckedRadioButtonId();
                        radioButton = findViewById(selectId);
                        String value = radioButton.getText().toString();

                        resultText.setText("You have selected " + value);
                    }catch(Exception e){
                    resultText.setText("Not selected yet.");
                }
            }

        });


    }
}
