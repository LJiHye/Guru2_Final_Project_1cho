package com.example.cho1.guru2_final_project_1cho.bean;

import android.graphics.Bitmap;

import java.io.Serializable;

public class FleaBean implements Serializable {
    public String imgUrl;
    public String imgName;
    public String title;  //타이틀
    public String subtitle;  //서브 타이틀
    public String price;  //정가
    public String saleprice;  //판매가
    public String state; // 물건 상태
    public String fault; // 하자
    public String buyday;  //구매일
    public String expire; // 유통기한
    public String size; // 사이즈

    public transient Bitmap bmpTitle;

    public String date; // 게시물 올린 날짜
}