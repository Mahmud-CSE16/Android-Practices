package com.example.mahmud.edittext_demo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    TextView result ;
    EditText number1,number2;
    Button add,sub;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        number1=findViewById(R.id.number1);
        number2=findViewById(R.id.number2);

        add=findViewById(R.id.add);
        sub=findViewById(R.id.sub);

        result=findViewById(R.id.Text);

        add.setOnClickListener(this);
        sub.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        try{
            String n1=number1.getText().toString();
            String n2=number2.getText().toString();

            //convert into double

            double N1=Double.parseDouble(n1);
            double N2=Double.parseDouble(n2);

            if(v.getId()==R.id.add){
                double sum=N1+N2;
                result.setText("Result: "+sum);
            }
            else if(v.getId()==R.id.sub){
                double sub=N1-N2;
                result.setText("Result: "+sub);
            }

        }catch(Exception e){
            Toast.makeText(MainActivity.this,"Please Enter Number",Toast.LENGTH_SHORT).show();
        }
    }
}
