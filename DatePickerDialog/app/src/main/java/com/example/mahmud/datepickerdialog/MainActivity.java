package com.example.mahmud.datepickerdialog;

import android.app.DatePickerDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;

import org.w3c.dom.Text;

public class MainActivity extends AppCompatActivity {

    TextView textView;
    Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textView = findViewById(R.id.TextViewId);
        button = findViewById(R.id.ButtonId);

        DatePicker datePicker = new DatePicker(MainActivity.this);

        int currentday = datePicker.getDayOfMonth();
        int currentmonth= datePicker.getMonth()+1;
        int currentyear = datePicker.getYear();

        textView.setText("Date: "+currentday+"/"+currentmonth+"/"+currentyear);

        button.setOnClickListener(new View.OnClickListener() {

            DatePicker datePicker = new DatePicker(MainActivity.this);

            int currentday = datePicker.getDayOfMonth();
            int currentmonth= datePicker.getMonth();
            int currentyear = datePicker.getYear();
            @Override
            public void onClick(View v) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(MainActivity.this,


                        new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                                textView.setText("Date: "+dayOfMonth+"/"+(month+1)+"/"+year);
                            }
                        }, currentyear, currentmonth, currentday);
                datePickerDialog.show();
            }
        });


    }
}
