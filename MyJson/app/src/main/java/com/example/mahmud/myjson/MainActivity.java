package com.example.mahmud.myjson;

import android.app.ProgressDialog;
import android.net.UrlQuerySanitizer;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.JsonReader;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringBufferInputStream;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    TextView textView;
    Button button;
    String stringUrl ="https://www.json-generator.com/api/json/get/ceocqcrmDC?indent=2";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textView = findViewById(R.id.TextViewId);
        button = findViewById(R.id.ButtonId);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new MyTask().execute(stringUrl);
            }
        });

    }

    public class MyTask extends AsyncTask<String ,String,String>{


        @Override
        protected String doInBackground(String... strings) {

            StringBuilder stringBuilder = new StringBuilder();

            HttpURLConnection urlConnection = null;

            try {
                URL url = new URL(strings[0]);
                urlConnection = (HttpURLConnection) url.openConnection();

                InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                BufferedReader reader = new BufferedReader(new InputStreamReader(in));

                String line;
                while((line = reader.readLine())!=null){
                    stringBuilder.append(line);
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            String result =stringBuilder.toString();

            StringBuilder text = new StringBuilder();

            String name;
            Array contacts;
            try {
                JSONArray jsonArray = new JSONArray(result);


                for (int i=0;i<jsonArray.length();i++){
                    JSONObject o = jsonArray.getJSONObject(i);
                    JSONArray jArray=o.getJSONArray("friends");
                    for(int j=0;j<jArray.length();j++){
                        JSONObject ob = jArray.getJSONObject(i);
                        name = ob.getString("name");
                        text.append("Friends: "+name+":\n");
                        JSONObject obj = ob.getJSONObject("mobile");
                        JSONArray objArray = obj.getJSONArray("numbers");
                        for(int k=0;k<objArray.length();k++){
                            text.append("   "+objArray.getString(i)+"\n");

                        }
                    }

                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

            return text.toString();

        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            textView.setText(s);
        }
    }
}


