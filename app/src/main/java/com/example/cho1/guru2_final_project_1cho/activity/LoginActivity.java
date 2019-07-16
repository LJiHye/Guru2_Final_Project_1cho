package com.example.cho1.guru2_final_project_1cho.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;

public class LoginActivity extends AppCompatActivity {

    //멤버변수 자리
    private EditText mEdtId, mEdtPw;

    // 구글 로그인 클라이언트 제어자
    private GoogleSignInClient mGoogleSignInClient;

    //FireBase 인증객체 할당
    public static final String STORAGE_DB_URL = "gs://guru2-final-project-1cho.appspot.com";
    private FirebaseAuth mFirebaseAuth = FirebaseAuth.getInstance();
    private FirebaseStorage mFirebaseStorage = FirebaseStorage.getInstance(STORAGE_DB_URL);
    private FirebaseDatabase mFirebaseDatabase = FirebaseDatabase.getInstance();

    private String userMail;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mEdtId = findViewById(R.id.edtId);
        mEdtPw = findViewById(R.id.edtPw);

        // 구글 로그인 객체선언
        GoogleSignInOptions googleSignInOptions =
                new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestIdToken(getString(R.string.default_web_client_id))
                        .requestEmail()
                        .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions);

        findViewById(R.id.btnLogin).setOnClickListener(mClicks);
        findViewById(R.id.btnJoin).setOnClickListener(mClicks);
        findViewById(R.id.btnGoogleSignIn).setOnClickListener(mClicks);

    }//end onCreate()

    // 버튼 클릭 이벤트
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
                    finish();
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

    private void googleSignIn() {
        Intent i = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(i, 1004);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // 구글 로그인 버튼 응답
        if(requestCode == 1004) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // 구글 로그인 성공
                final GoogleSignInAccount account = task.getResult(ApiException.class);
                Toast.makeText(getBaseContext(), "구글 로그인 성공", Toast.LENGTH_SHORT).show();

                userMail = mFirebaseAuth.getCurrentUser().getEmail();
                String guid = JoinActivity.getUserIdFromUUID(userMail); // 고유 id

                mFirebaseDatabase.getReference().child("member").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        boolean flag = false;

                        for(DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            MemberBean bean = snapshot.getValue(MemberBean.class);
                            if(TextUtils.equals(bean.memId, userMail)) {
                                //FileDB.addMember(LoginActivity.this, bean); 이거 말고
                                //저장 setLoginMember
                                flag = true;
                                FileDB.setLoginMember(LoginActivity.this, bean);
                                // firebase 로그인..?
                                firebaseAuthWithGoogle(account);
                                break;
                            }
                        }

                        if(!flag) {
                            // 회원이 없는 경우..?
                            Intent i = new Intent(LoginActivity.this, JoinActivity.class);
                            i.putExtra("userEmail", userMail);
                            i.putExtra("tokenId", account.getIdToken());
                            startActivity(i);
                            finish();
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) { }
                });
            } catch (ApiException e) {
                e.printStackTrace();
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {
        //Firebase 인증
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        mFirebaseAuth.signInWithCredential(credential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()) {
                    //Firebase 로그인 성공
                    Toast.makeText(getBaseContext(), "FireBase 로그인 성공", Toast.LENGTH_SHORT).show();
                    //메인 화면으로 이동
                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                    finish();
                } else {
                    // 로그인 실패
                    Toast.makeText(getBaseContext(), "FireBase 로그인 실패", Toast.LENGTH_SHORT).show();
                    Log.w("TEST", "인증실패: " + task.getException());
                }
            }
        });
    }


}