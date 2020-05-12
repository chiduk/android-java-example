package kr.co.toyappfactory.gosung;


import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;

import kr.co.toyappfactory.gosung.viewpager.search.NewsFeedSearchResult;
import kr.co.toyappfactory.gosung.viewpager.search.ReviewSearchResult;

public class SearchResult extends AppCompatActivity {

    private String hashTag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_result);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);


        Intent intent = getIntent();
        hashTag = intent.getStringExtra("hashTag");

        ViewPager viewPager = (ViewPager)findViewById(R.id.view_pager);
        TabLayout tabLayout = (TabLayout)findViewById(R.id.tab_layout);
        FragmentManager fragmentManager = getSupportFragmentManager();

        PagerAdapter pagerAdapter = new PagerAdapter(fragmentManager);
        viewPager.setAdapter(pagerAdapter);

        tabLayout.setupWithViewPager(viewPager);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (item.getItemId()) {
            case android.R.id.home:
                finish();

                return true;

            default:
                return super.onOptionsItemSelected(item);
        }

    }

    @Override
    public void onBackPressed() {

        finish();
        super.onBackPressed();

    }

    private class PagerAdapter extends FragmentStatePagerAdapter {

        public PagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {

            Fragment fragment = null;

            switch (position){

                case 0:
                    fragment = new NewsFeedSearchResult();

                    break;

                case 1:
                    fragment = new ReviewSearchResult();
                    break;

            }

            Bundle bundle = new Bundle();
            bundle.putString("hashTag", hashTag);
            fragment.setArguments(bundle);

            return fragment;
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position){
            String title = "";

            switch (position){
                case 0:
                    title = "뉴스피드";
                    break;

                case 1:
                    title = "리뷰";
                    break;
            }

            return title;
        }
    }

}
