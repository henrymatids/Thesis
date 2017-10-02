package com.example.henrymatidios.thesis;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.henrymatidios.thesis.LogsFragment.OnListFragmentInteractionListener;
import com.example.henrymatidios.thesis.Models.Logs;

import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link Logs} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 */
public class MyLogsRecyclerViewAdapter extends RecyclerView.Adapter<MyLogsRecyclerViewAdapter.ViewHolder> {

    private final List<Logs> mValues;
    private final OnListFragmentInteractionListener mListener;

    public MyLogsRecyclerViewAdapter(List<Logs> items, OnListFragmentInteractionListener listener) {
        mValues = items;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_logs, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {

        holder.mItem = mValues.get(position);
        holder.mImage.setImageResource(mValues.get(position).values.getImage());
        holder.mLogsKey.setText(mValues.get(position).getKey());
        holder.mAlert.setText(R.string.alert_gas_leak);
        holder.mLocation.setText(mValues.get(position).values.getLocation());
        holder.mDate.setText(mValues.get(position).values.getDate());
        holder.mTime.setText(mValues.get(position).values.getTime());

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onListFragmentInteraction(holder.mView, holder.mItem);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final ImageView mImage;
        public final TextView mLogsKey;
        public final TextView mAlert;
        public final TextView mLocation;
        public final TextView mDate;
        public final TextView mTime;

        public Logs mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mImage = (ImageView) view.findViewById(R.id.imageView);
            mLogsKey = (TextView) view.findViewById(R.id.logs_key);
            mAlert = (TextView) view.findViewById(R.id.alert_editText);
            mLocation = (TextView) view.findViewById(R.id.location_editText);
            mDate = (TextView) view.findViewById(R.id.date_editText);
            mTime = (TextView) view.findViewById(R.id.time_editText);
        }
//        @Override
//        public String toString() {
//            return super.toString() + " '" + mContentView.getText() + "'";
//        }
    }
}
