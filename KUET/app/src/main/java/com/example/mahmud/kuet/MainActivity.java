package com.example.mahmud.kuet;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {


    Button homeButton,studentCornerButton,academicButton,libraryButton,busScheduleButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        homeButton = findViewById(R.id.homeButtonId);
        studentCornerButton =findViewById(R.id.studentCornerButtonId);
        academicButton = findViewById(R.id.academicButtonId);
        libraryButton = findViewById(R.id.libraryButtonId);
        busScheduleButton = findViewById(R.id.busScheduleButtonId);

        homeButton.setOnClickListener(this);
        studentCornerButton.setOnClickListener(this);
        academicButton.setOnClickListener(this);
        libraryButton.setOnClickListener(this);
        busScheduleButton.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.homeButtonId){
            Intent intent = new Intent(MainActivity.this,WebSide.class);
            intent.putExtra("name","Kuet");
            startActivity(intent);
        }
        if(v.getId()==R.id.studentCornerButtonId){
            Intent intent = new Intent(MainActivity.this,WebSide.class);
            intent.putExtra("name","StudentCorner");
            startActivity(intent);
        }
        if(v.getId()==R.id.academicButtonId){
            Intent intent = new Intent(MainActivity.this,WebSide.class);
            intent.putExtra("name","AcademicCorner");
            startActivity(intent);
        }
        if(v.getId()==R.id.libraryButtonId){
            Intent intent = new Intent(MainActivity.this,WebSide.class);
            intent.putExtra("name","CentralLibrary");
            startActivity(intent);
        }
        if(v.getId()==R.id.busScheduleButtonId){
            Intent intent = new Intent(MainActivity.this,WebSide.class);
            intent.putExtra("name","BusSchedule");
            startActivity(intent);
        }
    }

    @Override
    public void onBackPressed() {
            AlertDialog.Builder alertdialogBuilder = new AlertDialog.Builder(MainActivity.this);

            alertdialogBuilder.setIcon(R.drawable.ic_help_black_24dp);
            alertdialogBuilder.setTitle(R.string.app_name);
            alertdialogBuilder.setMessage("Do you want to exit?");
            alertdialogBuilder.setCancelable(false);
            alertdialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    }

            );
            alertdialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });

            AlertDialog alertDialog = alertdialogBuilder.create();
            alertDialog.show();
        }
}
