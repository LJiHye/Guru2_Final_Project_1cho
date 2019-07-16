package com.example.cho1.guru2_final_project_1cho.bean;

import android.graphics.Bitmap;

import java.io.Serializable;

//회원정보 Bean
public class MemberBean implements Serializable {

    public String photoPath;
    public String id;
    public String memId;
    public String memName;
    public String memPw;
    public String memKakaoId;
    public String imgUrl;
    public String imgName;
    public String date;

    public transient Bitmap bmpTitle;
}
