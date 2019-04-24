package com.example.mahmud.scrolltab;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;

public class MainActivity extends FragmentActivity {

    ViewPager viewPager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        viewPager = findViewById(R.id.ViewPagerId);
        CustomAdabter adabter= new CustomAdabter(getSupportFragmentManager());
        viewPager.setAdapter(adabter);
    }
}

class CustomAdabter extends FragmentStatePagerAdapter {

    public CustomAdabter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int i) {
        Fragment fragment=null;

        if(i==0){
            fragment = new Fragment1();
        }else if(i==1){
            fragment= new Fragment2();
        }else if(i==2){
            fragment = new Fragment3();
        }
        return fragment;
    }

    @Override
    public int getCount() {
        return 3;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        if(position==0){
            return "Tab One";
        }else if(position==1){
            return "Tab Two";
        }else if(position==2){
            return "Tab Three";
        }
        return null;
    }
}
