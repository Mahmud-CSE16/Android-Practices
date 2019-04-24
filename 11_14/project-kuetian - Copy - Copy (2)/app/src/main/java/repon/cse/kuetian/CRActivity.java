
package repon.cse.kuetian;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

public class CRActivity extends AppCompatActivity implements View.OnClickListener{

    private static CRActivity instance;
    TableLayout routineDetailsTable;
    TextView routineDetailsTitle;
    Button updateButton;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference myRef;
    String name;
    String rollNumber;
    String department;
    String deptBatch;
    String section;
    String pathReference;
    static boolean activeCRActivity = false;
    public boolean isActive;
    String key;
    AlarmManager alarmManager;
    PendingIntent pendingIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();

        setContentView(R.layout.activity_cr);
        instance = this;
        isActive = true;

        Bundle bundle = getIntent().getExtras();
        key = bundle.get("key").toString();
        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        rollNumber = st.readRollNumber(getApplicationContext());
        if(rollNumber==null) finish();

        Log.d(st.TAG,"rollNumber is "+rollNumber);

        routineDetailsTable = findViewById(R.id.routineDetailsTableLayout);
        routineDetailsTitle = findViewById(R.id.routineDetailsTitleId);
        updateButton = findViewById(R.id.btn_updateRoutine);
        ImageButton classAlarmButton = findViewById(R.id.classAlarmImageButton);
        FloatingActionButton routineAlarmOkfab = findViewById(R.id.routineAlarmOkfab);
        classAlarmButton.setOnClickListener(this);

        routineDetailsTitle.setOnClickListener(this);
        if(st.isCR&&key.equals("routine")) updateButton.setVisibility(View.VISIBLE);
        if(key.equals("classNotification")) {
            classAlarmButton.setVisibility(View.GONE);
            routineDetailsTitle.setText("Select classes for notification!");
            routineAlarmOkfab.setVisibility(View.VISIBLE);
            routineAlarmOkfab.setOnClickListener(this);
        } else if(key.equals("classVibrate")) {
            classAlarmButton.setVisibility(View.GONE);
            routineDetailsTitle.setText("Select classes for auto vibrate mode!");
            routineAlarmOkfab.setVisibility(View.VISIBLE);
            routineAlarmOkfab.setOnClickListener(this);
        }
        HashMap<String, String> set= st.readNotificationData(getApplicationContext());
        if(!(set!=null&&set.get("notification")!=null&&set.get("notification").equals("true")&&st.isChecked("classNotification",getApplicationContext()))) classAlarmButton.setVisibility(View.GONE);
    }

    @Override
    protected void onStart() {
        super.onStart();
        loadRoutine(st.readRoutineData(getApplicationContext()));
    }

    public void updateRoutine()     {
        HashMap<String, HashMap<String, HashMap<String,String> > > days = new HashMap<>();
        for(int i=2;i<=11;i++)
        {
            TableRow subRow = (TableRow)routineDetailsTable.getChildAt(i);
            TableRow teacherRow = (TableRow)routineDetailsTable.getChildAt(++i);

            HashMap<String,HashMap<String,String> > day = new HashMap<>();
            HashMap<String,String> subject = new HashMap<>(), teacher = new HashMap<>();

            for(int j=1;j<=9;j++){
                TextView subTextView = (TextView) subRow.getChildAt(j);
                TextView teacherTextView = (TextView) teacherRow.getChildAt(j);
                subject.put("period"+j,subTextView.getText().toString());
                teacher.put("period"+j,teacherTextView.getText().toString());
            }
            day.put("subject",subject);
            day.put("teacher",teacher);
            days.put("day"+i/2,day);
        }

        loadRoutine(days);
        st.writeRoutineData(days,getApplicationContext());
        uploadRoutine(getApplicationContext(), days);
    }
    public void writeRoutineAlarm()     {
        @SuppressLint("UseSparseArrays") HashMap<Integer, Boolean> alarm = new HashMap<>();
        int [] hr = {8,8,9,10,11,12,14,15,16};
        int [] mn = {0,50,40,40,30,20,30,20,10};
        String [] stringTime = {"","8:00 am","8:50 am","9:40 am","10:40 am", "11:30 am","12:20 am", "2:30 pm","3:20 pm","4:10 pm"};


        for(int i=2;i<=11;i++)
        {
            TableRow subRow = (TableRow)routineDetailsTable.getChildAt(i);
            TableRow teacherRow = (TableRow)routineDetailsTable.getChildAt(++i);
            for(int j=1;j<=9;j++){
                TextView subTextView = (TextView) subRow.getChildAt(j);
                TextView teacherTextView = (TextView) teacherRow.getChildAt(j);

                Intent intent = new Intent(this, AlarmReceiver.class);
                if(key.equals("classNotification")) pendingIntent = PendingIntent.getBroadcast(this, (((i/2)-1)*9+j)+45, intent, 0);
                else pendingIntent = PendingIntent.getBroadcast(this, ((i/2)-1)*9+j, intent, 0);
                alarmManager.cancel(pendingIntent);

                alarm.put(((i/2)-1)*9+j, subTextView.getHint().toString().equals("on"));

                if(subTextView.getHint().toString().equals("on"))
                {
                    intent.putExtra("key", key);
                    if(key.equals("classNotification")) {
                        String cls = ""; if(!subTextView.getText().toString().toLowerCase().contains("lab")) cls = " class";
                        intent.putExtra("title",subTextView.getText().toString()+cls+" at "+stringTime[j]);
                        intent.putExtra("msg",teacherTextView.getText().toString());
                    }

                    Calendar calendar = Calendar.getInstance();
                    calendar.set(Calendar.DAY_OF_WEEK,(i/2));
                    calendar.set(Calendar.HOUR_OF_DAY,hr[j-1]);
                    calendar.set(Calendar.MINUTE, mn[j-1]);

                    long time=(calendar.getTimeInMillis()-(calendar.getTimeInMillis()%60000));
                    if(System.currentTimeMillis()>time)
                    {
                        if (calendar.AM_PM == 0)
                            time = time + (1000*60*60*12*7);
                        else
                            time = time + (1000*60*60*24*7);
                    }
                    long dif = (time-System.currentTimeMillis())/(1000*60);
                    if(key.equals("classNotification"))Log.d(st.TAG, "classNotification set on: "+ (dif/1440)+"day "+(dif%1440)/60+"hour "+((dif%1440)%60)+"mnt");
                    else Log.d(st.TAG, "classVibrate set on: "+ (dif/1440)+"day "+(dif%1440)/60+"hour "+((dif%1440)%60)+"mnt");

                    alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, time,  TimeUnit.DAYS.toMillis(7), pendingIntent);
                    if(subTextView.getText().toString().toLowerCase().contains("lab")) j+=2;
                    }
            }
        }
        st.writeRoutineAlarmData(key,alarm,getApplicationContext());
    }


    public void loadRoutineAlarm()     {
        HashMap<Integer, Boolean> alarm= st.readRoutineAlarmData(key,getApplicationContext());
        if (alarm!=null)
        for(int i=2;i<=11;i++)
        {
            TableRow subRow = (TableRow)routineDetailsTable.getChildAt(i);
            i++;
            for(int j=1;j<=9;j++){
                TextView subTextView = (TextView) subRow.getChildAt(j);

                if(alarm.get(((i/2)-1)*9+j))
                {
                    subTextView.setHint("on");
                    subTextView.setBackgroundResource(R.drawable.routine_alarm_background);
                }
                if(subTextView.getText().toString().toLowerCase().contains("lab")) j+=2;
            }
        }
    }

    public void loadRoutine(HashMap<String, HashMap<String, HashMap<String, String>>> days){
        if(days == null) return;

        for(int i=2;i<=11;i++)
        {
            TableRow subRow = (TableRow)routineDetailsTable.getChildAt(i);
            TableRow teacherRow = (TableRow)routineDetailsTable.getChildAt(++i);

            HashMap<String,HashMap<String,String> > day = days.get("day"+i/2);
            HashMap<String,String> subject = day.get("subject"),teacher = day.get("teacher");

            for(int j=1;j<=9;j++){
                TextView subTextView = (TextView) subRow.getChildAt(j);
                TextView teacherTextView = (TextView) teacherRow.getChildAt(j);

                subTextView.setText(subject.get("period"+j));
                teacherTextView.setText(teacher.get("period"+j));
                TableRow.LayoutParams param = new TableRow.LayoutParams(0,TableRow.LayoutParams.MATCH_PARENT,1.0f);
                subTextView.setLayoutParams(param);
                teacherTextView.setLayoutParams(param);
                subTextView.setVisibility(View.VISIBLE);
                teacherTextView.setVisibility(View.VISIBLE);

                String sub = subject.get("period"+j);
                if(sub.toLowerCase().contains("lab"))
                {
                    param = new TableRow.LayoutParams(0,TableRow.LayoutParams.MATCH_PARENT,3.0f);
                    subTextView.setLayoutParams(param);
                    teacherTextView.setLayoutParams(param);

                    j++;
                    subTextView = (TextView) subRow.getChildAt(j);
                    teacherTextView = (TextView) teacherRow.getChildAt(j);
                    subTextView.setVisibility(View.GONE);
                    teacherTextView.setVisibility(View.GONE);

                    j++;
                    subTextView = (TextView) subRow.getChildAt(j);
                    teacherTextView = (TextView) teacherRow.getChildAt(j);
                    subTextView.setVisibility(View.GONE);
                    teacherTextView.setVisibility(View.GONE);
                }
                else if(!st.isCR & sub.equals(""))
                {
                    subTextView.setVisibility(View.INVISIBLE);
                    teacherTextView.setVisibility(View.INVISIBLE);
                }
            }
        }
        if(key.equals("classNotification")||key.equals("classVibrate"))
            loadRoutineAlarm();
    }

    private void uploadRoutine(Context context, HashMap<String, HashMap<String, HashMap<String,String> > > days){
        deptBatch = rollNumber.substring(2,4)+"/"+rollNumber.substring(0,4);
        if(rollNumber.substring(4,7).compareTo("060")<=0) section = "A";   else section = "B";

        HashMap<String, Integer> flags = st.readFlags(context);

        Integer r= flags.get("r"+section); r++;  days.get("day1").get("subject").put("ver", String.valueOf(r));
        database.getReference(deptBatch+"/routine/"+section).setValue(days);

        flags.put("r"+section,r);
        st.writeFlags(flags,CRActivity.this);
        database.getReference(deptBatch+"/f/r"+section).setValue(r);
    }


    @Override
    protected void onStop() {
        super.onStop();
        isActive = false;

    }

    public void changeRoutineSubText(View view) {
        if(st.isCR && key.equals("routine"))
        {
            if( view instanceof TextView ) {
                TextView textView = (TextView) view;
                getInput(textView);
            }
        }
        else {
            TextView textView = (TextView) view;
            if(textView.getHint().toString().equals("of"))
            {
                textView.setBackgroundResource(R.drawable.routine_alarm_background);
                textView.setHint("on");
            }
            else {
                textView.setBackgroundResource(R.drawable.routine_subject_background);
                textView.setHint("of");
            }

        }
    }
    public void changeRoutineTeachText(View view) {
        if(st.isCR && key.equals("routine"))
        {
            if( view instanceof TextView ) {
                TextView textView = (TextView) view;
                getInput(textView);
            }
        }

    }

    public void getInput(final TextView textView)  {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
       // builder.setTitle("Edit");

// Set up the input
        final EditText input = new EditText(this);
// Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        input.setText(textView.getText().toString());
        builder.setView(input);

// Set up the buttons
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String s = input.getText().toString();
                textView.setText(s);
                st.makeText(CRActivity.this, s, Toast.LENGTH_SHORT);
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }


    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.btn_updateRoutine)
        {
            updateRoutine();
        }
        else if(v.getId()==R.id.routineDetailsTitleId)
        {
            st.downloadRoutine(getApplicationContext());
        }
        else if(v.getId()==R.id.classAlarmImageButton)
        {
            Intent intent = new Intent(getApplicationContext(), CRActivity.class);
            intent.putExtra("key","classNotification");
            startActivity(intent);
        }
        else if(v.getId()==R.id.routineAlarmOkfab)
        {
            writeRoutineAlarm();

            finish();
        }

    }

    public static CRActivity getInstance() {
        return instance;
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        Runtime.getRuntime().gc();
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
    }
}
