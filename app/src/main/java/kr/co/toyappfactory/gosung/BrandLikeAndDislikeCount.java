package kr.co.toyappfactory.gosung;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.kakao.kakaolink.KakaoLink;
import com.kakao.kakaolink.KakaoTalkLinkMessageBuilder;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;

import kr.co.toyappfactory.gosung.response.LikeAndDislikeCount;
import kr.co.toyappfactory.gosung.rest.RestApi;
import kr.co.toyappfactory.gosung.util.Constants;
import kr.co.toyappfactory.gosung.util.Ranker;
import kr.co.toyappfactory.gosung.util.Util;
import kr.co.toyappfactory.gosung.viewpager.chart.DailyChart;
import kr.co.toyappfactory.gosung.viewpager.chart.MonthlyChart;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class BrandLikeAndDislikeCount extends AppCompatActivity {

    private TextView textViewLikeDislikeCount;
    private TextView textViewBrandRank;
    private TextView textViewPrefSelection;
    private String brandId;
    private String brandName;
    private Integer brandImageId = 0;
    private KakaoLink kakaoLink;
    private KakaoTalkLinkMessageBuilder kakaoTalkLinkMessageBuilder;


    //ImageView imageViewGraphTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_brand_like_and_dislike_count);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);


        Intent intent = getIntent();
        brandId = intent.getStringExtra("brandId");
        brandName = intent.getStringExtra("brandName");


        getSupportActionBar().setTitle(brandName);

        SimpleDraweeView imageView = (SimpleDraweeView) findViewById(R.id.imageview_selected_brand);
        String imgUri = Constants.appServerHost + "/big/b/" + brandId + "/thm.jpeg";
        imageView.setImageURI(imgUri);

        textViewLikeDislikeCount = (TextView) findViewById(R.id.textview_like_dislike_count);

        try {
            kakaoLink = KakaoLink.getKakaoLink(this);
            kakaoTalkLinkMessageBuilder = kakaoLink.createKakaoTalkLinkMessageBuilder();
        } catch (com.kakao.util.KakaoParameterException e) {
            e.printStackTrace();

        }

        Button nextButton = (Button) findViewById(R.id.button_next);


        if (nextButton != null) {
            nextButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(BrandLikeAndDislikeCount.this, EmotionSelection.class);
                    intent.putExtra("brandId", brandId);
                    intent.putExtra("brandImageId", brandImageId);
                    intent.putExtra("brandName", brandName);

                    startActivity(intent);
                    finish();
                }
            });
        }

        addShareButton();
        getLikeAndDislikeCount();




        ViewPager viewPager = (ViewPager)findViewById(R.id.view_pager);
        TabLayout tabLayout = (TabLayout)findViewById(R.id.tab_layout);
        android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();

        ChartPagerAdapter pagerAdapter = new ChartPagerAdapter(fragmentManager);
        viewPager.setAdapter(pagerAdapter);

        tabLayout.setupWithViewPager(viewPager);

    }



    public  boolean isStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
                    checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                System.out.println("Permission is granted");
                return true;
            } else {

                System.out.println("Permission is revoked");
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE, android.Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
                return false;
            }
        }
        else { //permission is automatically granted on sdk<23 upon installation
            System.out.println("Permission is granted");
            return true;
        }


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(grantResults[0]== PackageManager.PERMISSION_GRANTED){
            System.out.println("Permission: " + permissions[0] + "was " + grantResults[0]);
            //resume tasks needing this permission
            Util.shareRank(BrandLikeAndDislikeCount.this, getWindow().getDecorView().getRootView());
        }
    }

    private void addShareButton() {
        Button shareButton = (Button) findViewById(R.id.button_share);

        if (shareButton != null) {
            shareButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if(isStoragePermissionGranted()){
                        Util.shareRank(BrandLikeAndDislikeCount.this, getWindow().getDecorView().getRootView());
                    }

                }
            });
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater inflater = getMenuInflater();
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                //NavUtils.navigateUpFromSameTask(this);

                finish();
                //this.overridePendingTransition(R.anim.slide_exit, R.anim.slide_enter);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void getLikeAndDislikeCount() {
        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").create();
        Retrofit retrofit = new Retrofit.Builder().baseUrl(Constants.appServerHost).addConverterFactory(GsonConverterFactory.create(gson)).build();


        RestApi apiService = retrofit.create(RestApi.class);
        String userId = Constants.getInstance().getUserId();
        int category = Constants.getInstance().getCategory();

        HashMap<String, String> params = new HashMap<String, String>();
        params.put("userId", userId);
        params.put("brandId", brandId);

        Call<LikeAndDislikeCount> call = apiService.getLikeAndDislikeCount(params);
        call.enqueue(new Callback<LikeAndDislikeCount>() {
            @Override
            public void onResponse(Call<LikeAndDislikeCount> call, Response<LikeAndDislikeCount> response) {
                int likeCount = response.body().likeCount;
                int dislikeCount = response.body().dislikeCount;

                TextView textViewStarCount = (TextView) findViewById(R.id.textview_star_count);

                DecimalFormat formatter = new DecimalFormat("#,###");
                textViewStarCount.setText(String.valueOf(formatter.format(likeCount)) + "개");
            }

            @Override
            public void onFailure(Call<LikeAndDislikeCount> call, Throwable t) {
                System.out.println("Get Like Dislike Count Fail: " + t.getMessage());
            }
        });

        params = new HashMap<String, String>();
        params.put("userId", userId);
        params.put("brandId", brandId);
        params.put("category", String.valueOf(0));
        Call<ArrayList<LikeAndDislikeCount>> totalRankingCall = apiService.getLikeRanking(params);
        totalRankingCall.enqueue(new Callback<ArrayList<LikeAndDislikeCount>>() {
            @Override
            public void onResponse(Call<ArrayList<LikeAndDislikeCount>> call, Response<ArrayList<LikeAndDislikeCount>> response) {
                ArrayList<LikeAndDislikeCount> finalList = Ranker.getRanking(response);
                TextView textViewTotalRank = (TextView) findViewById(R.id.textview_total_rank);

                int index = 0;

                for (LikeAndDislikeCount elem : finalList) {
                    if (elem.brandId.equals(brandId)) {
                        textViewTotalRank.setText(String.valueOf(elem.rank + "위"));
                        break;
                    }
                    index++;
                }

                if (index == finalList.size()) {
                    textViewTotalRank.setText(getResources().getString(R.string.no_rank));
                }
            }

            @Override
            public void onFailure(Call<ArrayList<LikeAndDislikeCount>> call, Throwable t) {

            }
        });


        params = new HashMap<String, String>();
        params.put("userId", userId);
        params.put("brandId", brandId);
        params.put("category", String.valueOf(category));
        Call<ArrayList<LikeAndDislikeCount>> categoryRankingCall = apiService.getLikeRanking(params);
        categoryRankingCall.enqueue(new Callback<ArrayList<LikeAndDislikeCount>>() {
            @Override
            public void onResponse(Call<ArrayList<LikeAndDislikeCount>> call, Response<ArrayList<LikeAndDislikeCount>> response) {
                ArrayList<LikeAndDislikeCount> finalList = Ranker.getRanking(response);
                TextView textViewCategoryRank = (TextView) findViewById(R.id.textview_category_rank);

                int index = 0;

                for (LikeAndDislikeCount elem : finalList) {
                    if (elem.brandId.equals(brandId)) {
                        textViewCategoryRank.setText(String.valueOf(elem.rank + "위"));
                        break;
                    }

                    index++;
                }

                if (index == finalList.size()) {
                    textViewCategoryRank.setText(getResources().getString(R.string.no_rank));
                }
            }

            @Override
            public void onFailure(Call<ArrayList<LikeAndDislikeCount>> call, Throwable t) {

            }
        });
    }

    private class ChartPagerAdapter extends FragmentStatePagerAdapter {

        public ChartPagerAdapter(android.support.v4.app.FragmentManager fm) {
            super(fm);
        }

        @Override
        public android.support.v4.app.Fragment getItem(int position) {

            android.support.v4.app.Fragment fragment = null;
            Bundle bundle = new Bundle();
            bundle.putString("brandId", brandId);

            switch (position){

                case 0:
                    fragment = new DailyChart();
                    fragment.setArguments(bundle);
                    break;

                case 1:
                    fragment = new MonthlyChart();
                    fragment.setArguments(bundle);
                    break;

            }

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
                    title = "일별";
                    break;

                case 1:
                    title = "월별";
                    break;
            }

            return title;
        }
    }
}
