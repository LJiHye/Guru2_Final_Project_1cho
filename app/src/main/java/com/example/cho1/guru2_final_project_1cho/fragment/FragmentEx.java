package com.example.cho1.guru2_final_project_1cho.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.cho1.guru2_final_project_1cho.R;

public class FragmentEx extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ex, container, false);

        ImageView imgEx = view.findViewById(R.id.imgEx);
        TextView txtExTitle = view.findViewById(R.id.txtExTitle);
        TextView txtExSubTitle = view.findViewById(R.id.txtExSubTitle);
        TextView txtExPrice = view.findViewById(R.id.txtExPrice);
        TextView txtExDate = view.findViewById(R.id.txtExDate);


        return view;
    }
}
