package com.example.cho1.guru2_final_project_1cho.bean;

import android.graphics.Bitmap;

import java.io.Serializable;

public class ExBean implements Serializable {
    public String imgUrl;
    public String imgName;
    public String mine; // 내 물건
    public String want; // 교환하고 싶은 물건
    public String state; // 물건 상태
    public String fault; // 하자
    public String expire; // 유통기한
    public String size; // 사이즈

    public transient Bitmap bmpTitle;

    public String date; // 게시물 올린 날짜
}
