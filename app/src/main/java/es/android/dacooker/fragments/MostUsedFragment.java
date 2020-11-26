package es.android.dacooker.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import es.android.dacooker.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class MostUsedFragment extends Fragment {

    public MostUsedFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_most_used, container, false);

        return view;
    }
}