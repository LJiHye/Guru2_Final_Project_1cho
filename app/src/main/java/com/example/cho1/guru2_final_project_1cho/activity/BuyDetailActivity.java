package com.example.cho1.guru2_final_project_1cho.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.media.Image;
import android.os.Bundle;
import android.renderscript.ScriptGroup;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.cho1.guru2_final_project_1cho.R;
import com.example.cho1.guru2_final_project_1cho.bean.CommentBean;
import com.example.cho1.guru2_final_project_1cho.bean.FleaBean;
import com.example.cho1.guru2_final_project_1cho.firebase.DownloadImgTaskFlea;
import com.example.cho1.guru2_final_project_1cho.bean.MemberBean;
import com.example.cho1.guru2_final_project_1cho.db.FileDB;
import com.example.cho1.guru2_final_project_1cho.firebase.CommentAdapter;
import com.example.cho1.guru2_final_project_1cho.firebase.FleaAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class BuyDetailActivity extends AppCompatActivity {
    private TextView txtBuyDetailDate;
    private FirebaseAuth mFirebaseAuth = FirebaseAuth.getInstance();
    private FirebaseDatabase mFirebaseDB = FirebaseDatabase.getInstance();

    private Context mContext;

    private FleaBean mFleaBean;
    private ImageView imgDetail;
    private TextView txtBuyDetailId, txtBuyDetailProduct, txtBuyDetailPrice, txtBuyDetailFinalPrice,
            txtBuyDetailState, txtBuyDetailFault, txtBuyDetailBuyDate, txtBuyDetailExpire, txtBuyDetailSize;
    private ListView lstBuyComment;
    private Button btnBuyComment;
    private EditText edtBuyComment;

    private List<FleaBean> mFleaList = new ArrayList<>();
    private FleaAdapter mFleaAdapter;
    private MemberBean mLoginMember;

    private List<CommentBean> mCommentList = new ArrayList<>();
    private CommentAdapter mCommentAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buy_detail);

        mFleaBean = (FleaBean) getIntent().getSerializableExtra("ITEM");
        CommentAdapter.setFleaBean(mFleaBean);
        mLoginMember = FileDB.getLoginMember(this);

        lstBuyComment = findViewById(R.id.lstBuyComment);

        // Header, Footer 생성 및 등록
        View header = getLayoutInflater().inflate(R.layout.activity_buy_detail_header, null, false);
        View footer = getLayoutInflater().inflate(R.layout.activity_buy_detail_footer, null, false);

        lstBuyComment.addHeaderView(header);
        lstBuyComment.addFooterView(footer);

        btnBuyComment = findViewById(R.id.btnBuyComment);
        edtBuyComment = findViewById(R.id.edtBuyComment);

        //edtBuyComment.requestFocus();

        txtBuyDetailId = header.findViewById(R.id.txtBuyDetailId); //아이디
        txtBuyDetailDate = header.findViewById(R.id.txtBuyDetailDate); //날짜
        imgDetail = header.findViewById(R.id.imgDetail); //이미지
        GradientDrawable drawable=
                (GradientDrawable) this.getDrawable(R.drawable.background_rounding);
        imgDetail.setBackground(drawable);
        imgDetail.setClipToOutline(true);
        txtBuyDetailProduct = header.findViewById(R.id.txtBuyDetailProduct); //제품명
        txtBuyDetailPrice = header.findViewById(R.id.txtBuyDetailPrice); //정가
        txtBuyDetailFinalPrice = header.findViewById(R.id.txtBuyDetailFinalPrice); //판매가
        txtBuyDetailState = header.findViewById(R.id.txtBuyDetailState); //제품상태
        txtBuyDetailFault = header.findViewById(R.id.txtBuyDetailFault); //하자유무
        txtBuyDetailBuyDate = header.findViewById(R.id.txtBuyDetailBuyDate); //구매일
        txtBuyDetailExpire = header.findViewById(R.id.txtBuyDetailExpire); //유통기한
        txtBuyDetailSize = header.findViewById(R.id.txtBuyDetailSize); //실측사이즈

        mCommentAdapter = new CommentAdapter(this, mCommentList);
        lstBuyComment.setAdapter(mCommentAdapter);


        LinearLayout layoutBuyVisibility = findViewById(R.id.layoutBuyVisibility); //수정, 삭제 버튼 감싼 레이아웃
        Button btnModify = footer.findViewById(R.id.btnModify);
        Button btnDel = footer.findViewById(R.id.btnDel);

        //상단 아이디(글쓴이 아이디)와 로그인 아이디가 같으면 수정, 삭제버튼 visibility 풀기
        if (TextUtils.equals(mFleaBean.userId, mFirebaseAuth.getCurrentUser().getEmail())) {
            layoutBuyVisibility.setVisibility(View.VISIBLE);
        }


        //상단 아이디바(아이디, 날짜), 글 내용 불러와 출력
        mCommentAdapter = new CommentAdapter(this, mCommentList);
        lstBuyComment.setAdapter(mCommentAdapter);


        mFirebaseDB.getReference().child("buy").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //데이터를 받아와서 List에 저장.
                mFleaList.clear();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    for (DataSnapshot snapshot2 : snapshot.getChildren()) {
                        FleaBean bean = snapshot2.getValue(FleaBean.class);
                        if (TextUtils.equals(bean.id, mFleaBean.id)) {
                            imgDetail.setImageBitmap(bean.bmpTitle);
                            //imgDetail.setImageURI(bean.imgUrl);
//                            final FleaBean fleaBean = mFleaList.get(i);
//
//                            // imgTitle 이미지를 표시할 때는 원격 서버에 있는 이미지이므로, 비동기로 표시한다.
//                            try{
//                                if(fleaBean.bmpTitle == null) {
//                                    new DownloadImgTaskFlea(mContext, imgDetail, mFleaList, ).execute(new URL(fleaBean.imgUrl));
//                                } else {
//                                    imgDetail.setImageBitmap(fleaBean.bmpTitle);
//                                }
//                            } catch(Exception e) {
//                                e.printStackTrace();
//                            }

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

        btnBuyComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!TextUtils.isEmpty(edtBuyComment.getText().toString())) {
                    DatabaseReference dbRef = mFirebaseDB.getReference();
                    String id = dbRef.push().getKey(); // key 를 메모의 고유 ID 로 사용한다.

                    CommentBean commentBean = new CommentBean();
                    commentBean.comment = edtBuyComment.getText().toString();
                    commentBean.date = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date());
                    commentBean.userId = mLoginMember.memId;
                    commentBean.id = id;
                    commentBean.flag = 1;

                    //고유번호를 생성한다
                    String guid = JoinActivity.getUserIdFromUUID(mFleaBean.userId);
                    String uuid = JoinActivity.getUserIdFromUUID(mLoginMember.memId);
                    dbRef.child("buy").child( guid ).child( mFleaBean.id ).child("comments").child(id).setValue(commentBean);
                    Toast.makeText(BuyDetailActivity.this, "댓글이 등록 되었습니다", Toast.LENGTH_LONG).show();
                    edtBuyComment.setText(null);
                    if(view != null) {
                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                    }

                    dbRef.child("buy").child( guid ).child( mFleaBean.id ).child("comments").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            //데이터를 받아와서 List에 저장.
                            mCommentList.clear();
                        /*mFleaBean.commentList.clear();
                        mLoginMember.commentList.clear();*/

                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                CommentBean bean = snapshot.getValue(CommentBean.class);
                                mCommentList.add(bean);
                            /*mFleaBean.commentList.add(bean);
                            mLoginMember.commentList.add(bean);*/
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
                } else {
                    Toast.makeText(BuyDetailActivity.this, "댓글을 입력하세요", Toast.LENGTH_SHORT).show();
                }
            }
        });

        //수정버튼
        btnModify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(BuyDetailActivity.this, BuyModifyActivity.class);
                startActivity(intent);
            }
        });
    }

   @Override
    protected void onResume() {
        super.onResume();

        //데이터 취득
        //String userEmail = mFirebaseAuth.getCurrentUser().getEmail();
        //String uuid = SellWriteActivity.getUserIdFromUUID(userEmail);
       DatabaseReference dbRef = mFirebaseDB.getReference();
       String guid = JoinActivity.getUserIdFromUUID(mFleaBean.userId);
       dbRef.child("buy").child( guid ).child( mFleaBean.id ).child("comments").addValueEventListener(new ValueEventListener() {
           @Override
           public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
               //데이터를 받아와서 List에 저장.
               mCommentList.clear();

               for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                   CommentBean bean = snapshot.getValue(CommentBean.class);
                   mCommentList.add(bean);
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
}
