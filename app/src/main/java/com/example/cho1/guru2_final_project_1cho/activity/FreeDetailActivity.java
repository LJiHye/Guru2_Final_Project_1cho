package com.example.cho1.guru2_final_project_1cho.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.GradientDrawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.cho1.guru2_final_project_1cho.R;
import com.example.cho1.guru2_final_project_1cho.bean.CommentBean;
import com.example.cho1.guru2_final_project_1cho.bean.FreeBean;
import com.example.cho1.guru2_final_project_1cho.bean.MemberBean;
import com.example.cho1.guru2_final_project_1cho.db.FileDB;
import com.example.cho1.guru2_final_project_1cho.firebase.CommentAdapter;
import com.example.cho1.guru2_final_project_1cho.firebase.DownloadImgTaskFree;
import com.example.cho1.guru2_final_project_1cho.firebase.FreeAdapter;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.StringTokenizer;

public class FreeDetailActivity extends AppCompatActivity {
    private FirebaseAuth mFirebaseAuth = FirebaseAuth.getInstance();
    private FirebaseDatabase mFirebaseDB = FirebaseDatabase.getInstance();

    private TextView txtFreeDetailId, txtFreeDetailDate, txtFreeTitle, txtFreeDetailPlace, txtFreePlace, txtFreeDetailOption;
    private ImageView imgFreeDetail,imgStar;


    private Context mContext;

    private List<CommentBean> mCommentList = new ArrayList<>();
    private CommentAdapter mCommentAdapter;
    private ListView lstFreeComment;
    private Button btnFreeComment, btnFreeWriter;
    private ImageButton btnFreeModify, btnFreeDel;
    private EditText edtFreeComment;

    private MemberBean mLoginMember;
    private FreeBean mFreeBean;

    private List<String> mScrapList = new ArrayList<>();
    private List<FreeBean> mFreeList = new ArrayList<>();
    private FreeAdapter mFreeAdapter;

    private SupportMapFragment mMapFragment;
    private LocationManager mLocationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_free_detail);

        ActivityCompat.requestPermissions(this, new String[]{
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
        }, 0);

        mLoginMember = FileDB.getLoginMember(this);
        mFreeBean = (FreeBean) getIntent().getSerializableExtra("FREEITEM");
        CommentAdapter.setFreeBean(mFreeBean);

        lstFreeComment = findViewById(R.id.lstFreeComment);
        View header = getLayoutInflater().inflate(R.layout.activity_free_detail_header, null, false);
        lstFreeComment.addHeaderView(header);

        mMapFragment = (SupportMapFragment)getSupportFragmentManager().findFragmentById(R.id.map);

        //GSP 가 켜져 있는지 확인한다.
        mLocationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        if(!mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            //GSP 설정하는 Setting 화면으로 이동한다.
            Intent i = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            i.addCategory(Intent.CATEGORY_DEFAULT);
            startActivity(i);
        }

        if(
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                        ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
        )
        {
            return;
        }

        //GPS 위치를 0.1초마다 10m 간격범위안에서 이동하면 위치를 listener 로 보내주도록 등록한다.
        mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 100, 10, locationListener);
        //WIFI 위치를 0.1초마다 10m 간격범위안에서 이동하면 위치를 listener 로 보내주도록 등록한다.
        mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 100, 10, locationListener);

        imgStar = header.findViewById(R.id.imgFreeStar); //스크랩버튼

        header.findViewById(R.id.btnFreeModify).setOnClickListener(BtnClick);
        txtFreeDetailId = header.findViewById(R.id.txtFreeDetailId); //아이디
        txtFreeDetailDate = header.findViewById(R.id.txtFreeDetailDate); //날짜
        imgFreeDetail = header.findViewById(R.id.imgFreeDetail);
        GradientDrawable drawable = (GradientDrawable) this.getDrawable(R.drawable.background_rounding);
        imgFreeDetail.setBackground(drawable);
        imgFreeDetail.setClipToOutline(true);

        txtFreeTitle = header.findViewById(R.id.txtFreeTitle);
        txtFreePlace=header.findViewById(R.id.txtFreePlace);
        txtFreeDetailPlace = header.findViewById(R.id.txtFreeDetailPlace); //무나장소
        txtFreeDetailOption = header.findViewById(R.id.txtFreeDetailOption); //하고싶은말,,

        btnFreeComment = findViewById(R.id.btnFreeComment);
        edtFreeComment = findViewById(R.id.edtFreeComment);
        btnFreeWriter = header.findViewById(R.id.btnFreeWriter);
        btnFreeModify = header.findViewById(R.id.btnFreeModify);
        btnFreeDel = header.findViewById(R.id.btnFreeDel);

        mCommentAdapter = new CommentAdapter(this, mCommentList);
        lstFreeComment.setAdapter(mCommentAdapter);

        //수정, 삭제 버튼에 클릭리스너 달아주기
        header.findViewById(R.id.btnFreeModify).setOnClickListener(BtnClick);
        header.findViewById(R.id.btnFreeDel).setOnClickListener(BtnClick);
        header.findViewById(R.id.btnFreeWriter).setOnClickListener(BtnClick);
        header.findViewById(R.id.imgFreeStar).setOnClickListener(BtnClick);

        String userEmail = mFirebaseAuth.getCurrentUser().getEmail();
        String uuid = JoinActivity.getUserIdFromUUID(userEmail);
        mFirebaseDB.getReference().child("member").child(uuid).child("scrap").child("free").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mScrapList.clear();

                for(DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String str = snapshot.getValue(String.class);
                    if(!TextUtils.isEmpty(str)) {
                        mScrapList.add(0, str);
                    }
                }

                Boolean exist = false;
                for(int i=0; i<mScrapList.size(); i++) {
                    if(TextUtils.equals(mFreeBean.id, mScrapList.get(i))) {
                        exist = true;
                    }
                }
                if(exist) {
                    imgStar.setImageResource(R.drawable.full_star);
                } else {
                    imgStar.setImageResource(R.drawable.empty_star);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        //상단 아이디 바 글쓴이 아이디, 올린 날짜 출력
        mFirebaseDB.getReference().child("free").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //데이터를 받아와서 List에 저장.
                mFreeList.clear();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    //for (DataSnapshot snapshot2 : snapshot.getChildren()) {
                    FreeBean bean = snapshot.getValue(FreeBean.class);

                    if (TextUtils.equals(bean.id, mFreeBean.id)) {
                        if (mFreeBean != null) {
                            // imgTitle 이미지를 표시할 때는 원격 서버에 있는 이미지이므로 비동기로 표시한다.
                            try {
                                if (bean.bmpTitle == null) {
                                    new DownloadImgTaskFree(mContext, imgFreeDetail, mFreeList, 0).execute(new URL(bean.imgUrl));
                                } else {
                                    imgFreeDetail.setImageBitmap(bean.bmpTitle);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            txtFreeTitle.setText(bean.title);
                            txtFreeDetailOption.setText(bean.explain);

                            StringTokenizer tokens = new StringTokenizer(bean.userId);
                            String userId = tokens.nextToken("@");
                            txtFreeDetailId.setText(userId);
                            txtFreeDetailDate.setText(bean.date);
                            txtFreePlace.setText(bean.place);
                            txtFreeDetailPlace.setText( bean.detailPlace);
                        }

                        LinearLayout layoutExVisibility = findViewById(R.id.layoutFreeVisibility);
                        //상단 아이디(글쓴이 아이디)와 로그인 아이디가 같으면 수정, 삭제버튼 visibility 풀기
                        if (TextUtils.equals(mFreeBean.userId, mFirebaseAuth.getCurrentUser().getEmail())) {
                            btnFreeModify.setVisibility(View.VISIBLE);
                            btnFreeDel.setVisibility(View.VISIBLE);
                        }
                        //상단 아이디(글쓴이 아이디)와 로그인 아이디가 다르면 작성자 페이지 가는 버튼 visibility 풀기
                        if (!TextUtils.equals(mFreeBean.userId, mFirebaseAuth.getCurrentUser().getEmail())) {
                            btnFreeWriter.setVisibility(View.VISIBLE);
                            imgStar.setVisibility(View.VISIBLE);

                        }
                        // }
                    }
                }

                if (mFreeAdapter != null) {
                    mFreeAdapter.setList(mFreeList);
                    mFreeAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

        btnFreeComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!TextUtils.isEmpty(edtFreeComment.getText().toString())) {
                    DatabaseReference dbRef = mFirebaseDB.getReference();
                    String id = dbRef.push().getKey(); // key 를 메모의 고유 ID 로 사용한다.

                    CommentBean commentBean = new CommentBean();
                    commentBean.comment = edtFreeComment.getText().toString();
                    commentBean.date = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date());
                    commentBean.userId = mFirebaseAuth.getCurrentUser().getEmail();
                    commentBean.id = id;
                    commentBean.flag = 4;

                    //고유번호를 생성한다
                    String uuid = JoinActivity.getUserIdFromUUID(mFirebaseAuth.getCurrentUser().getEmail());
                    dbRef.child("free").child(mFreeBean.id).child("comments").child(id).setValue(commentBean);
                    Toast.makeText(FreeDetailActivity.this, "댓글이 등록 되었습니다", Toast.LENGTH_LONG).show();
                    edtFreeComment.setText(null);
                    if (view != null) {
                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                    }
                    lstFreeComment.setTranscriptMode(ListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
                    lstFreeComment.setSelection(mCommentAdapter.getCount() - 1);
                    lstFreeComment.setTranscriptMode(ListView.TRANSCRIPT_MODE_DISABLED);

                    dbRef.child("free").child(mFreeBean.id).child("comments").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            //데이터를 받아와서 List에 저장.
                            mCommentList.clear();
                       /* mExBean.commentList.clear();
                        mLoginMember.commentList.clear();*/

                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                CommentBean bean = snapshot.getValue(CommentBean.class);
                                mCommentList.add(bean);
                           /* mExBean.commentList.add(bean);
                            mLoginMember.commentList.add(bean);*/
                            }
                            //바뀐 데이터로 Refresh 한다.
                            if (mCommentAdapter != null) {
                                mCommentAdapter.setList(mCommentList);
                                mCommentAdapter.notifyDataSetChanged();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                        }
                    });
                } else {
                    Toast.makeText(FreeDetailActivity.this, "댓글을 입력하세요", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }//end onCreate


    private LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            //구글맵을 현재 위치로 이동시킨다.
            mMapFragment.getMapAsync(mapReadyCallback);
            //현재 위치를 한번만 호출하기 위해 리스너 해지
            mLocationManager.removeUpdates(locationListener);
        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) { }

        @Override
        public void onProviderEnabled(String s) { }

        @Override
        public void onProviderDisabled(String s) { }
    };

    //구글맵 로딩완료후 이벤트
    private OnMapReadyCallback mapReadyCallback = new OnMapReadyCallback() {
        @Override
        public void onMapReady(final GoogleMap googleMap) {

            if(
                    ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                            ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
            )
            {
                return;
            }
            //현재버튼 추가
            googleMap.setMyLocationEnabled(true);
            //줌인 줌아웃 버튼 추가
            googleMap.getUiSettings().setZoomControlsEnabled(true);
            //나침반 추가
            googleMap.getUiSettings().setCompassEnabled(true);


            if(TextUtils.equals(mFreeBean.place, "X")) {
                mMapFragment.getView().setVisibility(View.GONE);
            }
            MarkerOptions markerOptions = new MarkerOptions();
            LatLng latLng = new LatLng(mFreeBean.latitude, mFreeBean.longitude);
            markerOptions.position(latLng);
            markerOptions.title(mFreeBean.place);
            markerOptions.snippet("위도:" + latLng.latitude + ", 경도: " + latLng.longitude);

            googleMap.addMarker(markerOptions).showInfoWindow();
            googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
            googleMap.animateCamera(CameraUpdateFactory.zoomTo(17));
        }
    };

    View.OnClickListener BtnClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.btnFreeModify:
                    modify();
                    break;
                case R.id.btnFreeDel:
                    delete();
                    break;
                case R.id.btnFreeWriter:
                    writerPage();
                    break;
                case R.id.imgFreeStar:
                    scrap();
                    break;
            }
        }
    };

    //수정
    private void modify() {
        //처리
        Intent intent = new Intent(FreeDetailActivity.this, FreeModifyActivity.class);
        intent.putExtra("FREEITEM", mFreeBean);
        startActivity(intent);
        finish();
    }

    //삭제
    private void delete() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("삭제");
        builder.setMessage("삭제하시겠습니까?");
        builder.setNegativeButton("아니오", null);
        builder.setPositiveButton("예", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //DB에서 삭제처리
                FirebaseDatabase.getInstance().getReference().child("free").child(mFreeBean.id).removeValue();
                //Storage 삭제처리
                if (mFreeBean.imgName != null) {
                    try {
                        FirebaseStorage.getInstance("gs://guru2-final-project-1cho.appspot.com/").getReference().child("images").child(mFreeBean.imgName).delete();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                Toast.makeText(getApplicationContext(), "삭제 되었습니다.", Toast.LENGTH_LONG).show();
                finish();
            }
        });
        builder.create().show();
    }

    //작성자 페이지
    private void writerPage(){
        //처리
        Intent intent = new Intent(FreeDetailActivity.this, UserBoardActivity.class);
        intent.putExtra("ID", txtFreeDetailId.getText().toString());
        startActivity(intent);
    }

    private void scrap() {
        String userEmail = mFirebaseAuth.getCurrentUser().getEmail();
        String uuid = JoinActivity.getUserIdFromUUID(userEmail);

        mFirebaseDB.getReference().child("member").child(uuid).child("scrap").child("free").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mScrapList.clear();
                Boolean flag= false;

                for(DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String str = snapshot.getValue(String.class);
                    if (TextUtils.equals(mFreeBean.id, str)) {
                        flag = true;
                        break;
                    }
                }
                if (!flag) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(FreeDetailActivity.this);
                    String userEmail = mFirebaseAuth.getCurrentUser().getEmail();
                    String uuid = JoinActivity.getUserIdFromUUID(userEmail);
                    mFirebaseDB.getReference().child("member").child(uuid).child("scrap").child("free").child(mFreeBean.id).setValue(mFreeBean.id);
                    Toast.makeText(FreeDetailActivity.this, "스크랩 되었습니다.", Toast.LENGTH_SHORT).show();

                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(FreeDetailActivity.this);
                    builder.setTitle("스크랩");
                    builder.setMessage("스크랩 취소하시겠습니까?");
                    builder.setNegativeButton("아니오", null);
                    builder.setPositiveButton("예", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            String userEmail = mFirebaseAuth.getCurrentUser().getEmail();
                            String uuid = JoinActivity.getUserIdFromUUID(userEmail);
                            mFirebaseDB.getReference().child("member").child(uuid).child("scrap").child("free").child(mFreeBean.id).removeValue();
                            Toast.makeText(FreeDetailActivity.this, "스크랩 취소되었습니다.", Toast.LENGTH_SHORT).show();
                        }
                    });
                    builder.create().show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();

        //상단 아이디 바 글쓴이 아이디, 올린 날짜 출력
        mFirebaseDB.getReference().child("free").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //데이터를 받아와서 List에 저장.
                mFreeList.clear();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    //for (DataSnapshot snapshot2 : snapshot.getChildren()) {
                    FreeBean bean = snapshot.getValue(FreeBean.class);

                    if (TextUtils.equals(bean.id, mFreeBean.id)) {
                        if (mFreeBean != null) {
                            // imgTitle 이미지를 표시할 때는 원격 서버에 있는 이미지이므로 비동기로 표시한다.
                            try {
                                if (bean.bmpTitle == null) {
                                    new DownloadImgTaskFree(mContext, imgFreeDetail, mFreeList, 0).execute(new URL(bean.imgUrl));
                                } else {
                                    imgFreeDetail.setImageBitmap(bean.bmpTitle);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            txtFreeTitle.setText(bean.title);
                            txtFreeDetailOption.setText(bean.explain);

                            StringTokenizer tokens = new StringTokenizer(bean.userId);
                            String userId = tokens.nextToken("@");
                            txtFreeDetailId.setText(userId);
                            txtFreeDetailDate.setText(bean.date);
                            txtFreePlace.setText(bean.place);
                            txtFreeDetailPlace.setText( bean.detailPlace);
                        }

                        LinearLayout layoutExVisibility = findViewById(R.id.layoutFreeVisibility);
                        //상단 아이디(글쓴이 아이디)와 로그인 아이디가 같으면 수정, 삭제버튼 visibility 풀기
                        if (TextUtils.equals(mFreeBean.userId, mFirebaseAuth.getCurrentUser().getEmail())) {
                            btnFreeModify.setVisibility(View.VISIBLE);
                            btnFreeDel.setVisibility(View.VISIBLE);
                        }
                        //상단 아이디(글쓴이 아이디)와 로그인 아이디가 다르면 작성자 페이지 가는 버튼 visibility 풀기
                        if (!TextUtils.equals(mFreeBean.userId, mFirebaseAuth.getCurrentUser().getEmail())) {
                            btnFreeWriter.setVisibility(View.VISIBLE);
                            imgStar.setVisibility(View.VISIBLE);

                        }
                        // }
                    }
                }

                if (mFreeAdapter != null) {
                    mFreeAdapter.setList(mFreeList);
                    mFreeAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

        //구글맵이 로딩이 완료되면 아래의 이벤트가 발생한다.
        mMapFragment.getMapAsync(mapReadyCallback);

        //데이터 취득
        DatabaseReference dbRef = mFirebaseDB.getReference();
        dbRef.child("free").child(mFreeBean.id).child("comments").addValueEventListener(new ValueEventListener() {
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
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }
}