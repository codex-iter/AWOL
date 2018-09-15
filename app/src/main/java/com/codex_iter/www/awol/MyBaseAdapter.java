package com.codex_iter.www.awol;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;


import java.util.ArrayList;
import java.util.Scanner;

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
        Animation animation = null;
        animation = AnimationUtils.loadAnimation(context, R.anim.action);
        animation.setDuration(200);
        convertView.startAnimation(animation);
        animation = null;
         mViewHolder.sub.setText(myList.get(position).getSub());
        mViewHolder.ta.setText(myList.get(position).getPercent()+"%");
//        mViewHolder.tha.setText(myList.get(position).getThat());
//        mViewHolder.la.setText(myList.get(position).getLabt());
        String s=myList.get(position).getOld();
        if(!s.equals(""))
        {
            double n= new Scanner(myList.get(position).getPercent()).nextDouble();
            double o= new Scanner(s).nextDouble();
            if(n>o)
                mViewHolder.up.setVisibility(View.VISIBLE);
            else
                mViewHolder.down.setVisibility(View.VISIBLE);
        }
//        if(myList.get(position).getStatus()==1)
//            mViewHolder.at.setBackgroundResource(R.drawable.circp);
//        else if(myList.get(position).getStatus()==2)
//            mViewHolder.at.setBackgroundResource(R.drawable.circs);
//        else if(myList.get(position).getStatus()==3)
//            mViewHolder.at.setBackgroundResource(R.drawable.circo);
//        else
//            mViewHolder.at.setBackgroundResource(R.drawable.circe);

        mViewHolder.lu.setText(myList.get(position).getUpd());
        mViewHolder.th.setText(myList.get(position).getTheory()+myList.get(position).getThat());
        mViewHolder.prac.setText(myList.get(position).getLab()+myList.get(position).getLabt());
        mViewHolder.ab.setText(myList.get(position).getAbsent());
        mViewHolder.tc.setText(myList.get(position).getClasses());
        return convertView;
    }


    private class MyViewHolder {

        TextView sub,ta,lu,th,prac,ab,tc,tha,la;
        ImageView up,down;

        private MyViewHolder(View view) {
            sub =  view.findViewById(R.id.sub);
            lu=   view.findViewById(R.id.lu);
            th= view.findViewById(R.id.theory);
            prac= view.findViewById(R.id.prac);
            ab= view.findViewById(R.id.ab);
            tc=view.findViewById(R.id.tc);
            ta=view.findViewById(R.id.ta);
//            tha=view.findViewById(R.id.tha);
//            la=view.findViewById(R.id.la);
            up=view.findViewById(R.id.up);
            down=view.findViewById(R.id.down);
        }
    }
}


