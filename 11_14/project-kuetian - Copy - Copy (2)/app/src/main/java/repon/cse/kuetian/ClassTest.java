package repon.cse.kuetian;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TimePicker;

import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import repon.cse.kuetian.refresh_view.PullToRefreshView;

public class ClassTest extends AppCompatActivity implements View.OnClickListener {

    ImageView ctRefreshImageView;
    ListView ctDetailsListView;
    TextView ctDateTextView;
    TextView ctTimeTextView;
    TextView ctOkTextView;
    TextView ctCancelTextView;
    EditText ctTitleEditText;
    EditText ctDescriptionEditText;
    Dialog dialog;
    String ctRawTime;
    String ctRawDate;
    String ctDate;
    String ctTime;
    String ctDayOfWeek;
    int pos=-1;
    static ClassTest instance;
    static ClassTestObject ct = new ClassTestObject();
    static String key="";
    PullToRefreshView mPullToRefreshView;


    public static ClassTest getInstance() {
        return instance;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_class_test);

        instance = this;
        key = getIntent().getExtras().get("key").toString();

        if(key.equals("CT"))
        {
            ct = st.readCTData(getApplicationContext());
            initializeLayout();

        }
        else if(key.equals("Schedule"))
        {
            ct = st.readScheduleData(getApplicationContext());
            initializeLayout();
        }




    }
    private void createPullRefresh() {
        mPullToRefreshView = findViewById(R.id.ctPullRefresh);
        mPullToRefreshView.setOnRefreshListener(new PullToRefreshView.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if(key.equals("CT")) st.downloadCT(getApplicationContext());
                Log.d(st.TAG, "onRefresh: ClassTest:Refreshing");
                mPullToRefreshView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mPullToRefreshView.setRefreshing(false);
                    }
                }, 2000);
            }
        });
    }

    @SuppressLint("RestrictedApi")
    private void initializeLayout() {

        FloatingActionButton ctAddFAB = findViewById(R.id.ctDetailsAddId);
        ctDetailsListView = findViewById(R.id.ctDetailsListViewId);
        TextView ctDetailsHeadView = findViewById(R.id.ctDetailsHeadId);
        if(key.equals("Schedule")) ctDetailsHeadView.setText("My Schedules");

        createPullRefresh();


        if(key.equals("Schedule")||st.isCR) ctAddFAB.setOnClickListener(ClassTest.this);
        else ctAddFAB.setVisibility(View.GONE);
        if(key.equals("Schedule")||st.isCR) ctDetailsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {


                    AlertDialog.Builder builder = new AlertDialog.Builder(ClassTest.this);
                    builder.setTitle("");
                    builder.setPositiveButton("EDIT", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            pos = position;
                            getCTInput();
                            dialog.cancel();
                        }
                    });
                    builder.setNegativeButton("DELETE", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            ct.dateTimeMap.remove(ct.rawDateTimeList.get(position));
                            ct.titleMap.remove(ct.rawDateTimeList.get(position));
                            ct.descriptionMap.remove(ct.rawDateTimeList.get(position));
                            ct.rawDateTimeList.remove(position);
                            java.util.Collections.sort(ct.rawDateTimeList);
                            if(key.equals("CT")) {st.cancelctScheduleNotification("ctNot",getApplicationContext());st.writeCTData(ct,ClassTest.this); uploadCT(getApplicationContext(),ct);}
                            else if(key.equals("Schedule")){st.cancelctScheduleNotification("perScheNot",getApplicationContext()); st.writeScheduleData(ct,ClassTest.this);}

                            updateCTListData();
                            dialog.cancel();
                        }
                    });

                    builder.show();
                }

        });

        if(ct!=null) updateCTListData();

    }



    public void getCTInput()  {
        dialog = new Dialog(ClassTest.this);

        dialog.setContentView(R.layout.activity_classtest_input);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        Window window = dialog.getWindow();
        lp.copyFrom(window.getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        window.setAttributes(lp);

        ctDateTextView = dialog.findViewById(R.id.ctDateId);
        ctTimeTextView = dialog.findViewById(R.id.ctTimeId);
        ctTitleEditText = dialog.findViewById(R.id.ctTitleId);
        ctDescriptionEditText = dialog.findViewById(R.id.ctDescriptionId);
        ctOkTextView = dialog.findViewById(R.id.ctOkButtonId);
        ctCancelTextView = dialog.findViewById(R.id.ctCancelButtonId);

        if(pos!=-1){
            ctRawDate = ct.rawDateTimeList.get(pos).substring(0,10);
            ctDate = ct.dateTimeMap.get(ct.rawDateTimeList.get(pos)).substring(0,6);
            ctDayOfWeek = ct.dateTimeMap.get(ct.rawDateTimeList.get(pos)).substring(7,10);
            ctRawTime = ct.rawDateTimeList.get(pos).substring(11,ct.rawDateTimeList.get(pos).length());
            ctTime = ct.dateTimeMap.get(ct.rawDateTimeList.get(pos)).substring(12,20);
            ctDateTextView.setText(ct.rawDateTimeList.get(pos).substring(0,10));
            ctTimeTextView.setText(ct.rawDateTimeList.get(pos).substring(11,ct.rawDateTimeList.get(pos).length()));
            ctTitleEditText.setText(ct.titleMap.get(ct.rawDateTimeList.get(pos)));
            ctDescriptionEditText.setText(ct.descriptionMap.get(ct.rawDateTimeList.get(pos)));
        }

        ctDateTextView.setOnClickListener(this);
        ctTimeTextView.setOnClickListener(this);
        ctOkTextView.setOnClickListener(this);
        ctCancelTextView.setOnClickListener(this);

        dialog.show();
    }
    @Override
    public void onClick(View v) {

        if(v.getId()==R.id.ctDetailsAddId)
        {
            getCTInput();
        }

        else if(v.getId()==R.id.ctDateId)
        {
            DatePicker dp = new DatePicker(this);
            DatePickerDialog datePickerDialog = new DatePickerDialog(ClassTest.this, new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                    String mon[] = {"Jan","Feb","Mar","Apr","May","Jun","Jul","Aug","Sep","Oct","Nov","Dec"};
                    ctDateTextView.setText(st.format(dayOfMonth)+"-"+st.format(month+1)+"-"+year);
                    ctRawDate = year+"-"+st.format(month+1)+"-"+st.format(dayOfMonth);
                    ctDate = st.format(dayOfMonth)+" "+mon[month];

                    SimpleDateFormat simpledateformat = new SimpleDateFormat("EEEE");
                    Date date = new Date(year, month, dayOfMonth-1);
                    ctDayOfWeek = simpledateformat.format(date);
                }
            },dp.getYear(),dp.getMonth(),dp.getDayOfMonth());

            datePickerDialog.show();

        }
        else if(v.getId()==R.id.ctTimeId)
        {
            Calendar mcurrentTime = Calendar.getInstance();
            int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
            int minute = mcurrentTime.get(Calendar.MINUTE);
            TimePickerDialog mTimePicker;
            mTimePicker = new TimePickerDialog(ClassTest.this, new TimePickerDialog.OnTimeSetListener() {
                @Override
                public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                    ctRawTime = st.format(selectedHour) + ":" + st.format(selectedMinute);
                    if(selectedHour>12)
                    {
                        if(selectedHour>11) ctTime = st.format(selectedHour-12)+":"+st.format(selectedMinute)+" pm";
                        else ctTime = st.format(selectedHour-12)+":"+st.format(selectedMinute)+" am";
                    }
                    else ctTime = st.format(selectedHour)+":"+st.format(selectedMinute)+" am";
                    ctTimeTextView.setText( ctTime );
                }
            }, hour, minute, false);//Yes 24 hour time
            mTimePicker.setTitle("Select Time");
            mTimePicker.show();
        }
        else if(v.getId()==R.id.ctOkButtonId)
        {
            if(ctRawDate!=null&ctRawTime!=null)
            {
                if(pos != -1) {
                    ct.dateTimeMap.remove(ct.rawDateTimeList.get(pos));
                    ct.titleMap.remove(ct.rawDateTimeList.get(pos));
                    ct.descriptionMap.remove(ct.rawDateTimeList.get(pos));
                    ct.rawDateTimeList.remove(pos);
                    pos = -1;
                }
                String ctRawDateTime = ctRawDate+" "+ctRawTime;
                if(ct==null) ct = new ClassTestObject();
                ct.rawDateTimeList.add(ctRawDateTime);
                ct.dateTimeMap.put(ctRawDateTime,ctDate+" "+ctDayOfWeek.substring(0,3)+", "+ctTime);
                ct.titleMap.put(ctRawDateTime,ctTitleEditText.getText().toString());
                ct.descriptionMap.put(ctRawDateTime,ctDescriptionEditText.getText().toString());

                java.util.Collections.sort(ct.rawDateTimeList);
                if(key.equals("CT")) {st.cancelctScheduleNotification("ctNot",getApplicationContext());st.writeCTData(ct,ClassTest.this); uploadCT(getApplicationContext(),ct);}
                else if(key.equals("Schedule")){st.cancelctScheduleNotification("perScheNot",getApplicationContext()); st.writeScheduleData(ct,ClassTest.this);}

                updateCTListData();
            }
            dialog.dismiss();

        }
        else if(v.getId()==R.id.ctCancelButtonId)
        {
            pos = -1;
            dialog.dismiss();
        }

    }

    public static void updateCTListData() {


            ListView ctDetailsListView = ClassTest.getInstance().findViewById(R.id.ctDetailsListViewId);
            ListViewCustomAdapter customAdapter = new ListViewCustomAdapter(ClassTest.getInstance(), ClassTest.ct);
            ctDetailsListView.setAdapter(customAdapter);

    }

    private void uploadCT(Context context, ClassTestObject ct) {
        HashMap<String, Integer> flags = st.readFlags(context);

        Integer ctf = flags.get("ct");
        ctf++;
        ct.dateTimeMap.put("ver", String.valueOf(ctf));
        String pathReference = st.rollNumber.substring(2,4)+"/"+st.rollNumber.substring(0,4)+"/ct";
        FirebaseDatabase.getInstance().getReference(pathReference).setValue(ct);

        pathReference = st.rollNumber.substring(2,4)+"/"+st.rollNumber.substring(0,4)+"/f/ct";
        flags.put("ct",ctf);
        st.writeFlags(flags,ClassTest.this);
        FirebaseDatabase.getInstance().getReference(pathReference).setValue(ctf);
    }


    public static void updateMainCTData(Context context) {
        ct = st.readCTData(context);
        ExpandableListView ctListView = MainActivity.instance.findViewById(R.id.ctListViewId);
        if(ct.rawDateTimeList.size()>4) ctListView.setNestedScrollingEnabled(true);
        CTCustomAdapter CTCustomAdapter = new CTCustomAdapter(MainActivity.context, ct);
        ctListView.setAdapter(CTCustomAdapter);

    }
    public static void updateMainScheduleData(Context context) {
        ct = st.readScheduleData(context);
        ExpandableListView scheduleListView = MainActivity.instance.findViewById(R.id.scheduleListViewId);
        if(ct.rawDateTimeList.size()>4) scheduleListView.setNestedScrollingEnabled(true);
        CTCustomAdapter CTCustomAdapter = new CTCustomAdapter(MainActivity.context, ct);
        scheduleListView.setAdapter(CTCustomAdapter);

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
