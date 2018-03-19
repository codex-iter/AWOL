import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
public class MyBaseAdapter extends ArrayAdapter<ListData> {
    private ArrayList<ListData> myList = new ArrayList<ListData>();
    LayoutInflater inflater;

    public MyBaseAdapter(Context context, ArrayList<ListData> myList) {
        super(context, -1, myList);
        inflater=LayoutInflater.from(context);
        this.myList=myList;
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
        mViewHolder.status.setText(myList.get(position).getStatus());
        return convertView;
    }

    private class MyViewHolder {
        TextView sub,at,status,lu,th,prac,ab;

        private MyViewHolder(View view) {
            sub = (TextView) view.findViewById(R.id.sub);
            at = (TextView) view.findViewById(R.id.at);
            status= (TextView) view.findViewById(R.id.status);
            lu= (TextView) view.findViewById(R.id.lu);
            th= (TextView) view.findViewById(R.id.th);
            prac= (TextView) view.findViewById(R.id.prac);
            ab= (TextView) view.findViewById(R.id.ab);


        }
    }
}


