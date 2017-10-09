package com.example.henrymatidios.thesis;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * @author  by Henry Matidios
 * @since  03/10/2017.
 */

public class CustomAdapter extends BaseAdapter {

    private class ViewHolder {
        TextView mSpinnerItem;
    }

    private Context context;
    private List<?> mData;
    private LayoutInflater layoutInflater;

    CustomAdapter(Context context, List<String> mData) {
        this.context = context;
        this.mData = mData;
        this.layoutInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TextView spinnerItem;
        ViewHolder holder;

        holder = new ViewHolder();

        if(convertView == null) {
            if(context instanceof AddNewUserActivity) {
                convertView = layoutInflater.inflate(R.layout.spinner_item, parent, false);

                holder.mSpinnerItem = (TextView) convertView.findViewById(R.id.spinnerItem);

                convertView.setTag(holder);

            }
        }else {
            holder = (ViewHolder) convertView.getTag();
        }

        //POPULATE FIELDS
        if(context instanceof AddNewUserActivity) {
            spinnerItem = holder.mSpinnerItem;
            spinnerItem.setText(mData.get(position).toString());

        }
            return convertView;
    }
}
