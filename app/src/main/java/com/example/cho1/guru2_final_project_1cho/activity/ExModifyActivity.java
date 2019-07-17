package com.example.cho1.guru2_final_project_1cho.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;

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

import com.example.cho1.guru2_final_project_1cho.R;
import com.example.cho1.guru2_final_project_1cho.bean.ExBean;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

public class ExModifyActivity extends AppCompatActivity {

   public static final String STORAGE_DB_URL = "gs://guru2-final-project-1cho.appspot.com/";

    private ImageView mImgItem; // 내 물건
    private EditText mEdtTitle; // 내 물건명
    private  EditText mEdtItem; // 교환하고자 하는 물건명
    private EditText mEdtBuyDate; // 구매한 날짜
    private EditText mEdtExpDate; // 유통기한
    private EditText mEdtFault; // 결함
    private EditText mEdtSize; // 사이즈
    private Spinner mSprState; // 상태

    ExBean mExBean = new ExBean();

    private FirebaseAuth mFirebaseAuth = FirebaseAuth.getInstance();
    private FirebaseStorage mFirebaseStorage = FirebaseStorage.getInstance(STORAGE_DB_URL);
    private FirebaseDatabase mFirebaseDatabase = FirebaseDatabase.getInstance();


    // 사진이 저장되는 경로
    private Uri mCaptureUri;
    //사진이 저장된 단말기상의 실제 경로
    public String mPhotoPath;
    //startActivityForResult() 에 넘겨주는 값, 이 값이 나중에 onActivityResult()로 돌아와서
    //내가 던진값인지를 구별할 때 사용하는 상수.
    public static final int REQUEST_IMAGE_CAPTURE = 200;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ex_modify);

        //카메라를 사용하기 위한 퍼미션을 요청한다.
        ActivityCompat.requestPermissions(this, new String[]{
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA
        }, 0);

        mImgItem = findViewById(R.id.imgItem);
        mEdtTitle = findViewById(R.id.edtTitle);
        mEdtItem = findViewById(R.id.edtItem);
        mEdtBuyDate = findViewById(R.id.edtBuyDate);
        mEdtExpDate = findViewById(R.id.edtExpDate);
        mEdtFault = findViewById(R.id.edtFault);
        mEdtSize = findViewById(R.id.edtSize);
        mSprState = findViewById(R.id.sprState);

        mExBean = (ExBean)getIntent().getSerializableExtra(ExBean.class.getName());
        if (mExBean != null) {
            mExBean.bmpTitle = getIntent().getParcelableExtra("titleBitmap");
            if(mExBean.bmpTitle != null){
                mImgItem.setImageBitmap(mExBean.bmpTitle);
            }

            mEdtTitle.setText(mExBean.mine); // 내 물건
            mEdtItem.setText(mExBean.want); // 상대방 물건
            mExBean.state = mSprState.getSelectedItem().toString(); // 물건 상태
            mEdtFault.setText(mExBean.fault); // 하자
            mEdtExpDate.setText(mExBean.expire); // 유통기한
            mEdtBuyDate.setText(mExBean.buyDate); // 구매한 날짜
            mEdtSize.setText(mExBean.size); // 사이즈

        }


        mImgItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                takePicture();
            }
        });

        findViewById(R.id.btnModify).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // DB 업로드
                update();
            }
        });

        //제품상태 드롭다운 스피너 추가
        Spinner dropdown = (Spinner)findViewById(R.id.sprState);
        String[] states = new String[]{"상", "중", "하"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, states);
        dropdown.setAdapter(adapter);


    } // onCreate()

    // 게시물 수정
    private void update() {
        // 사진을 찍었을 경우, 안 찍었을 경우
        if(mPhotoPath == null) {
            // 안 찍었을 경우, DB만 업데이트 시켜준다.
            mExBean.mine = mEdtTitle.getText().toString(); // 내물건 이름
            mExBean.want = mEdtItem.getText().toString(); // 교환하고 싶은 물건
            mExBean.state = mSprState.getSelectedItem().toString(); // 물건 상태
            mExBean.fault = mEdtFault.getText().toString(); // 하자
            mExBean.expire = mEdtExpDate.getText().toString(); // 유통기한
            mExBean.buyDate = mEdtBuyDate.getText().toString(); // 구매날짜
            mExBean.size = mEdtSize.getText().toString(); // 실측사이즈
            // DB 업로드
            DatabaseReference dbRef = mFirebaseDatabase.getReference();
            String uuid = getUserIdFromUUID(mExBean.userId);
            // 동일 ID로 데이터 수정
            dbRef.child("memo").child(uuid).child(mExBean.id).setValue(mExBean);
            Toast.makeText(this, "수정되었습니다.", Toast.LENGTH_LONG).show();
            finish();
            return;

        }

        // 사진을 찍었을 경우, 사진부터 업로드하고 DB 업데이트한다.
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
                // 파일 업로드 완료후 호출된다.
                // 기존 이미지 파일을 삭제한다.
                if (mExBean.imgName != null) {
                    try {
                        mFirebaseStorage.getReference().child("images").child(mExBean.imgName).delete();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                mExBean.imgUrl = task.getResult().toString();
                mExBean.imgName = mCaptureUri.getLastPathSegment();
                mExBean.mine = mEdtTitle.getText().toString(); // 내물건 이름
                mExBean.want = mEdtItem.getText().toString(); // 교환하고 싶은 물건
                mExBean.state = mSprState.getSelectedItem().toString(); // 물건 상태
                mExBean.fault = mEdtFault.getText().toString(); // 하자
                mExBean.expire = mEdtExpDate.getText().toString(); // 유통기한
                mExBean.buyDate = mEdtBuyDate.getText().toString(); // 구매날짜
                mExBean.size = mEdtSize.getText().toString(); // 실측사이즈

                // 수정된 날짜로
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                mExBean.date = sdf.format(new Date());

                String uuid = getUserIdFromUUID(mExBean.userId);
                mFirebaseDatabase.getReference().child("memo").child(uuid).child(mExBean.id).setValue(mExBean);

                Toast.makeText(getBaseContext(), "수정되었습니다.", Toast.LENGTH_LONG).show();
                finish();
            }
        });
    }

    public static String getUserIdFromUUID(String userEmail) {
        long val = UUID.nameUUIDFromBytes(userEmail.getBytes()).getMostSignificantBits();
        return String.valueOf(val);
    }

    private void takePicture() {

        Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            mCaptureUri = Uri.fromFile( getOutPutMediaFile() );
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
        if(!mediaStorageDir.exists()) {
            if(!mediaStorageDir.mkdirs()) {
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
        } catch(Exception e) {
            e.printStackTrace();
        }
        int exifOrientation;
        int exifDegree;
        if(exif != null) {
            exifOrientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL);
            exifDegree = exifOrientToDegree(exifOrientation);
        } else {
            exifDegree = 0;
        }
        Bitmap rotatedBmp = roate(resizedBmp, exifDegree);
        mImgItem.setImageBitmap( rotatedBmp );
        //줄어든 이미지를 다시 저장한다
        saveBitmapToFileCache(resizedBmp, mPhotoPath);

        Toast.makeText(this, "사진경로: " + mPhotoPath, Toast.LENGTH_LONG).show();
    }

    private void saveBitmapToFileCache(Bitmap bitmap, String strFilePath) {

        File fileCacheItem = new File(strFilePath);
        OutputStream out = null;

        try
        {
            fileCacheItem.createNewFile();
            out = new FileOutputStream(fileCacheItem);

            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            try
            {
                out.close();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }

    private int exifOrientToDegree(int exifOrientation) {
        if(exifOrientation == ExifInterface.ORIENTATION_ROTATE_90) {
            return 90;
        }
        else if(exifOrientation == ExifInterface.ORIENTATION_ROTATE_180) {
            return 180;
        }
        else if(exifOrientation == ExifInterface.ORIENTATION_ROTATE_270) {
            return 270;
        }
        return 0;
    }

    private Bitmap roate(Bitmap bmp, float degree) {
        Matrix matrix = new Matrix();
        matrix.postRotate(degree);
        return Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(),
                matrix, true);
    }

    //비트맵의 사이즈를 줄여준다.
    public static Bitmap getResizedBitmap(Bitmap srcBmp, int size, int width, int height){
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = size;
        Bitmap resized = Bitmap.createScaledBitmap(srcBmp, width, height, true);
        return resized;
    }

    public static Bitmap getResizedBitmap(Resources resources, int id, int size, int width, int height){
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = size;
        Bitmap src = BitmapFactory.decodeResource(resources, id, options);
        Bitmap resized = Bitmap.createScaledBitmap(src, width, height, true);
        return resized;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //카메라로부터 오는 데이터를 취득한다.
        if(resultCode == RESULT_OK) {
            if(requestCode == REQUEST_IMAGE_CAPTURE) {
                sendPicture();
            }
        }
    }
}
