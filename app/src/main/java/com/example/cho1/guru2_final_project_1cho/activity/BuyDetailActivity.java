package com.example.cho1.guru2_final_project_1cho.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.cho1.guru2_final_project_1cho.R;
import com.example.cho1.guru2_final_project_1cho.bean.FleaBean;
import com.example.cho1.guru2_final_project_1cho.firebase.FleaAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class BuyDetailActivity extends AppCompatActivity {
    private TextView txtBuyDetailDate;
    private FirebaseAuth mFirebaseAuth = FirebaseAuth.getInstance();
    private FirebaseDatabase mFirebaseDB = FirebaseDatabase.getInstance();

    private FleaBean mFleaBean;
    private ImageView imgDetail;
    private TextView txtBuyDetailId, txtBuyDetailProduct, txtBuyDetailPrice, txtBuyDetailFinalPrice, txtBuyDetailState, txtBuyDetailFault, txtBuyDetailBuyDate, txtBuyDetailExpire, txtBuyDetailSize; //제품명

    private List<FleaBean> mFleaList = new ArrayList<>();
    private FleaAdapter mFleaAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buy_detail);

        mFleaBean = (FleaBean) getIntent().getSerializableExtra("ITEM");

        txtBuyDetailId = findViewById(R.id.txtBuyDetailId); //아이디
        txtBuyDetailDate = findViewById(R.id.txtBuyDetailDate); //날짜

        imgDetail = findViewById(R.id.imgDetail); //이미지
        txtBuyDetailProduct = findViewById(R.id.txtBuyDetailProduct); //제품명
        txtBuyDetailPrice = findViewById(R.id.txtBuyDetailPrice); //정가
        txtBuyDetailFinalPrice = findViewById(R.id.txtBuyDetailFinalPrice); //판매가
        txtBuyDetailState = findViewById(R.id.txtBuyDetailState); //제품상태
        txtBuyDetailFault = findViewById(R.id.txtBuyDetailFault); //하자유무
        txtBuyDetailBuyDate = findViewById(R.id.txtBuyDetailBuyDate); //구매일
        txtBuyDetailExpire = findViewById(R.id.txtBuyDetailExpire); //유통기한
        txtBuyDetailSize = findViewById(R.id.txtBuyDetailSize); //실측사이즈

        LinearLayout layoutVisibility = findViewById(R.id.layoutVisibility); //수정, 삭제 버튼 감싼 레이아웃
        Button btnModify = findViewById(R.id.btnModify);
        Button btnDel = findViewById(R.id.btnDel);

        //상단 아이디(글쓴이 아이디)와 로그인 아이디가 같으면 수정, 삭제버튼 visibility 풀기
        if (TextUtils.equals(mFleaBean.userId, mFirebaseAuth.getCurrentUser().getEmail())) {
            layoutVisibility.setVisibility(View.VISIBLE);
        }

        //상단 아이디바(아이디, 날짜), 글 내용 불러와 출력
        mFirebaseDB.getReference().child("buy").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //데이터를 받아와서 List에 저장.
                mFleaList.clear();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    for (DataSnapshot snapshot2 : snapshot.getChildren()) {
                        FleaBean bean = snapshot2.getValue(FleaBean.class);
                        if (TextUtils.equals(bean.id, mFleaBean.id)) {
                            txtBuyDetailProduct.setText(bean.title);
                            txtBuyDetailPrice.setText(bean.price);
                            txtBuyDetailFinalPrice.setText(bean.saleprice);
                            txtBuyDetailState.setText(bean.state);
                            txtBuyDetailFault.setText(bean.fault);
                            txtBuyDetailBuyDate.setText(bean.buyday);
                            txtBuyDetailExpire.setText(bean.expire);
                            txtBuyDetailSize.setText(bean.size);

                            txtBuyDetailDate.setText(bean.date);
                            txtBuyDetailId.setText(bean.userId);
                        }
                    }
                }

                if (mFleaAdapter != null) {
                    mFleaAdapter.setList(mFleaList);
                    mFleaAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    } //end onCreate



    /*
     * 1. 상단 아이디(글쓴이 아이디)와 로그인 아이디가 같으면 수정, 삭제버튼 visibility 풀기
     * 2. 댓글 구현 (db를 더 만들어야 하는가??, 뿌린다면 리스트로?)
     * */


}
