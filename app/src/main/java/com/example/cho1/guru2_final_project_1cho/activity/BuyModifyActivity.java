package com.example.cho1.guru2_final_project_1cho.activity;

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
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.example.cho1.guru2_final_project_1cho.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class BuyModifyActivity extends AppCompatActivity {

    private ImageView mimgBuyWrite;
    //사진이 저장된 경로 - onActivityResult()로부터 받는 데이터
    private Uri mCaptureUri;
    //사진이 저장된 단말기상의 실제 경로
    public String mPhotoPath;
    //startActivityForResult() 에 넘겨주는 값, 이 값이 나중에 onActivityResult()로 돌아와서
    //내가 던진값인지를 구별할 때 사용하는 상수이다.
    public static final int REQUEST_IMAGE_CAPTURE = 200;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buy_modify);
//        intent = getActivity().getIntent();
//
//        mbtnImgReg = findViewById(R.id.btnImgReg);
//        mbtnImgReg.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                takePicture();
//            }
//        });


        //카테고리 드롭다운 스피너 추가
        Spinner dropdown = (Spinner)findViewById(R.id.spinner1);
        String[] items = new String[]{"옷", "책", "생활물품", "기프티콘", "데이터", "대리 예매", "전자기기", "화장품", "기타"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, items);
        dropdown.setAdapter(adapter);

        //제품상태 드롭다운 스피너 추가
        Spinner dropdown2 = (Spinner)findViewById(R.id.spinner2);
        String[] items2 = new String[]{"상", "중", "하"};
        ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, items2);
        dropdown2.setAdapter(adapter2);
    }  //end onCreate


//    private void takePicture() {
//
//        Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//
//        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
//            mCaptureUri = Uri.fromFile( getOutPutMediaFile() );
//        } else {
//            mCaptureUri = FileProvider.getUriForFile(getActivity(),
//                    "com.example.semiprojectsample", getOutPutMediaFile());
//        }
//
//        i.putExtra(MediaStore.EXTRA_OUTPUT, mCaptureUri);
//
//        startActivityForResult(i, REQUEST_IMAGE_CAPTURE);
//    }
//
//    private File getOutPutMediaFile() {
//        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
//                Environment.DIRECTORY_PICTURES), "cameraDemo");
//        if(!mediaStorageDir.exists()) {
//            if(!mediaStorageDir.mkdirs()) {
//                return null;
//            }
//        }
//
//        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
//        File file = new File(mediaStorageDir.getPath() + File.separator + "IMG_" + timeStamp + ".jpg");
//
//        mPhotoPath = file.getAbsolutePath();
//
//        return file;
//    }
//
//    private void sendPicture() {
//        Bitmap bitmap = BitmapFactory.decodeFile(mPhotoPath);
//        Bitmap resizedBmp = getResizedBitmap(bitmap, 4, 100, 100);
//
//        bitmap.recycle();
//
//        //사진이 캡쳐되서 들어오면 뒤집어져 있다. 이애를 다시 원상복구 시킨다.
//        ExifInterface exif = null;
//        try {
//            exif = new ExifInterface(mPhotoPath);
//        } catch(Exception e) {
//            e.printStackTrace();
//        }
//        int exifOrientation;
//        int exifDegree;
//        if(exif != null) {
//            exifOrientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION,
//                    ExifInterface.ORIENTATION_NORMAL);
//            exifDegree = exifOrientToDegree(exifOrientation);
//        } else {
//            exifDegree = 0;
//        }
//        Bitmap rotatedBmp = roate(resizedBmp, exifDegree);
//        mimgModifyCamera.setImageBitmap( rotatedBmp );
//
//        //줄어든 이미지를 다시 저장한다
//        saveBitmapToFileCache(rotatedBmp, mPhotoPath);
//
//        Toast.makeText(getActivity(), "사진경로:"+mPhotoPath, Toast.LENGTH_SHORT).show();
//    }
//
//    private void saveBitmapToFileCache(Bitmap bitmap, String strFilePath) {
//
//        File fileCacheItem = new File(strFilePath);
//        OutputStream out = null;
//
//
//        try
//        {
//            fileCacheItem.createNewFile();
//            out = new FileOutputStream(fileCacheItem);
//
//            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
//        }
//        catch (Exception e)
//        {
//            e.printStackTrace();
//        }
//        finally
//        {
//            try
//            {
//                out.close();
//            }
//            catch (IOException e)
//            {
//                e.printStackTrace();
//            }
//        }
//    }
//
//    private int exifOrientToDegree(int exifOrientation) {
//        if(exifOrientation == ExifInterface.ORIENTATION_ROTATE_90) {
//            return 90;
//        }
//        else if(exifOrientation == ExifInterface.ORIENTATION_ROTATE_180) {
//            return 180;
//        }
//        else if(exifOrientation == ExifInterface.ORIENTATION_ROTATE_270) {
//            return 270;
//        }
//        return 0;
//    }
//
//    private Bitmap roate(Bitmap bmp, float degree) {
//        Matrix matrix = new Matrix();
//        matrix.postRotate(degree);
//        return Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(),
//                matrix, true);
//    }
//
//    //비트맵의 사이즈를 줄여준다.
//    public static Bitmap getResizedBitmap(Bitmap srcBmp, int size, int width, int height){
//        BitmapFactory.Options options = new BitmapFactory.Options();
//        options.inSampleSize = size;
//        Bitmap resized = Bitmap.createScaledBitmap(srcBmp, width, height, true);
//        return resized;
//    }
//
//    public static Bitmap getResizedBitmap(Resources resources, int id, int size, int width, int height){
//        BitmapFactory.Options options = new BitmapFactory.Options();
//        options.inSampleSize = size;
//        Bitmap src = BitmapFactory.decodeResource(resources, id, options);
//        Bitmap resized = Bitmap.createScaledBitmap(src, width, height, true);
//        return resized;
//    }
//
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//
//        //카메라로부터 오는 데이터를 취득한다.
//        if(resultCode == getActivity().RESULT_OK) {
//            if(requestCode == REQUEST_IMAGE_CAPTURE) {
//                sendPicture();
//            }
//        }
//    }
}
