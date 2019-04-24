package repon.cse.kuetian;

import android.app.ActivityManager;

import android.app.Service;
import android.content.ComponentName;

import android.content.Intent;
import android.os.IBinder;



import com.google.firebase.database.ValueEventListener;


import java.util.List;


public class FirebaseBackgroundService extends Service {

    static String TAG = "myLog";
    private ValueEventListener handler;
    static String rollNumber;
    static FirebaseBackgroundService instance;

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance=this;

        initialize();




    }

    public void initialize() {
        st.downloadFlagsFBS(instance);
    }
    public boolean isForeground(String myPackage) {
        ActivityManager manager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> runningTaskInfo = manager.getRunningTasks(1);
        ComponentName componentInfo = runningTaskInfo.get(0).topActivity;
        return componentInfo.getPackageName().equals(myPackage);
    }

}
