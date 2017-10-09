package com.example.henrymatidios.thesis;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.henrymatidios.thesis.Models.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class AccountFragment extends Fragment {

    private static final String ARG_COLUMN_COUNT = "column-count";

    private DatabaseReference dbRef;
    private static List<User> mAccountList = new ArrayList<User>();
    private int mColumnCount = 1;
    private AccountFragment.OnListFragmentInteractionListener mListener;
    private ValueEventListener valueEventListener;

    private ProgressBar mPb;
    private RecyclerView recyclerView;
    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public AccountFragment() {
    }

    @SuppressWarnings("unused")
    public static AccountFragment newInstance(int columnCount) {
        AccountFragment fragment = new AccountFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_account_list, container, false);
        populateAccountListView(view);
        // Set the adapter
        if (view instanceof FrameLayout) {
            Context context = view.getContext();
            mPb = (ProgressBar) view.findViewById(R.id.pb_view_users);
            mPb.setVisibility(View.VISIBLE);
            recyclerView = (RecyclerView) view.findViewById(R.id.list);

            if (mColumnCount <= 1) {
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }
            recyclerView.setAdapter(new MyAccountRecyclerViewAdapter(mAccountList, mListener));
        }
        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnListFragmentInteractionListener) {
            mListener = (OnListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
        dbRef.removeEventListener(valueEventListener);
    }

    @SuppressWarnings("unchecked")
    public void populateAccountListView(View view) {

        dbRef = Utils.getDatabase(true).getReference("Accounts");
        dbRef.keepSynced(true);

        valueEventListener = dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mAccountList.clear();

                for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                    Map<String, String> mAccounts = (Map<String, String>) childSnapshot.getValue();

                    if(mAccounts != null){
                        User mUser = new User(null, mAccounts.get("type"), mAccounts.get("name"), R.drawable.ic_menu_person_black);
                        mAccountList.add(mUser);
                    }
                }

                mPb.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
                recyclerView.getAdapter().notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getActivity(), "Network Error has Occurred", Toast.LENGTH_SHORT).show();
            }
        });
    }
    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnListFragmentInteractionListener {
        void onListFragmentInteraction(View view, User info);
    }
}
