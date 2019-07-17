package com.example.cho1.guru2_final_project_1cho.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.cho1.guru2_final_project_1cho.R;
import com.example.cho1.guru2_final_project_1cho.bean.ExBean;
import com.example.cho1.guru2_final_project_1cho.db.FileDB;
import com.example.cho1.guru2_final_project_1cho.firebase.ExAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;
import java.util.List;

public class ExDetailActivity extends AppCompatActivity {

    private FirebaseAuth mFirebaseAuth = FirebaseAuth.getInstance();
    private FirebaseDatabase mFirebaseDB = FirebaseDatabase.getInstance();


    private Context mContext;
    private ExBean mExBean;
    private List<ExBean> mExList= new ArrayList<>();
    private ExAdapter mExAdapter;

    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ex_detail);

        mExBean = (ExBean) getIntent().getSerializableExtra("ITEM");
        final TextView txtExDetailId = findViewById(R.id.txtExDetailId); //아이디
        final TextView txtExDetailDate = findViewById(R.id.txtExDetailDate); //날짜

        final TextView txtExDetailTitle = findViewById(R.id.txtExDetailTitle); // 내 물건
        final TextView txtExDetailWant = findViewById(R.id.txtExDetailWant); // 상대방 물건
        final TextView txtExDetailPrice = findViewById(R.id.txtExDetailPrice); // 원가
        final TextView txtExDetailState = findViewById(R.id.txtExDetailState); // 상태
        final TextView txtExDetailFault = findViewById(R.id.txtExDetailFault); // 하자
        final TextView txtExDetailBuyDate = findViewById(R.id.txtExDetailBuyDate); // 구매날짜
        final TextView txtExDetailExpire = findViewById(R.id.txtExDetailExpire); // 유통기한
        final TextView txtExDetailSize = findViewById(R.id.txtExDetailSize); // 사이즈

        findViewById(R.id.btnExModify).setOnClickListener(mBtnClick);

        //상단 아이디 바 글쓴이 아이디, 올린 날짜 출력
        mFirebaseDB.getReference().child("ex").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //데이터를 받아와서 List에 저장.
                mExList.clear();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    for (DataSnapshot snapshot2 : snapshot.getChildren()) {
                        ExBean bean = snapshot2.getValue(ExBean.class);
                        if(TextUtils.equals(bean.id,mExBean.id)) {
                            txtExDetailTitle.setText(bean.mine);
                            txtExDetailWant.setText(bean.want);
                            txtExDetailPrice.setText(bean.price);
                            txtExDetailState.setText(bean.state);
                            txtExDetailFault.setText(bean.fault);
                            txtExDetailBuyDate.setText(bean.buyDate);
                            txtExDetailExpire.setText(bean.expire);
                            txtExDetailSize.setText(bean.size);
                            txtExDetailId.setText(bean.userId);
                            txtExDetailDate.setText(bean.date);
                            //상단 아이디(글쓴이 아이디)와 로그인 아이디가 같으면 수정, 삭제버튼 visibility 풀기
                            if (TextUtils.equals(mExBean.userId, mFirebaseAuth.getCurrentUser().getEmail())) {
                              //  layoutVisibility.setVisibility(View.VISIBLE);
                            }
                        }
                    }
                }

                if (mExAdapter != null) {
                    mExAdapter.setList(mExList);
                    mExAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

    } // onCreate()

    public void onResume() {
        super.onResume();
        mExAdapter = new ExAdapter(this, FileDB.getExList(getApplicationContext()) );
        
    }

    ExBean exBean = new ExBean();
    private View.OnClickListener mBtnClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            //어떤 버튼이 클릭 됐는지 구분한다
            switch (view.getId()) {
                case R.id.btnExModify:
                    //처리
                    Intent intent = new Intent(mContext, ExModifyActivity.class);
                    intent.putExtra(ExBean.class.getName(), exBean); // exBean을 넘긴다
                    intent.putExtra("titleBitmap", exBean.bmpTitle); // exBean에서 넘어가지 않으므로 따로 넘겨준다
                    mContext.startActivity(intent);
                    break;

                case R.id.btnDel:
                    //처리
                    AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                    builder.setTitle("삭제");
                    builder.setMessage("삭제 하시겠습니까?");
                    builder.setNegativeButton("아니오" , null);
                    builder.setPositiveButton("예", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            String email = FirebaseAuth.getInstance().getCurrentUser().getEmail();
                            String uuid = ExWriteActivity.getUserIdFromUUID(email);

                            //DB에서 삭제처리
                            FirebaseDatabase.getInstance().getReference().child("ex").child(uuid).child(exBean.id).removeValue();
                            //Storage 삭제처리
                            if(exBean.imgName != null) {
                                try {
                                    FirebaseStorage.getInstance().getReference().child("images").child(exBean.imgName).delete();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }

                            Toast.makeText(mContext, "삭제 되었습니다.", Toast.LENGTH_LONG).show();
                        }
                    });
                    break;
            }
        }
    };
}
