package com.example.mahmud.countryprofile;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

public class ProfileActivity extends AppCompatActivity {

    ImageView imageView;
    TextView textView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        imageView= findViewById(R.id.ImageViewId);
        textView= findViewById(R.id.TextViewId);

        Bundle bundle = getIntent().getExtras();
        if(bundle!=null){
           String CountryName = bundle.getString("name");

            ShowDetails(CountryName);
        }
    }
    void ShowDetails(String CountryName){

        if(CountryName.equals("Bangladesh")){
            imageView.setImageResource(R.drawable.bangladesh_profile);
            textView.setText(R.string.bangladesh_profile_text);

        }
        else if(CountryName.equals("India")){
            imageView.setImageResource(R.drawable.india_profile);
            textView.setText(R.string.india_profile_text);

        }
        else if(CountryName.equals("Pakistan")) {
            imageView.setImageResource(R.drawable.pakistan_profile);
            textView.setText(R.string.pakistan_profile_text);

        }


    }
}
