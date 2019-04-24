package com.example.mahmud.myfirstproject;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity{

    private TextView textview;
    private Button logInButton;
    private Button logOutButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textview =(TextView) findViewById(R.id.text);
        logInButton = (Button)findViewById(R.id.login);
        logOutButton = (Button) findViewById(R.id.Logout);
    }

    void showMessage(View v){
        if(v.getId()==R.id.login){
            Toast toast =Toast.makeText(MainActivity.this,"login",Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.TOP,0,100);
            toast.show();
        }
        else if(v.getId()==R.id.Logout){
            Toast.makeText(MainActivity.this,"Logout",Toast.LENGTH_SHORT).show();
        }
    }

}
