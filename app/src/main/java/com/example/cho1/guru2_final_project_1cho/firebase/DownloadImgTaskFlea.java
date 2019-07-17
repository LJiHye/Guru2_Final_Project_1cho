package com.example.cho1.guru2_final_project_1cho.firebase;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.ImageView;

import com.example.cho1.guru2_final_project_1cho.bean.ExBean;
import com.example.cho1.guru2_final_project_1cho.bean.FleaBean;

import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.URL;
import java.util.List;

public class DownloadImgTaskFlea extends AsyncTask<URL, Void, Bitmap> {

    private Context mContext;
    private WeakReference<ImageView> mImageView = null;

    private List<FleaBean> mFleaList;
    private int mPosition;

    // FleaBean 생성자
    public DownloadImgTaskFlea(Context context, ImageView imageView, List<FleaBean> fleaList, int position) {
        mContext = context;
        mImageView = new WeakReference<>(imageView);
        mFleaList = fleaList;
        mPosition = position;
    }

    @Override
    protected void onPreExecute() { // doInBackground 전
        //super.onPreExecute();
    }

    @Override
    protected Bitmap doInBackground(URL... params) { // 무조건 구현해야 하는 메소드 (인자 url -> params로 바꿈)
        URL imageURL = params[0];
        Bitmap downloadedBitmap = null;

        try {
            InputStream inputStream = imageURL.openStream();
            downloadedBitmap = BitmapFactory.decodeStream(inputStream);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return downloadedBitmap;
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) { // doInBackground 후 실행
        if(bitmap != null && mImageView != null) {
            try {
                // 이미지 다운로드 성공
                mImageView.get().setImageBitmap(bitmap);
                // 리스트 갱신 저장
                if(mFleaList != null) {
                    mFleaList.get(mPosition).bmpTitle = bitmap;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
