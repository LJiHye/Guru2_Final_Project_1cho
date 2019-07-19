package com.example.cho1.guru2_final_project_1cho.activity;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.example.cho1.guru2_final_project_1cho.R;
import com.example.cho1.guru2_final_project_1cho.fragment.FragmentBuy;
import com.example.cho1.guru2_final_project_1cho.fragment.FragmentEx;
import com.example.cho1.guru2_final_project_1cho.fragment.FragmentMyBuy;
import com.example.cho1.guru2_final_project_1cho.fragment.FragmentMyEx;
import com.example.cho1.guru2_final_project_1cho.fragment.FragmentMySell;
import com.example.cho1.guru2_final_project_1cho.fragment.FragmentSell;
import com.example.cho1.guru2_final_project_1cho.fragment.FragmentUserBuy;
import com.example.cho1.guru2_final_project_1cho.fragment.FragmentUserEx;
import com.example.cho1.guru2_final_project_1cho.fragment.FragmentUserSell;
import com.google.android.material.tabs.TabLayout;

public class UserBoardActivity extends AppCompatActivity {

    private TabLayout mTabMyBoard;
    private ViewPager mViewMyBoard;

    private TextView titleUserDetail;
    private String mWriterID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_board);

        mTabMyBoard = findViewById(R.id.tabMyBoard);
        mViewMyBoard = findViewById(R.id.viewrMyBoard);

        titleUserDetail = findViewById(R.id.titleUserDetail);
        mWriterID = getIntent().getStringExtra("ID");
        titleUserDetail.setText(mWriterID);

        // 탭 생성
        mTabMyBoard.addTab(mTabMyBoard.newTab().setText("사주세요"));
        mTabMyBoard.addTab(mTabMyBoard.newTab().setText("팔아주세요"));
        mTabMyBoard.addTab(mTabMyBoard.newTab().setText("물물교환"));
        mTabMyBoard.setTabGravity(TabLayout.GRAVITY_FILL);

        // ViewPager 생성
        ViewPagerAdapter adapter =
                new ViewPagerAdapter(getSupportFragmentManager(), mTabMyBoard.getTabCount());

        // 탭과 뷰페이저를 서로 연결
        mViewMyBoard.setAdapter(adapter);
        mViewMyBoard.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(mTabMyBoard));
        mTabMyBoard.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                mViewMyBoard.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) { }

            @Override
            public void onTabReselected(TabLayout.Tab tab) { }
        });
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private int tabCount;

        public ViewPagerAdapter(FragmentManager fm, int count) {
            super(fm);
            this.tabCount = count;
        }

        @Override
        public Fragment getItem(int position) {
            switch(position) {
                case 0:
                    return new FragmentUserBuy();
                case 1:
                    return new FragmentUserSell();
                case 2:
                    return new FragmentUserEx();
            }
            return null;
        }

        @Override
        public int getCount() {
            return tabCount;
        }
    } //end class ViewPagerAdapter
}
