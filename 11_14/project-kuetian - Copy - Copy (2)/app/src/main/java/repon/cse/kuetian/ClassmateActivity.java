package repon.cse.kuetian;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ExpandableListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeSet;

import repon.cse.kuetian.refresh_view.PullToRefreshView;

public class ClassmateActivity extends AppCompatActivity {

    SearchView searchView;
    Spinner deptSpinner;
    public static ClassmateActivity instance;
    HashMap<String ,User> users = new HashMap<>();
    PullToRefreshView mPullToRefreshView;
    ArrayList<String> idx = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_classmate);

        instance = this;

        TextView textView = findViewById(R.id.tDeptTextView);
        TextView dep = MainActivity.instance.findViewById(R.id.deptTextId);
        textView.setText(dep.getText().toString());
        createPullRefresh();

        updateClassmate();
        createSearch();


    }


    private void createSearch() {
        searchView = findViewById(R.id.cmSearchView);

        searchView.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchView.setBackgroundColor(0xFFFFFFFF);
                users = st.readAllUser(ClassmateActivity.this);
                if (users != null) idx = initIndexList(users);
            }
        });
        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                updateClassmateUI(initIndexList(users), users);
                searchView.setBackgroundColor(0x00FFFFFF);
                return false;
            }
        });
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {


                ArrayList<String> id = new ArrayList<>();
                for (int i = 0; i < idx.size(); i++) {
                    User user = users.get(idx.get(i));
                    Boolean flag = false;
                    if (user.un!=null && user.un.toLowerCase().contains(newText.toLowerCase())) flag = true;
                    else if (idx.get(i)!=null && idx.get(i).substring(4,7).contains(newText)) flag = true;
                    else if (user.ph!=null && user.ph.toLowerCase().contains(newText.toLowerCase())) flag = true;
                    else if (user.ml!=null && user.ml.toLowerCase().contains(newText.toLowerCase())) flag = true;
                    else if (user.nn!=null && user.nn.toLowerCase().contains(newText.toLowerCase())) flag = true;
                    else if (user.ht!=null && user.ht.toLowerCase().contains(newText.toLowerCase())) flag = true;
                    else if (user.cl!=null && user.cl.toLowerCase().contains(newText.toLowerCase())) flag = true;
                    else if (user.bd!=null && user.bd.toLowerCase().contains(newText.toLowerCase())) flag = true;
                    else if (user.bg!=null && user.bg.toLowerCase().contains(newText.toLowerCase())) flag = true;
                    else if (user.dp!=null && user.dp.toLowerCase().contains(newText.toLowerCase())) flag = true;
                    else if (user.rl!=null && user.rl.toLowerCase().contains(newText.toLowerCase())) flag = true;
                    if (flag) {
                        id.add(idx.get(i));
                    }
                }
                updateClassmateUI(id, users);
                return false;
            }
        });
    }

    public static void downloadAllUser(final Context context) {
        Log.d(st.TAG, "downloadUserData");

        String pathReference = st.rollNumber.substring(2, 4) + "/" + st.rollNumber.substring(0, 4) + "/user";

        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference(pathReference);

        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    HashMap<String, User> users = (HashMap<String, User>) dataSnapshot.getValue();
                    st.writeAllUser(users, context);
                    if (ClassmateActivity.instance != null)
                        ClassmateActivity.instance.updateClassmate();
                } else {
                    Log.d(st.TAG, "Data Loading failed");
                    st.makeText(context, "Data Loading failed", Toast.LENGTH_SHORT);
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.d(st.TAG, "Data Loading failed");
                st.makeText(context, "Data Loading failed", Toast.LENGTH_SHORT);
            }
        });
    }


    private void createPullRefresh() {
        mPullToRefreshView = findViewById(R.id.pull_to_refresh);
        mPullToRefreshView.setOnRefreshListener(new PullToRefreshView.OnRefreshListener() {
            @Override
            public void onRefresh() {
                downloadAllUser(ClassmateActivity.this);
                mPullToRefreshView.setRefreshing(true);
                mPullToRefreshView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mPullToRefreshView.setRefreshing(false);
                    }
                }, 2000);
            }
        });
    }


    private void updateClassmate() {
        users = st.readAllUser(ClassmateActivity.this);
        if (users != null) updateClassmateUI(initIndexList(users), users);
        else {
            downloadAllUser(ClassmateActivity.this);
        }

    }

    private ArrayList<String> initIndexList(HashMap<String, User> users) {
        ArrayList<String> cmIndexList = new ArrayList<>();
        cmIndexList.addAll(new TreeSet(users.keySet()));
        return cmIndexList;
    }

    public void updateClassmateUI(ArrayList<String> cmIndexList, HashMap<String, User> users) {
        ExpandableListView tELV = ClassmateActivity.getInstance().findViewById(R.id.classmateExListView);
        ClassmateCustomAdapter classmateCustomAdapter= new ClassmateCustomAdapter(ClassmateActivity.instance, users, cmIndexList);
        tELV.setAdapter(classmateCustomAdapter);
        Log.d(st.TAG, "updateClassmateUI: Hre4");

    }


    private static ClassmateActivity getInstance() {
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
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {

            case 123:
                if ((grantResults.length > 0) && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                } else {
                    Log.d("TAG", "Call Permission Not Granted");
                }
                break;

            default:
                break;
        }
    }
    public void onCall(String number) {
        int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE);

        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{Manifest.permission.CALL_PHONE},
                    123);
        } else {
            startActivity(new Intent(Intent.ACTION_CALL).setData(Uri.parse("tel:"+number)));
        }
    }
}


