package kr.co.toyappfactory.gosung;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;


import com.facebook.drawee.view.SimpleDraweeView;
import com.github.mikephil.charting.charts.BarChart;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.kakao.kakaolink.KakaoLink;
import com.kakao.kakaolink.KakaoTalkLinkMessageBuilder;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import kr.co.toyappfactory.gosung.chart.LikeCountLineChart;
import kr.co.toyappfactory.gosung.response.CountDate;
import kr.co.toyappfactory.gosung.response.CountList;
import kr.co.toyappfactory.gosung.response.DailyLikeCountResponse;
import kr.co.toyappfactory.gosung.response.LikeAndDislikeCount;
import kr.co.toyappfactory.gosung.response.MonthlyLikeCountResponse;
import kr.co.toyappfactory.gosung.response.UploadSSResponse;
import kr.co.toyappfactory.gosung.rest.RestApi;
import kr.co.toyappfactory.gosung.util.Constants;
import kr.co.toyappfactory.gosung.util.Ranker;
import kr.co.toyappfactory.gosung.util.Util;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class BrandPage extends AppCompatActivity {

    private String brandId;
    private String brandName;

    ChartLoader chartLoader = new ChartLoader();
    //ImageView imageViewGraphTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_brand_page);

        Intent intent = getIntent();
        Integer likeCount = 0;
        Integer dislikeCount = 0;

        brandId = intent.getStringExtra("brandId");
        brandName = intent.getStringExtra("brandName");
        likeCount = intent.getIntExtra("likeCount", likeCount);
        dislikeCount = intent.getIntExtra("dislikeCount", dislikeCount);

        String brandImageUrl = Constants.appServerHost + "/big/b/" + brandId + "/thm.jpeg";

        SimpleDraweeView imageViewBrand = (SimpleDraweeView)findViewById(R.id.imageview_selected_brand);
        imageViewBrand.setImageURI(brandImageUrl);


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle(brandName);

        getLikeAndDislikeCount();
        //imageViewGraphTitle = (ImageView)findViewById(R.id.imageview_daily_graph_title);
        //imageViewGraphTitle.setImageResource(R.drawable.daily_graph_title);
        chartLoader.execute(Constants.DATATYPE_DAILY);



        addShareButton();
        addGraphButton();
    }

    private void addGraphButton(){
        ImageButton dailyButton = (ImageButton)findViewById(R.id.imagebutton_daily_graph_title);
        ImageButton monthlyButton = (ImageButton)findViewById(R.id.imagebutton_monthly_graph_title);

        if(dailyButton != null){
            dailyButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ChartLoader dailychartLoader = new ChartLoader();
                    dailychartLoader.execute(Constants.DATATYPE_DAILY);
                }
            });
        }

        if(monthlyButton != null){
            monthlyButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ChartLoader dailychartLoader = new ChartLoader();
                    dailychartLoader.execute(Constants.DATATYPE_MONTHLY);
                }
            });
        }
    }

    private void addShareButton(){
        Button shareButton = (Button) findViewById(R.id.button_share);

        if (shareButton != null) {
            shareButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (isStoragePermissionGranted()) {
                        Util.shareRank(BrandPage.this, getWindow().getDecorView().getRootView());

                    }


                }
            });
        }
    }

    public boolean isStoragePermissionGranted() {
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
            Util.shareRank(BrandPage.this, getWindow().getDecorView().getRootView());
        }
    }


    private void getLikeAndDislikeCount() {
        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").create();
        Retrofit retrofit = new Retrofit.Builder().baseUrl(Constants.appServerHost).addConverterFactory(GsonConverterFactory.create(gson)).build();

        RestApi apiService = retrofit.create(RestApi.class);
        String userId =  Constants.getInstance().getUserId();
        int category = Constants.getInstance().getCategory();

        HashMap<String, String> params = new HashMap<String, String>();
        params.put("userId", userId);
        params.put("brandId", brandId);

        Call<LikeAndDislikeCount> call = apiService.getLikeAndDislikeCount(params);
        call.enqueue(new Callback<LikeAndDislikeCount>() {
            @Override
            public void onResponse(Call<LikeAndDislikeCount> call, Response<LikeAndDislikeCount> response) {
                int likeCount = response.body().likeCount;

                TextView textViewStarCount = (TextView)findViewById(R.id.textview_star_count);
                textViewStarCount.setText(String.valueOf(likeCount));
            }

            @Override
            public void onFailure(Call<LikeAndDislikeCount> call, Throwable t) {
                System.out.println("Get Like Dislike Count Fail: " + t.getMessage());
            }
        });

        params = new HashMap<String, String>();
        params.put("userId",userId);
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
                        textViewTotalRank.setText(String.valueOf(elem.rank));
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
        params.put("category", String.valueOf(Constants.getInstance().getCategory()));
        Call<ArrayList<LikeAndDislikeCount>> categoryRankingCall = apiService.getLikeRanking(params);
        categoryRankingCall.enqueue(new Callback<ArrayList<LikeAndDislikeCount>>() {
            @Override
            public void onResponse(Call<ArrayList<LikeAndDislikeCount>> call, Response<ArrayList<LikeAndDislikeCount>> response) {
                ArrayList<LikeAndDislikeCount> finalList = Ranker.getRanking(response);
                TextView textViewCategoryRank = (TextView) findViewById(R.id.textview_category_rank);

                int index = 0;

                for (LikeAndDislikeCount elem : finalList) {
                    if (elem.brandId.equals(brandId)) {
                        textViewCategoryRank.setText(String.valueOf(elem.rank));
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
                finish();

                return true;


            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private class ChartLoader extends AsyncTask<Integer, Void, Void>{

        @Override
        protected Void doInBackground(Integer... params) {

            Gson gson = new GsonBuilder()
                    .setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ")
                    .create();
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(Constants.appServerHost)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build();

            RestApi apiService = retrofit.create(RestApi.class);

            HashMap<String, String> parameters = new HashMap<String, String>();
            parameters.put("userId", Constants.getInstance().getUserId());
            parameters.put("brandId", brandId);

            if(params[0] == Constants.DATATYPE_DAILY){

                Call<ArrayList<DailyLikeCountResponse>> call = apiService.getDailyLikeCount(parameters);
                call.enqueue(new Callback<ArrayList<DailyLikeCountResponse>>() {
                    @Override
                    public void onResponse(Call<ArrayList<DailyLikeCountResponse>> call, Response<ArrayList<DailyLikeCountResponse>> response) {

                        CountList list = new CountList();

                        for(DailyLikeCountResponse elem : response.body()){
                            CountDate countDate = new CountDate(elem.year, elem.month, elem.day, elem.likeCount);
                            list.add(countDate);
                        }

                        Fragment fragment = new LikeCountLineChart();
                        Bundle bundle = new Bundle();
                        bundle.putParcelable("dataList", list);
                        bundle.putInt("dataType", Constants.DATATYPE_DAILY);
                        fragment.setArguments(bundle);
                        FragmentManager fragmentManager = getSupportFragmentManager();
                        fragmentManager.beginTransaction().replace(R.id.framelayout_chart, fragment).commit();

                    }

                    @Override
                    public void onFailure(Call<ArrayList<DailyLikeCountResponse>> call, Throwable t) {
                        System.out.println("Get Daily Like Count Error " + t.getMessage());
                    }
                });
            }else{
                Call<ArrayList<MonthlyLikeCountResponse>> call = apiService.getMonthlyLikeCount(parameters);
                call.enqueue(new Callback<ArrayList<MonthlyLikeCountResponse>>() {
                    @Override
                    public void onResponse(Call<ArrayList<MonthlyLikeCountResponse>> call, Response<ArrayList<MonthlyLikeCountResponse>> response) {

                        CountList list = new CountList();

                        for(MonthlyLikeCountResponse elem : response.body()){
                            CountDate countDate = new CountDate(elem.year, elem.month, 0, elem.likeCount);
                            list.add(countDate);
                        }

                        Fragment fragment = new LikeCountLineChart();
                        Bundle bundle = new Bundle();
                        bundle.putParcelable("dataList", list);
                        bundle.putInt("dataType", Constants.DATATYPE_MONTHLY);
                        fragment.setArguments(bundle);
                        FragmentManager fragmentManager = getSupportFragmentManager();
                        fragmentManager.beginTransaction().replace(R.id.framelayout_chart, fragment).commit();

                    }

                    @Override
                    public void onFailure(Call<ArrayList<MonthlyLikeCountResponse>> call, Throwable t) {
                        System.out.println("Get Daily Like Count Error " + t.getMessage());
                    }
                });
            }


            return null;
        }
    }
}
