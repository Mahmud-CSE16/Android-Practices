package repon.cse.kuetian;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.AudioManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;
import android.widget.DatePicker;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import static android.content.Context.AUDIO_SERVICE;
import static repon.cse.kuetian.st.TAG;

public class AlarmReceiver extends BroadcastReceiver
{
    String key;
    Context context;
    @Override
    public void onReceive(Context context, Intent intent)
    {
        st.rollNumber = st.readRollNumber(context);
        this.context = context;
        Log.d(TAG, "onReceive: "+context.getClass().getSimpleName()+" "+intent.getExtras().get("key"));
        Bundle bundle = intent.getExtras();
        if(bundle.get("key")!=null)   key = bundle.get("key").toString();
        else key = "other";
        if(key.equals("classNotification"))
        {
                notification(bundle.get("title").toString(),bundle.get("msg").toString(),MainActivity.class);
        }
        else if(key.equals("classVibrate"))
        {
            AudioManager manager = (AudioManager) context.getSystemService(AUDIO_SERVICE);
            manager.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
        }
        else if(key.equals("perScheNot")||key.equals("ctNot"))
        {
            notification(bundle.get("title").toString(),bundle.get("msg").toString(),MainActivity.class);
        }
        else if(key.equals("libraryAutoRenew")||key.equals("other"))
        {
            HashMap<String, String> set = st.readNotificationData(context);
            if(set!=null&&set.get("autoRenew")!=null&&set.get("autoRenew").equals("true"))
            {
                Log.d(TAG, "onReceive: libraryAutoRenew");
                ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo ni = cm.getActiveNetworkInfo();
                if (ni != null) {
                    new libraryRenewClass().execute();
                }
            }
        }
    }
    private void notification(String title, String description, Class<?> cls) {
        PendingIntent pi = PendingIntent.getActivity(context, 0,
                new Intent(context, cls), 0);

        Notification notification = new NotificationCompat.Builder(context)
                .setContentTitle(title)
                .setContentText(description)
                .setColor(Color.GREEN)
                .setAutoCancel(true)
                .setContentIntent(pi)
                .setSmallIcon(R.drawable.kuet_notif_logo)
                .setShowWhen(true)
                .setLocalOnly(true)
                .build();

        NotificationManagerCompat.from(context).notify(new Random().nextInt(), notification);
    }
    public class libraryRenewClass extends AsyncTask {

        @Override
        protected Object doInBackground(Object[] objects) {


                String urlLibraryLogin ="http://library.kuet.ac.bd:8000/cgi-bin/koha/opac-user.pl";
                int dif=1000;
                Document docLibrary=null;
                HashMap<String, String> settings = st.readUserSettings( context);
                if(settings!=null)

                try {

                    Log.d(TAG, "LibraryData::doInBackground");

                    String userAgent ="Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:60.0) Gecko/20100101 Firefox/60.0";

                    // Parsing Library Data
                    Connection.Response responseLibrary = Jsoup.connect(urlLibraryLogin)
                            .method(Connection.Method.GET)
                            .userAgent(userAgent)
                            .referrer(urlLibraryLogin)
                            .execute();
                    Connection.Response responseLibrary1 = Jsoup.connect(urlLibraryLogin)
                            .cookies(responseLibrary.cookies())
                            .userAgent(userAgent)
                            .referrer(urlLibraryLogin)
                            .data("userid",settings.get("libraryId"))
                            .data("password",settings.get("libraryPass"))
                            .data("koha_login_context","opac")
                            .method(Connection.Method.POST)
                            .followRedirects(true)
                            .execute();
                    docLibrary = responseLibrary1.parse();

                    if(true)
                    {
                        Boolean isRenewable = false;

                        if(docLibrary != null) {
                            Elements elementsDateDue = docLibrary.getElementsByClass("date_due");
                            for (int i =0; i< elementsDateDue.size();i++) {
                                String date= elementsDateDue.get(i).text().toString();
                                date = date.substring(10,date.length());
                                String[] dates = date.split("/");
                                int dueTime = Integer.valueOf(dates[2])*365+Integer.valueOf(dates[1])*30+Integer.valueOf(dates[0]);
                                DatePicker dp = new DatePicker(context);
                                int currentTime = dp.getYear()*365+(dp.getMonth()+1)*30+dp.getDayOfMonth();
                                dif = dueTime-currentTime;
                                if(dif<=2) isRenewable=true;
                            }
                        }

                        if(isRenewable)
                        {
                            Log.d(TAG, "doInBackground: renewAll");

                            String from= docLibrary.select("input[name=from]").val();
                            String borNum = docLibrary.select("input[name=borrowernumber]").val();
                            Elements elementsItem = docLibrary.select("input[name=item]");
                            ArrayList<String> items = new ArrayList<String>();
                            for (int i =0; i< elementsItem.size()/2;i++) {
                                items.add(elementsItem.get(i).val());
                            }

                            if(items.size()==1)
                            {
                                Connection.Response responseLibrary2  = Jsoup
                                        .connect("http://library.kuet.ac.bd:8000/cgi-bin/koha/opac-renew.pl")
                                        .cookies(responseLibrary1.cookies())
                                        .userAgent(userAgent)
                                        .referrer("http://library.kuet.ac.bd:8000/cgi-bin/koha/opac-user.pl")
                                        .data("from",from)
                                        .data("borrowernumber",borNum)
                                        .data("item",items.get(0))
                                        .data("btn","Renew all")
                                        .method(Connection.Method.POST)
                                        .followRedirects(true)
                                        .execute();
                                docLibrary = responseLibrary2.parse();
                            }
                            else if(items.size()==2)
                            {
                                Log.d(TAG, "doInBackground: items.size()==2");
                                Connection.Response responseLibrary2  = Jsoup
                                        .connect("http://library.kuet.ac.bd:8000/cgi-bin/koha/opac-renew.pl")
                                        .cookies(responseLibrary1.cookies())
                                        .userAgent(userAgent)
                                        .referrer("http://library.kuet.ac.bd:8000/cgi-bin/koha/opac-user.pl")
                                        .data("from",from)
                                        .data("borrowernumber",borNum)
                                        .data("item",items.get(0))
                                        .data("item",items.get(1))
                                        .data("btn","Renew all")
                                        .method(Connection.Method.POST)
                                        .followRedirects(true)
                                        .execute();
                                docLibrary = responseLibrary2.parse();
                            }
                            else if(items.size()==3)
                            {
                                Connection.Response responseLibrary2  = Jsoup
                                        .connect("http://library.kuet.ac.bd:8000/cgi-bin/koha/opac-renew.pl")
                                        .cookies(responseLibrary1.cookies())
                                        .userAgent(userAgent)
                                        .referrer("http://library.kuet.ac.bd:8000/cgi-bin/koha/opac-user.pl")
                                        .data("from",from)
                                        .data("borrowernumber",borNum)
                                        .data("item",items.get(0))
                                        .data("item",items.get(1))
                                        .data("item",items.get(2))
                                        .data("btn","Renew all")
                                        .method(Connection.Method.POST)
                                        .followRedirects(true)
                                        .execute();
                                docLibrary = responseLibrary2.parse();
                            }
                            if(docLibrary != null) {
                                Elements elementsTitle = docLibrary.getElementsByClass("title");
                                Elements elementsDateDue = docLibrary.getElementsByClass("date_due");
                                Elements elementsRenewals = docLibrary.getElementsByClass("renew");
                                String dataLibrary=null;
                                for (int i = 0; i< elementsTitle.size()/2; i++) {
                                    dataLibrary +=(i+1)+".\t" + elementsTitle.get(2*i).text().toString() + "\n";
                                    dataLibrary +="\t" + elementsDateDue.get(i).text().toString() + "\n";
                                    dataLibrary +="\t" + elementsRenewals.get(i).text().toString() + "\n";
                                }
                                int dueTime=9999999;
                                elementsDateDue = docLibrary.getElementsByClass("date_due");
                                for (int i =0; i< elementsDateDue.size();i++) {
                                    String date= elementsDateDue.get(i).text().toString();
                                    date = date.substring(10,date.length());
                                    String[] dates = date.split("/");
                                    int dueTime2 = Integer.valueOf(dates[2])*365+Integer.valueOf(dates[1])*30+Integer.valueOf(dates[0]);
                                    if(dueTime>dueTime2) dueTime = dueTime2;
                                }
                                st.writeLibrary(dataLibrary, dueTime, context);
                                notification("Library Auto Renew Success!",dataLibrary,SettingActivity.class);
                            }
                        }
                        if(dif==1000){
                            Log.d(TAG, "renewLibrary: Sorry you've no renewable book.");
                        }
                        else {
                            Log.d(TAG, "renewLibrary: You have still "+dif+" days remaining. Please renew later or manually.\nYou can only auto renew 2 days before due date.");
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

            return null;
        }
    }
}