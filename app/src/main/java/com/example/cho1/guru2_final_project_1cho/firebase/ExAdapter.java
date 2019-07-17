package com.example.cho1.guru2_final_project_1cho.firebase;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.cho1.guru2_final_project_1cho.R;
import com.example.cho1.guru2_final_project_1cho.bean.ExBean;

import java.net.URL;
import java.util.List;

public class ExAdapter extends BaseAdapter {

    private Context mContext;
    private List<ExBean> mExList;

    public void setList(List<ExBean> exList) {
        mExList = exList;
    }

    public ExAdapter(Context context, List<ExBean> exList) {
        mContext = context;
        mExList = exList;
    }
    @Override
    public int getCount() {
        return mExList.size();
    }

    @Override
    public Object getItem(int i) {
        return mExList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = inflater.inflate(R.layout.view_ex_item, null);

        ImageView imgEx = view.findViewById(R.id.imgEx);
        TextView txtExMine = view.findViewById(R.id.txtExMine);
        TextView txtExWant = view.findViewById(R.id.txtExWant);
        TextView txtExDate = view.findViewById(R.id.txtExDate);

        final ExBean exBean = mExList.get(i);

        // imtTitle 이미지를 표시할 때는 원격 서버에 있는 이미지이므로, 비동기로 표시한다.
        try{
            if(exBean.bmpTitle == null) {
                new DownloadImgTaskEx(mContext, imgEx, mExList, i).execute(new URL(exBean.imgUrl));
            } else {
                imgEx.setImageBitmap(exBean.bmpTitle);
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
        txtExMine.setText(exBean.mine);
        txtExWant.setText(exBean.want);
        txtExDate.setText(exBean.date);

        return view;
    }
}
