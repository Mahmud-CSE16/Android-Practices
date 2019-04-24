package com.example.mahmud.listviewdemo;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class FeedBack extends AppCompatActivity implements View.OnClickListener {

    EditText nameEditText,messageEditText;
    Button sendButton,clearButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed_back);

        nameEditText = findViewById(R.id.nameEditTextId);
        messageEditText= findViewById(R.id.messageEditTextId);

        sendButton = findViewById(R.id.sendButtonId);
        clearButton = findViewById(R.id.clearButtonId);

        sendButton.setOnClickListener(this);
        clearButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        String name = nameEditText.getText().toString();
        String message = messageEditText.getText().toString();

        try{
            if(v.getId()==R.id.sendButtonId){
               /* Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/email");
                intent.putExtra(Intent.EXTRA_EMAIL,new String[] {"tusharbd021@gmail.com"});
                intent.putExtra(Intent.EXTRA_SUBJECT,"Feedback From App.");
                intent.putExtra(Intent.EXTRA_TEXT,"Name: "+name+"\nMassage: "+message);
                startActivity(Intent.createChooser(intent,"Feedback with"));*/

                Intent intent = new Intent (Intent.ACTION_SEND);
                intent.setType("message/rfc822");
                intent.putExtra(Intent.EXTRA_EMAIL, new String[]{"tusharbd021@gmail.com"});
                intent.putExtra(Intent.EXTRA_SUBJECT, "App feadback");
                intent.putExtra(Intent.EXTRA_TEXT,"Name: "+name+"\nMassage: "+message);
                intent.setPackage("com.google.android.gm");
                if (intent.resolveActivity(getPackageManager())!=null)
                    startActivity(intent);
                else
                    Toast.makeText(this,"Gmail App is not installed",Toast.LENGTH_SHORT).show();
            }
            if(v.getId()==R.id.clearButtonId){
                nameEditText.setText("");
                messageEditText.setText("");
            }
        }catch (Exception e){
            Toast.makeText(getApplicationContext() ,"Eexception "+e,Toast.LENGTH_SHORT).show();
        }
    }
}
