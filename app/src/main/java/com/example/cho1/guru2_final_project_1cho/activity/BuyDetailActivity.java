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

import com.example.cho1.guru2_final_project_1cho.R;
import com.example.cho1.guru2_final_project_1cho.bean.FleaBean;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class BuyDetailActivity extends AppCompatActivity {
    private TextView txtBuyDetailDate;
    private FirebaseAuth mFirebaseAuth = FirebaseAuth.getInstance();
    private FirebaseDatabase mFirebaseDB = FirebaseDatabase.getInstance();

    private FleaBean mFleaBean;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buy_detail);

        TextView txtBuyDetailId = findViewById(R.id.txtBuyDetailId); //아이디
       txtBuyDetailDate = findViewById(R.id.txtBuyDetailDate);

        ImageView imgDetail = findViewById(R.id.imgDetail); //이미지
        TextView txtBuyDetailProduct = findViewById(R.id.txtBuyDetailProduct); //제품명
        TextView txtBuyDetailPrice = findViewById(R.id.txtBuyDetailPrice); //정가
        TextView txtBuyDetailFinalPrice = findViewById(R.id.txtBuyDetailFinalPrice); //판매가
        TextView txtBuyDetailState = findViewById(R.id.txtBuyDetailState); //제품상태
        TextView txtBuyDetailFault = findViewById(R.id.txtBuyDetailFault); //하자유무
        TextView txtBuyDetailBuyDate = findViewById(R.id.txtBuyDetailBuyDate); //구매일
        TextView txtBuyDetailExpire = findViewById(R.id.txtBuyDetailExpire); //유통기한
        TextView txtBuyDetailSize = findViewById(R.id.txtBuyDetailSize); //실측사이즈

        LinearLayout layoutVisibility = findViewById(R.id.layoutVisibility); //수정, 삭제 버튼 감싼 레이아웃
        Button btnModify = findViewById(R.id.btnModify);
        Button btnDel = findViewById(R.id.btnDel);


        //상단 아이디 바 글쓴이 아이디, 올린 날짜 출력
        String userEmail = mFirebaseAuth.getCurrentUser().getEmail();
        final String uuid = BuyWriteActivity.getUserIdFromUUID(userEmail);

        //txtBuyDetailId.setText(mFirebaseDB.getReference());
        //txtBuyDetailDate.setText(mFirebaseDB.getReference().child("buy").child(uuid).child(mFleaBean.date));

        //상단 아이디(글쓴이 아이디)와 로그인 아이디가 같으면 수정, 삭제버튼 visibility 풀기
//        if (TextUtils.equals(mFleaBean.userId, mFirebaseAuth.getCurrentUser().getEmail())) {
//            layoutVisibility.setVisibility(View.VISIBLE);
//        }

        DatabaseReference userDBRef = FirebaseDatabase.getInstance().getReference();
        userDBRef.child("buy").child(uuid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                FleaBean fleaOption = dataSnapshot.child(uuid).getValue(FleaBean.class);
                //txtBuyDetailDate.setText(fleaOption.date);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        Intent intent = getIntent(); //던진 인텐트 받기
        FleaBean fleaOption;

        int index = intent.getIntExtra("INDEX", -1); //만약 인덱스를 못받았다면 -1을 보내도록 디폴트 값 지정
        if (index != -1){
            fleaOption = (FleaBean) intent.getSerializableExtra("ITEM");

            //원본 Item을 UI에 적용
//            imgDetail.setImageResource(fleaOption.());
//            txtViewTitle.setText(fleaOption.getTitle());
//            txtViewSubTitle.setText(fleaOption.getSubTitle());
//            txtViewPrice.setText(fleaOption.getPrice());
        }


        /*
         * 1. 상단 아이디(글쓴이 아이디)와 로그인 아이디가 같으면 수정, 삭제버튼 visibility 풀기
         * 2. 기존에 올린 게시물에서 값 가져와서 setText
         * 3. 댓글 구현 (db를 더 만들어야 하는가??, 뿌린다면 리스트로?)
         * */
    }
}
