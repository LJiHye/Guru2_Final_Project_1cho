package com.example.cho1.guru2_final_project_1cho.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.cho1.guru2_final_project_1cho.R;
import com.example.cho1.guru2_final_project_1cho.activity.SellWriteActivity;
import com.example.cho1.guru2_final_project_1cho.bean.FleaBean;
import com.example.cho1.guru2_final_project_1cho.bean.FreeBean;
import com.example.cho1.guru2_final_project_1cho.bean.MemberBean;
import com.example.cho1.guru2_final_project_1cho.db.FileDB;
import com.example.cho1.guru2_final_project_1cho.firebase.MyFreeAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class FragmentMyFree extends Fragment {

    private FirebaseAuth mFirebaseAuth = FirebaseAuth.getInstance();
    private FirebaseDatabase mFirebaseDB = FirebaseDatabase.getInstance();

    public ListView mLstMyFree;
    private List<FreeBean> mFreeList = new ArrayList<>();
    private MyFreeAdapter mFreeAdapter;

    private MemberBean mLoginMember;

    private Boolean flag;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_free, container, false);

        mLstMyFree = view.findViewById(R.id.lstMyFree);
        mLoginMember = FileDB.getLoginMember(getActivity());

        //최초 데이터 세팅
        mFreeAdapter = new MyFreeAdapter(getActivity(), mFreeList);
        mLstMyFree.setAdapter(mFreeAdapter);

        view.findViewById(R.id.btnDelMyFree).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                delete();
            }
        });

        return view;
    }

    //삭제
    private void delete() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("삭제");
        builder.setMessage("삭제하시겠습니까?");
        builder.setNegativeButton("아니오", null);
        builder.setPositiveButton("예", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String email = FirebaseAuth.getInstance().getCurrentUser().getEmail();
                String uuid = SellWriteActivity.getUserIdFromUUID(email);
                flag = false;

                mFirebaseDB.getReference().child("free").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        //data가 바뀔 때마다 이벤트가 들어옴
                        //data를 받아와서 List에 저장
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()){  //파이어베이스가 이중 구조여서
                            FreeBean bean = snapshot.getValue(FreeBean.class);
                            if(TextUtils.equals(mFirebaseAuth.getCurrentUser().getEmail(), bean.userId)) {
                                FirebaseDatabase.getInstance().getReference().child("free").child(bean.id).removeValue();
                                flag = true;
                            }
                        }
                        if(flag) {
                            Toast.makeText(getActivity(), "삭제 되었습니다", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(getActivity(), "삭제할 게시물이 없습니다", Toast.LENGTH_LONG).show();
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }
        });
        builder.create().show();
    }

    @Override
    public void onResume() {
        super.onResume();

        //데이터 취득
        mFirebaseDB.getReference().child("free").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //data가 바뀔 때마다 이벤트가 들어옴
                //data를 받아와서 List에 저장
                mFreeList.clear();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()){  //파이어베이스가 이중 구조여서
                    FreeBean bean = snapshot.getValue(FreeBean.class);
                    if(TextUtils.equals(mFirebaseAuth.getCurrentUser().getEmail(), bean.userId))
                        mFreeList.add(0, bean);
                }
                //바뀐 데이터로 Refresh 한다
                if(mFreeAdapter != null){
                    mFreeAdapter.setList(mFreeList);
                    mFreeAdapter.notifyDataSetChanged();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        //어댑터 생성
    }
}
