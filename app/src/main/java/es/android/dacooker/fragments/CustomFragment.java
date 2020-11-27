package es.android.dacooker.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import es.android.dacooker.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class CustomFragment extends Fragment {

    //Views
    private TextView tvCustomComingNext;

    // Required empty public constructor
    public CustomFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //Inflate the View
        View view = inflater.inflate(R.layout.fragment_custom, container, false);
        //Find the views
        tvCustomComingNext = view.findViewById(R.id.tvCustomComingNext);
        return view;
    }
}