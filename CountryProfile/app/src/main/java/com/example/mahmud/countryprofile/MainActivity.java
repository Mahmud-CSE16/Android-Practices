package com.example.mahmud.countryprofile;

import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    Button BangladeshButton,IndiaButton,PakistanButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BangladeshButton = findViewById(R.id.BangladeshButtonId);
        IndiaButton = findViewById(R.id.IndiaButtonId);
        PakistanButton = findViewById(R.id.PakistanButtonId);

        BangladeshButton.setOnClickListener(this);
        IndiaButton.setOnClickListener(this);
        PakistanButton.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {

        if(v.getId()==R.id.BangladeshButtonId){
           Intent intent = new Intent(MainActivity.this,ProfileActivity.class);
           intent.putExtra("name","Bangladesh");
           startActivity(intent);
        }
        if(v.getId()==R.id.IndiaButtonId){
            Intent intent = new Intent(MainActivity.this,ProfileActivity.class);
            intent.putExtra("name","India");
            startActivity(intent);
        }
        if(v.getId()==R.id.PakistanButtonId){
            Intent intent = new Intent(MainActivity.this,ProfileActivity.class);
            intent.putExtra("name","Pakistan");
            startActivity(intent);
        }
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder alertdialogBuilder = new AlertDialog.Builder(MainActivity.this);

        alertdialogBuilder.setIcon(R.drawable.ic_help_black_24dp);
        alertdialogBuilder.setTitle(R.string.app_name);
        alertdialogBuilder.setMessage("Do you want to exit?");
        //alertdialogBuilder.setCancelable(false);

        alertdialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        alertdialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        alertdialogBuilder.setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        AlertDialog alertDialog = alertdialogBuilder.create();
        alertDialog.show();
    }
}
