package repon.cse.kuetian;

import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static android.content.Context.ACTIVITY_SERVICE;
import static android.content.Context.ALARM_SERVICE;
import static android.content.Context.NOTIFICATION_SERVICE;

public class st {

    static String rollNumber;
    static String CRList = "001,011,061";
    static boolean isCR = false;
    static String TAG = "myLog";
    static HashMap<String,String> deptList = new HashMap<>();


    public static HashMap<String, String> initDeptList() {
        st.deptList.put("01","CE");
        st.deptList.put("03","EEE");
        st.deptList.put("05","ME");
        st.deptList.put("07","CSE");
        st.deptList.put("09","ECE");
        st.deptList.put("11","IEM");
        st.deptList.put("13","ESE");
        st.deptList.put("15","BME");
        st.deptList.put("17","URP");
        st.deptList.put("19","LE");
        st.deptList.put("21","TE");
        st.deptList.put("23","BECM");
        st.deptList.put("25","ARCH");
        st.deptList.put("27","MSE");
        st.deptList.put("29","Chemical");
        st.deptList.put("31","Mechat");
        st.deptList.put("51","MATH");
        st.deptList.put("53","CHEM");
        st.deptList.put("55","PHY");
        st.deptList.put("57","HUM");

        return st.deptList;
    }
    public static String[] getDeptArr() {
        String[] deptArr = new String[]{"EEE", "CSE", "ECE","BME","MSE","ME","IEM","LE","TE","ESE","Chemical","Mechat","CE","URP","BECM","ARCH","MATH","CHEM","PHY","HUM"};
        return deptArr;
    }
    public static int findIdx(String dept) {
        String[] deptArr = new String[]{"EEE", "CSE", "ECE","BME","MSE","ME","IEM","LE","TE","ESE","Chemical","Mechat","CE","URP","BECM","ARCH","MATH","CHEM","PHY","HUM"};
        for(int i=0;i<deptArr.length;i++)
            if(deptArr[i].equals(dept)) return i;
        return 0;
    }


    public static String format(int num)  {
        if(num<10) return "0"+num;
        return ""+num;
    }

    public static void writeUserData(Object object, Context context){
        SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor prefsEditor = mPrefs.edit();
        prefsEditor.putString("userData"+rollNumber, new Gson().toJson(object));
        prefsEditor.apply();
        Log.d(TAG,"writeUserData:success");
    }

    public static User readUserData(Context context){
        SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        Gson gson = new Gson();
        String json = mPrefs.getString("userData"+rollNumber, "");
        Log.d(TAG,"readUserData:success");
        return gson.fromJson(json, User.class);
    }
    public static void writeAllUser(Object object, Context context){
        SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor prefsEditor = mPrefs.edit();
        prefsEditor.putString("userAll"+rollNumber.substring(0,4), new Gson().toJson(object));
        prefsEditor.apply();
        Log.d(TAG,"writeAllUser:success");
    }
    public static void writeUserSettings(Object object, Context context){
        SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor prefsEditor = mPrefs.edit();
        prefsEditor.putString("settings"+rollNumber, new Gson().toJson(object));
        prefsEditor.apply();
        Log.d(TAG,"writeUserSettings:success");
    }
    public static HashMap<String, String> readUserSettings(Context context){
        SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        Gson gson = new Gson();
        String json = mPrefs.getString("settings"+rollNumber, "");
        Log.d(TAG,"readUserSettings:success");
        return gson.fromJson(json,new TypeToken<HashMap<String, String>>(){}.getType());
    }
    public static void writeLibrary(String libInfo, int dueTime, Context context){
        SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor prefsEditor = mPrefs.edit();
        prefsEditor.putString("library"+rollNumber, libInfo);
        prefsEditor.putInt("libraryDate"+rollNumber, dueTime);
        prefsEditor.apply();
        Log.d(TAG,"writeLibrary:success");
    }
    public static String readLibrary(Context context){
        SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        Log.d(TAG,"readLibrary:success");
        return mPrefs.getString("library"+rollNumber, "");
    }
    public static int readLibraryDate(Context context){
        SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        Log.d(TAG,"readLibrary:success");
        return mPrefs.getInt("libraryDate"+rollNumber, 0);
    }
    public static HashMap<String, User> readAllUser(Context context){
        SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        Gson gson = new Gson();
        String json = mPrefs.getString("userAll"+rollNumber.substring(0,4), "");
        Log.d(TAG,"readAllUser:success");
        return gson.fromJson(json,new TypeToken<HashMap<String, User>>(){}.getType());
    }
    public static void writeRollNumber(String rollNumber,Context context){
        SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor prefsEditor = mPrefs.edit();
        prefsEditor.putString("rollNumber",rollNumber);
        prefsEditor.apply();
        Log.d(TAG,"writeRollNumber:success");
    }
    public static String readRollNumber(Context context){
        SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        Log.d(TAG,"readRollNumber:success"+mPrefs.getString("rollNumber", ""));
        return mPrefs.getString("rollNumber", "");
    }
    public static void writeSalahCentral(String sC,Context context){
        SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor prefsEditor = mPrefs.edit();
        prefsEditor.putString("sC",sC);
        prefsEditor.apply();
    }
    public static String readSalahCentral(Context context){
        SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        return mPrefs.getString("sC", "");
    }

    public static void writeCRList(Context context){
        SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor prefsEditor = mPrefs.edit();
        prefsEditor.putString("CRList",CRList);
        prefsEditor.apply();
        Log.d(TAG,"writeCRList:success");
    }
    public static String readCRList(Context context){
        SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        Log.d(TAG,"readCRList:success");
        return mPrefs.getString("CRList", "");
    }

    public static void writeRoutineData(Object object, Context context){
        SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor prefsEditor = mPrefs.edit();
        String section; if(rollNumber.substring(4,7).compareTo("060")<=0) section = "A"; else section = "B";
        prefsEditor.putString("routineData"+rollNumber.substring(0,4)+section, new Gson().toJson(object));
        prefsEditor.apply();
        Log.d(TAG,"writeRoutineData:success");
    }
    public static HashMap<String, HashMap<String, HashMap<String, String>>> readRoutineData(Context context){
        SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        Gson gson = new Gson();
        String section; if(rollNumber.substring(4,7).compareTo("060")<=0) section = "A"; else section = "B";
        String json = mPrefs.getString("routineData"+rollNumber.substring(0,4)+section, "");
        Log.d(TAG,"readRoutineData:success"+rollNumber.substring(0,4)+section);
        return gson.fromJson(json,new TypeToken<HashMap<String, HashMap<String, HashMap<String, String>>>>(){}.getType());
    }
    public static void writeRoutineAlarmData(String key,Object object, Context context){
        SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor prefsEditor = mPrefs.edit();
        prefsEditor.putString(key+rollNumber, new Gson().toJson(object));
        prefsEditor.apply();
        Log.d(TAG,"writeRoutineAlarmData:success");
    }
    public static HashMap<Integer, Boolean> readRoutineAlarmData(String key,Context context){
        SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        Gson gson = new Gson();
        String json = mPrefs.getString(key+rollNumber, "");
        Log.d(TAG,"readRoutineAlarmData:success"+rollNumber);
        return gson.fromJson(json,new TypeToken<HashMap<Integer, Boolean>>(){}.getType());
    }

    public static void writeFlags(Object object, Context context){
        SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor prefsEditor = mPrefs.edit();
        prefsEditor.putString("flags"+rollNumber.substring(0,4), new Gson().toJson(object));
        prefsEditor.apply();
        Log.d(TAG,"writeFlags:success");
    }
    public static HashMap<String, Integer> readFlags(Context context){
        SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        Gson gson = new Gson();
        String json = mPrefs.getString("flags"+rollNumber.substring(0,4), "");
        Log.d(TAG,"readFlags:success");
        return gson.fromJson(json,new TypeToken<HashMap<String, Integer>>(){}.getType());
    }
    public static void writeNotificationData(Object object, Context context){
        SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor prefsEditor = mPrefs.edit();
        prefsEditor.putString("notData"+rollNumber, new Gson().toJson(object));
        prefsEditor.apply();
        Log.d(TAG,"writeNotificationData:success");
    }
    public static HashMap<String, String> readNotificationData(Context context){
        SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        Gson gson = new Gson();
        String json = mPrefs.getString("notData"+rollNumber, "");
        Log.d(TAG,"readNotificationData:success");
        return gson.fromJson(json,new TypeToken<HashMap<String, String>>(){}.getType());
    }

    public static void writeTeachers(Object object, Context context, String dept){
        SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor prefsEditor = mPrefs.edit();
        prefsEditor.putString("t"+dept, new Gson().toJson(object));
        prefsEditor.apply();
        Log.d(TAG,"writeTeachers:success");
    }
    public static HashMap<String, List<String>> readTeachers(Context context, String dept){
        SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        Gson gson = new Gson();
        String json = mPrefs.getString("t"+dept, "");
        Log.d(TAG,"readTeachers:success");
        return gson.fromJson(json,new TypeToken<HashMap<String, List<String>>>(){}.getType());
    }

    public static void writeCTData(Object object, Context context){
        SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor prefsEditor = mPrefs.edit();
        prefsEditor.putString("CTData"+rollNumber.substring(0,4), new Gson().toJson(object));
        prefsEditor.apply();
        ctScheduleNotification("ctNot",context);
        //checkNot(context);
        Log.d(TAG,"writeCTData:success");
    }
    public static void writeScheduleData(Object object, Context context){
        SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor prefsEditor = mPrefs.edit();
        prefsEditor.putString("ScheduleData"+rollNumber, new Gson().toJson(object));
        prefsEditor.apply();
        ctScheduleNotification("perScheNot",context);
        Log.d(TAG,"writeScheduleData:success");
    }

    public static void ctScheduleNotification(String key, Context context) {
        HashMap<String, String> set= readNotificationData(context);

        if(set!=null&&set.get("notification")!=null&&set.get("notification").equals("true"))
        {if(isChecked(key,context)) {
            AlarmManager alarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);
            ClassTestObject ct=null;
            if(key.equals("perScheNot")) ct = st.readScheduleData(context);
            else if(key.equals("ctNot")) ct = st.readCTData(context);

            String t = st.currentTime(context);
            int curTime = Integer.valueOf(t.substring(0, 4)) * 365 * 1440 + Integer.valueOf(t.substring(5, 7)) * 30 * 1440 + Integer.valueOf(t.substring(8, 10)) * 1440 + Integer.valueOf(t.substring(11, 13)) * 60 + Integer.valueOf(t.substring(14, 16));
            int dif;
            if(ct != null)
            for (int i = 0; i < ct.rawDateTimeList.size(); i++) {
                t = ct.rawDateTimeList.get(i);
                int ctTime = Integer.valueOf(t.substring(0, 4)) * 365 * 1440 + Integer.valueOf(t.substring(5, 7)) * 30 * 1440 + Integer.valueOf(t.substring(8, 10)) * 1440 + Integer.valueOf(t.substring(11, 13)) * 60 + Integer.valueOf(t.substring(14, 16));
                dif = ctTime - curTime;
                if (dif >= 0) {

                    Intent intent = new Intent(context, AlarmReceiver.class);
                    intent.putExtra("key", key);
                    intent.putExtra("title", ct.titleMap.get(t) + " at " + ct.dateTimeMap.get(t));
                    intent.putExtra("msg", ct.descriptionMap.get(t));
                    PendingIntent pendingIntent = PendingIntent.getBroadcast(context, ctTime, intent, 0);


                        assert alarmManager != null;
                        Log.d(st.TAG, "writeRoutineAlarm on : "+ (dif/1440)+"day "+(dif%1440)/60+"hour "+((dif%1440)%60)+"mnt");
                        alarmManager.setExact(AlarmManager.RTC_WAKEUP, System.currentTimeMillis()+TimeUnit.MINUTES.toMillis(dif), pendingIntent);

                }

            }
        }
        }
    }
    public static void scheduleClassNotification(String key, Context context) {
        HashMap<Integer, Boolean> alarm= st.readRoutineAlarmData(key,context);
        if (alarm!=null)
        {
            int [] hr = {8,8,9,10,11,12,14,15,16};
            int [] mn = {0,50,40,40,30,20,30,20,10};
            String [] stringTime = {"","8:00 am","8:50 am","9:40 am","10:40 am", "11:30 am","12:20 am", "2:30 pm","3:20 pm","4:10 pm"};
            AlarmManager alarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);

            HashMap<String, HashMap<String, HashMap<String, String>>> days = st.readRoutineData(context);
            for(int i=2;i<=11;i++)
            {
                HashMap<String,HashMap<String,String> > day = days.get("day"+i/2);
                HashMap<String,String> subject = day.get("subject"),teacher = day.get("teacher");
                i++;
                for(int j=1;j<=9;j++){


                    Intent intent = new Intent(context, AlarmReceiver.class);
                    PendingIntent pendingIntent;
                    if(key.equals("classNotification")) pendingIntent = PendingIntent.getBroadcast(context, (((i/2)-1)*9+j)+45, intent, 0);
                    else pendingIntent = PendingIntent.getBroadcast(context, ((i/2)-1)*9+j, intent, 0);
                    alarmManager.cancel(pendingIntent);
                    if(alarm.get(((i/2)-1)*9+j))
                    {
                        intent.putExtra("key", key);
                        if(key.equals("classNotification")) {
                            String cls = ""; if(!subject.get("period"+j).toLowerCase().contains("lab")) cls = " class";
                            intent.putExtra("title",subject.get("period"+j)+cls+" at "+stringTime[j]);
                            intent.putExtra("msg",teacher.get("period"+j));
                        }

                        Calendar calendar = Calendar.getInstance();
                        calendar.set(Calendar.DAY_OF_WEEK,(i/2));
                        calendar.set(Calendar.HOUR_OF_DAY,hr[j-1]);
                        calendar.set(Calendar.MINUTE, mn[j-1]);

                        long time=(calendar.getTimeInMillis()-(calendar.getTimeInMillis()%60000));
                        if(System.currentTimeMillis()>time)
                        {
                            if (Calendar.AM_PM == 0)
                                time = time + (1000*60*60*12*7);
                            else
                                time = time + (1000*60*60*24*7);
                        }
                        long dif = (time-System.currentTimeMillis())/(1000*60);
                        if(key.equals("classNotification"))Log.d(st.TAG, "classNotification set on: "+ (dif/1440)+"day "+(dif%1440)/60+"hour "+((dif%1440)%60)+"mnt");
                        else Log.d(st.TAG, "classVibrate set on: "+ (dif/1440)+"day "+(dif%1440)/60+"hour "+((dif%1440)%60)+"mnt");

                        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, time,  TimeUnit.DAYS.toMillis(7), pendingIntent);
                        if(subject.get("period"+j).toLowerCase().contains("lab")) j+=2;
                    }
                }
            }
        }
    }
    public static void scheduleLibraryAutoRenew( Context context) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);
        Intent intent = new Intent(context, AlarmReceiver.class);
        intent.putExtra("key","libraryAutoRenew");
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(),  TimeUnit.DAYS.toMillis(1), pendingIntent);
    }
    public static void cancelLibraryAutoRenew(Context context) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);
        Intent intent = new Intent(context, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
        alarmManager.cancel(pendingIntent);
    }
    public static boolean isChecked(String key,Context context) {
        HashMap<String, String> set = st.readNotificationData(context);
        return set != null && set.get(key) != null && set.get(key).equals("true");
    }

    public static void cancelctScheduleNotification(String key, Context context) {

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);
        ClassTestObject ct=null;
        if(key.equals("perScheNot")) ct = st.readScheduleData(context);
        else if(key.equals("ctNot")) ct = st.readCTData(context);
        String t;
        if(ct != null)
        {
            for(int i = 0; i<ct.rawDateTimeList.size(); i++)
            {
                t = ct.rawDateTimeList.get(i);
                int ctTime = Integer.valueOf(t.substring(0,4))*365*3600+Integer.valueOf(t.substring(5,7))*30*3600+Integer.valueOf(t.substring(8,10))*3600+Integer.valueOf(t.substring(11,13))*60+Integer.valueOf(t.substring(14,16));
                Intent intent = new Intent(context, AlarmReceiver.class);
                PendingIntent pendingIntent = PendingIntent.getBroadcast(context, ctTime, intent, 0);
                assert alarmManager != null;
                alarmManager.cancel(pendingIntent);
            }
        }
    }

    public static ClassTestObject readScheduleData(Context context){
        SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        Gson gson = new Gson();
        String json = mPrefs.getString("ScheduleData"+rollNumber, "");
        Log.d(TAG,"readScheduleData:success"+rollNumber.substring(0,4));
        return gson.fromJson(json,ClassTestObject.class);
    }

    public static void writeNoteData(String note,Context context){
        SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor prefsEditor = mPrefs.edit();
        prefsEditor.putString("Note"+rollNumber,note);
        prefsEditor.apply();
        Log.d(TAG,"writeNoteData:success");
    }
    public static String readNoteData(Context context){
        SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        Log.d(TAG,"readNoteData:success");
        return mPrefs.getString("Note"+rollNumber, "");
    }

    public static ClassTestObject readCTData(Context context){
        SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        Gson gson = new Gson();
        String json = mPrefs.getString("CTData"+rollNumber.substring(0,4), "");
        Log.d(TAG,"readCTData:success"+rollNumber.substring(0,4));
        return gson.fromJson(json,ClassTestObject.class);
    }



    public static void downloadRoll(final Context context) {

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        Log.d(TAG,"entered to downloadRoll");
        if(currentUser==null)
        {
            Log.d(TAG,"currentUser is null");
            return;
        }

        String Uid = currentUser.getUid();
        Log.d(TAG,"currentUser is "+Uid);

        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference(Uid);
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    rollNumber = dataSnapshot.getValue(String.class);
                    writeRollNumber(rollNumber,context);
                } else {
                    Log.d(TAG,"Data Loading failed");
                }
            }
            @Override
            public void onCancelled(DatabaseError error) {
                Log.d(TAG,"Data Loading failed");
            }

        });

    }

    public static void downloadUserData(final Context context){
        Log.d(TAG,"downloadUserData");

        String pathReference = rollNumber.substring(2,4)+"/"+rollNumber.substring(0,4)+"/user/"+rollNumber;

        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference(pathReference);

        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists())
                {
                    User newUser = dataSnapshot.getValue(User.class);
                    writeUserData(newUser,context);
                    if(context == MainActivity.context) MainActivity.getInstance().updateUserData();
                    if(ProfileActivity.instance != null) ProfileActivity.instance.loadUserData(newUser);
                }
                else
                {
                    Log.d(TAG,"Data Loading failed");
                    st.makeText(context, "Data Loading failed", Toast.LENGTH_SHORT);
                }
            }
            @Override
            public void onCancelled(DatabaseError error) {
                Log.d(TAG,"Data Loading failed");
                st.makeText(context, "Data Loading failed", Toast.LENGTH_SHORT);
            }
        });
    }

    public static void downloadFlags(final Context context){
        Log.d(TAG,"downloadFlags:st");
        rollNumber = st.readRollNumber(context);
        if(rollNumber==null) st.downloadRoll(context);
        FirebaseApp.initializeApp(context);
        String pathReference = rollNumber.substring(2,4)+"/"+rollNumber.substring(0,4)+"/f";
        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference(pathReference);
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                HashMap<String, Integer> flags = st.readFlags(context);
                if(dataSnapshot.exists())
                {
                    Log.d(TAG,"get downloadFlags:st");
                    HashMap<String, Integer> f = (HashMap<String, Integer>) dataSnapshot.getValue();
                    String section; if(rollNumber.substring(4,7).compareTo("060")<=0) section = "A"; else section = "B";
                    if(flags==null || !String.valueOf(f.get("r"+section)).equals(String.valueOf(flags.get("r"+section))))
                    {
                        st.downloadRoutine(context);
                        st.postNotif(context,"Routine Update!","Routine has been updated");
                    }
                    if(flags==null || !String.valueOf(f.get("ct")).equals(String.valueOf(flags.get("ct"))))
                    {
                        st.downloadCT(context);
                        st.postNotif(context,"CT Update!","CT and others has been updated");
                    }
                }
                else
                {
                    Log.d(TAG,"Flags Loading failed");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d(TAG,"Flags Loading cancelled: "+databaseError);
            }
        });
    }

    public static boolean isForeground(Context context, String myPackage) {
        ActivityManager manager = (ActivityManager) context.getSystemService(ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> runningTaskInfo = manager.getRunningTasks(1);
        ComponentName componentInfo = runningTaskInfo.get(0).topActivity;
        return componentInfo.getPackageName().equals(myPackage);
    }

    public static void downloadFlagsFBS(final Context context){
        Log.d(TAG,"downloadFlags:FBS");
        rollNumber = st.readRollNumber(context);
        if(rollNumber==null) st.downloadRoll(context);
        FirebaseApp.initializeApp(context);
        String pathReference = rollNumber.substring(2,4)+"/"+rollNumber.substring(0,4)+"/f";
        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference(pathReference);
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                HashMap<String, Integer> flags = st.readFlags(context);

                if(dataSnapshot.exists()&&!st.isForeground(context,"repon.cse.kuetian"))
                {
                    Log.d(TAG,"get downloadFlags:FBS");
                    HashMap<String, Integer> f = (HashMap<String, Integer>) dataSnapshot.getValue();
                    String section; if(rollNumber.substring(4,7).compareTo("060")<=0) section = "A"; else section = "B";
                    if(flags==null || !String.valueOf(f.get("r"+section)).equals(String.valueOf(flags.get("r"+section))))
                    {
                        Log.d(TAG,"flag called Routine"+f.get("r"+section)+"!="+flags.get("r"+section));
                        st.downloadRoutine(context);
                        st.postNotif(context,"Routine Update!","Routine has been updated");
                    }
                    if(flags==null || !String.valueOf(f.get("ct")).equals(String.valueOf(flags.get("ct"))))
                    {
                        Log.d(TAG,"flag called CT "+f.get("ct")+"!="+flags.get("ct"));
                        st.downloadCT(context);
                        st.postNotif(context,"CT Update!","CT and others has been updated");
                    }
                }
                else
                {
                    Log.d(TAG,"Flags Loading failed");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d(TAG,"Flags Loading cancelled: "+databaseError);
            }
        });
    }

    public static void postNotif(Context context, String title, String msg) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
        Intent intent = new Intent(context, MainActivity.class);
        PendingIntent pIntent = PendingIntent.getActivity(context, (int) System.currentTimeMillis(), intent, 0);

        // build notification
        // the addAction re-use the same intent to keep the example short
        Notification n  = new Notification.Builder(context)
                .setContentTitle(title)
                .setContentText(msg)
                .setColor(Color.GREEN)
                .setSmallIcon(R.drawable.kuet_notif_logo)
                .setContentIntent(pIntent)
                .setAutoCancel(true)
                .setStyle(new Notification.BigTextStyle().bigText("")).build();
        //  .addAction(R.drawable.line, "", pIntent).build();
        n.flags |= Notification.FLAG_AUTO_CANCEL;
        notificationManager.notify(0, n);
    }

    public static void downloadRoutine(final Context context){
        Log.d(TAG,"downloadRoutine");

        final String section; if(rollNumber.substring(4,7).compareTo("060")<=0) section = "A"; else section = "B";

        String pathReference = rollNumber.substring(2,4)+"/"+rollNumber.substring(0,4)+"/routine/"+section;
        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference(pathReference);
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists())
                {
                    HashMap<String, Integer> flags = st.readFlags(context);
                    if(flags==null) {flags = new HashMap<>(); downloadFlags(context);}
                    Log.d(TAG,"get downloadRoutine:");
                    HashMap<String, HashMap<String, HashMap<String,String> > > days;
                    days = (HashMap<String, HashMap<String, HashMap<String, String>>>) dataSnapshot.getValue();
                    st.writeRoutineData(days,context);
                    st.makeText(context, "Routine updated!", Toast.LENGTH_SHORT);
                    Integer r = Integer.valueOf(days.get("day1").get("subject").get("ver")); // update flag
                    flags.put("r"+section,r);
                    st.writeFlags(flags,context);
                    if(MainActivity.getInstance()!=null) MainActivity.getInstance().updateRoutine();
                    if(CRActivity.getInstance()!=null)   CRActivity.getInstance().loadRoutine(days);
                }
                else
                {
                    Log.d(TAG,"Routine is empty. Please contact with CR for update");
                    st.makeText(context, "Routine is empty. Please contact with CR for update", Toast.LENGTH_SHORT);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d(TAG,"Routine Loading Cancelled:"+databaseError);
                st.makeText(context, "Routine Loading Cancelled", Toast.LENGTH_SHORT);
            }
        });
    }

    public static void downloadCT(final Context context) {
        String pathReference = st.rollNumber.substring(2,4)+"/"+st.rollNumber.substring(0,4)+"/ct";
        FirebaseDatabase.getInstance().getReference(pathReference).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists())
                {
                    HashMap<String, Integer> flags = st.readFlags(context);
                    if(flags==null) {flags = new HashMap<>(); downloadFlags(context);}
                    ClassTest.ct =  dataSnapshot.getValue(ClassTestObject.class);
                    st.cancelctScheduleNotification("ctNot",context); st.writeCTData(ClassTest.ct,context);
                    Integer ctf=Integer.valueOf(ClassTest.ct.dateTimeMap.get("ver"));
                    flags.put("ct",ctf);
                    st.writeFlags(flags,context);
                    if(ClassTest.getInstance()!=null)   if(ClassTest.key.equals("CT")) ClassTest.updateCTListData();
                    if(MainActivity.getInstance()!=null) ClassTest.updateMainCTData(context);
                    Log.d(TAG,"Class Test updated!");
                    st.makeText(context, "Class Test updated!", Toast.LENGTH_SHORT);
                }
                else
                {
                    Log.d(TAG,"Class Test Data is Empty!");
                    st.makeText(context, "Class Test Data is Empty!", Toast.LENGTH_SHORT);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d(TAG,"Class Test Data onCancelled: "+databaseError);
            }
        });

    }

    public static String currentTime(Context context) {
        Calendar mcurrentTime = Calendar.getInstance();
        int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
        int minute = mcurrentTime.get(Calendar.MINUTE);
        DatePicker dp = new DatePicker(context);
        String currentTime = dp.getYear()+"-"+ st.format(dp.getMonth() + 1)+"-"+st.format(dp.getDayOfMonth())+" "+st.format(hour) + ":" + st.format(minute);
        return currentTime;
    }

    public static void makeText(Context context, String s, int lengthLong) {
        Toast toast = Toast.makeText(context, s, Toast.LENGTH_LONG);
        View view = toast.getView();
        view.setBackgroundResource(R.drawable.toast_background);
        TextView text = view.findViewById(android.R.id.message);
        text.setTextColor(Color.WHITE);
        text.setPadding(15,5,15,5);
        toast.show();
    }
}
