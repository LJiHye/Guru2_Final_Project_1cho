package com.example.cho1.guru2_final_project_1cho.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.TextView;

import com.example.cho1.guru2_final_project_1cho.R;
import com.example.cho1.guru2_final_project_1cho.bean.FleaBean;
import com.example.cho1.guru2_final_project_1cho.db.FileDB;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class SellDetailActivity extends AppCompatActivity {

    private FirebaseAuth mFirebaseAuth = FirebaseAuth.getInstance();
    private FirebaseDatabase mFirebaseDB = FirebaseDatabase.getInstance();

    private List<FleaBean> mFleaList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sell_detail);

        TextView txtSellDetailId = findViewById(R.id.txtSellDetailId); //아이디
        TextView txtSellDetailDate = findViewById(R.id.txtSellDetailDate); //날짜

        TextView txtSellDetailPrice = findViewById(R.id.txtSellDetailPrice); //희망가
        TextView txtSellDetailOption = findViewById(R.id.txtSellDetailOption); //희망 옵션

        //FleaBean fleaBean = mFleaList;

        //상단 아이디 바 글쓴이 아이디, 올린 날짜 출력
        txtSellDetailId.setText(mFirebaseAuth.getUid());
        //txtSellDetailDate.setText(mFirebaseDB.getReference().child("memo").);

        //상단 아이디(글쓴이 아이디)와 로그인 아이디가 같으면 수정, 삭제버튼 visibility 풀기
        //if (TextUtils.equals(, mFirebaseAuth.getCurrentUser().getEmail()));

        /*
        * 1. 상단 아이디(글쓴이 아이디)와 로그인 아이디가 같으면 수정, 삭제버튼 visibility 풀기
        * 2. 기존에 올린 게시물에서 값 가져와서 setText
        * 3. 댓글 구현 (db를 더 만들어야 하는가??, 뿌린다면 리스트로?)
        * */
    }
}
