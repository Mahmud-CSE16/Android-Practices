package repon.cse.kuetian;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

public class SettingActivity extends AppCompatActivity {
    ArrayList<String> hallList = new ArrayList<String>(Arrays.asList("fhh", "lsh", "khaja", "marh", "rkh", "aeh", "bbh"));
    TextView libraryRenewTextView;
    Button libraryRenewRefreshButton;
    Button libraryRenewAllButton;
    Button libraryRenewScheduleButton;
    String key;
    EditText libraryIdEditText;
    EditText libraryPasswordEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_setting);

        initialize();


    }

    private void initialize() {
        final Switch hallSwitch = findViewById(R.id.hallSwitch);
        final Switch acaSwitch = findViewById(R.id.acaSwitch);
        final Switch librarySwitch = findViewById(R.id.librarySwitch);
        final Switch libraryRenewSwitch = findViewById(R.id.libraryRenewSwitch);
        final Switch vibrateModeSwitch = findViewById(R.id.vibrateModeSwitch);
        final Switch classNotSwitch = findViewById(R.id.classNotSwitch);
        final Switch ctNotSwitch = findViewById(R.id.ctNotSwitch);
        final Switch perScheNotSwitch = findViewById(R.id.perScheNotSwitch);
        final Switch salahNotSwitch = findViewById(R.id.salahNotSwitch);
        final Switch notificationSwitch = findViewById(R.id.notificationSwitch);
        final Switch salahSwitch = findViewById(R.id.salahSwitch);
        final Spinner hallSpinner = findViewById(R.id.hallSpinner);
        final EditText hallIdEditText = findViewById(R.id.hallIdEditText);
        final EditText acaIdEditText = findViewById(R.id.acaIdEditText);
        libraryIdEditText = findViewById(R.id.libraryIdEditText);
        final EditText hallPasswordEditText = findViewById(R.id.hallPasswordEditText);
        final EditText acaPasswordEditText = findViewById(R.id.acaPasswordEditText);

        libraryPasswordEditText = findViewById(R.id.libraryPasswordEditText);
        FloatingActionButton menucloseFAB = findViewById(R.id.menuclosefab);
        final LinearLayout hallSettingLinearLayout = findViewById(R.id.hallSettingLinearLayout);
        final LinearLayout acaSettingLinearLayout = findViewById(R.id.acaSettingLinearLayout);
        final LinearLayout librarySettingLinearLayout = findViewById(R.id.librarySettingLinearLayout);
        final LinearLayout notificationSettingLinearLayout = findViewById(R.id.notificationSettingLinearLayout);

        final EditText CRListEditText = findViewById(R.id.CRListEditText);
        CRListEditText.setText(st.CRList);

        final Button updateCRListButton = findViewById(R.id.updateCRListButton);
        final Button refreshCRListButton = findViewById(R.id.refreshCRListButton);
        if(st.rollNumber.contains("001")||st.rollNumber.contains("061")) CRListEditText.setInputType(InputType.TYPE_CLASS_NUMBER);
        else updateCRListButton.setVisibility(View.GONE);
        updateCRListButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String pathReference = st.rollNumber.substring(2,4)+"/"+st.rollNumber.substring(0,4)+"/CRList";
                st.CRList=CRListEditText.getText().toString();
                st.writeCRList(getApplicationContext());
                FirebaseDatabase.getInstance().getReference(pathReference).setValue(st.CRList);

            }
        });
        refreshCRListButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.checkCRShip("refresh",getApplicationContext());
                refreshCRListButton.setEnabled(false);
            }
        });


        final EditText groupLinkEditText = findViewById(R.id.groupLinkEditText);
        final Button updateGroupLinkButton = findViewById(R.id.updateGroupLinkButton);
        final Button refreshGroupLinkButton = findViewById(R.id.refreshGroupLinkButton);

        if(!st.isCR) updateGroupLinkButton.setVisibility(View.GONE);
        updateGroupLinkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String pathReference = st.rollNumber.substring(2,4)+"/"+st.rollNumber.substring(0,4)+"/groupLink";
                FirebaseDatabase.getInstance().getReference(pathReference).setValue(groupLinkEditText.getText().toString());
            }
        });
        refreshGroupLinkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String pathReference = st.rollNumber.substring(2,4)+"/"+st.rollNumber.substring(0,4)+"/groupLink";
                FirebaseDatabase.getInstance().getReference(pathReference).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        groupLinkEditText.setText(dataSnapshot.getValue().toString());
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        st.makeText(SettingActivity.this, "Please connect with Internet!", Toast.LENGTH_SHORT);
                    }
                });
            }
        });


        HashMap<String, String> settings = st.readUserSettings(getApplicationContext());
        if (settings == null) settings = new HashMap<>();
        if (settings.get("hallSwitch") != null && settings.get("hallSwitch").equals("enabled")) hallSwitch.setChecked(true);
        else {
            hallSwitch.setChecked(false);
            hallSettingLinearLayout.setVisibility(View.GONE);
        }
        if (settings.get("acaSwitch") != null && settings.get("acaSwitch").equals("enabled")) acaSwitch.setChecked(true);
        else {
            acaSwitch.setChecked(false);
            acaSettingLinearLayout.setVisibility(View.GONE);
        }
        if (settings.get("librarySwitch") != null && settings.get("librarySwitch").equals("enabled"))   librarySwitch.setChecked(true);
        else {
            librarySwitch.setChecked(false);
            librarySettingLinearLayout.setVisibility(View.GONE);
        }
        if (settings.get("notificationSwitch") != null && settings.get("notificationSwitch").equals("enabled"))   notificationSwitch.setChecked(true);
        else {
            notificationSwitch.setChecked(false);
            notificationSettingLinearLayout.setVisibility(View.GONE);
        }
        if (settings.get("salahSwitch") != null && settings.get("salahSwitch").equals("enabled"))   salahSwitch.setChecked(true);
        else {
            salahSwitch.setChecked(false);
        }
        if(isChecked("autoRenew")) libraryRenewSwitch.setChecked(true);
        if(isChecked("autoVibrate")) vibrateModeSwitch.setChecked(true);
        if(isChecked("classNotification")) classNotSwitch.setChecked(true);
        if(isChecked("ctNot")) ctNotSwitch.setChecked(true);
        if(isChecked("perScheNot")) perScheNotSwitch.setChecked(true);
        if(isChecked("salahNot")) salahNotSwitch.setChecked(true);
        if(settings.get("groupLink")!=null) groupLinkEditText.setText(settings.get("groupLink"));

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.dept_spinner_layout, R.id.deptSpinnerTextView, new ArrayList<String>(Arrays.asList(
                "Fazlul Haque Hall",
                "Lalan Shah Hall",
                "Khan Jahan Ali Hall",
                "Dr. M.A Rahsid Hall",
                "Rokeya Hall",
                "Amar Ekushey Hall",
                "Bangabandhu S.M.R. Hall")));
        hallSpinner.setAdapter(adapter);
        if (settings.get("hall") != null)
            hallSpinner.setSelection(hallList.indexOf(settings.get("hall")));

        hallIdEditText.setText(settings.get("hallId")); if(settings.get("hallId")==null) hallIdEditText.setText(st.rollNumber);
        acaIdEditText.setText(settings.get("acaId")); if(settings.get("acaId")==null) acaIdEditText.setText("05"+st.rollNumber);
        libraryIdEditText.setText(settings.get("libraryId")); if(settings.get("libraryId")==null) libraryIdEditText.setText("05"+st.rollNumber);
        hallPasswordEditText.setText(settings.get("hallPass"));
        acaPasswordEditText.setText(settings.get("acaPass"));
        libraryPasswordEditText.setText(settings.get("libraryPass"));


        hallSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) hallSettingLinearLayout.setVisibility(View.VISIBLE);
                else hallSettingLinearLayout.setVisibility(View.GONE);
            }
        });
        acaSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) acaSettingLinearLayout.setVisibility(View.VISIBLE);
                else acaSettingLinearLayout.setVisibility(View.GONE);
            }
        });
        librarySwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) librarySettingLinearLayout.setVisibility(View.VISIBLE);
                else librarySettingLinearLayout.setVisibility(View.GONE);
            }
        });
        notificationSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {notificationSettingLinearLayout.setVisibility(View.VISIBLE); updateNotSetting("notification","true");}
                else {notificationSettingLinearLayout.setVisibility(View.GONE); updateNotSetting("notification","false");}
            }
        });
        libraryRenewSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    updateNotSetting("autoRenew","true");
                    st.scheduleLibraryAutoRenew(getApplicationContext());
                }
                else {
                    updateNotSetting("autoRenew","false");
                    st.cancelLibraryAutoRenew(getApplicationContext());

                }
            }
        });
        vibrateModeSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    updateNotSetting("autoVibrate","true");
                    Intent intent = new Intent(getApplicationContext(), CRActivity.class);
                    intent.putExtra("key","classVibrate");
                    startActivity(intent);
                    // BackgroundJob.cancelPeriodicJob();BackgroundJob.schedulePeriodicJob();
                }
                else {
                    updateNotSetting("autoVibrate","false");//BackgroundJob.cancelJob(BackgroundJob.routineTAG);
                    AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
                    Intent intent = new Intent(getApplicationContext(), AlarmReceiver.class);
                    for(int i=2;i<=11;i++)
                    {
                        for(int j=1;j<=9;j++){
                            PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), (((i / 2) - 1) * 9 + j)+45, intent, 0);
                            alarmManager.cancel(pendingIntent);
                        }
                    }
                    }
                }

        });
        classNotSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    updateNotSetting("classNotification","true");
                    Intent intent = new Intent(getApplicationContext(), CRActivity.class);
                    intent.putExtra("key","classNotification");
                    startActivity(intent);
                   // BackgroundJob.cancelPeriodicJob();BackgroundJob.schedulePeriodicJob();
                }
                else {
                    updateNotSetting("classNotification","false");
                    AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
                    Intent intent = new Intent(getApplicationContext(), AlarmReceiver.class);
                    for(int i=2;i<=11;i++)
                    {
                        for(int j=1;j<=9;j++){
                            PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), ((i / 2) - 1) * 9 + j, intent, 0);
                            alarmManager.cancel(pendingIntent);
                        }
                    }
                }
            }
        });
        ctNotSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    updateNotSetting("ctNot","true");
                    st.ctScheduleNotification("ctNot",getApplicationContext());

                }
                else {
                    updateNotSetting("ctNot","false");
                    st.cancelctScheduleNotification("ctNot",getApplicationContext());
                }
            }
        });
        perScheNotSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){ updateNotSetting("perScheNot","true");st.ctScheduleNotification("perScheNot",getApplicationContext());}
                else {updateNotSetting("perScheNot","false");st.cancelctScheduleNotification("perScheNot",getApplicationContext());}
            }
        });
        salahNotSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){ updateNotSetting("salahNot","true");}
                else {updateNotSetting("salahNot","false");}
            }
        });

        menucloseFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                HashMap<String, String> settings = new HashMap<>();
                if (hallSwitch.isChecked()) settings.put("hallSwitch", "enabled");
                else settings.put("hallSwitch", "disabled");
                settings.put("hall", hallList.get(hallSpinner.getSelectedItemPosition()));
                settings.put("hallId", hallIdEditText.getText().toString());
                settings.put("hallPass", hallPasswordEditText.getText().toString());

                if (acaSwitch.isChecked()) settings.put("acaSwitch", "enabled");
                else settings.put("acaSwitch", "disabled");
                settings.put("acaId", acaIdEditText.getText().toString());
                settings.put("acaPass", acaPasswordEditText.getText().toString());

                if (librarySwitch.isChecked()) settings.put("librarySwitch", "enabled");
                else settings.put("librarySwitch", "disabled");
                settings.put("libraryId", libraryIdEditText.getText().toString());
                settings.put("libraryPass", libraryPasswordEditText.getText().toString());

                if (notificationSwitch.isChecked()) settings.put("notificationSwitch", "enabled");
                else settings.put("notificationSwitch", "disabled");
                if (salahSwitch.isChecked()) settings.put("salahSwitch", "enabled");
                else settings.put("salahSwitch", "disabled");
                settings.put("groupLink",groupLinkEditText.getText().toString());
                st.writeUserSettings(settings, getApplicationContext());
                finish();
            }
        });

        libraryRenewTextView = findViewById(R.id.libraryRenewTextView);
        if(!st.readLibrary(getApplicationContext()).isEmpty()) libraryRenewTextView.setText(st.readLibrary(getApplicationContext()));
        libraryRenewRefreshButton = findViewById(R.id.libraryRenewRefreshId);
        libraryRenewAllButton = findViewById(R.id.libraryRenewAll);
        libraryRenewScheduleButton = findViewById(R.id.libraryRenewSchedule);
        libraryRenewRefreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                key = "refresh";
                new LibraryData(libraryIdEditText.getText().toString(),libraryPasswordEditText.getText().toString()).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }
        });
        libraryRenewAllButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                key = "renewAll";
                new LibraryData(libraryIdEditText.getText().toString(),libraryPasswordEditText.getText().toString()).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }
        });
        libraryRenewScheduleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(st.readLibrary(getApplicationContext()).isEmpty())
                {
                    st.makeText(SettingActivity.this, "Nothing to add. Please Refresh.", Toast.LENGTH_LONG);
                }
                else{
                    addRenewToSchedule();
                }
            }
        });

    }

    private void addRenewToSchedule() {
        String mon[] = {"Jan","Feb","Mar","Apr","May","Jun","Jul","Aug","Sep","Oct","Nov","Dec"};
        String dayOfWeek[] = {"Sat","Sun","Mon","Tue","Wed","Thu","Fri"};
        Calendar mcurrentTime = Calendar.getInstance();
        DatePicker dp = new DatePicker(getApplicationContext());
        int currentTime = dp.getYear()*365+(dp.getMonth()+1)*30+dp.getDayOfMonth();
        int dueTime = st.readLibraryDate(getApplicationContext())-2;
        int dif = dueTime-currentTime;
        String ctRawTime = "21:30";
        String ctTime = "09:30 pm";
        String title = "Library Renew";
        if(st.readLibraryDate(getApplicationContext())==9999999)
        {
            st.makeText(SettingActivity.this, "You have no library renew remaining! Please contact with the Library.", Toast.LENGTH_LONG);
            dif =0;
            ctRawTime = "09:00";
            ctTime = "09:00 am";
            title = "Contact with Library for renew";
        }

        mcurrentTime.setTimeInMillis(System.currentTimeMillis()+dif*24*60*60*1000);
        String ctRawDate = mcurrentTime.get(Calendar.YEAR)+"-"+st.format(mcurrentTime.get(Calendar.MONTH)+1)+"-"+st.format(mcurrentTime.get(Calendar.DAY_OF_MONTH));

        String ctDate = st.format(mcurrentTime.get(Calendar.DAY_OF_MONTH))+" "+mon[mcurrentTime.get(Calendar.MONTH)];
        String ctDayOfWeek = dayOfWeek[mcurrentTime.get(Calendar.DAY_OF_WEEK)-1] ;


        ClassTestObject ct = st.readScheduleData(getApplicationContext());
        String ctRawDateTime = ctRawDate + " " + ctRawTime;
        if (ct == null) ct = new ClassTestObject();
        ct.rawDateTimeList.add(ctRawDateTime);
        ct.dateTimeMap.put(ctRawDateTime, ctDate + " " + ctDayOfWeek + ", " + ctTime);
        ct.titleMap.put(ctRawDateTime, title);
        ct.descriptionMap.put(ctRawDateTime, st.readLibrary(getApplicationContext()));

        java.util.Collections.sort(ct.rawDateTimeList);

        st.cancelctScheduleNotification("perScheNot", getApplicationContext());
        st.writeScheduleData(ct, getApplicationContext());
        st.makeText(SettingActivity.this,"Next library renew date on "+ctDate + " " + ctDayOfWeek + ", " + ctTime+" successfully added to My Schedules", Toast.LENGTH_LONG);

    }

    private boolean isChecked(String key) {
        HashMap<String, String> set = st.readNotificationData(getApplicationContext());
        if(set!=null&&set.get(key)!=null&&set.get(key).equals("true")) return true;
        else return false;
    }

    private void updateNotSetting(String key, String value) {
        HashMap<String, String> set = st.readNotificationData(getApplicationContext());
        if(set==null) set = new HashMap<>();
        set.put(key,value);
        st.writeNotificationData(set,getApplicationContext());
    }

    public class LibraryData extends AsyncTask<Void, Void, Void> {

        String title;
        String test;
        String libraryId;
        String libraryPassword;
        String urlLibraryLogin ="http://library.kuet.ac.bd:8000/cgi-bin/koha/opac-user.pl";
        String dataLibrary = "";
        int dif=1000;
        ArrayList<String> name;
        Document docLibrary=null;

        public LibraryData(String libraryId, String libraryPassword) {
            this.libraryId = libraryId;
            this.libraryPassword = libraryPassword;
        }

        public String getTitle() {
            return title;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            libraryRenewAllButton.setEnabled(false);if(key.equals("renewAll"))libraryRenewAllButton.setText("Renewing...");
            libraryRenewRefreshButton.setEnabled(false);if(key.equals("refresh"))libraryRenewRefreshButton.setText("Refreshing...");
            libraryRenewScheduleButton.setEnabled(false);
            Log.d(st.TAG, "started");
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            Log.d(st.TAG, "LibraryData::onPostExecute");

            if(docLibrary == null) return;
            Elements elementsTitle = docLibrary.getElementsByClass("title");
            Elements elementsDateDue = docLibrary.getElementsByClass("date_due");
            Elements elementsRenewals = docLibrary.getElementsByClass("renew");
            for (int i =0; i< elementsTitle.size()/2;i++) {
                dataLibrary +=(i+1)+".\t" + elementsTitle.get(2*i).text().toString() + "\n";
                dataLibrary +="\t " + elementsDateDue.get(i).text().toString() + "\n";
                dataLibrary +="\t " + elementsRenewals.get(i).text().toString() + "\n";
            }
            libraryRenewTextView.setText(dataLibrary);
            int dueTime = 9999999;
            if(docLibrary != null) {
               elementsDateDue = docLibrary.getElementsByClass("date_due");
                for (int i =0; i< elementsDateDue.size();i++) {
                    String date= elementsDateDue.get(i).text().toString();
                    date = date.substring(10,date.length());
                    String[] dates = date.split("/");
                    int dueTime2 = Integer.valueOf(dates[2])*365+Integer.valueOf(dates[1])*30+Integer.valueOf(dates[0]);

                    if(dueTime>dueTime2) dueTime = dueTime2;
                }
            }
            st.writeLibrary(dataLibrary,dueTime,getApplicationContext());

            libraryRenewAllButton.setEnabled(true);libraryRenewAllButton.setText("Renew All");
            libraryRenewRefreshButton.setEnabled(true);libraryRenewRefreshButton.setText("Refresh");
            libraryRenewScheduleButton.setEnabled(true);
            if(key.equals("renewAll"))
            {
                if(dif==1000){
                    st.makeText(SettingActivity.this, "Sorry you've no renewable book.", Toast.LENGTH_LONG);
                }
                else if(dif>15){
                    st.makeText(SettingActivity.this, "You have still "+dif+" days remaining. Please renew later or manually.", Toast.LENGTH_LONG);
                    st.makeText(SettingActivity.this, "You can renew here only 15 days before due date.", Toast.LENGTH_LONG);
                }
                else
                    st.makeText(SettingActivity.this, "Library Renew Successful!", Toast.LENGTH_LONG);

            }
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {

                Log.d(st.TAG, "LibraryData::doInBackground");

                String userAgent ="Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:60.0) Gecko/20100101 Firefox/60.0";

                // Parsing Library Data
                Connection.Response responseLibrary = Jsoup.connect(urlLibraryLogin)
                        .method(Connection.Method.GET)
                        .userAgent(userAgent)
                        .referrer(urlLibraryLogin)
                        .execute();
                Connection.Response responseLibrary1  = Jsoup.connect(urlLibraryLogin)
                        .cookies(responseLibrary.cookies())
                        .userAgent(userAgent)
                        .referrer(urlLibraryLogin)
                        .data("userid",libraryId)
                        .data("password",libraryPassword)
                        .data("koha_login_context","opac")
                        .method(Connection.Method.POST)
                        .followRedirects(true)
                        .execute();
                docLibrary = responseLibrary1.parse();


                if(key.equals("renewAll"))
                {
                    Boolean isRenewable = false;

                    if(docLibrary != null) {
                        Elements elementsDateDue = docLibrary.getElementsByClass("date_due");
                        for (int i =0; i< elementsDateDue.size();i++) {
                            String date= elementsDateDue.get(i).text().toString();
                            date = date.substring(10,date.length());
                            String[] dates = date.split("/");
                            int dueTime = Integer.valueOf(dates[2])*365+Integer.valueOf(dates[1])*30+Integer.valueOf(dates[0]);
                            DatePicker dp = new DatePicker(getApplicationContext());
                            int currentTime = dp.getYear()*365+(dp.getMonth()+1)*30+dp.getDayOfMonth();
                            dif = dueTime-currentTime;
                            if(dif<15) isRenewable=true;
                        }
                    }

                    if(isRenewable)
                    {
                        Log.d(st.TAG, "doInBackground: renewAll");

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
                            Log.d(st.TAG, "doInBackground: items.size()==2");
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
                        }else if(items.size()==3)
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

                    }

                }

            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);

    }
}