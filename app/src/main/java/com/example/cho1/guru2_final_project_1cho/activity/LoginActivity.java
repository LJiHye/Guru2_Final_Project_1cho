package com.example.cho1.guru2_final_project_1cho.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.cho1.guru2_final_project_1cho.R;
import com.example.cho1.guru2_final_project_1cho.bean.MemberBean;
import com.example.cho1.guru2_final_project_1cho.db.FileDB;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;

public class LoginActivity extends AppCompatActivity {

    //멤버변수 자리
    private EditText mEdtId, mEdtPw;

    //구글 로그인 클라이언트 제어자
    private GoogleSignInClient mGoogleSignInClient;
    //FireBase 인증 객체
    private FirebaseAuth mFirebaseAuth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mEdtId = findViewById(R.id.edtId);
        mEdtPw = findViewById(R.id.edtPw);
        Button btnLogin = findViewById(R.id.btnLogin);
        Button btnJoin = findViewById(R.id.btnJoin);

        findViewById(R.id.btnGoogleSignIn).setOnClickListener(mClicks);
        findViewById(R.id.btnLogin).setOnClickListener(mClicks);
        findViewById(R.id.btnJoin).setOnClickListener(mClicks);
//        btnLogin.setOnClickListener(mBtnLoginClick);
//        btnJoin.setOnClickListener(mBtnJoinClick);

        //구글 로그인 객체선언
        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions);
    }//end onCreate()

    @Override
    protected void onResume() {
        super.onResume();

        if(mFirebaseAuth.getCurrentUser() != null && mFirebaseAuth.getCurrentUser().getEmail() != null) {
            //이미 로그인 되어 있다. 따라서 메인 화면으로 바로 이동한다.
            Toast.makeText(this, "로그인 성공 - 메인화면 이동",Toast.LENGTH_LONG).show();
            goMainActivity();
        }
    }

    //게시판 메인 화면으로 이동
    private void goMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

//    //로그인 버튼 클릭 이벤트
//    private View.OnClickListener mBtnLoginClick = new View.OnClickListener() {
//        @Override
//        public void onClick(View view) {
//            String memId = mEdtId.getText().toString();
//            String memPw = mEdtPw.getText().toString();
//
//            MemberBean memberBean = FileDB.getFindMember(LoginActivity.this, memId);
//            if(memberBean == null) {
//                Toast.makeText(LoginActivity.this, "해당 아이디는 가입이 되어 있지 않습니다.", Toast.LENGTH_LONG).show();
//                return;
//            }
//            //패스워드 비교
//            if( TextUtils.equals(memberBean.memPw, memPw) ) {
//                FileDB.setLoginMember(LoginActivity.this, memberBean); //저장
//                //비밀번호 일치
//                Intent i = new Intent(LoginActivity.this, MainActivity.class);
//                startActivity(i);
//            } else {
//                Toast.makeText(LoginActivity.this, "패스워드가 일치하지 않습니다.", Toast.LENGTH_LONG).show();
//                return;
//            }
//        }
//    };
//
//    //회원가입 버튼 클릭 이벤트
//    private View.OnClickListener mBtnJoinClick = new View.OnClickListener() {
//        @Override
//        public void onClick(View view) {
//            Intent ii = new Intent(LoginActivity.this, CameraCaptureActivity.class);
//            startActivity(ii);
//        }
//    };

    private View.OnClickListener mClicks = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.btnGoogleSignIn:
                    googleSignIn();
                    break;

                case R.id.btnJoin:
                    Intent ii = new Intent(LoginActivity.this, CameraCaptureActivity.class);
                    startActivity(ii);
                    break;

                case R.id.btnLogin:
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
                    break;
            }
        }
    };

    //구글 로그인 처리
    private void googleSignIn(){
        Intent i = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(i, 1004);
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {
        //FireBase 인증
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        mFirebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()) {
                            //FireBase 로그인 성공
                            Toast.makeText(getBaseContext(), "Firebase 로그인 성공", Toast.LENGTH_LONG).show();

                            //메인 화면으로 이동
                            goMainActivity();
                            finish();
                        } else {
                            //로그인 실패
                            Toast.makeText(getBaseContext(), "Firebase 로그인 실패", Toast.LENGTH_LONG).show();

                            Log.w("TEST", "인증실패 : " + task.getException());
                        }
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //구글 로그인 버튼 응답
        if (requestCode == 1004) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                //구글 로그인 성공
                GoogleSignInAccount account = task.getResult(ApiException.class);
                Toast.makeText(getBaseContext(), "로그인에 성공 하였습니다.", Toast.LENGTH_LONG).show();

                //FireBase 인증하러 가자
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                e.printStackTrace();
            }
        }
    }
}