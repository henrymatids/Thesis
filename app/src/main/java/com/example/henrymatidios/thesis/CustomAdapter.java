package com.example.henrymatidios.thesis;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * @author Henry Matidios
 * @since 15/08/2017
 */

public class CustomAdapter extends BaseAdapter {
    private class ViewHolder {
        TextView mSpinnerItem;
        ImageView mImage;
        TextView mAlertEditText;
        TextView mLocationEditText;
        TextView mDateEditText;
        TextView mTimeEditText;
        TextView mKey;
        TextView mProfileName;
        TextView mProfileType;
    }
    private Context context;
    private List<?> mData;
    private LayoutInflater layoutInflater;
    private int mImage;

    CustomAdapter(Context context, List<String> mData) {
        this.context = context;
        this.mData = mData;
        this.layoutInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    CustomAdapter(Context context, List<?> mData, int image) {
        this.context = context;
        this.mData = mData;
        this.mImage = image;
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

        ImageView mImage;
        TextView mAlert;
        TextView mDate;
        TextView mLocation;
        TextView mTime;
        TextView mKey;
        TextView mProfileName;
        TextView mProfileType;
        TextView spinnerItem;
        ViewHolder holder;

        holder = new ViewHolder();

//        if(convertView == null) {
//            if(context instanceof AddNewUserActivity) {
//                convertView = layoutInflater.inflate(R.layout.spinner_item, parent, false);
//
//                holder.mSpinnerItem = (TextView) convertView.findViewById(R.id.spinnerItem);
//
//                convertView.setTag(holder);
//
//            } else if (context instanceof LogsActivity) {
//                convertView = layoutInflater.inflate(R.layout.listview_logs, parent, false);
//
//                holder.mImage = (ImageView) convertView.findViewById(R.id.imageView);
//                holder.mAlertEditText = (TextView) convertView.findViewById(R.id.alert_editText);
//                holder.mLocationEditText = (TextView) convertView.findViewById(R.id.location_editText);
//                holder.mDateEditText = (TextView) convertView.findViewById(R.id.date_editText);
//                holder.mTimeEditText = (TextView) convertView.findViewById(R.id.time_editText);
//                holder.mKey = (TextView) convertView.findViewById(R.id.logs_key);
//
//                convertView.setTag(holder);
//            } else if (context instanceof ViewUsersActivity) {
//                convertView = layoutInflater.inflate(R.layout.listview_view_users, parent, false);
//
//                holder.mImage = (ImageView) convertView.findViewById(R.id.imageView_profile_picture);
//                holder.mProfileName = (TextView) convertView.findViewById(R.id.profile_name);
//                holder.mProfileType = (TextView) convertView.findViewById(R.id.profile_account_type);
//
//                convertView.setTag(holder);
//            }
//        } else {
//            holder = (ViewHolder) convertView.getTag();
//        }
//
//        //POPULATE FIELDS
//        if(context instanceof AddNewUserActivity) {
//            spinnerItem = holder.mSpinnerItem;
//            spinnerItem.setText(mData.get(position).toString());
//
//        } else if (context instanceof  LogsActivity) {
//            mImage = holder.mImage;
//            mAlert = holder.mAlertEditText;
//            mLocation = holder.mLocationEditText;
//            mDate = holder.mDateEditText;
//            mTime = holder.mTimeEditText;
//            mKey = holder.mKey;
//
//            Logs logs = (Logs) mData.get(position);
//
//            mImage.setImageResource(logs.values.getImage());
//            mAlert.setText(context.getString(R.string.alert_gas_leak));
//            mLocation.setText(logs.values.getLocation());
//            mDate.setText(logs.values.getDate());
//            mTime.setText(logs.values.getTime());
//            mKey.setText(logs.getKey());
//
//        } else if (context instanceof ViewUsersActivity){
//            mImage = holder.mImage;
//            mProfileName = holder.mProfileName;
//            mProfileType = holder.mProfileType;
//
//            User mUser = (User) mData.get(position);
//            String accountType;
//
//            mImage.setImageResource(mUser.getImage());
//            mProfileName.setText(mUser.getName());
//
//            if(mUser.getType().equals("0")) {
//                accountType = convertView.getResources().getString(R.string.account_type_admin);
//            } else {
//                accountType = convertView.getResources().getString(R.string.account_type_user);
//            }
//
//            mProfileType.setText(accountType);
//        }
        return convertView;
    }
}