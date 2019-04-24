package com.example.mahmud.checkboxdemo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private CheckBox milkCheckBox,sugarCheckBox,waterCheckBox;
    private Button button;
    private TextView resultText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        milkCheckBox = findViewById(R.id.milk_id);
        sugarCheckBox = findViewById(R.id.sugar_id);
        waterCheckBox =findViewById(R.id.water_id);

        button = findViewById(R.id.button_id);
        resultText = findViewById(R.id.resultTest_id);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                StringBuilder stringBuilder= new StringBuilder();
                if(milkCheckBox.isChecked()){
                    String s = milkCheckBox.getText().toString();
                    stringBuilder.append(s+"is odered\n");
                }
                if(sugarCheckBox.isChecked()){
                    String s = sugarCheckBox.getText().toString();
                    stringBuilder.append(s+"is odered\n");
                }
                if(waterCheckBox.isChecked()){
                    String s = waterCheckBox.getText().toString();
                    stringBuilder.append(s+"is odered\n");
                }

                resultText.setText(stringBuilder);
            }
        });
    }
}
