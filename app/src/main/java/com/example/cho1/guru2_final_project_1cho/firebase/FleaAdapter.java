package com.example.cho1.guru2_final_project_1cho.firebase;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.cho1.guru2_final_project_1cho.R;
import com.example.cho1.guru2_final_project_1cho.bean.FleaBean;

import java.net.URL;
import java.util.List;

public class FleaAdapter extends BaseAdapter {

    private Context mContext;
    private List<FleaBean> mFleaList;

    public FleaAdapter(Context context, List<FleaBean> fleaList) {
        mContext = context;
        mFleaList = fleaList;
    }
    @Override
    public int getCount() {
        return mFleaList.size();
    }

    @Override
    public Object getItem(int i) {
        return mFleaList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = inflater.inflate(R.layout.view_buy_item, null);

        ImageView imgBuy = view.findViewById(R.id.imgBuy);
        TextView txtBuyTitle = view.findViewById(R.id.txtBuyTitle);
        TextView txtBuySubTitle = view.findViewById(R.id.txtBuySubTitle);
        TextView txtBuyPrice = view.findViewById(R.id.txtBuyPrice);

        final FleaBean fleaBean = mFleaList.get(i);

        // imgTitle 이미지를 표시할 때는 원격 서버에 있는 이미지이므로, 비동기로 표시한다.
        try{
            if(fleaBean.bmpTitle == null) {
                new DownloadImgTaskFlea(mContext, imgBuy, mFleaList, i).execute(new URL(fleaBean.imgUrl));
            } else {
                imgBuy.setImageBitmap(fleaBean.bmpTitle);
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
        txtBuyTitle.setText(fleaBean.title);
        txtBuySubTitle.setText(fleaBean.subtitle);
        txtBuyPrice.setText(fleaBean.saleprice);

        return view;
    }
}
