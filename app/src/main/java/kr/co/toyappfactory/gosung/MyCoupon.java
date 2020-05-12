package kr.co.toyappfactory.gosung;

import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import kr.co.toyappfactory.gosung.viewpager.coupon.CurrentCoupon;
import kr.co.toyappfactory.gosung.viewpager.coupon.ExpiredCoupon;
import kr.co.toyappfactory.gosung.viewpager.coupon.UsedCoupon;

public class MyCoupon extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_coupon);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);


        ViewPager viewPager = (ViewPager)findViewById(R.id.view_pager);
        TabLayout tabLayout = (TabLayout)findViewById(R.id.tab_layout);
        FragmentManager fragmentManager = getSupportFragmentManager();

        PagerAdapter pagerAdapter = new PagerAdapter(fragmentManager);
        viewPager.setAdapter(pagerAdapter);

        tabLayout.setupWithViewPager(viewPager);
    }

    private class PagerAdapter extends FragmentStatePagerAdapter{

        public PagerAdapter(FragmentManager fm){
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            Fragment fragment = null;
            switch (position){
                case 0:
                    fragment = new CurrentCoupon();
                    break;

                case 1:
                    fragment = new UsedCoupon();
                    break;

                case 2:
                    fragment = new ExpiredCoupon();
                    break;
            }
            return fragment;
        }

        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position){
            String title = "";

            switch (position){
                case 0:
                    title = "사용 가능 쿠폰";
                    break;

                case 1:
                    title = "사용한 쿠폰";
                    break;

                case 2:
                    title = "지난 쿠폰";
                    break;
            }

            return title;
        }
    }
}
