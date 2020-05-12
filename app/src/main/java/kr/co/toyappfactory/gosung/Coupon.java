package kr.co.toyappfactory.gosung;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.HashMap;

import kr.co.toyappfactory.gosung.rest.RestApi;
import kr.co.toyappfactory.gosung.util.Constants;
import kr.co.toyappfactory.gosung.util.DialogBox;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class Coupon extends AppCompatActivity {

    private String couponId;
    private String couponName;
    private String couponProvider;
    private String couponImage;
    private String startDate;
    private String endDate;
    private String stores;
    private String info;
    private SimpleDraweeView couponImageView;
    private ImageButton couponIssueImageButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coupon);
        Intent intent = getIntent();

        couponId = intent.getStringExtra("couponId");
        couponProvider = intent.getStringExtra("provider");
        couponName = intent.getStringExtra("name");
        couponImage = intent.getStringExtra("image");
        startDate = intent.getStringExtra("startDate");
        endDate = intent.getStringExtra("endDate");
        stores = intent.getStringExtra("stores");
        info = intent.getStringExtra("info");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        couponImageView = (SimpleDraweeView)findViewById(R.id.imageview_coupon);
        couponIssueImageButton = (ImageButton)findViewById(R.id.imagebutton_issue_coupon);

        couponImageView.setImageURI(couponImage);

        couponIssueImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println("issue coupon");
                issueCoupon();
            }
        });

        TextView couponProviderTextView = (TextView)findViewById(R.id.textview_coupon_provider);
        TextView couponNameTextView = (TextView)findViewById(R.id.textview_coupon_name);
        TextView couponValidDateTextView = (TextView)findViewById(R.id.textview_valid_date);
        TextView storeTextView = (TextView)findViewById(R.id.textview_coupon_store);
        TextView infoTextView = (TextView)findViewById(R.id.textview_coupon_info);

        couponNameTextView.setTypeface(null, Typeface.BOLD);
        infoTextView.setTypeface(null,  Typeface.BOLD);
        storeTextView.setTypeface(null, Typeface.BOLD);

        couponProviderTextView.setText(couponProvider);
        couponNameTextView.setText(couponName);
        couponValidDateTextView.setText(startDate.split("T")[0] + " ~ " + endDate.split("T")[0]);

        storeTextView.setText(stores);
        infoTextView.setText(info);

        checkCoupon();
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

    @Override
    public void onBackPressed() {
        setResult(Activity.RESULT_CANCELED);
        super.onBackPressed();

    }

    private void checkCoupon(){
        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").create();
        Retrofit retrofit = new Retrofit.Builder().baseUrl(Constants.appServerHost).addConverterFactory(GsonConverterFactory.create(gson)).build();
        RestApi apiService = retrofit.create(RestApi.class);

        HashMap<String, String> params = new HashMap<>();
        params.put("userId", Constants.getInstance().getUserId());
        params.put("couponId", couponId);

        Call<ResponseBody> call = apiService.checkCoupon(params);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                int code = response.code();

                if(code == 201){
                    couponIssueImageButton.setEnabled(false);
                    couponIssueImageButton.setImageResource(R.drawable.coupon_end_btn);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });

    }

    private void issueCoupon(){
        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").create();
        Retrofit retrofit = new Retrofit.Builder().baseUrl(Constants.appServerHost).addConverterFactory(GsonConverterFactory.create(gson)).build();

        RestApi apiService = retrofit.create(RestApi.class);

        HashMap<String, String> params = new HashMap<>();
        params.put("userId", Constants.getInstance().getUserId());
        params.put("couponId", couponId);

        Call<ResponseBody> call = apiService.issueCoupon(params);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                int code = response.code();

                if(code == 200){
                    DialogBox.show(Coupon.this, R.string.message_coupon_issued);
                    couponIssueImageButton.setEnabled(false);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });

    }
}