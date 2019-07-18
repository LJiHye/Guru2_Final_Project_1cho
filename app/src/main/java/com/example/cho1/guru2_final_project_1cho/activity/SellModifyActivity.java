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
import android.text.TextUtils;
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
import com.example.cho1.guru2_final_project_1cho.firebase.SellAdapter;
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

public class SellModifyActivity extends AppCompatActivity {

    public static final String STORAGE_DB_URL = "gs://guru2-final-project-1cho.appspot.com/";

    private ImageView mImgSellWrite;
    private EditText mEdtTitle, mEdtWishPrice, mEdtWishOption;
    private Spinner mspinner1;  //카테고리

    //Firebase DB 저장 변수
    private FirebaseAuth mFirebaseAuth = FirebaseAuth.getInstance();
    private FirebaseStorage mFirebaseStorage = FirebaseStorage.getInstance(STORAGE_DB_URL);
    private FirebaseDatabase mFirebaseDatabase = FirebaseDatabase.getInstance();

    //사진 찍기 변수
    private Uri mCaptureUri;
    public String mPhotoPath;
    public static final int REQUEST_IMAGE_CAPTURE = 200;

    private FleaBean mFleaBean;

    private FirebaseDatabase mFirebaseDB = FirebaseDatabase.getInstance();

    private List<FleaBean> mFleaList = new ArrayList<>();
    private SellAdapter mSellAdapter;
    FleaBean mCurrentSellBean;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sell_modify);

        //카테고리 드롭다운 스피너 추가
        Spinner dropdown = (Spinner)findViewById(R.id.spinCategory);
        String[] items = new String[]{"옷", "책", "생활물품", "기프티콘", "데이터", "대리 예매", "전자기기", "화장품", "기타"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, items);
        dropdown.setAdapter(adapter);

        //카메라를 사용하기 위한 퍼미션을 요청한다.
        ActivityCompat.requestPermissions(this, new String[]{
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA
        }, 0);

        //사진찍기
        mImgSellWrite = findViewById(R.id.imgSellWrite);
        mEdtTitle = findViewById(R.id.edtTitle);
        mEdtWishPrice = findViewById(R.id.edtWishPrice);
        mEdtWishOption = findViewById(R.id.edtWishOption);
        mspinner1 = findViewById(R.id.spinCategory);
        Button mBtnImgReg = findViewById(R.id.btnImgReg);
        Button mBtnSellModifyReg = findViewById(R.id.btnSellModifyReg);

        mBtnImgReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                takePicture();
            }
        });

        mBtnSellModifyReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                update();
            }
        });

        mFleaBean = (FleaBean) getIntent().getSerializableExtra(FleaBean.class.getName());
        if (mFleaBean != null) {
            getIntent().getParcelableArrayExtra("titleBitmap");
            if (mFleaBean.bmpTitle != null) {
                mImgSellWrite.setImageBitmap(mFleaBean.bmpTitle);
            }
            mEdtTitle.setText(mFleaBean.title);
            mEdtWishPrice.setText(mFleaBean.wishoption);
            mEdtWishOption.setText(mFleaBean.wishoption);

        }

        mCurrentSellBean = (FleaBean) getIntent().getSerializableExtra("SELLITEM");

        mFirebaseDB.getReference().child("sell").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //데이터를 받아와서 List에 저장.
                mFleaList.clear();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    FleaBean bean = snapshot.getValue(FleaBean.class);
                    if (TextUtils.equals(bean.id, mCurrentSellBean.id)) {  //bean.id - null에러,,
                        mEdtTitle.setText(bean.selltitle);
                        mEdtWishOption.setText(bean.wishoption);
                        mEdtWishPrice.setText(bean.wishprice);
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
    }//end onCreate()

    //수정하기
    private void update() {
        // 사진을 찍었을 경우, 안 찍었을 경우
        if(mPhotoPath == null) {
            //사진을 새로 안찍었을 경우
            mFleaBean.userId = mFirebaseAuth.getCurrentUser().getEmail();
            mFleaBean.title = mEdtTitle.getText().toString();
            mFleaBean.wishprice = mEdtWishPrice.getText().toString();
            mFleaBean.wishoption = mEdtWishOption.getText().toString();
            mFleaBean.date = new SimpleDateFormat("yyyy=MM-dd hh:mm:ss").format(new Date());

            // DB 업로드
            DatabaseReference dbRef = mFirebaseDatabase.getReference();
            String uuid = getUserIdFromUUID(mFleaBean.userId);
            // 동일 ID로 데이터 수정
            dbRef.child("memo").child(uuid).child(mFleaBean.id).setValue(mFleaBean);
            Toast.makeText(this, "수정이 완료되었습니다.", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        //새로 찍었을 경우, 사진부터 업로드하고 DB 업데이트한다.
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
                if (mFleaBean.imgName != null) {
                    try {
                        mFirebaseStorage.getReference().child("images").child(mFleaBean.imgName).delete();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                mFleaBean.imgUrl = task.getResult().toString();
                mFleaBean.imgName = mCaptureUri.getLastPathSegment();
                mFleaBean.userId = mFirebaseAuth.getCurrentUser().getEmail();
                mFleaBean.title = mEdtTitle.getText().toString();
                mFleaBean.wishprice = mEdtWishPrice.getText().toString();
                mFleaBean.wishoption = mEdtWishOption.getText().toString();
                mFleaBean.date = new SimpleDateFormat("yyyy=MM-dd hh:mm:ss").format(new Date());

                String uuid = getUserIdFromUUID(mFleaBean.userId);
                mFirebaseDatabase.getReference().child("memo").child(uuid).child(mFleaBean.id).setValue(mFleaBean);

                Toast.makeText(getBaseContext(), "수정이 완료되었습니다.", Toast.LENGTH_LONG).show();
                finish();
            }
        });
    }

    public static String getUserIdFromUUID(String userEmail) {
        long val = UUID.nameUUIDFromBytes(userEmail.getBytes()).getMostSignificantBits();
        return String.valueOf(val);
    }

    //사진 찍기
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
        Bitmap resizedBmp = getResizedBitmap(bitmap, 4, 100, 100);  //이미지 사이즈를 줄여줌 > size를 1로 하면 원본 크기로 나옴 > 4는 1/4사이즈

        bitmap.recycle();

        //사진이 캡쳐되서 들어오면 뒤집어져 있다. 이 애를 다시 원상복구 시킨다.
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
        Bitmap rotatedBmp = rotate(resizedBmp, exifDegree);
        mImgSellWrite.setImageBitmap(rotatedBmp);
        //줄어든 이미지를 다시 저장한다
        saveBitmapToFileCache(resizedBmp, mPhotoPath);

        //사진이 저장된 경로 보여주기
        Toast.makeText(this, "사진 경로 : " + mPhotoPath, Toast.LENGTH_SHORT).show();
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

    private Bitmap rotate(Bitmap bmp, float degree) {
        Matrix matrix = new Matrix();
        matrix.postRotate(degree);
        return Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(),
                matrix, true);
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
    //사진찍기완료
}
