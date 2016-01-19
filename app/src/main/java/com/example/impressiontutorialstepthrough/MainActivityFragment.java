package com.example.impressiontutorialstepthrough;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {
    private RecyclerView mRecyclerView;
    private ArrayList<String> mDataSet = new ArrayList<>();

    public MainActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view);
        mDataSet = getMockData();

        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.setAdapter(new ImpressionAdapter(getActivity(), mDataSet));

        return rootView;
    }

    private ArrayList<String> getMockData() {
        ArrayList<String> data = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            String item = "Title " + i;
            data.add(item);

        }
        return data;
    }
}