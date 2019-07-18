package com.example.cho1.guru2_final_project_1cho.activity;

import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.cho1.guru2_final_project_1cho.R;
import com.example.cho1.guru2_final_project_1cho.bean.MemberBean;
import com.example.cho1.guru2_final_project_1cho.db.FileDB;
import com.example.cho1.guru2_final_project_1cho.firebase.DownloadImgTaskMember;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.net.URL;

public class ModifyMemberActivity extends AppCompatActivity {

    private MemberBean loginMember;
    private EditText mEdtDetailId, mEdtDetailName, mEdtDetailPw, mEdtDetailPw1, mEdtDetailPw2, mEdtDetailKakakoId;
    private Button mBtnModify, mBtnLogout;
    private ImageView mImgDetailProfile;

    private String id, name, kakaoId, imgUrl, uuid;
    private boolean googleLoginFlag;

    private FirebaseAuth mFirebaseAuth = FirebaseAuth.getInstance();
    private FirebaseDatabase mFirebaseDatabase = FirebaseDatabase.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_member);

        loginMember = FileDB.getLoginMember(this);
        googleLoginFlag = getIntent().getBooleanExtra("googleLogin", false);

        mEdtDetailId = findViewById(R.id.edtDetailId);
        mEdtDetailName = findViewById(R.id.edtDetailName);
        mEdtDetailPw = findViewById(R.id.edtDetailPw);
        mEdtDetailPw1 = findViewById(R.id.edtDetailPw1);
        mEdtDetailPw2 = findViewById(R.id.edtDetailPw2);
        mEdtDetailKakakoId = findViewById(R.id.edtDetailKakaoId);
        mBtnModify = findViewById(R.id.btnModify);
        mBtnLogout = findViewById(R.id.btnLogout);
        mImgDetailProfile = findViewById(R.id.imgDetailProfile);
        GradientDrawable drawable=
                (GradientDrawable) this.getDrawable(R.drawable.background_rounding);
        mImgDetailProfile.setBackground(drawable);
        mImgDetailProfile.setClipToOutline(true);

        id = loginMember.memId;
        name = loginMember.memName;
        //pw = loginMember.memPw;
        kakaoId = loginMember.memKakaoId;
        imgUrl = loginMember.imgUrl;

        // imtTitle 이미지를 표시할 때는 원격 서버에 있는 이미지이므로, 비동기로 표시한다.
        try{
            if(loginMember.bmpTitle == null) {
                new DownloadImgTaskMember(this, mImgDetailProfile, loginMember).execute(new URL(loginMember.imgUrl));
            } else {
                mImgDetailProfile.setImageBitmap(loginMember.bmpTitle);
            }
        } catch(Exception e) {
            e.printStackTrace();
        }

        mEdtDetailId.setText(id);
        mEdtDetailId.setEnabled(false);
        mEdtDetailName.setText(name);
        mEdtDetailName.setEnabled(false);
        mEdtDetailKakakoId.setText(kakaoId);

        //로그아웃 버튼
        mBtnLogout.setOnClickListener(mClicks);
        //수정 버튼
        mBtnModify.setOnClickListener(mClicks);
    }

    //로그아웃 처리
    private void logout() {
        try{
            if(googleLoginFlag) {
                mFirebaseAuth.signOut();
            }
            Toast.makeText(this, "로그아웃 되었습니다.", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    //회원정보 수정 처리
    private void modify() {
        String pw1 = mEdtDetailPw1.getText().toString();
        String pw2 = mEdtDetailPw2.getText().toString();
        String pw = mEdtDetailPw.getText().toString();

        if(!TextUtils.isEmpty(pw) || !TextUtils.isEmpty(pw1) || !TextUtils.isEmpty(pw2)) { // 패스워드 칸에 뭐라도 입력한 경우
            if (!TextUtils.equals(pw, loginMember.memPw)) {
                Toast.makeText(ModifyMemberActivity.this, "현재 패스워드가 일치하지 않습니다", Toast.LENGTH_SHORT).show();
                return;
            }

            if (TextUtils.isEmpty(pw1) || TextUtils.isEmpty(pw2)) {
                Toast.makeText(ModifyMemberActivity.this, "변경할 패스워드를 입력해 주세요", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!TextUtils.equals(pw1, pw2)) {
                Toast.makeText(ModifyMemberActivity.this, "변경할 패스워드가 일치하지 않습니다", Toast.LENGTH_SHORT).show();
                return;
            }

            loginMember.memPw = mEdtDetailPw1.getText().toString();
        }

        loginMember.memKakaoId = mEdtDetailKakakoId.getText().toString();
        uuid = JoinActivity.getUserIdFromUUID(loginMember.memId);

        mFirebaseDatabase.getReference().child("member").child(uuid).setValue(loginMember);
        Toast.makeText(ModifyMemberActivity.this, "수정이 완료되었습니다.", Toast.LENGTH_SHORT).show();
        FileDB.setLoginMember(this, loginMember);

        finish();
    }

    //버튼 클릭 이벤트
    private View.OnClickListener mClicks = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.btnLogout:
                    logout();
                    break;

                case R.id.btnModify:
                    modify();
                    break;
            }
        }
    };

}
