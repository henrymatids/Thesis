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

import com.example.henrymatidios.thesis.Models.Logs;
import com.google.firebase.database.ChildEventListener;
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
public class LogsFragment extends Fragment {

    private static final String ARG_COLUMN_COUNT = "column-count";

    private DatabaseReference dbRef;
    private ChildEventListener childEventListener;
    private static List<Logs> mListLogs = new ArrayList<Logs>();
    private int mColumnCount = 1;
    private OnListFragmentInteractionListener mListener;

    private ProgressBar mPb;
    private RecyclerView recyclerView;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public LogsFragment() {
    }

    @SuppressWarnings("unused")
    public static LogsFragment newInstance(int columnCount) {
        LogsFragment fragment = new LogsFragment();
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
        View view = inflater.inflate(R.layout.fragment_logs_list, container, false);
        mPb = (ProgressBar) view.findViewById(R.id.progress_bar);
        mPb.setVisibility(View.VISIBLE);
        getLogs(view);
        // Set the adapter
        if (view instanceof FrameLayout) {
            Context context = view.getContext();
            recyclerView = (RecyclerView) view.findViewById(R.id.list);
            if (mColumnCount <= 1) {
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }
            recyclerView.setAdapter(new MyLogsRecyclerViewAdapter(mListLogs, mListener));
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
    }

    @SuppressWarnings("unchecked")
    public void getLogs(View view) {
        mListLogs.clear();

        dbRef = Utils.getDatabase(false).getReference("Notification");

        childEventListener = dbRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                onChildAddedLogs(dataSnapshot);
                recyclerView.getAdapter().notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                removeValueFromList(dataSnapshot);
                recyclerView.getAdapter().notifyDataSetChanged();
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mPb.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @SuppressWarnings("unchecked")
    public void onChildAddedLogs(DataSnapshot dataSnapshot) {
        Map<String, String> mLogs = (Map<String, String>) dataSnapshot.getValue();

        if(mLogs != null)
        {
            int image;

            if(mLogs.get("processed").equals("false")) {
                image = R.mipmap.ic_redcircle;
            } else {
                image = R.mipmap.ic_greencircle;
            }

            Logs listLogs = new Logs(dataSnapshot.getKey());
            listLogs.values.setDate(mLogs.get("date"));
            listLogs.values.setLocation(mLogs.get("location"));
            listLogs.values.setTime(mLogs.get("time"));
            listLogs.values.setImage(image);
            mListLogs.add(listLogs);
        }
    }

    @SuppressWarnings("unchecked")
    public void removeValueFromList(DataSnapshot dataSnapshot) {
        Map<String, String> mLogs = (Map<String, String>) dataSnapshot.getValue();
        String key = dataSnapshot.getKey();

        if(mLogs != null) {
            int image;
            if(mLogs.get("processed").equals("false")) {
                image = R.mipmap.ic_redcircle;
            } else {
                image = R.mipmap.ic_greencircle;
            }
            Logs listLogs = new Logs(dataSnapshot.getKey());
            listLogs.values.setDate(mLogs.get("date"));
            listLogs.values.setLocation(mLogs.get("location"));
            listLogs.values.setTime(mLogs.get("time"));
            listLogs.values.setImage(image);

            mListLogs.remove(listLogs);
        }
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
        void onListFragmentInteraction(View view, Logs item);
    }
}
