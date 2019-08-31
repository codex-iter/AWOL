package mohit.codex_iter.www.awol;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import java.util.ArrayList;
import java.util.Scanner;

public class MyBaseAdapter extends ArrayAdapter<ListData> {
    private ArrayList<ListData> myList;
    private LayoutInflater inflater;
    private Context context;


    public MyBaseAdapter(Context context, ArrayList<ListData> myList) {
        super(context, -1, myList);
        inflater=LayoutInflater.from(context);
        this.myList=myList;
        this.context=context;
    }

    @NonNull
    @SuppressLint("SetTextI18n")
    @Override
    public View getView(final int position, View convertView, @NonNull ViewGroup parent) {
        MyViewHolder mViewHolder;
        SharedPreferences theme = context.getSharedPreferences("theme",0);
        boolean dark = theme.getBoolean("dark_theme", false);
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.dummy, parent, false);
            mViewHolder = new MyViewHolder(convertView);
            convertView.setTag(mViewHolder);
        } else {
            mViewHolder = (MyViewHolder) convertView.getTag();
        }
        Animation animation;
        animation = AnimationUtils.loadAnimation(context, R.anim.action);
        animation.setDuration(200);
        convertView.startAnimation(animation);
        mViewHolder.sub.setText(myList.get(position).getSub());
        String p = myList.get(position).getPercent();
        double percent = Double.valueOf(p);

        if (percent > (float)80){
            mViewHolder.ta.setBackgroundResource(R.drawable.percent_back_green);
        } else if (percent >= (float)60 && percent <=80){
                mViewHolder.ta.setBackgroundResource(R.drawable.percent_back_yellow);
        } else {
            mViewHolder.ta.setBackgroundResource(R.drawable.percent_back_red);
        }
        mViewHolder.ta.setText(myList.get(position).getPercent()+"%");
//        mViewHolder.tha.setText(myList.get(position).getThat());
//        mViewHolder.la.setText(myList.get(position).getLabt());
        String s=myList.get(position).getOld();
      //  Toast.makeText(context, "Old Attendance : " + s, Toast.LENGTH_SHORT).show();
        if(!s.equals(""))
        {
            double n = Double.valueOf(myList.get(position).getPercent());
            double o = Double.valueOf(s);
        //    Toast.makeText(context, "New Attendance : " + n, Toast.LENGTH_SHORT).show();
            if(n>=o) {
                mViewHolder.up.setBackgroundResource(R.drawable.up);
            }
            else {
                mViewHolder.up.setBackgroundResource(R.drawable.down);
            }
        }
//        if(myList.get(position).getStatus()==1)
//            mViewHolder.at.setBackgroundResource(R.drawable.circp);
//        else if(myList.get(position).getStatus()==2)
//            mViewHolder.at. setBackgroundResource(R.drawable.circs);
//        else if(myList.get(position).getStatus()==3)
//            mViewHolder.at.setBackgroundResource(R.drawable.circo);
//        else
//            mViewHolder.at.setBackgroundResource(R.drawable.circe);

        mViewHolder.lu.setText(myList.get(position).getUpd());
        mViewHolder.th.setText(myList.get(position).getTheory()+myList.get(position).getThat());
        mViewHolder.prac.setText(myList.get(position).getLab()+myList.get(position).getLabt());
        mViewHolder.ab.setText(myList.get(position).getAbsent());
        mViewHolder.tc.setText(myList.get(position).getClasses());
        mViewHolder.bunk_text.setText(myList.get(position).getBunk_text_str());
        //to be

        if (!dark){
            mViewHolder.cardView.setCardBackgroundColor(Color.parseColor("#FFFFFF"));
            mViewHolder.ta.setTextColor(Color.parseColor("#141831"));
            mViewHolder.lu.setTextColor(Color.parseColor("#141831"));
            mViewHolder.th.setTextColor(Color.parseColor("#141831"));
            mViewHolder.prac.setTextColor(Color.parseColor("#141831"));
            mViewHolder.ab.setTextColor(Color.parseColor("#141831"));
            mViewHolder.tc.setTextColor(Color.parseColor("#141831"));
            // mViewHolder.total.setTextColor(Color.parseColor("#141831"));
            mViewHolder.updated.setTextColor(Color.parseColor("#141831"));
            mViewHolder.absents.setTextColor(Color.parseColor("#141831"));
            mViewHolder.pract.setTextColor(Color.parseColor("#141831"));
            mViewHolder.theory.setTextColor(Color.parseColor("#141831"));
            mViewHolder.classes.setTextColor(Color.parseColor("#141831"));
            mViewHolder.bunk_text.setTextColor(Color.parseColor("#141831"));
        }
        return convertView;
    }


    private class MyViewHolder {

        TextView sub,lu,th,prac,ab,tc,tha,la, total, theory, updated, pract,classes,absents,bunk_text ;
        Button ta;
        ImageView up,down;
        CardView cardView;
        private MyViewHolder(View view) {
            //  total = view.findViewById(R.id.total);
            theory = view.findViewById(R.id.theory_t);
            updated = view.findViewById(R.id.updated);
            pract = view.findViewById(R.id.practicle);
            classes = view.findViewById(R.id.classes);
            absents = view.findViewById(R.id.absents);
            cardView = view.findViewById(R.id.card_view);
            sub =  view.findViewById(R.id.sub);
            lu=   view.findViewById(R.id.lu);
            th= view.findViewById(R.id.theory);
            prac= view.findViewById(R.id.prac);
            ab= view.findViewById(R.id.ab);
            tc=view.findViewById(R.id.tc);
            ta=view.findViewById(R.id.ta);
            bunk_text=view.findViewById(R.id.bunk_text);

//            tha=view.findViewById(R.id.tha);
//            la=view.findViewById(R.id.la);
            up=view.findViewById(R.id.up);
            //down=view.findViewById(R.id.down);
        }
    }
}


