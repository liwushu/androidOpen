package com.flying.test.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;

import com.flying.test.R;

import java.util.ArrayList;

public class TestFragmentActivity extends FragmentActivity {

    ViewPager viewPager;
    FragmentPagerAdapter adapter;
    ArrayList<Fragment> fragmentList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_fragment);
        initViews();
        initValues();
        viewPager.setAdapter(adapter);
    }

    private void initViews(){
        viewPager = (ViewPager)findViewById(R.id.view_page);
        initAdapter();
    }

    private void initValues(){
        fragmentList = new ArrayList<>();
        TestFragment1 fragment1 = new TestFragment1();
        TestFragment2 fragment2 = new TestFragment2();
        TestFragment3 fragment3 = new TestFragment3();
        TestFragment4 fragment4 = new TestFragment4();
        fragmentList.add(fragment1);
        fragmentList.add(fragment2);
        fragmentList.add(fragment3);
        fragmentList.add(fragment4);
    }

    private void initAdapter(){
        adapter = new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                return fragmentList == null? null:fragmentList.get(position);
            }

            @Override
            public int getCount() {
                return fragmentList == null? 0:fragmentList.size();
            }
        };
    }
}
