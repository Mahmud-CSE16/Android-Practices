package repon.cse.kuetian;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ExpandableListView;
import android.support.v7.widget.SearchView;
import android.widget.Spinner;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import repon.cse.kuetian.refresh_view.PullToRefreshView;

public class TeachersActivity extends AppCompatActivity {

    SearchView searchView;
    Spinner deptSpinner;
    private static TeachersActivity instance;
    HashMap<String,List<String>> t = new HashMap<>();
    PullToRefreshView mPullToRefreshView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_teachers);

        instance = this;

        createSpinner();

        createPullRefresh();

        createSearch();


    }

    private void createSpinner() {
        st.initDeptList();

        deptSpinner = findViewById(R.id.deptSpinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,R.layout.dept_spinner_layout,R.id.deptSpinnerTextView,st.getDeptArr());
        deptSpinner.setAdapter(adapter);
        Log.d(st.TAG, "onCreate: "+st.findIdx(st.deptList.get(st.rollNumber.substring(2,4))));
        deptSpinner.setSelection(st.findIdx(st.deptList.get(st.rollNumber.substring(2,4))));
        deptSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedDept = deptSpinner.getSelectedItem().toString();
                Log.d(st.TAG, "onItemSelected: "+selectedDept);
                updateTeachers(selectedDept);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void createSearch() {
        searchView = findViewById(R.id.tSearchView);

        searchView.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchView.setBackgroundColor(0xFFFFFFFF);
            }
        });
        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                updateTeachersUI(initIndexList(t), t);
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

                ArrayList<Integer> tIndexList= new ArrayList<>();
                for(int i=0;i<t.get("tName").size();i++)
                {   Boolean flag = false;
                    if(t.get("tName").get(i).toLowerCase().contains(newText.toLowerCase())) flag=true;
                    else if(t.get("tDesg").get(i).toLowerCase().contains(newText.toLowerCase())) flag=true;
                    else if(t.get("tEmail").get(i).toLowerCase().contains(newText.toLowerCase())) flag=true;
                    else if(t.get("tContact").get(i).toLowerCase().contains(newText.toLowerCase())) flag=true;
                    else if(t.get("tWeb").get(i).toLowerCase().contains(newText.toLowerCase())) flag=true;
                    if(flag) {
                        tIndexList.add(i);
                    }
                }
                Log.d(st.TAG, "onQueryTextChange: "+t.get("tName").size());
                updateTeachersUI(tIndexList,t);

                return false;
            }
        });
    }

    private void createPullRefresh() {
        mPullToRefreshView = findViewById(R.id.pull_to_refresh);
        mPullToRefreshView.setOnRefreshListener(new PullToRefreshView.OnRefreshListener() {
            @Override
            public void onRefresh() {
                String selectedDept = deptSpinner.getSelectedItem().toString();
                new FacultyData(selectedDept,getApplicationContext()).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                mPullToRefreshView.setRefreshing(true);
              /*  mPullToRefreshView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mPullToRefreshView.setRefreshing(false);
                    }
                }, 2000);*/
            }
        });
    }


    private void updateTeachers(String dept) {
        t = st.readTeachers(TeachersActivity.this, dept);
        if(t !=null) updateTeachersUI(initIndexList(t), t);
        else {st.initDeptList(); new FacultyData(dept,getApplicationContext()).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);}

    }

    private ArrayList<Integer> initIndexList(HashMap<String, List<String>> t) {
        ArrayList<Integer> tIndexList = new ArrayList<>();
        for(int i=0; i<t.get("tName").size();i++)
            tIndexList.add(i);
        return tIndexList;
    }

    public void updateTeachersUI(ArrayList<Integer> tIndexList, HashMap<String, List<String>> tt) {
        ExpandableListView tELV = TeachersActivity.getInstance().findViewById(R.id.teachersExListView);
        TeachersCustomAdapter teachersCustomAdapter= new TeachersCustomAdapter(TeachersActivity.getInstance(), t, tIndexList);
        tELV.setAdapter(teachersCustomAdapter);
        Log.d(st.TAG, "updateClassmateUI: Hre4");

    }


    public static TeachersActivity getInstance() {
        return instance;
    }


    public class FacultyData extends AsyncTask<Void, Void, Void> {

        String urlFacultyMember;
        String title;

        ArrayList<String> name;
        Document docFacultyMember=null;

        String deptName;
        public List<String> tName = new ArrayList<>();
        public List<String> tDesg = new ArrayList<>();
        public List<String> tContact = new ArrayList<>();
        public List<String> tEmail = new ArrayList<>();
        public List<String> tWeb = new ArrayList<>();
        Context context;


        public FacultyData(String deptName, Context context) {
            this.deptName = deptName;
            this.context = context;
        }

        public String getTitle() {
            return title;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            urlFacultyMember = "http://www.kuet.ac.bd/department/"+deptName+"/index.php/welcome/facultymember";
            Log.d(st.TAG, "started "+urlFacultyMember);
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            Log.d(st.TAG, "FacultyData::onPostExecute");

            if(docFacultyMember!=null){
                title = docFacultyMember.title();
                Log.d(st.TAG, title);
                Elements elements = docFacultyMember.select("h6");
                Elements elementsHref = docFacultyMember.select("h5");
                int j=0;

                for (int i=0; i<elements.size()-3;i++) {
                    tName.add(elements.get(i++).text().toString());
                    tDesg.add(elements.get(i++).text().toString());
                    tContact.add(elements.get(i++).text().toString());
                    tEmail.add(elements.get(i).text().toString().replace("AT","@"));
                    tWeb.add(elementsHref.get(j++).select("a").attr("abs:href"));
                }
                if(t==null) t = new HashMap<>();
                t.put("tName",tName);
                t.put("tDesg",tDesg);
                t.put("tContact",tContact);
                t.put("tEmail",tEmail);
                t.put("tWeb",tWeb);
                st.writeTeachers(t, context, deptName);
                if(TeachersActivity.getInstance()!=null) updateTeachersUI(initIndexList(t), t);

                st.readTeachers(context, st.deptList.get(st.rollNumber.substring(2, 4)));
                mPullToRefreshView.setRefreshing(false);
                Log.d(st.TAG, "onPostExecute: ");
            }
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                Log.d(st.TAG, "FacultyData::doInBackground");

                // Parsing Faculty Data
                docFacultyMember = Jsoup.connect(urlFacultyMember).get();

            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
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
