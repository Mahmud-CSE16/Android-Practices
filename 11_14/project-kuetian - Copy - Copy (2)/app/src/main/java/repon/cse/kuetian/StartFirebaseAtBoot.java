package repon.cse.kuetian;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;



public class StartFirebaseAtBoot extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        context.startService(new Intent(context, FirebaseBackgroundService.class));

        st.rollNumber = st.readRollNumber(context);
        if(st.isChecked("autoRenew",context)) st.scheduleLibraryAutoRenew(context);
        if(st.isChecked("autoVibrate",context)) st.scheduleClassNotification("classVibrate",context);
        if(st.isChecked("classNotification",context)) st.scheduleClassNotification("classNotification",context);
        if(st.isChecked("ctNot",context)) st.ctScheduleNotification("ctNot",context);
        if(st.isChecked("perScheNot",context)) st.ctScheduleNotification("perScheNot",context);
       // if(st.isChecked("salahNot",context)) ;


    }
}