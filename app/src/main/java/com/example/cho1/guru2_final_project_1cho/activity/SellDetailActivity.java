package com.example.cho1.guru2_final_project_1cho.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.cho1.guru2_final_project_1cho.R;
import com.example.cho1.guru2_final_project_1cho.bean.CommentBean;
import com.example.cho1.guru2_final_project_1cho.bean.FleaBean;
import com.example.cho1.guru2_final_project_1cho.bean.MemberBean;
import com.example.cho1.guru2_final_project_1cho.db.FileDB;
import com.example.cho1.guru2_final_project_1cho.firebase.CommentAdapter;
import com.example.cho1.guru2_final_project_1cho.firebase.SellAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class SellDetailActivity extends AppCompatActivity {

    private FirebaseAuth mFirebaseAuth = FirebaseAuth.getInstance();
    private FirebaseDatabase mFirebaseDB = FirebaseDatabase.getInstance();

    private FleaBean mFleaBean;
    private List<FleaBean> mFleaList = new ArrayList<>();
    private SellAdapter mSellAdapter;

    private List<CommentBean> mCommentList = new ArrayList<>();
    private CommentAdapter mCommentAdapter;
    private ListView lstSellComment;
    private Button btnSellComment;
    private EditText edtSellComment;

    private MemberBean mLoginMember;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sell_detail);

        mFleaBean = (FleaBean) getIntent().getSerializableExtra("ITEM");
        mLoginMember = FileDB.getLoginMember(this);

        final TextView txtSellDetailId = findViewById(R.id.txtSellDetailId); //아이디
        final TextView txtSellDetailDate = findViewById(R.id.txtSellDetailDate); //날짜

        ImageView imgDetail = findViewById(R.id.imgDetail);
        final TextView txtSellTitle = findViewById(R.id.txtSellTitle);
        final TextView txtSellDetailPrice = findViewById(R.id.txtSellDetailPrice); //희망가
        final TextView txtSellDetailOption = findViewById(R.id.txtSellDetailOption); //희망 옵션

        final LinearLayout layoutVisibility = findViewById(R.id.layoutVisibility); //수정, 삭제 버튼 감싼 레이아웃
        Button btnModify = findViewById(R.id.btnModify);
        Button btnDel = findViewById(R.id.btnDel);

        lstSellComment = findViewById(R.id.lstSellComment);
        btnSellComment = findViewById(R.id.btnSellComment);
        edtSellComment = findViewById(R.id.edtSellComment);

        mCommentAdapter = new CommentAdapter(this, mCommentList);
        lstSellComment.setAdapter(mCommentAdapter);

        //상단 아이디 바 글쓴이 아이디, 올린 날짜 출력
        mFirebaseDB.getReference().child("sell").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //데이터를 받아와서 List에 저장.
                mFleaList.clear();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    for (DataSnapshot snapshot2 : snapshot.getChildren()) {
                        FleaBean bean = snapshot2.getValue(FleaBean.class);
                        if(TextUtils.equals(bean.id,mFleaBean.id)) {
                            txtSellTitle.setText(bean.selltitle);
                            txtSellDetailOption.setText(bean.wishoption);
                            txtSellDetailPrice.setText(bean.wishprice);
                            txtSellDetailId.setText(bean.userId);
                            txtSellDetailDate.setText(bean.date);
                            //상단 아이디(글쓴이 아이디)와 로그인 아이디가 같으면 수정, 삭제버튼 visibility 풀기
                            if (TextUtils.equals(mFleaBean.userId, mFirebaseAuth.getCurrentUser().getEmail())) {
                                layoutVisibility.setVisibility(View.VISIBLE);
                            }
                        }
                    }
                }

                if (mSellAdapter != null) {
                    mSellAdapter.setList(mFleaList);
                    mSellAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

        btnSellComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatabaseReference dbRef = mFirebaseDB.getReference();
                String id = dbRef.push().getKey(); // key 를 메모의 고유 ID 로 사용한다.

                CommentBean commentBean = new CommentBean();
                commentBean.comment = edtSellComment.getText().toString();
                commentBean.date = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date());
                commentBean.userId = mLoginMember.memId;
                commentBean.id = id;

                //고유번호를 생성한다
                String guid = JoinActivity.getUserIdFromUUID(mFleaBean.userId);
                String uuid = JoinActivity.getUserIdFromUUID(mLoginMember.memId);
                dbRef.child("sell").child( guid ).child( mFleaBean.id ).child("comments").child(uuid).setValue(commentBean);
                Toast.makeText(SellDetailActivity.this, "게시물이 등록 되었습니다.", Toast.LENGTH_LONG).show();

                dbRef.child("sell").child( guid ).child( mFleaBean.id ).child("comments").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        //데이터를 받아와서 List에 저장.
                        mCommentList.clear();

                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            CommentBean bean = snapshot.getValue(CommentBean.class);
                            mCommentList.add(0, bean);
                        }
                        //바뀐 데이터로 Refresh 한다.
                        if (mCommentAdapter != null) {
                            mCommentAdapter.setList(mCommentList);
                            mCommentAdapter.notifyDataSetChanged();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {}
                });
            }
        });
    }

        /*
         * 1. 상단 아이디(글쓴이 아이디)와 로그인 아이디가 같으면 수정, 삭제버튼 visibility 풀기
         * 2. 기존에 올린 게시물에서 값 가져와서 setText    */


        /* 3. 댓글 구현 (db를 더 만들어야 하는가??, 뿌린다면 리스트로?)
         * */

}
