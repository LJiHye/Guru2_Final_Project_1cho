package com.example.cho1.guru2_final_project_1cho.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.cho1.guru2_final_project_1cho.R;
import com.example.cho1.guru2_final_project_1cho.bean.ExBean;
import com.example.cho1.guru2_final_project_1cho.bean.FleaBean;
import com.example.cho1.guru2_final_project_1cho.firebase.ExAdapter;
import com.example.cho1.guru2_final_project_1cho.firebase.FleaAdapter;

import java.util.ArrayList;
import java.util.List;

public class FragmentBuy extends Fragment {

    public ListView mLstFlea;
    private List<FleaBean> mFleaList = new ArrayList<>();
    private FleaAdapter mFleaAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_buy, container, false);

        mLstFlea = view.findViewById(R.id.lstBuy);

        mFleaAdapter = new FleaAdapter(getActivity(), mFleaList);
        mLstFlea.setAdapter(mFleaAdapter);

        return view;
    }
}
