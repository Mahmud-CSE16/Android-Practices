package repon.cse.kuetian;

import android.Manifest;
import android.app.Dialog;
import android.app.DownloadManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.webkit.CookieManager;
import android.webkit.DownloadListener;
import android.webkit.JavascriptInterface;
import android.webkit.URLUtil;
import android.webkit.ValueCallback;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;


import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;


import repon.cse.kuetian.refresh_view.PullToRefreshView;

public class WEB extends AppCompatActivity implements View.OnClickListener {

    private WebView webView;
    FloatingActionButton webBackFab;
    ImageView imageModeFab;
    ImageView jsModeFab;
    DownloadManager dm;
    DownloadManager.Request request;
    AlertDialog.Builder alertDialogBuilder;
    AlertDialog alertDialog;
    String key;
    String id;
    String password;
    static String url;
    private SwipeRefreshLayout swipe;
    private String currentURL = "";
    boolean isBackPressed = false;
    boolean isInitial = true;
    boolean isInitialLogin = true;
    ProgressBar pbar;
    PullToRefreshView mPullToRefreshView;
    Dialog dialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_web);

        Bundle bundle = getIntent().getExtras();
        if(bundle!=null&&isNetworkConnected())
        {  key = bundle.get("key").toString();
           if(key.contains("Login"))
           {
               id =  bundle.get("id").toString();
               password =  bundle.get("password").toString();
           }
           url =  bundle.get("url").toString();
            createWebView();
            createDialog();
            if(key.equals("automation"))
            {
                showAutomation();
            }
            else webView.loadUrl(url);
        }
        else st.makeText(this, "Please connect with Internet!", Toast.LENGTH_SHORT);



    }

    public void showAutomation()  {
        dialog = new Dialog(WEB.this);
        dialog.setCancelable(false);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().getDecorView().getBackground().setColorFilter(0x005129a9, PorterDuff.Mode.SRC);
        dialog.setContentView(R.layout.dialog_automation);
        Button acaSysButton = dialog.findViewById(R.id.acaSysButton);
        Button centralLibraryButton = dialog.findViewById(R.id.centralLibraryButton);
        Button fhhButton = dialog.findViewById(R.id.fhhButton);
        Button lshButton = dialog.findViewById(R.id.lshButton);
        Button khButton = dialog.findViewById(R.id.khButton);
        Button arhButton = dialog.findViewById(R.id.arhButton);
        Button rkhButton = dialog.findViewById(R.id.rkhButton);
        Button aehButton = dialog.findViewById(R.id.aehButton);
        Button bbhButton = dialog.findViewById(R.id.bbhButton);

        FloatingActionButton automationCloseFab = dialog.findViewById(R.id.automationCloseFab);

        acaSysButton.setOnClickListener(this);
        centralLibraryButton.setOnClickListener(this);
        fhhButton.setOnClickListener(this);
        lshButton.setOnClickListener(this);
        khButton.setOnClickListener(this);
        arhButton.setOnClickListener(this);
        rkhButton.setOnClickListener(this);
        aehButton.setOnClickListener(this);
        bbhButton.setOnClickListener(this);
        automationCloseFab.setOnClickListener(this);

        dialog.show();

    }


    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.acaSysButton:
                webView.loadUrl("http://academic.kuet.ac.bd/");
                dialog.cancel();
                break;
            case R.id.centralLibraryButton:
                webView.loadUrl("http://library.kuet.ac.bd:8000/cgi-bin/koha/opac-user.pl");
                dialog.cancel();
                break;
            case R.id.fhhButton:
                webView.loadUrl("http://portal.kuet.ac.bd/fhh/");
                dialog.cancel();
                break;
            case R.id.lshButton:
                webView.loadUrl("http://portal.kuet.ac.bd/lsh");
                dialog.cancel();
                break;
            case R.id.khButton:
                webView.loadUrl("http://portal.kuet.ac.bd/khaja");
                dialog.cancel();
                break;
            case R.id.arhButton:
                webView.loadUrl("http://portal.kuet.ac.bd/marh");
                dialog.cancel();
                break;
            case R.id.rkhButton:
                webView.loadUrl("http://portal.kuet.ac.bd/rkh");
                dialog.cancel();
                break;
            case R.id.aehButton:
                webView.loadUrl("http://portal.kuet.ac.bd/aeh");
                dialog.cancel();
                break;
            case R.id.bbhButton:
                webView.loadUrl("http://portal.kuet.ac.bd/bbh");
                dialog.cancel();
                break;
            case R.id.automationCloseFab:
                dialog.cancel();
                finish();
        }


    }

    private class MyJavaScriptInterface {
        @JavascriptInterface
        public void handleHtml(String html) {
            // Use jsoup on this String here to search for your content.
            Log.d(st.TAG, "handleHtml: "+html);
            Document doc = Jsoup.parse(html);

            // Now you can, for example, retrieve a div with id="username" here
            Element usernameDiv = doc.select("#username").first();
        }
    }

    private void createPullRefresh() {
        mPullToRefreshView = findViewById(R.id.webRefresh);
        mPullToRefreshView.setOnRefreshListener(new PullToRefreshView.OnRefreshListener() {
            @Override
            public void onRefresh() {
                pbar.setVisibility(View.VISIBLE);
                webView.reload();
                mPullToRefreshView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mPullToRefreshView.setRefreshing(false);
                    }
                }, 2000);
            }
        });
        mPullToRefreshView.setRefreshing(true);
        mPullToRefreshView.postDelayed(new Runnable() {
            @Override
            public void run() {
                mPullToRefreshView.setRefreshing(false);
            }
        },3000);
    }

    private void createWebView() {
        webView = findViewById(R.id.webView);
        webBackFab = findViewById(R.id.webBackFABId);
        imageModeFab = findViewById(R.id.imageMode);
        jsModeFab = findViewById(R.id.jsMode);
        imageModeFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(webView.getSettings().getLoadsImagesAutomatically())
                {
                    imageModeFab.setImageResource(R.drawable.icon_image_off);
                    webView.getSettings().setBlockNetworkImage(true);
                    webView.getSettings().setLoadsImagesAutomatically(false);
                    webView.reload();
                    mPullToRefreshView.setRefreshing(true);
                    mPullToRefreshView.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mPullToRefreshView.setRefreshing(false);
                        }
                    },2000);
                }
                else
                {
                    imageModeFab.setImageResource(R.drawable.icon_image);
                    webView.getSettings().setBlockNetworkImage(false);
                    webView.getSettings().setLoadsImagesAutomatically(true);
                    webView.reload();
                    mPullToRefreshView.setRefreshing(true);
                    mPullToRefreshView.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mPullToRefreshView.setRefreshing(false);
                        }
                    },2000);
                }

            }
        });
        jsModeFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(webView.getSettings().getJavaScriptEnabled())
                {
                    jsModeFab.setImageResource(R.drawable.icon_flash);
                    webView.getSettings().setJavaScriptEnabled(false);
                    webView.reload();
                    mPullToRefreshView.setRefreshing(true);
                    mPullToRefreshView.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mPullToRefreshView.setRefreshing(false);
                        }
                    },2000);
                }
                else
                {
                    jsModeFab.setImageResource(R.drawable.icon_flash_off);
                    webView.getSettings().setJavaScriptEnabled(true);
                    webView.reload();
                    mPullToRefreshView.setRefreshing(true);
                    mPullToRefreshView.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mPullToRefreshView.setRefreshing(false);
                        }
                    },2000);
                }

            }
        });

        webBackFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        pbar = findViewById(R.id.webPBar);
        createPullRefresh();

        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setAllowContentAccess(true);
        webSettings.setAllowFileAccess(true);
        webSettings.setBlockNetworkImage(true);
        webSettings.setBuiltInZoomControls(true);
        webSettings.setLoadsImagesAutomatically(false);
        webSettings.setSupportZoom(true);
        webSettings.setSaveFormData(true);
        webView.addJavascriptInterface(new MyJavaScriptInterface(), "HtmlHandler");
        webView.setDownloadListener(new DownloadListener()
        {
            @Override
            public void onDownloadStart(String url, String userAgent,
                                        String contentDisposition, String mimeType,
                                        long contentLength) {
                request = new DownloadManager.Request(
                        Uri.parse(url));
                request.setMimeType(mimeType);
                String cookies = CookieManager.getInstance().getCookie(url);
                request.addRequestHeader("cookie", cookies);
                request.addRequestHeader("User-Agent", userAgent);
                request.setDescription("Downloading file...");
                request.setTitle(URLUtil.guessFileName(url, contentDisposition,
                        mimeType));
                request.allowScanningByMediaScanner();
                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                request.setDestinationInExternalPublicDir(
                        Environment.DIRECTORY_DOWNLOADS, URLUtil.guessFileName(
                                url, contentDisposition, mimeType));
                dm = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
                Log.d(st.TAG,"onDownloadStart:enqueue");
                downloadFile();
                st.makeText(getApplicationContext(), "Downloading File",
                        Toast.LENGTH_LONG);
            }});

        final String halljs = "javascript:document.getElementById('txtLogin').value='"+id+"';" +"document.getElementById('txtPassword').value='"+password+"';" + "document.getElementById('btnLogin').click()";
        final String libraryrenewjs = "javascript:document.getElementById('btn').click()";
        webView.setWebViewClient(new WebViewClient(){
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                if(url.contains("/Home.aspx")&&isInitial)
                {
                    view.loadUrl(WEB.url);
                    isInitial = false;
                }
                currentURL = url;
            }

            @Override
            public void onPageFinished(final WebView view, final String url) {
                // Check here if url is equal to your site URL.
                pbar.setVisibility(View.INVISIBLE);
                if(isBackPressed) isBackPressed = false;
                else if(url.contains("/frmLogin.aspx")&&key.contains("hallLogin")&&isInitialLogin)
                {
                    if(Build.VERSION.SDK_INT >= 19)
                       view.evaluateJavascript(halljs, new ValueCallback<String>() {
                            @Override
                            public void onReceiveValue(String s) {
                            }
                        });
                    isInitialLogin = false;
                }
                else if(url.contains("/index.php")&&key.equals("acaLogin")&&isInitialLogin)
                {
                    try {
                        String postData = "userid=" + URLEncoder.encode(id, "UTF-8") + "&password=" + URLEncoder.encode(password, "UTF-8") + "&login=" + URLEncoder.encode("Login", "UTF-8");
                        view.postUrl(url,postData.getBytes());
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    isInitialLogin = false;
                }
                else if(url.equals(WEB.url)&&key.equals("libraryLogin")&&isInitialLogin)
                {
                    if(Build.VERSION.SDK_INT >= 19)
                   try {
                        String postData = "userid=" + URLEncoder.encode(id, "UTF-8") + "&password=" + URLEncoder.encode(password, "UTF-8") + "&koha_login_context=" + URLEncoder.encode("opac", "UTF-8");
                        view.postUrl(url,postData.getBytes());
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }

                    isInitialLogin = false;
                }
               // view.loadUrl("javascript:window.HtmlHandler.handleHtml" +"('<html>'+document.getElementsByTagName('html')[0].innerHTML+'</html>');");
                mPullToRefreshView.setRefreshing(false);
            }
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String urlNewString) {
                currentURL = urlNewString;
                return false;
            }
        });


    }

    public void createDialog()    {
        alertDialogBuilder = new AlertDialog.Builder(WEB.this);

        // set title
        alertDialogBuilder.setTitle("No connection");

        // set dialog message
        alertDialogBuilder
                .setMessage("No connection, Retry")
                .setCancelable(true);

        // create alert dialog
        alertDialog = alertDialogBuilder.create();
    }

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        if (ni == null) {
            // There are no active networks.
            alertDialog.show();
            return false;
        } else
        return true;
    }

    public boolean downloadFile() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.v(st.TAG, "Permission is granted");
                dm.enqueue(request);
                //writeFilesToSdCard();
                return true;
            } else {
                Log.v(st.TAG, "Permission is revoked");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                return false;
            }
        } else { //permission is automatically granted on sdk<23 upon installation
           // writeFilesToSdCard();
            dm.enqueue(request);
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Log.v(st.TAG, "Permission: " + permissions[0] + "was " + grantResults[0]);
            //resume tasks needing this permission
            dm.enqueue(request);
        }
    }

    @Override
    public void onBackPressed() {
       if(webView.canGoBack())  { isBackPressed = true; webView.goBack();}
       else                     { finish();                 overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);    }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        ViewGroup view = (ViewGroup) getWindow().getDecorView();
        view.removeAllViews();
        webView.destroy();
        Runtime.getRuntime().gc();
    }
}
