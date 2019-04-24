package repon.cse.kuetian;

import android.content.Context;
import android.content.Intent;
import android.database.DataSetObserver;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static repon.cse.kuetian.st.TAG;

class TeachersCustomAdapter extends BaseExpandableListAdapter {
    Context context;
    HashMap<String, List<String>> t ;
    ArrayList<Integer> tIndexList;

    public TeachersCustomAdapter(Context context, HashMap<String, List<String>> t, ArrayList<Integer> tIndexList) {
        this.context = context;
        this.t = t;
        this.tIndexList = tIndexList;
    }


    @Override
    public int getGroupCount() {
        return tIndexList.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return 1;
    }

    @Override
    public Object getGroup(int groupPosition) {
        return t.get("tName").get(tIndexList.get(groupPosition));
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return t.get("tEmail").get(tIndexList.get(groupPosition));
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

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        String tName = t.get("tName").get(tIndexList.get(groupPosition));
        String tDesg = t.get("tDesg").get(tIndexList.get(groupPosition));

        if(convertView == null)
        {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.teachers_head_layout,null);
        }

        if(groupPosition%3==0) convertView.setBackgroundColor(0xff9320ff);
        else if(groupPosition%3==1) convertView.setBackgroundColor(0xff008ad4);
        else if (groupPosition%3==2) convertView.setBackgroundColor(0xff01a7a0);

        TextView tNameTextView = convertView.findViewById(R.id.tNameTextView);
        TextView tDesgTextView = convertView.findViewById(R.id.tDesgTextView);
        tNameTextView.setText(tName);
        tDesgTextView.setText(tDesg);

        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {

        String tEmail = t.get("tEmail").get(tIndexList.get(groupPosition));
        String tContact = t.get("tContact").get(tIndexList.get(groupPosition));
        final String tWeb = t.get("tWeb").get(tIndexList.get(groupPosition));


        if(convertView == null)
        {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.teachers_child_layout,null);
        }

        if(groupPosition%3==0) convertView.setBackgroundColor(0xff5903a9);
        else if(groupPosition%3==1) convertView.setBackgroundColor(0xff036fa9);
        else if (groupPosition%3==2) convertView.setBackgroundColor(0xff01847e);
        TextView tEmailTextView = convertView.findViewById(R.id.tEmailTextView);
        TextView tContactTextView = convertView.findViewById(R.id.tContactTextView);
        TextView tWebTextView = convertView.findViewById(R.id.tWebTextView);

        tEmailTextView.setText(tEmail);
        tContactTextView.setText(tContact);
        tWebTextView.setText(tWeb);

        tEmailTextView.setPaintFlags(View.INVISIBLE);
        tContactTextView.setPaintFlags(View.INVISIBLE);
        tWebTextView.setPaintFlags(View.INVISIBLE);
        tWebTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, WEB.class);
                intent.putExtra("key","browse");
                intent.putExtra("url",tWeb);
                context.startActivity(intent, null);
            }
        });

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }
}
