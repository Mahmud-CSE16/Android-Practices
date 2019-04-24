package com.example.mahmud.htmldemo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.WebView;

public class MainActivity extends AppCompatActivity {

    WebView webView;
    String string = "<h1>This is heading One</h1>\n"+
                    "<h2>This is heading Two</h2>\n"+
                    "<h3>This is heading Three</h3>\n"+
                    "<p>This is a paragraph</p>\n"+
                    "<p><u>This is an underline paragraph</u></p>\n"+
                    "<p><i>This is an italic paragraph</i></p>\n"+
                    "<p><b>This is a bold paragraph</b></p>\n"+
                    "Programing language Oder list\n"+
                    "<uol>\n"+
                    "<li>C</li>\n"+
                    "<li>C++</li>\n"+
                    "<li>Java</li>\n"+
                    "</uol>\n\n"+
                    "(a+b)<sup>2</sup> = a<sup>2</sup>+2ab+b<sup>2</sup>\n";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        webView=findViewById(R.id.WebViewId);
        webView.loadDataWithBaseURL(null,string,"text/html","utf-8",null);

    }
}
