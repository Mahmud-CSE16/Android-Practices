package repon.cse.kuetian;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.FirebaseDatabase;


import repon.cse.kuetian.refresh_view.PullToRefreshView;

public class ProfileActivity extends AppCompatActivity {

    public static ProfileActivity instance;
    User user = new User();
    EditText fName,nName,hometown,college,birthDay,bloodGroup,religion,mobile;
    PullToRefreshView mPullToRefreshView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_profile);

        instance = this;

        initialize();

        user = st.readUserData(getApplicationContext());
        loadUserData(user);
        createPullRefresh();
        Button updateButton = findViewById(R.id.btn_updateProfile);
        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateProfile();
            }
        });

    }

    private void initialize() {
        fName = findViewById(R.id.input_fullname);
        nName = findViewById(R.id.input_nickname);
        hometown = findViewById(R.id.input_hometown);
        college = findViewById(R.id.input_college);
        birthDay = findViewById(R.id.input_birthday);
        bloodGroup = findViewById(R.id.input_bloodgroup);
        religion = findViewById(R.id.input_Religion);
        mobile = findViewById(R.id.input_mobile);
    }

    private void createPullRefresh() {
        mPullToRefreshView = findViewById(R.id.profileRefresh);
        mPullToRefreshView.setOnRefreshListener(new PullToRefreshView.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Log.d(st.TAG, "onRefresh: ProfileActivity:Refreshing");
                st.downloadUserData(getApplicationContext());
                mPullToRefreshView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mPullToRefreshView.setRefreshing(false);
                    }
                }, 2000);
            }
        });
    }
    private void updateProfile() {
        user.un = fName.getText().toString();
        user.nn = nName.getText().toString();
        user.ht = hometown.getText().toString();
        user.cl = college.getText().toString();
        user.bd = birthDay.getText().toString();
        user.bg = bloodGroup.getText().toString();
        user.rl = religion.getText().toString();
        user.ph = mobile.getText().toString();
        st.writeUserData(user,getApplicationContext());
        String pathReference = st.rollNumber.substring(2,4)+"/"+st.rollNumber.substring(0,4)+"/user/"+st.rollNumber;
        FirebaseDatabase.getInstance().getReference(pathReference).setValue(user).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                st.makeText(ProfileActivity.this, "Profile successfully updated!", Toast.LENGTH_SHORT);
            }
        });
    }

    public void loadUserData(User user) {
        if(user.profile==null) return;
            fName.setText(user.un);
            nName.setText(user.nn);
            hometown.setText(user.ht);
            college.setText(user.cl);
            birthDay.setText(user.bd);
            bloodGroup.setText(user.bg);
            religion.setText(user.rl);
            mobile.setText(user.ph);
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
