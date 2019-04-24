package repon.cse.kuetian;

import android.content.Context;
import android.graphics.Paint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;



class CTCustomAdapter extends BaseExpandableListAdapter {
    Context context;
    ClassTestObject ct ;

    public CTCustomAdapter(Context context, ClassTestObject ct) {
        this.context = context;
        this.ct = ct;
    }

    @Override
    public int getGroupCount() {
        return ct.rawDateTimeList.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return 1;
    }

    @Override
    public Object getGroup(int groupPosition) {
        return ct.titleMap.get(ct.rawDateTimeList.get(groupPosition));
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return ct.descriptionMap.get(ct.rawDateTimeList.get(groupPosition));
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
        String headerText = ct.dateTimeMap.get(ct.rawDateTimeList.get(groupPosition));
        String headerTitleText = (String) getGroup(groupPosition);

        if(convertView == null)
        {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.ct_group_layout,null);
        }

        TextView textView = convertView.findViewById(R.id.ctHeaderId);
        TextView titleTextView = convertView.findViewById(R.id.ctHeaderTitleId);
        textView.setText(headerText);
        titleTextView.setText(headerTitleText);



        Log.d(st.TAG,"currentTime: "+st.currentTime(context));
        Log.d(st.TAG,"rawDateTime: "+ct.rawDateTimeList.get(groupPosition));

        if(ct.rawDateTimeList.get(groupPosition).compareTo(st.currentTime(context))<0)
        {
            textView.setPaintFlags(textView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            titleTextView.setPaintFlags(titleTextView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        }
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        String childText = (String) getChild(groupPosition,childPosition);

        if(convertView == null)
        {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.ct_child_layout,null);
        }

        TextView textView = convertView.findViewById(R.id.ctChildTextId);
        textView.setText(childText);


        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }
}
