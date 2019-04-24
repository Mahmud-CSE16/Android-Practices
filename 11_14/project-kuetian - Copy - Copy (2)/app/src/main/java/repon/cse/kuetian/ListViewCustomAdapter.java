package repon.cse.kuetian;

import android.content.Context;
import android.graphics.Paint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import android.widget.TextView;



class ListViewCustomAdapter extends BaseAdapter{
    Context context;
    ClassTestObject ct ;

    public ListViewCustomAdapter(Context context, ClassTestObject ct) {
        this.context = context;
        this.ct = ct;
    }

    LayoutInflater inflater;


    @Override
    public int getCount() {
        return ct.rawDateTimeList.size();
    }

    @Override
    public Object getItem(int position) {
        return ct.rawDateTimeList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView==null)
        {
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.ct_details_layout,parent,false);
        }

       /* if(position%3==0) convertView.setBackgroundColor(0xff035c8c);
        else if(position%3==1) convertView.setBackgroundColor(0xff017476);
        else if (position%3==2) convertView.setBackgroundColor(0xff842ab9);*/

        TextView ctDetailsDateText = convertView.findViewById(R.id.ctDetailsDate);
        TextView ctDetailsHeaderTitle = convertView.findViewById(R.id.ctDetailsHeaderTitle);
        TextView ctDetailsChild = convertView.findViewById(R.id.ctDetailsChild);


        ctDetailsDateText.setText(ct.dateTimeMap.get(ct.rawDateTimeList.get(position)));
        ctDetailsHeaderTitle.setText(ct.titleMap.get(ct.rawDateTimeList.get(position)));
        ctDetailsChild.setText(ct.descriptionMap.get(ct.rawDateTimeList.get(position)));



        Log.d(st.TAG,"currentTime: "+st.currentTime(context));
        Log.d(st.TAG,"rawDateTime: "+ct.rawDateTimeList.get(position));

        if(ct.rawDateTimeList.get(position).compareTo(st.currentTime(context))<0)
        {
            ctDetailsDateText.setPaintFlags(ctDetailsDateText.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            ctDetailsHeaderTitle.setPaintFlags(ctDetailsHeaderTitle.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        }

        return convertView;
    }
}