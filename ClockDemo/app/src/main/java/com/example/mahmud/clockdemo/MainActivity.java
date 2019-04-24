package com.example.mahmud.clockdemo;

import android.support.constraint.solver.widgets.Analyzer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AnalogClock;
import android.widget.TextClock;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private AnalogClock analogClock;
    private TextClock textClock;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        analogClock = findViewById(R.id.AnalogClockId);
        textClock = findViewById(R.id.TextClockId);

        analogClock.setOnClickListener(this);
        textClock.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        if(v.getId()==R.id.AnalogClockId){
            Toast.makeText(MainActivity.this,"AnalogClock",Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(MainActivity.this,"TextClock",Toast.LENGTH_SHORT).show();
        }
    }
}
