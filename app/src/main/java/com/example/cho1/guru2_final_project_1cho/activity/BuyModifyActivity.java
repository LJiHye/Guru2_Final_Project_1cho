package com.example.cho1.guru2_final_project_1cho.activity;

import android.Manifest;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;

import com.example.cho1.guru2_final_project_1cho.R;
import com.example.cho1.guru2_final_project_1cho.bean.FleaBean;
import com.example.cho1.guru2_final_project_1cho.firebase.BuyAdapter;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class BuyModifyActivity extends AppCompatActivity {

    public static final String STORAGE_DB_URL = "gs://guru2-final-project-1cho.appspot.com/";

    private FirebaseAuth mFirebaseAuth = FirebaseAuth.getInstance();
    private FirebaseStorage mFirebaseStorage = FirebaseStorage.getInstance(STORAGE_DB_URL);
    private FirebaseDatabase mFirebaseDatabase = FirebaseDatabase.getInstance();

    private FirebaseDatabase mFirebaseDB = FirebaseDatabase.getInstance();
    private List<FleaBean> mBuyAdapter = new ArrayList<>();
    private BuyAdapter mFleaAdapter;
//    FleaBean mWriterFleaBean;

    private ImageView mimgBuyWrite;  //사진
    private EditText medtTitle;  //제목
    private EditText medtExplain;  //설명
    private EditText medtPrice;  //정가
    private EditText medtSalePrice;  //판매가
    private EditText medtBuyDay;  //구매일
    private EditText medtExprieDate;  //유통기한
    private EditText medtDefect;  //하자 유무
    private EditText medtSize;  //실제 측정 사이즈
    private Spinner mspinner1;  //카테고리
    private Spinner mspinner2;  //제품 상태

    private FleaBean mFleaBean;

    //사진
    private Uri mCaptureUri;
    public String mPhotoPath;
    public static final int REQUEST_IMAGE_CAPTURE = 200;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buy_modify);

        mFleaBean = (FleaBean) getIntent().getSerializableExtra("BUYITEM");
//        FleaAdapter.setFleaBean(mFleaBean);

        //카메라를 사용하기 위한 퍼미션을 요청한다.
        ActivityCompat.requestPermissions(this, new String[]{
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA
        }, 0);

        mimgBuyWrite = findViewById(R.id.imgBuyWrite);
        Button mbtnImgReg = findViewById(R.id.btnImgReg);
        //사진찍기
        mbtnImgReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                takePicture();
            }
        });

        medtTitle = findViewById(R.id.edtTitle);
        medtExplain = findViewById(R.id.edtExplain);
        medtPrice = findViewById(R.id.edtPrice);
        medtSalePrice = findViewById(R.id.edtSalePrice);
        medtBuyDay = findViewById(R.id.edtBuyDay);
        medtExprieDate = findViewById(R.id.edtExprieDate);
        medtDefect = findViewById(R.id.edtDefect);
        medtSize = findViewById(R.id.edtSize);
        mspinner1 = findViewById(R.id.spinCategory);
        mspinner2 = findViewById(R.id.spinState);

        findViewById(R.id.btnOk).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //수정 업데이트
                update();
            }
        });

        //글 내용 불러와 출력
        mFleaAdapter = new BuyAdapter(this, mBuyAdapter);
        //어디다가..? xml 자체에..?

        /** ... **/
        mFleaBean = (FleaBean) getIntent().getSerializableExtra(FleaBean.class.getName());
        if (mFleaBean != null) {
            getIntent().getParcelableArrayExtra("titleBitmap");
            if (mFleaBean.bmpTitle != null) {
                mimgBuyWrite.setImageBitmap(mFleaBean.bmpTitle);
            }
            medtTitle.setText(mFleaBean.title);
            medtExplain.setText(mFleaBean.subtitle);
            medtPrice.setText(mFleaBean.price);
            medtSalePrice.setText(mFleaBean.saleprice);
            medtBuyDay.setText(mFleaBean.buyday);
            medtExprieDate.setText(mFleaBean.expire);
            medtDefect.setText(mFleaBean.fault);
            medtSize.setText(mFleaBean.size);
            mFleaBean.category = mspinner1.getSelectedItem().toString();
            mFleaBean.state = mspinner2.getSelectedItem().toString();

        }
        //mWriterFleaBean = (FleaBean) getIntent().getSerializableExtra("ITEM");
        //mFleaBean = (FleaBean) getIntent().getSerializableExtra("BUYITEM");

        mFirebaseDB.getReference().child("buy").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //데이터를 받아와서 List에 저장.
                mBuyAdapter.clear();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    for (DataSnapshot snapshot2 : snapshot.getChildren()) {
                        FleaBean bean = snapshot2.getValue(FleaBean.class);
                       // if(TextUtils.equals(bean.id, mFleaBean.id)) {
                        medtTitle.setText(mFleaBean.title);
                        medtExplain.setText(mFleaBean.subtitle);
                        medtPrice.setText(mFleaBean.price);
                        medtSalePrice.setText(mFleaBean.saleprice);
                        medtBuyDay.setText(mFleaBean.buyday);
                        medtExprieDate.setText(mFleaBean.expire);
                        medtDefect.setText(mFleaBean.fault);
                        medtSize.setText(mFleaBean.size);
                        mFleaBean.category = mspinner1.getSelectedItem().toString();
                        mFleaBean.state = mspinner2.getSelectedItem().toString();
                    }
                }
//                if (mBuyAdapter != null) {
//                    mBuyAdapter.setList(mFleaList);
//                    mBuyAdapter.notifyDataSetChanged();
//                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

        //카테고리 드롭다운 스피너 추가
        Spinner dropdown = (Spinner) findViewById(R.id.spinCategory);
        String[] items = new String[]{"옷", "책", "생활물품", "기프티콘", "데이터", "대리 예매", "전자기기", "화장품", "기타"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, items);
        dropdown.setAdapter(adapter);

        //제품상태 드롭다운 스피너 추가
        Spinner dropdown2 = (Spinner) findViewById(R.id.spinState);
        String[] items2 = new String[]{"상", "중", "하"};
        ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, items2);
        dropdown2.setAdapter(adapter2);

    }  //end onCreate

    //게시물 수정
    private void update() {
        //안찍었을 경우, DB 만 업데이트 시켜준다.
        if (mPhotoPath == null) {
            mFleaBean.title = medtTitle.getText().toString();  //제목
            mFleaBean.subtitle = medtExplain.getText().toString();  //설명
            mFleaBean.price = medtPrice.getText().toString();  //정가
            mFleaBean.saleprice = medtSalePrice.getText().toString();  //판매가
            mFleaBean.buyday = medtBuyDay.getText().toString();  //구매일
            mFleaBean.expire = medtExprieDate.getText().toString();  //유통기한
            mFleaBean.fault = medtDefect.getText().toString();  //하자 유무
            mFleaBean.size = medtSize.getText().toString();  //실제 측정 사이즈
            mFleaBean.category = mspinner1.getSelectedItem().toString();  //카테고리
            mFleaBean.state = mspinner2.getSelectedItem().toString();  //제품 상태
            mFleaBean.date = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date());  //글 올린 날짜

            //DB 업로드
            DatabaseReference dbRef = mFirebaseDatabase.getReference();
            String uuid = getUserIdFromUUID(mFleaBean.userId);
            //동일 ID 로 데이터 수정
            dbRef.child("buy").child(uuid).child(mFleaBean.id).setValue(mFleaBean);
            Toast.makeText(this, "수정 되었습니다.", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        //사진을 찍었을 경우, 사진부터 업로드 하고 DB 업데이트 한다.
        StorageReference storageRef = mFirebaseStorage.getReference();
        final StorageReference imagesRef = storageRef.child("images/" + mCaptureUri.getLastPathSegment());
        UploadTask uploadTask = imagesRef.putFile(mCaptureUri);
        uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()) {
                    throw task.getException();
                }
                return imagesRef.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                //파일 업로드 완료후 호출된다.
                //기존 이미지 파일을 삭제한다.
                if (mFleaBean.imgName != null) {
                    try {
                        mFirebaseStorage.getReference().child("images").child(mFleaBean.imgName).delete();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                mFleaBean.imgUrl = task.getResult().toString();
                mFleaBean.imgName = mCaptureUri.getLastPathSegment();
                mFleaBean.subtitle = medtExplain.getText().toString();
                mFleaBean.title = medtTitle.getText().toString();  //제목
                mFleaBean.subtitle = medtExplain.getText().toString();  //설명
                mFleaBean.price = medtPrice.getText().toString();  //정가
                mFleaBean.saleprice = medtSalePrice.getText().toString();  //판매가
                mFleaBean.buyday = medtBuyDay.getText().toString();  //구매일
                mFleaBean.expire = medtExprieDate.getText().toString();  //유통기한
                mFleaBean.fault = medtDefect.getText().toString();  //하자 유무
                mFleaBean.size = medtSize.getText().toString();  //실제 측정 사이즈
                mFleaBean.category = mspinner1.getSelectedItem().toString();  //카테고리
                mFleaBean.state = mspinner2.getSelectedItem().toString();  //제품 상태

                //수정된 날짜로
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                mFleaBean.date = sdf.format(new Date());

                String uuid = getUserIdFromUUID(mFleaBean.userId);
                mFirebaseDatabase.getReference().child("buy").child(uuid).child(mFleaBean.id).setValue(mFleaBean);

                Toast.makeText(getBaseContext(), "수정 되었습니다.", Toast.LENGTH_SHORT).show();
                finish();
            }
        });

    }

//    @Override
//    protected void onResume() {
//        super.onResume();
//
//        //데이터 취득
//        //String userEmail = mFirebaseAuth.getCurrentUser().getEmail();
//        //String uuid = SellWriteActivity.getUserIdFromUUID(userEmail);
//        DatabaseReference dbRef = mFirebaseDB.getReference();
//        String guid = JoinActivity.getUserIdFromUUID(mFleaBean.userId);
//        dbRef.child("buy").child( guid ).child( mFleaBean.id ).addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                //데이터를 받아와서 List에 저장.
//                mFleaList.clear();
//
//                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
//                    CommentBean bean = snapshot.getValue(CommentBean.class);
//                    mFleaList.add(FleaBean);
//                }
//                //바뀐 데이터로 Refresh 한다.
//                if (mFleaAdapter != null) {
//                    mFleaAdapter.setList(mFleaList);
//                    mFleaAdapter.notifyDataSetChanged();
//                }
//            }
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {}
//        });
//    }

    public static String getUserIdFromUUID(String userEmail) {
        long val = UUID.nameUUIDFromBytes(userEmail.getBytes()).getMostSignificantBits();
        return String.valueOf(val);
    }

    private void takePicture() {

        Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            mCaptureUri = Uri.fromFile(getOutPutMediaFile());
        } else {
            mCaptureUri = FileProvider.getUriForFile(this,
                    "com.example.cho1.guru2_final_project_1cho", getOutPutMediaFile());
        }

        i.putExtra(MediaStore.EXTRA_OUTPUT, mCaptureUri);

        startActivityForResult(i, REQUEST_IMAGE_CAPTURE);
    }

    private File getOutPutMediaFile() {
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "cameraDemo");
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                return null;
            }
        }

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File file = new File(mediaStorageDir.getPath() + File.separator + "IMG_" + timeStamp + ".jpg");

        mPhotoPath = file.getAbsolutePath();

        return file;
    }

    private void sendPicture() {
        Bitmap bitmap = BitmapFactory.decodeFile(mPhotoPath);
        Bitmap resizedBmp = getResizedBitmap(bitmap, 4, 100, 100);

        bitmap.recycle();

        //사진이 캡쳐되서 들어오면 뒤집어져 있다. 이애를 다시 원상복구 시킨다.
        ExifInterface exif = null;
        try {
            exif = new ExifInterface(mPhotoPath);
        } catch (Exception e) {
            e.printStackTrace();
        }
        int exifOrientation;
        int exifDegree;
        if (exif != null) {
            exifOrientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL);
            exifDegree = exifOrientToDegree(exifOrientation);
        } else {
            exifDegree = 0;
        }
        Bitmap rotatedBmp = roate(resizedBmp, exifDegree);
        mimgBuyWrite.setImageBitmap(rotatedBmp);

        //줄어든 이미지를 다시 저장한다
        saveBitmapToFileCache(rotatedBmp, mPhotoPath);

        Toast.makeText(this, "사진경로:" + mPhotoPath, Toast.LENGTH_SHORT).show();
    }

    private void saveBitmapToFileCache(Bitmap bitmap, String strFilePath) {

        File fileCacheItem = new File(strFilePath);
        OutputStream out = null;


        try {
            fileCacheItem.createNewFile();
            out = new FileOutputStream(fileCacheItem);

            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private int exifOrientToDegree(int exifOrientation) {
        if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_90) {
            return 90;
        } else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_180) {
            return 180;
        } else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_270) {
            return 270;
        }
        return 0;
    }

    private Bitmap roate(Bitmap bmp, float degree) {
        Matrix matrix = new Matrix();
        matrix.postRotate(degree);
        return Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(), matrix, true);
    }

    //비트맵의 사이즈를 줄여준다.
    public static Bitmap getResizedBitmap(Bitmap srcBmp, int size, int width, int height) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = size;
        Bitmap resized = Bitmap.createScaledBitmap(srcBmp, width, height, true);
        return resized;
    }

    public static Bitmap getResizedBitmap(Resources resources, int id, int size, int width, int height) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = size;
        Bitmap src = BitmapFactory.decodeResource(resources, id, options);
        Bitmap resized = Bitmap.createScaledBitmap(src, width, height, true);
        return resized;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //카메라로부터 오는 데이터를 취득한다.
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_IMAGE_CAPTURE) {
                sendPicture();
            }
        }
    }
}
