package com.example.cho1.guru2_final_project_1cho.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.cho1.guru2_final_project_1cho.R;
import com.example.cho1.guru2_final_project_1cho.activity.ExWriteActivity;
import com.example.cho1.guru2_final_project_1cho.activity.MainActivity;
import com.example.cho1.guru2_final_project_1cho.activity.SellWriteActivity;
import com.example.cho1.guru2_final_project_1cho.bean.FleaBean;
import com.example.cho1.guru2_final_project_1cho.firebase.FleaAdapter;
import com.example.cho1.guru2_final_project_1cho.firebase.SellAdapter;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class FragmentSell extends Fragment {

    private FirebaseAuth mFirebaseAuth = FirebaseAuth.getInstance();
    private FirebaseDatabase mFirebaseDB = FirebaseDatabase.getInstance();

    public ListView mLstFlea;
    private List<FleaBean> mFleaList = new ArrayList<>();
    private SellAdapter mSellAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sell, container, false);

        mLstFlea = view.findViewById(R.id.lstSell);
        mSellAdapter = new SellAdapter(getActivity(), mFleaList);
        mLstFlea.setAdapter(mSellAdapter);

        view.findViewById(R.id.btnSellWrite).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), SellWriteActivity.class);
                startActivity(intent);
            }
        });

        return view;
    }//end onCreate()

    @Override
    public void onResume() {
        super.onResume();

        //데이터 취득
        String userEmail = mFirebaseAuth.getCurrentUser().getEmail();
        String uuid = SellWriteActivity.getUserIdFromUUID(userEmail);
        mFirebaseDB.getReference().child("Sell").child(uuid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //데이터를 받아와서 List에 저장.
                mFleaList.clear();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    FleaBean bean = snapshot.getValue(FleaBean.class);
                    mFleaList.add(0, bean);
                }
                //바뀐 데이터로 Refresh 한다.
                if (mSellAdapter != null) {
                    mSellAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        });
    }
}
