package com.example.cho1.guru2_final_project_1cho.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.cho1.guru2_final_project_1cho.R;
import com.example.cho1.guru2_final_project_1cho.bean.MemberBean;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ModifyMemberActivity extends AppCompatActivity {

    private MemberBean loginMember;
    private EditText mEdtDetailId, mEdtDetailName, mEdtDetailPw1, mEdtDetailPw2, mEdtDetailKakakoId;
    private Button mBtnModify, mBtnLogout;
    private ImageView mImgDetailProfile;

    private String id, name, pw, kakaoId, imgUrl, uuid;

    private FirebaseAuth mFirebaseAuth = FirebaseAuth.getInstance();
    private FirebaseDatabase mFirebaseDatabase = FirebaseDatabase.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_member);

        mEdtDetailId = findViewById(R.id.edtDetailId);
        mEdtDetailName = findViewById(R.id.edtDetailName);
        mEdtDetailPw1 = findViewById(R.id.edtDetailPw1);
        mEdtDetailPw2 = findViewById(R.id.edtDetailPw2);
        mEdtDetailKakakoId = findViewById(R.id.edtDetailKakaoId);
        mBtnModify = findViewById(R.id.btnModify);
        mBtnLogout = findViewById(R.id.btnLogout);
        mImgDetailProfile = findViewById(R.id.imgDetailProfile);

        if(getIntent().getSerializableExtra("loginMember") != null) {
            loginMember = (MemberBean) getIntent().getSerializableExtra("loginMember");
            id = loginMember.memId;
            name = loginMember.memName;
            pw = loginMember.memPw;
            kakaoId = loginMember.memKakaoId;
            imgUrl = loginMember.imgUrl;

            mEdtDetailId.setText(id);
            mEdtDetailId.setEnabled(false);
            mEdtDetailName.setText(name);
            mEdtDetailName.setEnabled(false);
            mEdtDetailPw1.setText(pw);
            mEdtDetailKakakoId.setText(kakaoId);
            mImgDetailProfile.setImageURI(Uri.parse(imgUrl));
        } else {
            id = mFirebaseAuth.getCurrentUser().getEmail();
            uuid = JoinActivity.getUserIdFromUUID(id);

            mFirebaseDatabase.getReference().child("member").child(uuid).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    MemberBean bean = dataSnapshot.getValue(MemberBean.class);
                    if (TextUtils.equals(bean.memId, id)) {
                        pw = bean.memPw;
                        name = bean.memName;
                        kakaoId = bean.memKakaoId;
                        imgUrl = bean.imgUrl;

                        mEdtDetailId.setText(id);
                        mEdtDetailId.setEnabled(false);
                        mEdtDetailName.setText(name);
                        mEdtDetailName.setEnabled(false);
                        mEdtDetailPw1.setText(pw);
                        mEdtDetailKakakoId.setText(kakaoId);
                        //mImgDetailProfile.setImageURI(Uri.parse(imgUrl));
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) { }
            });
        }

        //로그아웃 버튼
        mBtnLogout.setOnClickListener(mClicks);
        //수정 버튼
        mBtnModify.setOnClickListener(mClicks);
    }

    //로그아웃 처리
    private void logout() {
        try{
            mFirebaseAuth.signOut();
            Toast.makeText(this, "로그아웃 되었습니다.", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    private View.OnClickListener mClicks = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.btnLogout:
                    logout();
                    break;

                case R.id.btnModify:
                    //TODO
                    break;
            }
        }
    };

}
