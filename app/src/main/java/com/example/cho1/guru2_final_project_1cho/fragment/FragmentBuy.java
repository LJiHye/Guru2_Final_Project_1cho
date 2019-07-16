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
import com.example.cho1.guru2_final_project_1cho.bean.FleaBean;
import com.example.cho1.guru2_final_project_1cho.firebase.FleaAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


import java.util.ArrayList;
import java.util.List;

public class FragmentBuy extends Fragment {

    private FirebaseAuth mFirebaseAuth = FirebaseAuth.getInstance();
    private FirebaseDatabase mFirebaseDB = FirebaseDatabase.getInstance();

    public ListView mLstFlea;
    private List<FleaBean> mFleaList = new ArrayList<>();
    private FleaAdapter mFleaAdapter;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_buy, container, false);

        mLstFlea = view.findViewById(R.id.lstBuy);

        //최초 데이터 세팅
        mFleaAdapter = new FleaAdapter(getActivity(), mFleaList);
        mLstFlea.setAdapter(mFleaAdapter);

        return view;
    }  //end onCreateView

    @Override
    public void onResume() {
        super.onResume();

        //데이터 취득
        String userEmail = mFirebaseAuth.getCurrentUser().getEmail();
        mFirebaseDB.getReference().child("memo").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //data가 바뀔 때마다 이벤트가 들어옴
                //data를 받아와서 List에 저장
                mFleaList.clear();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    FleaBean bean = snapshot.getValue(FleaBean.class);
                    mFleaList.add(0, bean);  //데이터를 받아와서 위로 불러온다 > 메모 추가 하면 가장 위에 추가됨
                }
                //바뀐 데이터로 Refresh 한다
                if(mFleaAdapter != null){
                    mFleaAdapter.notifyDataSetChanged();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        //어댑터 생성
    }
}
