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
import com.example.cho1.guru2_final_project_1cho.activity.BuyWriteActivity;
import com.example.cho1.guru2_final_project_1cho.activity.ExDetailActivity;
import com.example.cho1.guru2_final_project_1cho.activity.ExWriteActivity;
import com.example.cho1.guru2_final_project_1cho.bean.ExBean;
import com.example.cho1.guru2_final_project_1cho.bean.FleaBean;
import com.example.cho1.guru2_final_project_1cho.firebase.ExAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FragmentEx extends Fragment {

    public ListView mLstEx;
    private List<ExBean> mExList = new ArrayList<>();
    private ExAdapter mExAdapter;
    private FirebaseAuth mFirebaseAuth = FirebaseAuth.getInstance();
    private FirebaseDatabase mFirebaseDB = FirebaseDatabase.getInstance();


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

        view.findViewById(R.id.btnWrite).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), ExWriteActivity.class);
                startActivity(intent);
            }
        });


        return view;
    } // onCreate()


    @Override
    public void onResume() {
        super.onResume();

        //데이터 취득
        String userEmail = mFirebaseAuth.getCurrentUser().getEmail();
        String uuid = BuyWriteActivity.getUserIdFromUUID(userEmail);
        mFirebaseDB.getReference().child("ex").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //data가 바뀔 때마다 이벤트가 들어옴
                //data를 받아와서 List에 저장
                mExList.clear();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    for(DataSnapshot snapshot2 : snapshot.getChildren()) {
                        ExBean bean = snapshot2.getValue(ExBean.class);
                        mExList.add(0, bean);  //데이터를 받아와서 위로 불러온다 > 메모 추가 하면 가장 위에 추가됨
                    }
                }
                //바뀐 데이터로 Refresh 한다
                if(mExAdapter != null){
                    mExAdapter.setList(mExList);
                    mExAdapter.notifyDataSetChanged();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        //어댑터 생성
    }
}