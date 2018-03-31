package com.codex_iter.www.awol;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
public class MyBaseAdapter extends ArrayAdapter<ListData> {
    private ArrayList<ListData> myList = new ArrayList<ListData>();
    LayoutInflater inflater;
    Context context;

    public MyBaseAdapter(Context context, ArrayList<ListData> myList) {
        super(context, -1, myList);
        inflater=LayoutInflater.from(context);
        this.myList=myList;
        this.context=context;
    }


    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        MyViewHolder mViewHolder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.dummy, parent, false);
            mViewHolder = new MyViewHolder(convertView);
            convertView.setTag(mViewHolder);
        } else {
            mViewHolder = (MyViewHolder) convertView.getTag();
        }
        mViewHolder.sub.setText(myList.get(position).getSub());
        mViewHolder.at.setText(myList.get(position).getPercent());
        mViewHolder.lu.setText(myList.get(position).getUpd());
        mViewHolder.th.setText(myList.get(position).getTheory());
        mViewHolder.prac.setText(myList.get(position).getLab());
        mViewHolder.ab.setText(myList.get(position).getAbsent());
        mViewHolder.tc.setText(myList.get(position).getClasses());
        mViewHolder.btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context.getApplicationContext(), Bunk.class);
                intent.putExtra("pos", position);
                context.startActivity(intent);
            }
        });
        return convertView;
    }

    private class MyViewHolder {
        TextView sub,at,lu,th,prac,ab,tc;
        Button btn;


        private MyViewHolder(View view) {
            sub =  view.findViewById(R.id.sub);
            at =   view.findViewById(R.id.att);
            lu=   view.findViewById(R.id.lu);
            th= view.findViewById(R.id.theory);
            prac= view.findViewById(R.id.prac);
            ab= view.findViewById(R.id.ta);
            tc=view.findViewById(R.id.tc);
            btn= view.findViewById(R.id.btn);


        }
    }
}


