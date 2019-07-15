package com.example.cho1.guru2_final_project_1cho.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.cho1.guru2_final_project_1cho.R;
import com.example.cho1.guru2_final_project_1cho.activity.ExDetailActivity;
import com.example.cho1.guru2_final_project_1cho.bean.ExBean;
import com.example.cho1.guru2_final_project_1cho.firebase.ExAdapter;

import java.util.ArrayList;
import java.util.List;

public class FragmentEx extends Fragment {

    public ListView mLstEx;
    private List<ExBean> mExList = new ArrayList<>();
    private ExAdapter mExAdapter;
    /*private FirebaseAuth mFirebaseAuth = FirebaseAuth.getInstance();
    private FirebaseDatabase mFirebaseDB = FirebaseDatabase.getInstance();*/


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ex, container, false);

        mLstEx = view.findViewById(R.id.lstEx);

        view.findViewById(R.id.detailTest).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getActivity(), ExDetailActivity.class);
                startActivity(i);
            }
        });

         /* view.findViewById(R.id.btnExModify).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getActivity(), ExModifyActivity.class);
                startActivity(i);
            }
        }); */

        mExAdapter = new ExAdapter(getActivity(), mExList);
        mLstEx.setAdapter(mExAdapter);

        return view;
    }
}