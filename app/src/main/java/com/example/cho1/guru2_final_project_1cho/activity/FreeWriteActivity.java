package com.example.cho1.guru2_final_project_1cho.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.GradientDrawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.example.cho1.guru2_final_project_1cho.R;
import com.example.cho1.guru2_final_project_1cho.bean.FreeBean;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
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

public class FreeWriteActivity extends AppCompatActivity {

    public static final String STORAGE_DB_URL = "gs://guru2-final-project-1cho.appspot.com/";

    private FirebaseAuth mFirebaseAuth = FirebaseAuth.getInstance();
    private FirebaseStorage mFirebaseStorage = FirebaseStorage.getInstance(STORAGE_DB_URL);
    private FirebaseDatabase mFirebaseDatabase = FirebaseDatabase.getInstance();


    private ImageView mimgFreeWrite;  //사진
    private EditText medtTitle;  //제목
    private EditText medtExplain;  //설명
    private EditText medtDetailPlace;
    //private Spinner spinFree;

    private File tempFile;

    private FreeBean mFreeBean;

    //사진이 저장되는 경로
    private Uri mCaptureUri;
    //사진이 저장된 단말기상의 실제 경로
    public String mPhotoPath;
    //startActivityForResult() 에 넘겨주는 값, 이 값이 나중에 onActivityResult()로 돌아와서
    //내가 던진값인지를 구별할 때 사용하는 상수이다.
    public static final int REQUEST_IMAGE_CAPTURE = 200;

//    private SupportMapFragment mMapFragment;
//    private LocationManager mLocationManager;
//    private LatLng mCurPosLatLng;    //현재위치 저장 위도,경도 변수
//    private int mClickIndex = 0; //어떤 버튼의 index 가 클릭됐는지를 저장

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_free_write);

//        Spinner dropdown = (Spinner) findViewById(R.id.spinFree);
//        String[] items = new String[]{"50주년기념관", "인문사회관", "제1과학관", "제2과학관", "도서관", "학생누리관", "조형예술관", "샬롬하우스", "바롬인성교육관", "체육관", "정문", "후문", "X"};
//        ArrayAdapter<String> adapter = new ArrayAdapter<String>(FreeWriteActivity.this, android.R.layout.simple_spinner_dropdown_item, items);
//        dropdown.setAdapter(adapter);

        //카메라를 사용하기 위한 퍼미션을 요청한다.
        ActivityCompat.requestPermissions(this, new String[]{
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA
        }, 0);

//        mMapFragment = (SupportMapFragment)getSupportFragmentManager().findFragmentById(R.id.map);
//        //구글맵이 로딩이 완료되면 아래의 이벤트가 발생한다.
//        mMapFragment.getMapAsync(mapReadyCallback);
//
//        //GSP 가 켜져 있는지 확인한다.
//        mLocationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
//        if(!mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
//            //GSP 설정하는 Setting 화면으로 이동한다.
//            Intent i = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
//            i.addCategory(Intent.CATEGORY_DEFAULT);
//            startActivity(i);
//        }
//
//        if(
//                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
//                        ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
//        )
//        {
//            return;
//        }
//
//        //GPS 위치를 0.1초마다 10m 간격범위안에서 이동하면 위치를 listener 로 보내주도록 등록한다.
//        mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 100, 10, locationListener);
//        //WIFI 위치를 0.1초마다 10m 간격범위안에서 이동하면 위치를 listener 로 보내주도록 등록한다.
//        mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 100, 10, locationListener);


        mimgFreeWrite = findViewById(R.id.imgFreeWrite);
        GradientDrawable drawable =
                (GradientDrawable) this.getDrawable(R.drawable.background_rounding);
        mimgFreeWrite.setBackground(drawable);
        mimgFreeWrite.setClipToOutline(true);

        Button mbtnImgReg = findViewById(R.id.btnFreeWriteImgReg);
        Button mbtnGalleryReg = findViewById(R.id.btnFreeWriteGalleryReg);

        //사진찍기
        mbtnImgReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                takePicture();
            }
        });

        mbtnGalleryReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToAlbum();
            }
        });

        medtTitle = findViewById(R.id.edtFreeWriteTitle);
        medtExplain = findViewById(R.id.edtFreeWriteExplain);
        medtDetailPlace = findViewById(R.id.edtFreeDetailPlace);


        findViewById(R.id.btnFreeWriteOk).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //신규 등록
                upload();
            }
        });

        mFreeBean = (FreeBean) getIntent().getSerializableExtra(FreeBean.class.getName());
        if (mFreeBean != null) {
            getIntent().getParcelableArrayExtra("titleBitmap");
            if (mFreeBean.bmpTitle != null) {
                mimgFreeWrite.setImageBitmap(mFreeBean.bmpTitle);
            }
            medtTitle.setText(mFreeBean.title);
            medtExplain.setText(mFreeBean.explain);
            medtDetailPlace.setText(mFreeBean.detailPlace);
        }

//        spinFree.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
//
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> adapterView) {
//                switch (spinFree.getSelectedItem().toString()){
//                    case "50주년기념관":
//                        mClickIndex = 1;
//                        mCurPosLatLng = new LatLng(37.626251, 127.093109);
//                        break;
//
//                    case "인문사회관":
//                        mClickIndex = 2;
//                        mCurPosLatLng = new LatLng(37.628044, 127.092574);
//                        break;
//
//                    case "제1과학관":
//                        mClickIndex = 3;
//                        mCurPosLatLng = new LatLng(37.628987, 127.089674);
//                        break;
//
//                    case "제2과학관":
//                        mClickIndex = 4;
//                        mCurPosLatLng = new LatLng(37.629285, 127.090489);
//                        break;
//
//                    case "도서관":
//                        mClickIndex = 5;
//                        mCurPosLatLng = new LatLng(37.628325, 127.091229);
//                        break;
//
//                    case "학생누리관":
//                        mClickIndex = 6;
//                        mCurPosLatLng = new LatLng(37.629039, 127.091734);
//                        break;
//
//                    case "조형예술관":
//                        mClickIndex = 7;
//                        mCurPosLatLng = new LatLng(37.628750, 127.090425);
//                        break;
//
//                    case "샬롬하우스":
//                        mClickIndex = 8;
//                        mCurPosLatLng = new LatLng(37.628911, 127.088933);
//                        break;
//
//                    case "바롬인성교육관":
//                        mClickIndex = 9;
//                        mCurPosLatLng = new LatLng(37.627569, 127.088440);
//                        break;
//
//                    case "체육관":
//                        mClickIndex = 10;
//                        mCurPosLatLng = new LatLng(37.625539, 127.088914);
//                        break;
//
//                    case "정문":
//                        mClickIndex = 11;
//                        mCurPosLatLng = new LatLng(37.625775, 127.093657);
//                        break;
//
//                    case "후문":
//                        mClickIndex = 12;
//                        mCurPosLatLng = new LatLng(37.625416, 127.088289);
//                        break;
//                    default:
//                        mClickIndex = 0;
//                        mCurPosLatLng = null;
//
//                }//end switch
//                mMapFragment.getMapAsync(mapReadyCallback); //map refresh
//            }
//        });
    }  //end onCreate()

//    private LocationListener locationListener = new LocationListener() {
//        @Override
//        public void onLocationChanged(Location location) {
//            //위치 변경시 위도, 경도 정보 update 수신
//            mCurPosLatLng = new LatLng(location.getLatitude(), location.getLongitude());
//            Toast.makeText(getBaseContext(), "현재 위치가 갱신 되었습니다. " + mCurPosLatLng.latitude + ", " + mCurPosLatLng.longitude, Toast.LENGTH_SHORT).show();
//            //구글맵을 현재 위치로 이동시킨다.
//            mMapFragment.getMapAsync(mapReadyCallback);
//            //현재 위치를 한번만 호출하기 위해 리스너 해지
//            mLocationManager.removeUpdates(locationListener);
//        }
//
//        @Override
//        public void onStatusChanged(String s, int i, Bundle bundle) { }
//
//        @Override
//        public void onProviderEnabled(String s) { }
//
//        @Override
//        public void onProviderDisabled(String s) { }
//    };
//
//
//    //구글맵 로딩완료후 이벤트
//    private OnMapReadyCallback mapReadyCallback = new OnMapReadyCallback() {
//        @Override
//        public void onMapReady(final GoogleMap googleMap) {
//
//            if(
//                    ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
//                            ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
//            )
//            {
//                return;
//            }
//            //현재버튼 추가
//            googleMap.setMyLocationEnabled(true);
//            //줌인 줌아웃 버튼 추가
//            googleMap.getUiSettings().setZoomControlsEnabled(true);
//            //나침반 추가
//            googleMap.getUiSettings().setCompassEnabled(true);
//
//
//
//            //맵을 클릭했을 때 이벤트를 등록한다.
//            googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
//                @Override
//                public void onMapClick(LatLng latLng) {
//                    MarkerOptions markerOptions = new MarkerOptions();
//                    markerOptions.position(latLng);
//                    markerOptions.title("클릭한 장소 ");
//                    markerOptions.snippet("위도:" + latLng.latitude + ", 경도: " + latLng.longitude);
//                    googleMap.addMarker(markerOptions).showInfoWindow();
//                }
//            });
//
//            //snippet 클릭시 마커삭제
//            googleMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
//                @Override
//                public void onInfoWindowClick(Marker marker) {
//                    marker.remove();
//                }
//            });
//
//            if(mCurPosLatLng != null) {
//                //깃발 표시
//                MarkerOptions markerOptions = new MarkerOptions();
//                markerOptions.position(mCurPosLatLng);
//
//                if(mClickIndex == 1) {
//                    //세종대왕
//                    markerOptions.title("서울여대");
//                    markerOptions.snippet("50주년기념관");
//                }
//                else if(mClickIndex == 2) {
//                    markerOptions.title("해운대");
//                    markerOptions.snippet("인문사회관");
//                }
//                else if(mClickIndex == 3) {
//                    markerOptions.title("서울여대");
//                    markerOptions.snippet("제1과학관");
//                }
//                else if(mClickIndex == 4) {
//                    markerOptions.title("서울여대");
//                    markerOptions.snippet("제2과학관");
//                }
//                else if(mClickIndex == 5) {
//                    markerOptions.title("서울여대");
//                    markerOptions.snippet("도서관");
//                }
//                else if(mClickIndex == 6) {
//                    markerOptions.title("서울여대");
//                    markerOptions.snippet("학생누리관");
//                }
//                else if(mClickIndex == 7) {
//                    markerOptions.title("서울여대");
//                    markerOptions.snippet("조형예술관");
//                }
//                else if(mClickIndex == 8) {
//                    markerOptions.title("서울여대");
//                    markerOptions.snippet("샬롬하우스");
//                }
//                else if(mClickIndex == 9) {
//                    markerOptions.title("서울여대");
//                    markerOptions.snippet("바롬인성교육관");
//                }
//                else if(mClickIndex == 10) {
//                    markerOptions.title("서울여대");
//                    markerOptions.snippet("체육관");
//                }
//                else if(mClickIndex == 11) {
//                    markerOptions.title("서울여대");
//                    markerOptions.snippet("정문");
//                }
//                else if(mClickIndex == 12) {
//                    markerOptions.title("서울여대");
//                    markerOptions.snippet("후문");
//                }
//
//                googleMap.addMarker(markerOptions).showInfoWindow();
//
//                //구글맵을 위도,경도 위치로 이동시킨다.
//                googleMap.moveCamera(CameraUpdateFactory.newLatLng(mCurPosLatLng));
//            }
//            googleMap.animateCamera(CameraUpdateFactory.zoomTo(17));
//        }
//    };


    // 새 게시글 작성
    private void upload() {

        //필수 항목 받기

        if (mPhotoPath == null) {
            Toast.makeText(this, "사진을 찍어주세요", Toast.LENGTH_SHORT).show();
            return;
        }
        if (medtTitle.length() == 0) {
            medtTitle.requestFocus();
            Toast.makeText(this, "제목을 적어주세요", Toast.LENGTH_SHORT).show();
            return;
        }

        if(medtExplain.length() == 0) {
            Toast.makeText(this, "설명을 적어주세요", Toast.LENGTH_SHORT).show();
            return;
        }
        if (medtDetailPlace.length() == 0) {
            medtDetailPlace.requestFocus();
            Toast.makeText(this, "세부 장소를 적어주세요", Toast.LENGTH_SHORT).show();
            return;
        }


        //사진부터 Storage 에 업로드한다.
        StorageReference storageRef = mFirebaseStorage.getReference();
        final StorageReference imagesRef = storageRef.child("images/" + mCaptureUri.getLastPathSegment()); //images/파일날짜.jpg

        UploadTask uploadTask = imagesRef.putFile(mCaptureUri);
        //파일 업로드 실패에 따른 콜백 처리를 한다.
        uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()) {
                    throw task.getException();
                }
                return imagesRef.getDownloadUrl();
            }
        }).

                addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        //database upload 를 호출한다.
                        uploadDB(task.getResult().toString(), mCaptureUri.getLastPathSegment());
                    }
                });
    }


    private void uploadDB(String imgUrl, String imgName) {
        //Firebase 데이터베이스에 메모를 등록한다.
        DatabaseReference dbRef = mFirebaseDatabase.getReference();
        String id = dbRef.push().getKey(); // key 를 게시글의 고유 ID 로 사용한다.

        //데이터베이스에 저장한다.
        FreeBean freeBean = new FreeBean();
        freeBean.id = id;
        freeBean.userId = mFirebaseAuth.getCurrentUser().getEmail(); // email
        freeBean.imgUrl = imgUrl;
        freeBean.imgName = imgName;
        freeBean.title = medtTitle.getText().toString(); // 타이틀
        //freeBean.detailPlace = medtPlace.getText().toString();
//        if(mClickIndex != 0) {
//            freeBean.place = spinFree.getSelectedItem().toString();
//            freeBean.latitude = mCurPosLatLng.latitude;
//            freeBean.latitude = mCurPosLatLng.longitude;
//        }
        freeBean.detailPlace = medtDetailPlace.getText().toString();
        freeBean.explain = medtExplain.getText().toString(); // 서브 타이틀(설명)

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        freeBean.date = sdf.format(new Date()); // 게시글 올린 날짜

        dbRef.child("free").child(freeBean.id).setValue(freeBean);
        Toast.makeText(this, "게시물이 등록 되었습니다.", Toast.LENGTH_LONG).show();

        finish();
    }

    public static String getUserIdFromUUID(String userEmail) {
        long val = UUID.nameUUIDFromBytes(userEmail.getBytes()).getMostSignificantBits();
        return String.valueOf(val);
    }

    //갤러리에서 이미지 가져오기
    private void goToAlbum() {

        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
        startActivityForResult(intent, PICK_FROM_ALBUM);
    }

    private static final int PICK_FROM_ALBUM = 1;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode != Activity.RESULT_OK) {

            Toast.makeText(this, "취소 되었습니다.", Toast.LENGTH_SHORT).show();

            if (tempFile != null) {
                if (tempFile.exists()) {
                    if (tempFile.delete()) {
                        Log.e("test", tempFile.getAbsolutePath() + " 삭제 성공");
                        tempFile = null;
                    }
                }
            }

            return;
        }

        if (requestCode == PICK_FROM_ALBUM) {

            Uri photoUri = data.getData();

            Cursor cursor = null;

            try {

                /*
                 *  Uri 스키마를
                 *  content:/// 에서 file:/// 로  변경한다.
                 */
                String[] proj = {MediaStore.Images.Media.DATA};

                assert photoUri != null;
                cursor = getContentResolver().query(photoUri, proj, null, null, null);

                assert cursor != null;
                int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);

                cursor.moveToFirst();

                tempFile = new File(cursor.getString(column_index));

            } finally {
                if (cursor != null) {
                    cursor.close();
                }
            }

            setImage();

        } else if (requestCode == REQUEST_IMAGE_CAPTURE) { //카메라로부터 오는 데이터를 취득한다.
            sendPicture();
        }
    }

    //갤러리에서 받아온 이미지 넣기
    private void setImage() {

        BitmapFactory.Options options = new BitmapFactory.Options();
        Bitmap originalBm = BitmapFactory.decodeFile(tempFile.getAbsolutePath(), options);
        Bitmap resizedBmp = getResizedBitmap(originalBm, 4, 100, 100);

        //줄어든 이미지를 다시 저장한다
        mPhotoPath = tempFile.getAbsolutePath();
        mCaptureUri = Uri.fromFile(tempFile);
        saveBitmapToFileCache(resizedBmp, mPhotoPath);

        mimgFreeWrite.setImageBitmap(resizedBmp);
    }

    //사진
    private void takePicture() {

        Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            mCaptureUri = Uri.fromFile(getOutPutMediaFile());
        } else {
            mCaptureUri = FileProvider.getUriForFile(this,
                    "com.example.cho1.guru2_final_project_1cho", getOutPutMediaFile());
        }

        i.putExtra(MediaStore.EXTRA_OUTPUT, mCaptureUri);

        //내가 원하는 액티비티로 이동하고, 그 액티비티가 종료되면 (finish되면)
        //다시금 나의 액티비티의 onActivityResult() 메서드가 호출되는 구조이다.
        //내가 어떤 데이터를 받고 싶을때 상대 액티비티를 호출해주고 그 액티비티에서
        //호출한 나의 액티비티로 데이터를 넘겨주는 구조이다. 이때 호출되는 메서드가
        //onActivityResult() 메서드 이다.
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
        Bitmap rotatedBmp = rotate(resizedBmp, 90);
        mimgFreeWrite.setImageBitmap(rotatedBmp);
        //줄어든 이미지를 다시 저장한다
        saveBitmapToFileCache(rotatedBmp, mPhotoPath);

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

}
