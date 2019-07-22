package com.example.cho1.guru2_final_project_1cho.activity;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.cho1.guru2_final_project_1cho.R;
import com.example.cho1.guru2_final_project_1cho.bean.CommentBean;
import com.example.cho1.guru2_final_project_1cho.bean.MemberBean;
import com.example.cho1.guru2_final_project_1cho.firebase.CommentAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class FreeShareDetailActivity extends AppCompatActivity {
    private FirebaseAuth mFirebaseAuth = FirebaseAuth.getInstance();
    private FirebaseDatabase mFirebaseDB = FirebaseDatabase.getInstance();

    private TextView txtFreeDetailId, txtFreeDetailDate, txtFreeTitle, txtFreeDetailPlace, txtFreeDetailOption;
    private ImageView imgFreeDetail,imgEmptyStar,imgFullStar;


    private Context mContext;

    private List<CommentBean> mCommentList = new ArrayList<>();
    private CommentAdapter mCommentAdapter;
    private ListView lstFreeComment;
    private Button btnFreeComment, btnFreeWriter;
    private ImageButton btnFreeModify, btnFreeDel;
    private EditText edtFreeComment;

    private MemberBean mLoginMember;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_free_share_detail_header);

        View header = getLayoutInflater().inflate(R.layout.activity_free_share_detail_header, null, false);

        imgEmptyStar = header.findViewById(R.id.emptyStar); //스크랩버튼

        header.findViewById(R.id.btnFreeModify).setOnClickListener(BtnClick);
        txtFreeDetailId = header.findViewById(R.id.txtFreeDetailId); //아이디
        txtFreeDetailDate = header.findViewById(R.id.txtFreeDetailDate); //날짜
        imgFreeDetail = header.findViewById(R.id.imgFreeDetail);
        GradientDrawable drawable = (GradientDrawable) this.getDrawable(R.drawable.background_rounding);
        imgFreeDetail.setBackground(drawable);
        imgFreeDetail.setClipToOutline(true);

        txtFreeTitle = header.findViewById(R.id.txtFreeTitle);
        txtFreeDetailPlace = header.findViewById(R.id.txtFreeDetailPlace); //무나장소
        txtFreeDetailOption = header.findViewById(R.id.txtFreeDetailOption); //하고싶은말,,

        btnFreeComment = findViewById(R.id.btnFreeComment);
        edtFreeComment = findViewById(R.id.edtFreeComment);
        btnFreeWriter = findViewById(R.id.btnFreeWriter);
        btnFreeModify = findViewById(R.id.btnFreeModify);
        btnFreeDel = findViewById(R.id.btnFreeDel);

        mCommentAdapter = new CommentAdapter(this, mCommentList);
        lstFreeComment.setAdapter(mCommentAdapter);

        //수정, 삭제 버튼에 클릭리스너 달아주기
        header.findViewById(R.id.btnFreeDel).setOnClickListener(BtnClick);
        header.findViewById(R.id.btnFreeWriter).setOnClickListener(BtnClick);
    }

    View.OnClickListener BtnClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

        }
    };
}