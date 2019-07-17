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

public class SellAdapter extends BaseAdapter {

    private Context mContext;
    private List<FleaBean> mFleaList;

    public SellAdapter(Context context, List<FleaBean> fleaList) {
        mContext = context;
        mFleaList = fleaList;
    }


    public void setList(List<FleaBean> fleaList) {
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
        view = inflater.inflate(R.layout.view_sell_item, null);

        ImageView imgSell = view.findViewById(R.id.imgSell);
        TextView txtSellTitle = view.findViewById(R.id.txtSellTitle);
        TextView txtSellSubTitle = view.findViewById(R.id.txtSellExplain);
        TextView txtSellPrice = view.findViewById(R.id.txtSellPrice);
        TextView txtSellId = view.findViewById(R.id.txtSellId);
        TextView txtSellDate = view.findViewById(R.id.txtSellDate);

        final FleaBean fleaBean = mFleaList.get(i);

        // imgTitle 이미지를 표시할 때는 원격 서버에 있는 이미지이므로, 비동기로 표시한다.
        try{
            if(fleaBean.bmpTitle == null) {
                new DownloadImgTaskFlea(mContext, imgSell, mFleaList, i).execute(new URL(fleaBean.imgUrl));
            } else {
                imgSell.setImageBitmap(fleaBean.bmpTitle);
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
        txtSellTitle.setText(fleaBean.selltitle);
        txtSellSubTitle.setText(fleaBean.wishoption);
        txtSellPrice.setText(fleaBean.wishprice);
        txtSellId.setText(fleaBean.userId);
        txtSellDate.setText(fleaBean.date);

        return view;
    }
}
