package com.example.icaremonitor;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import java.util.ArrayList;

public class ThreeColumn_ListAdapter  extends ArrayAdapter {

    private  int textViewResourceId;
    private LayoutInflater mInflater;
    private ArrayList<User> users;
    Context context;

    public ThreeColumn_ListAdapter(Context context, int textViewResourceId, ArrayList<User> users) {
        super(context, textViewResourceId, users);
        this.context=context;
        this.textViewResourceId = textViewResourceId;
        this.users = users;

    }

    public View getView(int position, View convertView, ViewGroup parent) {

        View row= convertView;
        ViewHolder viewHolder;
        if(row==null){
            row = LayoutInflater.from(context).inflate(R.layout.list_row,parent,false);
            viewHolder=new ViewHolder();
            viewHolder.tvValue=(TextView)row.findViewById(R.id.value);
            viewHolder.tvTime=(TextView)row.findViewById(R.id.time);
            viewHolder.tvEx=(TextView)row.findViewById(R.id.ex);
            row.setTag(viewHolder);
        }else{
            viewHolder=(ViewHolder)row.getTag();
        }
        User info = (User) getItem(position);

        viewHolder.tvValue.setText(info.getValue());
        viewHolder.tvTime.setText(info.getTime());
        viewHolder.tvEx.setText(info.getNote());
        return row;
    }
    public class ViewHolder{
        TextView tvValue, tvTime, tvEx;
    }
}

