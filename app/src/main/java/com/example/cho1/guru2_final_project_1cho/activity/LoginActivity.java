package com.example.cho1.guru2_final_project_1cho.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.cho1.guru2_final_project_1cho.R;
import com.example.cho1.guru2_final_project_1cho.bean.MemberBean;
import com.example.cho1.guru2_final_project_1cho.db.FileDB;

public class LoginActivity extends AppCompatActivity {

    //멤버변수 자리
    private EditText mEdtId, mEdtPw;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mEdtId = findViewById(R.id.edtId);
        mEdtPw = findViewById(R.id.edtPw);
        Button btnLogin = findViewById(R.id.btnLogin);
        Button btnJoin = findViewById(R.id.btnJoin);

        btnLogin.setOnClickListener(mBtnLoginClick);
        btnJoin.setOnClickListener(mBtnJoinClick);

    }//end onCreate()

    //로그인 버튼 클릭 이벤트
    private View.OnClickListener mBtnLoginClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            String memId = mEdtId.getText().toString();
            String memPw = mEdtPw.getText().toString();

            MemberBean memberBean = FileDB.getFindMember(LoginActivity.this, memId);
            if(memberBean == null) {
                Toast.makeText(LoginActivity.this, "해당 아이디는 가입이 되어 있지 않습니다.", Toast.LENGTH_LONG).show();
                return;
            }
            //패스워드 비교
            if( TextUtils.equals(memberBean.memPw, memPw) ) {
                FileDB.setLoginMember(LoginActivity.this, memberBean); //저장
                //비밀번호 일치
                Intent i = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(i);
            } else {
                Toast.makeText(LoginActivity.this, "패스워드가 일치하지 않습니다.", Toast.LENGTH_LONG).show();
                return;
            }
        }
    };

    //회원가입 버튼 클릭 이벤트
    private View.OnClickListener mBtnJoinClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent ii = new Intent(LoginActivity.this, CameraCaptureActivity.class);
            startActivity(ii);
        }
    };
}