package kr.co.toyappfactory.gosung;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.HashMap;

import kr.co.toyappfactory.gosung.response.LikeAndDislikeCount;
import kr.co.toyappfactory.gosung.rest.RestApi;
import kr.co.toyappfactory.gosung.util.Constants;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SelectionResult extends AppCompatActivity {

    private String brandId;
    private String brandName;
    private Integer brandImageId = 0;

    private TextView textViewLikeDislike;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selection_result);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);


        Intent intent = getIntent();
        brandId = intent.getStringExtra("brandId");
        brandName = intent.getStringExtra("brandName");
        brandImageId = intent.getIntExtra("brandImageId", brandImageId);

        ImageView imageView = (ImageView)findViewById(R.id.imageview_selected_brand);
        imageView.setImageResource(brandImageId);

        textViewLikeDislike = (TextView)findViewById(R.id.textview_like_dislike_count);

        Button closeButton = (Button)findViewById(R.id.button_close);
        if(closeButton != null){
            closeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
        }
        getLikeAndDislikeCount();
    }

    private void getLikeAndDislikeCount(){
        Gson gson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ")
                .create();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.appServerHost)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        RestApi apiService = retrofit.create(RestApi.class);

        HashMap<String, String> params = new HashMap<String, String>();

        params.put("brandId", brandId);

        Call<LikeAndDislikeCount> call = apiService.getLikeAndDislikeCount(params);
        call.enqueue(new Callback<LikeAndDislikeCount>() {
            @Override
            public void onResponse(Call<LikeAndDislikeCount> call, Response<LikeAndDislikeCount> response) {
                int likeCount = response.body().likeCount;
                int dislikeCount = response.body().dislikeCount;

                String strLikeDislike = getResources().getString(R.string.brandLikeDislike);
                textViewLikeDislike.setText(String.format(strLikeDislike, likeCount, dislikeCount));

            }

            @Override
            public void onFailure(Call<LikeAndDislikeCount> call, Throwable t) {
                System.out.println("Get Like Dislike Count Fail: " + t.getMessage());
            }
        });
    }
}
