package repon.cse.kuetian;


import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import repon.cse.kuetian.refresh_view.PullToRefreshView;



public class MainActivity extends AppCompatActivity implements View.OnClickListener{


    TextView  userName, deptText, classTitle;
    LinearLayout classLinearLayout;
    TableLayout classTableLayout;
    EditText noteEditText;
    Dialog dialog;
    FloatingActionButton menuOpenFAB;

    private static final int REQUEST_LOGIN = 0;

    static MainActivity instance;



    public boolean isActive;
    static Context context;
    PullToRefreshView mPullToRefreshView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_main);

        instance = this;
        context = getApplicationContext();
        isActive = true;

        Log.d(st.TAG, "onViewCreated");



        initializeLayout();
        checkValidity();
        createPullRefresh();

        if(FirebaseAuth.getInstance().getCurrentUser()!=null)
            startService(new Intent(this, FirebaseBackgroundService.class));
    }

    private void checkValidity() {

        FirebaseDatabase.getInstance().getReference("verSS").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String ver = dataSnapshot.getValue().toString();

                if(ver.equals("strictly disabled"))
                {
                    st.makeText(MainActivity.this, ver, Toast.LENGTH_LONG);
                    finish();
                }
                else if (!ver.equals("en")) {
                    final String[] msg = ver.split("___");

                   // st.makeText(MainActivity.this, msg[1], Toast.LENGTH_LONG);

                    final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setTitle(msg[0]);
                    builder.setMessage(msg[1]);
                    builder.setIcon(R.drawable.kuetian_app_logo);
                    builder.setPositiveButton("Download", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(getApplicationContext(), WEB.class);
                            intent.putExtra("key","browse");
                            intent.putExtra("url",msg[2]);
                            startActivity(intent, null);
                            overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                            dialog.cancel();
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
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

        private void createPullRefresh() {
        mPullToRefreshView = findViewById(R.id.mainRefresh);
        mPullToRefreshView.setOnRefreshListener(new PullToRefreshView.OnRefreshListener() {
            @Override
            public void onRefresh() {

                Log.d(st.TAG, "onRefresh: MainActivity:Refreshing");
                st.downloadFlags(getApplicationContext());
                HashMap<String, String> set = st.readUserSettings(getApplicationContext());
                if (set==null||set.get("salahSwitch") == null || set.get("salahSwitch").equals("enabled"))  updateSalahCentral("refresh");                mPullToRefreshView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mPullToRefreshView.setRefreshing(false);
                    }
                }, 2000);
            }
        });
    }

    public static MainActivity getInstance() {
        return instance;
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(st.TAG, "onStart");
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if(currentUser==null)
        {
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            startActivityForResult(intent, REQUEST_LOGIN);
            overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
        }
        else  updateUI();
        classLinearLayout.setVisibility(View.VISIBLE);
        checkSalahMenu();
    }

    private void checkSalahMenu() {
        LinearLayout mainSalahLinearLayout = findViewById(R.id.mainSalahLinearLayout);
        HashMap<String, String> settings = st.readUserSettings(getApplicationContext());
        if (settings!=null&&settings.get("salahSwitch") != null && settings.get("salahSwitch").equals("disabled"))
            mainSalahLinearLayout.setVisibility(View.GONE);
        else mainSalahLinearLayout.setVisibility(View.VISIBLE);
    }

    public void updateUI(){
        st.rollNumber = st.readRollNumber(getApplicationContext());
        if(st.rollNumber==null) st.downloadRoll(getApplicationContext());

        st.downloadFlags(getApplicationContext());
        checkCRShip("normal",getApplicationContext());
        updateUserData();
        updateRoutine();
        updateCT();
        noteEditText.setText(st.readNoteData(getApplicationContext()));
    }

    public static void checkCRShip(String key, final Context context) {
        st.CRList = st.readCRList(context);
        final String pathReference = st.rollNumber.substring(2,4)+"/"+st.rollNumber.substring(0,4)+"/CRList";

        if(st.CRList.isEmpty()||key.equals("refresh"))
        {
            st.CRList = "001,061";
            FirebaseDatabase.getInstance().getReference(pathReference).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        st.CRList = (String) dataSnapshot.getValue();
                        st.isCR  = st.CRList.contains(st.rollNumber.substring(4,7));
                        st.writeCRList(context);
                        st.makeText(context, "CR List updated successfully!", Toast.LENGTH_SHORT);
                    } else {
                        st.writeCRList(context);
                        FirebaseDatabase.getInstance().getReference(pathReference).setValue(st.CRList);
                        Log.d(st.TAG, "Data Loading failed");
                        st.makeText(context, "Data Loading failed!", Toast.LENGTH_SHORT);
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.d(st.TAG, "Data Loading Cancelled: "+error);;
                    st.makeText(context, "Data Loading Cancelled!", Toast.LENGTH_SHORT);
                }

            });
        }



        st.isCR = st.CRList.contains(st.rollNumber.substring(4,7));
    }


    private void updateCT() {
        ClassTest.ct = st.readCTData(getApplicationContext());
        if(ClassTest.ct != null) ClassTest.updateMainCTData(getApplicationContext());
        else st.downloadCT(getApplicationContext());
        ClassTest.ct = st.readScheduleData(getApplicationContext());
        if(ClassTest.ct != null) ClassTest.updateMainScheduleData(getApplicationContext());
    }

    public void showMenu()  {
        dialog = new Dialog(MainActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.fragment_first_menu);
        //dialog.getWindow().setBackgroundDrawableResource();
        dialog.getWindow().getDecorView().getBackground().setColorFilter(0x005129a9, PorterDuff.Mode.SRC);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        Window window = dialog.getWindow();
        lp.copyFrom(window.getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        window.setAttributes(lp);

        Button profileButton = dialog.findViewById(R.id.profileButton);
        Button routineButton = dialog.findViewById(R.id.routineButton);
        Button ctButton = dialog.findViewById(R.id.ctButton);
        Button scheduleButton = dialog.findViewById(R.id.scheduleButton);
        Button classmatesButton = dialog.findViewById(R.id.classmatesButton);
        Button teachersButton = dialog.findViewById(R.id.teachersButton);
        Button groupsButton = dialog.findViewById(R.id.groupsButton);
        Button salahTimesButton = dialog.findViewById(R.id.salahTimesButton);
        Button signoutButton = dialog.findViewById(R.id.signoutButton);
        Button hallButton = dialog.findViewById(R.id.hallButton);
        Button academicButton = dialog.findViewById(R.id.academicButton);
        Button libraryButton = dialog.findViewById(R.id.libraryButton);
        Button automationButton = dialog.findViewById(R.id.automationButton);
        Button kuetWebButton = dialog.findViewById(R.id.kuetWebButton);
        Button performanceButton = dialog.findViewById(R.id.performanceButton);
        Button busesButton = dialog.findViewById(R.id.busesButton);
        Button settingsButton = dialog.findViewById(R.id.settingsButton);
        Button aboutButton = dialog.findViewById(R.id.aboutButton);
        FloatingActionButton menucloseFAB = dialog.findViewById(R.id.menuclosefab);

        hallButton.setOnClickListener(this);
        academicButton.setOnClickListener(this);
        libraryButton.setOnClickListener(this);
        automationButton.setOnClickListener(this);
        kuetWebButton.setOnClickListener(this);
        performanceButton.setOnClickListener(this);
        busesButton.setOnClickListener(this);
        settingsButton.setOnClickListener(this);
        aboutButton.setOnClickListener(this);

        profileButton.setOnClickListener(this);
        routineButton.setOnClickListener(this);
        ctButton.setOnClickListener(this);
        scheduleButton.setOnClickListener(this);
        classmatesButton.setOnClickListener(this);
        teachersButton.setOnClickListener(this);
        groupsButton.setOnClickListener(this);
        salahTimesButton.setOnClickListener(this);
        signoutButton.setOnClickListener(this);
        menucloseFAB.setOnClickListener(this);

        dialog.show();
        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                menuOpenFAB.setVisibility(View.VISIBLE);
            }
        });
    }

    public void updateRoutine(){

        HashMap<String, HashMap<String, HashMap<String, String>>> days = st.readRoutineData(getApplicationContext());

        if(days == null)
        {
            st.downloadRoutine(getApplicationContext());
            return;
        }

        Log.d(st.TAG, "on updateRoutine");
        int dayNum=Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
        if(dayNum>5)
        {
            TableRow subRow = (TableRow)classTableLayout.getChildAt(2);
            TextView subTextView = (TextView) subRow.getChildAt(0);
            if(dayNum==6) subTextView.setText("It's FRIDAY");
            if(dayNum==7) subTextView.setText("It's SATURDAY");
            subTextView.setTextSize(15);
            TableRow.LayoutParams param = new TableRow.LayoutParams(0, TableRow.LayoutParams.MATCH_PARENT, 3.0f);
            subTextView.setLayoutParams(param);
            subTextView.setBackground(null);
            subTextView.setVisibility(View.VISIBLE);

            return;
        }
        HashMap<String,HashMap<String,String> > day = days.get("day"+dayNum);

        int count = 0;
        for(int i=0;i<=5;i++)
        {
            TableRow subRow = (TableRow)classTableLayout.getChildAt(i);
            TableRow teacherRow = (TableRow)classTableLayout.getChildAt(++i);


            HashMap<String,String> subject = day.get("subject"),teacher = day.get("teacher");

            for(int j=0;j<=2;j++){
                count++;
                TextView subTextView = (TextView) subRow.getChildAt(j);
                TextView teacherTextView = (TextView) teacherRow.getChildAt(j);

                subTextView.setText(subject.get("period"+count));
                teacherTextView.setText(teacher.get("period"+count));
                TableRow.LayoutParams param = new TableRow.LayoutParams(0,TableRow.LayoutParams.MATCH_PARENT,1.0f);
                subTextView.setLayoutParams(param);
                teacherTextView.setLayoutParams(param);
                subTextView.setVisibility(View.VISIBLE);
                teacherTextView.setVisibility(View.VISIBLE);

                String sub = subject.get("period"+count);
                if(sub.toLowerCase().contains("lab"))
                {
                    param = new TableRow.LayoutParams(0,TableRow.LayoutParams.MATCH_PARENT,3.0f);
                    subTextView.setLayoutParams(param);
                    teacherTextView.setLayoutParams(param);

                    j++; count++;
                    subTextView = (TextView) subRow.getChildAt(j);
                    teacherTextView = (TextView) teacherRow.getChildAt(j);
                    subTextView.setVisibility(View.GONE);
                    teacherTextView.setVisibility(View.GONE);

                    j++; count++;
                    subTextView = (TextView) subRow.getChildAt(j);
                    teacherTextView = (TextView) teacherRow.getChildAt(j);
                    subTextView.setVisibility(View.GONE);
                    teacherTextView.setVisibility(View.GONE);
                }
                else if(sub.equals(""))
                {
                    subTextView.setVisibility(View.INVISIBLE);
                    teacherTextView.setVisibility(View.INVISIBLE);
                }
            }
        }
    }

    public void updateUserData() {
        User newUser = st.readUserData(getApplicationContext());
        if(newUser == null)
        {
            Log.d(st.TAG, "Please update database");
            st.downloadUserData(getApplicationContext());
            return;
        }
        userName.setText(newUser.un);
        deptText.setText(newUser.dp+", 2K"+st.rollNumber.substring(0,2));
    }

    @Override
    public void onStop() {
        super.onStop();
        st.writeNoteData(noteEditText.getText().toString(),getApplicationContext());
        isActive = false;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_LOGIN) {
            if (resultCode == RESULT_OK) {

                // TODO: Implement successful signup logic here
                // By default we just finish the Activity and log them in automatically

               if(FirebaseBackgroundService.instance!=null) FirebaseBackgroundService.instance.initialize();
               recreate();
            }
        }
    }

    @Override
    public void onClick(View v) {

        if(v.getId()==R.id.routineRefreshId)
        {
            st.downloadRoutine(getApplicationContext());
        }
        else if(v.getId()==R.id.routineLaunchId)
        {
            Intent intent = new Intent(getApplicationContext(), CRActivity.class);
            intent.putExtra("key","routine");
            startActivity(intent);
            overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);

        }
        else if(v.getId()==R.id.ctLaunchId)
        {
            Intent intent = new Intent(getApplicationContext(), ClassTest.class);
            intent.putExtra("key","CT");
            startActivity(intent, null);
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
        }
        else if(v.getId()==R.id.scheduleLaunchId)
        {
            Intent intent = new Intent(getApplicationContext(), ClassTest.class);
            intent.putExtra("key","Schedule");
            startActivity(intent, null);
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
        }
        else if(v.getId()==R.id.ctRefreshId)
        {
            st.downloadCT(getApplicationContext());
        }
        else if(v.getId()==R.id.scheduleRefreshId)
        {
            //TODO:
        }
        else if(v.getId()==R.id.profilePhotoId)
        {
            Intent intent = new Intent(getApplicationContext(), ProfileActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);

        }
        else if(v.getId()==R.id.classLinearLayoutId)
        {
            Intent intent = new Intent(getApplicationContext(), CRActivity.class);
            intent.putExtra("key","routine");
            startActivity(intent);
            overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);

        }
        else switch (v.getId()) {
            case R.id.profileButton:
                Intent intent = new Intent(getApplicationContext(), ProfileActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);

                dialog.cancel();
                break;
            case R.id.routineButton:
                intent = new Intent(getApplicationContext(), CRActivity.class);
                intent.putExtra("key","routine");
                startActivity(intent);
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);

                dialog.cancel();
                break;
            case R.id.ctButton:
                intent = new Intent(getApplicationContext(), ClassTest.class);
                intent.putExtra("key","CT");
                startActivity(intent, null);
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                dialog.cancel();
                break;
            case R.id.scheduleButton:
                intent = new Intent(getApplicationContext(), ClassTest.class);
                intent.putExtra("key","Schedule");
                startActivity(intent, null);
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                dialog.cancel();
                break;
            case R.id.classmatesButton:
                intent = new Intent(getApplicationContext(), ClassmateActivity.class);
                startActivity(intent, null);
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                 dialog.cancel();
                break;
            case R.id.teachersButton:
                intent = new Intent(getApplicationContext(), TeachersActivity.class);
                startActivity(intent, null);
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                dialog.cancel();
                break;
            case R.id.groupsButton:
                if(st.readUserSettings(getApplicationContext())!=null){
                String link = st.readUserSettings(getApplicationContext()).get("groupLink");
                if(link!=null&&!link.isEmpty())
                {
                    intent = new Intent(getApplicationContext(), WEB.class);
                    intent.putExtra("key","browse");
                    intent.putExtra("url",link);
                    startActivity(intent, null);
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                }
                else st.makeText(getApplicationContext(), "Please set up your FB group link from setting.", Toast.LENGTH_LONG);
                }
                else st.makeText(getApplicationContext(), "Please set up your FB group link from setting.", Toast.LENGTH_LONG);
                dialog.cancel();
                break;
            case R.id.salahTimesButton:

                st.makeText(getApplicationContext(), "Upcoming feature", Toast.LENGTH_SHORT);
                dialog.cancel();
                break;
            case R.id.signoutButton:
                FirebaseAuth.getInstance().signOut();
                recreate();
                dialog.cancel();
                break;
            case R.id.hallButton:
                HashMap<String, String> settings = st.readUserSettings(getApplicationContext());

                    if(settings!=null && settings.get("hallSwitch").equals("enabled"))
                    {
                        String hallName = settings.get("hall");
                        String hallId =  settings.get("hallId");
                        String hallPassword =  settings.get("hallPass");
                        intent = new Intent(getApplicationContext(), WEB.class);
                        intent.putExtra("key","hallLogin");
                        intent.putExtra("url","http://portal.kuet.ac.bd/"+hallName+"/Student/frmStudentMealOnOff.aspx");
                        intent.putExtra("id",hallId);
                        intent.putExtra("password",hallPassword);
                        startActivity(intent, null);
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                    }
                    else st.makeText(this, "Please turn on Hall Auto Login from Settings", Toast.LENGTH_LONG);
                        
                    
                dialog.cancel();
                break;
            case R.id.academicButton:
                settings = st.readUserSettings(getApplicationContext());
                if(settings!=null && settings.get("acaSwitch").equals("enabled"))
                {
                    String academicId =  settings.get("acaId");
                    String academicPassword =  settings.get("acaPass");
                    intent = new Intent(getApplicationContext(), WEB.class);
                    intent.putExtra("key","acaLogin");
                    intent.putExtra("url","https://academic.kuet.ac.bd/results.php");
                    intent.putExtra("id",academicId);
                    intent.putExtra("password",academicPassword);
                    startActivity(intent, null);
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                }
                else st.makeText(this, "Please turn on Academic Auto Login from Settings", Toast.LENGTH_LONG);
                dialog.cancel();
                break;
            case R.id.libraryButton:
                settings = st.readUserSettings(getApplicationContext());
                if(settings!=null && settings.get("librarySwitch").equals("enabled"))
                {
                    String libraryId =  settings.get("libraryId");
                    String libraryPassword =  settings.get("libraryPass");
                    intent = new Intent(getApplicationContext(), WEB.class);
                    intent.putExtra("key","libraryLogin");
                    intent.putExtra("url","http://library.kuet.ac.bd:8000/cgi-bin/koha/opac-user.pl");
                    intent.putExtra("id",libraryId);
                    intent.putExtra("password",libraryPassword);
                    startActivity(intent, null);
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                }
                else st.makeText(this, "Please turn on Library Auto Login from Settings", Toast.LENGTH_LONG);
                dialog.cancel();
                break;
            case R.id.automationButton:
                intent = new Intent(getApplicationContext(), WEB.class);
                intent.putExtra("key","automation");
                intent.putExtra("url","http://www.kuet.ac.bd/index.php/welcome/automation");
                startActivity(intent, null);
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                dialog.cancel();
                break;
                case R.id.kuetWebButton:
                intent = new Intent(getApplicationContext(), WEB.class);
                intent.putExtra("key","browse");
                intent.putExtra("url","http://www.kuet.ac.bd/");
                startActivity(intent, null);
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                dialog.cancel();
                break;
            case R.id.performanceButton:
                st.makeText(getApplicationContext(), "Upcoming feature!", Toast.LENGTH_SHORT);
                dialog.cancel();
                break;
            case R.id.busesButton:
                intent = new Intent(getApplicationContext(), WEB.class);
                intent.putExtra("key","browse");
                intent.putExtra("url","http://www.kuet.ac.bd/index.php/welcome/transportation");
                startActivity(intent, null);
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                dialog.cancel();
                break;
            case R.id.settingsButton:
                intent = new Intent(getApplicationContext(), SettingActivity.class);
                startActivity(intent, null);
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                dialog.cancel();
                break;
            case R.id.aboutButton:
                dialog.cancel();
                showAbout();
                break;
            case R.id.menuclosefab:
                menuOpenFAB.setVisibility(View.VISIBLE);
                dialog.cancel();
                break;
            case R.id.menuopenfab:
                showMenu();
                menuOpenFAB.setVisibility(View.INVISIBLE);
                break;
            case R.id.weatherButton:
                intent = new Intent(getApplicationContext(), WeatherActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                break;
                case R.id.locationButton:
                    intent = new Intent(getApplicationContext(), LocationActivity.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                    break;
                case R.id.browserButton:
                    intent = new Intent(getApplicationContext(), BrowserActivity.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                    break;
                case R.id.blogButton:
                    intent = new Intent(getApplicationContext(), BlogLoginActivity.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                    break;

        }
    }

    private void showAbout() {
        final Dialog aboutDialog = new Dialog(MainActivity.this);
        aboutDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        aboutDialog.setContentView(R.layout.dialog_about);
        aboutDialog.getWindow().getDecorView().getBackground().setColorFilter(0x005129a9, PorterDuff.Mode.SRC);
        aboutDialog.show();
    }

    public void initializeLayout() {
        TextView welcomeText = findViewById(R.id.welcomeTextId);
        userName = findViewById(R.id.userNameId);
        deptText = findViewById(R.id.deptTextId);
        TextView dateText = findViewById(R.id.dateTextId);
        noteEditText = findViewById(R.id.noteEditTextId);
        menuOpenFAB = findViewById(R.id.menuopenfab);


        classTableLayout = findViewById(R.id.classTableLayout);
        ImageView routineLaunchImageView = findViewById(R.id.routineLaunchId);
        ImageView routineRefreshImageView = findViewById(R.id.routineRefreshId);
        ImageView ctLaunchImageView = findViewById(R.id.ctLaunchId);
        ImageView ctRefreshImageView = findViewById(R.id.ctRefreshId);
        ImageView scheduleLaunchImageView = findViewById(R.id.scheduleLaunchId);
        ImageView scheduleRefreshImageView = findViewById(R.id.scheduleRefreshId);
        ImageView photoImageView = findViewById(R.id.profilePhotoId);
        Button weatherButton = findViewById(R.id.weatherButton);
        Button locationButton = findViewById(R.id.locationButton);
        Button blogButton = findViewById(R.id.blogButton);
        Button browserButton = findViewById(R.id.browserButton);


        weatherButton.setOnClickListener(this);
        locationButton.setOnClickListener(this);
        blogButton.setOnClickListener(this);
        browserButton.setOnClickListener(this);

        classLinearLayout = findViewById(R.id.classLinearLayoutId);
        classTitle = findViewById(R.id.classTitleId);
        classLinearLayout.setOnClickListener(this);
        classTitle.setOnClickListener(this);
        routineLaunchImageView.setOnClickListener(this);
        routineRefreshImageView.setOnClickListener(this);
        ctLaunchImageView.setOnClickListener(this);
        ctRefreshImageView.setOnClickListener(this);
        scheduleLaunchImageView.setOnClickListener(this);
        scheduleRefreshImageView.setOnClickListener(this);
        photoImageView.setOnClickListener(this);
        menuOpenFAB.setOnClickListener(this);

        String date= new SimpleDateFormat("EEEE").format(new Date())+"\n"+new SimpleDateFormat("dd MMM").format(new Date())+"\n"+new SimpleDateFormat("yyyy").format(new Date());
        dateText.setText(date);
        Calendar mcurrentTime = Calendar.getInstance();
        int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
        if(hour<=4) welcomeText.setText("Good night!");
        else if(hour<12) welcomeText.setText("Good morning!");
        else if(hour<15) welcomeText.setText("Good noon!");
        else if(hour<18) welcomeText.setText("Good afternoon!");
        else if(hour<20) welcomeText.setText("Good evening!");
        else welcomeText.setText("Good night!");
        HashMap<String, String> set = st.readUserSettings(getApplicationContext());
        if (set==null||set.get("salahSwitch") == null || set.get("salahSwitch").equals("enabled"))  updateSalahCentral("normal");
    }
    public void updateSalahCentral(String key)
    {
        final TextView salahTime0 = findViewById(R.id.salahTime0);
        final TextView salahTime1 = findViewById(R.id.salahTime1);
        final TextView salahTime2 = findViewById(R.id.salahTime2);
        final TextView salahTime3 = findViewById(R.id.salahTime3);
        final TextView salahTime4 = findViewById(R.id.salahTime4);
        String ver = st.readSalahCentral(getApplicationContext());
        if(!ver.isEmpty()) {
            String[] sC = ver.split(";");
            salahTime0.setText(sC[0]);
            salahTime1.setText(sC[1]);
            salahTime2.setText(sC[2]);
            salahTime3.setText(sC[3]);
            salahTime4.setText(sC[4]);
        }
        if (key.equals("refresh")||ver.isEmpty()||(Integer.parseInt(ver.split(";")[5]) + 3 < new DatePicker(context).getDayOfMonth())) {
            FirebaseDatabase.getInstance().getReference("salahCentral").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    String ver = dataSnapshot.getValue().toString();
                    st.writeSalahCentral(ver + ";" + new DatePicker(context).getDayOfMonth(), getApplicationContext());
                    String[] sC = ver.split(";");
                    salahTime0.setText(sC[0]);
                    salahTime1.setText(sC[1]);
                    salahTime2.setText(sC[2]);
                    salahTime3.setText(sC[3]);
                    salahTime4.setText(sC[4]);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            });
        }
    }


}
