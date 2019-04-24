package com.example.mahmud.customtoast_demo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private Button LoginButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        LoginButton  = findViewById(R.id.button_id);

        LoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                LayoutInflater inflater = getLayoutInflater();
                View view = inflater.inflate(R.layout.customtoast_layout, (ViewGroup) findViewById(R.id.customtost_id));

                Toast toast= new Toast(MainActivity.this);
                toast.setDuration(Toast.LENGTH_SHORT);
                toast.setView(view);
                toast.setGravity(Gravity.BOTTOM,0,0);
                toast.show();

            }
        });
    }
}
