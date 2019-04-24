package repon.cse.kuetian;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

class ClassmateCustomAdapter extends BaseExpandableListAdapter {
    Context context;
    HashMap<String, User> t;
    ArrayList<String> uIndexList;

    public ClassmateCustomAdapter(Context context, HashMap<String, User> t, ArrayList<String> tIndexList) {
        this.context = context;
        this.t = t;
        this.uIndexList = tIndexList;
    }

    @Override
    public int getGroupCount() {
        return uIndexList.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return 1;
    }

    @Override
    public Object getGroup(int groupPosition) {
        return 0;
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return 0;
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @SuppressLint("MissingPermission")
    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        final User user = t.get(uIndexList.get(groupPosition));

        if(convertView == null)
        {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.classmate_head_layout,null);
        }

        if(groupPosition%3==0) convertView.setBackgroundColor(0xff9320ff);
        else if(groupPosition%3==1) convertView.setBackgroundColor(0xff008ad4);
        else if (groupPosition%3==2) convertView.setBackgroundColor(0xff01a7a0);

        TextView cmRollTextView = convertView.findViewById(R.id.cmRollTextView);
        TextView cmNameTextView = convertView.findViewById(R.id.cmNameTextView);
        TextView cmCall = convertView.findViewById(R.id.cmCall);
        TextView cmMessage = convertView.findViewById(R.id.cmMessage);
        TextView cmMail = convertView.findViewById(R.id.cmMail);
        cmRollTextView.setText(uIndexList.get(groupPosition).substring(4,7));
        cmNameTextView.setText(user.un);

        cmCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               ClassmateActivity.instance.onCall(user.ph);
            }
        });
        cmMessage.setText(user.ph);
        cmMail.setText(user.ml);

        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {

        User user = t.get(uIndexList.get(groupPosition));

        if(convertView == null)
        {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.classmate_child_layout,null);
        }

        if(groupPosition%3==0) convertView.setBackgroundColor(0xff9320ff);
        else if(groupPosition%3==1) convertView.setBackgroundColor(0xff008ad4);
        else if (groupPosition%3==2) convertView.setBackgroundColor(0xff01a7a0);

        TextView cmNickNameTextView = convertView.findViewById(R.id.cmNickNameTextView);
        TextView cmHomeTownTextView = convertView.findViewById(R.id.cmHomeTownTextView);
        TextView cmCollegeTextView = convertView.findViewById(R.id.cmCollegeTextView);
        TextView cmBDTextView = convertView.findViewById(R.id.cmBDTextView);
        TextView cmBGTextView = convertView.findViewById(R.id.cmBGTextView);
        TextView cmCallTextView = convertView.findViewById(R.id.cmCallTextView);
        TextView cmEmailTextView = convertView.findViewById(R.id.cmEmailTextView);

        cmNickNameTextView.setText(user.nn);
        cmHomeTownTextView.setText(user.ht);
        cmCollegeTextView.setText(user.cl);
        cmBDTextView.setText(user.bd);
        cmBGTextView.setText(user.bg);
        cmCallTextView.setText(user.ph);
        if(cmCallTextView != null){
            Linkify.addLinks(cmCallTextView, Patterns.PHONE,"tel:",Linkify.sPhoneNumberMatchFilter,Linkify.sPhoneNumberTransformFilter);
            cmCallTextView.setMovementMethod(LinkMovementMethod.getInstance());
        }
        cmCallTextView.setPaintFlags(View.INVISIBLE);

        cmEmailTextView.setText(user.ml);
        cmEmailTextView.setPaintFlags(View.INVISIBLE);

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }
}
