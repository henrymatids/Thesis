package com.example.henrymatidios.thesis;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.henrymatidios.thesis.AccountFragment.OnListFragmentInteractionListener;
import com.example.henrymatidios.thesis.Models.User;

import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link User} and makes a call to the
 * specified {@link AccountFragment.OnListFragmentInteractionListener}.
 */
public class MyAccountRecyclerViewAdapter extends RecyclerView.Adapter<MyAccountRecyclerViewAdapter.ViewHolder> {

    private final List<User> mValues;
    private final OnListFragmentInteractionListener mListener;

    public MyAccountRecyclerViewAdapter(List<User> items, OnListFragmentInteractionListener listener) {
        mValues = items;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_account, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mInfo = mValues.get(position);
        holder.mImage.setImageResource(mValues.get(position).getImage());
        holder.mProfileName.setText(mValues.get(position).getName());
        String type = (mValues.get(position).getType().contentEquals("0")) ? "Admin" : "User";
        holder.mProfileType.setText(type);

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onListFragmentInteraction(holder.mInfo);
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
        public final TextView mProfileName;
        public final TextView mProfileType;
        public User mInfo;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mImage = (ImageView) view.findViewById(R.id.imageView_profile_picture);
            mProfileName = (TextView) view.findViewById(R.id.profile_name);
            mProfileType = (TextView) view.findViewById(R.id.profile_account_type);
        }

//        @Override
//        public String toString() {
//            return super.toString() + " '" + mContentView.getText() + "'";
//        }
    }
}
