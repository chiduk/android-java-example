package kr.co.toyappfactory.gosung;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.HashMap;

import kr.co.toyappfactory.gosung.response.LikeBrandResponse;
import kr.co.toyappfactory.gosung.rest.RestApi;
import kr.co.toyappfactory.gosung.util.Constants;
import kr.co.toyappfactory.gosung.util.DialogBox;
import kr.co.toyappfactory.gosung.util.JoinUserInfo;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class EmotionSelection extends AppCompatActivity {

    private Context context;
    private String brandId;
    private String brandName;
    private Integer brandImageId = 0;
    private RatingBar ratingBar;


    private boolean skipCheckBoxFive = false;
    private boolean skipCheckBoxFour = false;
    private boolean skipCheckBoxThree = false;
    private boolean skipCheckBoxTwo = false;
    private boolean skipCheckBoxOne = false;

    private int numOfStars = 0;

    CheckBox checkBoxOneStar;
    CheckBox checkBoxTwoStars;
    CheckBox checkBoxThreeStars;
    CheckBox checkBoxFourStars;
    CheckBox checkBoxFiveStars;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emotion_selection);

        context = this;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        Intent intent = getIntent();
        brandId = intent.getStringExtra("brandId");
        brandName = intent.getStringExtra("brandName");
        brandImageId = intent.getIntExtra("brandImageId", brandImageId);

        getSupportActionBar().setTitle(brandName);

        SimpleDraweeView imageView = (SimpleDraweeView)findViewById(R.id.imageview_selected_brand);
        imageView.setImageURI(Constants.appServerHost + "/big/b/" + brandId + "/thm.jpeg");

        Button submitButton = (Button)findViewById(R.id.button_submit);
        if(submitButton != null){
            submitButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").create();
                    Retrofit retrofit = new Retrofit.Builder().baseUrl(Constants.appServerHost).addConverterFactory(GsonConverterFactory.create(gson)).build();

                    RestApi apiService = retrofit.create(RestApi.class);

                    HashMap<String, String> params = new HashMap<String, String>();
                    Constants constants = Constants.getInstance();

                    params.put("userId", constants.getUserId());
                    params.put("brandId", brandId);
                    params.put("brandName", brandName);
                    params.put("category", String.valueOf(Constants.getInstance().getCategory()));

                    if(checkBoxOneStar.isChecked()){
                        numOfStars = 1;
                    }else if(checkBoxTwoStars.isChecked()){
                        numOfStars = 2;
                    }else if(checkBoxThreeStars.isChecked()){
                        numOfStars = 3;
                    }else if(checkBoxFourStars.isChecked()) {
                        numOfStars = 4;
                    }else if(checkBoxFiveStars.isChecked()){
                        numOfStars = 5;
                    }

                    if(numOfStars > 0){
                        params.put("rating", String.valueOf(numOfStars));



                        likeBrand(apiService, params);
                    }else{

                    }
                }
            });
        }



        addCheckedChangeListener();

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

    private void addCheckedChangeListener(){
        checkBoxOneStar = (CheckBox)findViewById(R.id.checkbox_one_star);
        checkBoxTwoStars = (CheckBox)findViewById(R.id.checkbox_two_stars);
        checkBoxThreeStars = (CheckBox)findViewById(R.id.checkbox_three_stars);
        checkBoxFourStars = (CheckBox)findViewById(R.id.checkbox_four_stars);
        checkBoxFiveStars = (CheckBox)findViewById(R.id.checkbox_five_stars);

        if(checkBoxOneStar != null){
            checkBoxOneStar.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                    skipCheckBoxOne = true;
                    if(!skipCheckBoxTwo)checkBoxTwoStars.setChecked(false);
                    if(!skipCheckBoxThree) checkBoxThreeStars.setChecked(false);
                    if(!skipCheckBoxFour) checkBoxFourStars.setChecked(false);
                    if(!skipCheckBoxFive) checkBoxFiveStars.setChecked(false);

                    skipCheckBoxOne = false;
                    skipCheckBoxTwo = false;
                    skipCheckBoxThree = false;
                    skipCheckBoxFour = false;
                    skipCheckBoxFive = false;

                }
            });
        }

        if(checkBoxTwoStars != null ){
            checkBoxTwoStars.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override

                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {


                    skipCheckBoxTwo = true;

                    if(!skipCheckBoxOne) checkBoxOneStar.setChecked(false);
                    if(!skipCheckBoxThree) checkBoxThreeStars.setChecked(false);
                    if(!skipCheckBoxFour) checkBoxFourStars.setChecked(false);
                    if(!skipCheckBoxFive) checkBoxFiveStars.setChecked(false);

                    skipCheckBoxOne= false;
                    skipCheckBoxTwo = false;
                    skipCheckBoxThree = false;
                    skipCheckBoxFour = false;
                    skipCheckBoxFive = false;
                }
            });
        }

        if(checkBoxThreeStars != null){
            checkBoxThreeStars.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {


                    skipCheckBoxThree = true;

                    if(!skipCheckBoxOne)  checkBoxOneStar.setChecked(false);
                    if(!skipCheckBoxTwo) checkBoxTwoStars.setChecked(false);
                    if(!skipCheckBoxFour) checkBoxFourStars.setChecked(false);
                    if(!skipCheckBoxFive) checkBoxFiveStars.setChecked(false);

                    skipCheckBoxOne= false;
                    skipCheckBoxTwo = false;
                    skipCheckBoxThree = false;
                    skipCheckBoxFour = false;
                    skipCheckBoxFive = false;
                }
            });
        }

        if(checkBoxFourStars != null){
            checkBoxFourStars.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    skipCheckBoxFour = true;

                    if(!skipCheckBoxOne)  checkBoxOneStar.setChecked(false);
                    if(!skipCheckBoxTwo) checkBoxTwoStars.setChecked(false);
                    if(!skipCheckBoxThree) checkBoxThreeStars.setChecked(false);
                    if(!skipCheckBoxFive) checkBoxFiveStars.setChecked(false);

                    skipCheckBoxOne= false;
                    skipCheckBoxTwo = false;
                    skipCheckBoxThree = false;
                    skipCheckBoxFour = false;
                    skipCheckBoxFive = false;

                }
            });
        }

        if(checkBoxFiveStars != null){
            checkBoxFiveStars.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    skipCheckBoxFive = true;

                    if(!skipCheckBoxOne)  checkBoxOneStar.setChecked(false);
                    if(!skipCheckBoxTwo) checkBoxTwoStars.setChecked(false);
                    if(!skipCheckBoxThree) checkBoxThreeStars.setChecked(false);
                    if(!skipCheckBoxFour) checkBoxFourStars.setChecked(false);

                    skipCheckBoxOne = false;
                    skipCheckBoxTwo = false;
                    skipCheckBoxThree = false;
                    skipCheckBoxFour = false;
                    skipCheckBoxFive = false;
                }
            });
        }
    }

    private void likeBrand(RestApi apiService, HashMap<String, String> params){
        Call<LikeBrandResponse> call = apiService.likeBrand(params);
        call.enqueue(new Callback<LikeBrandResponse>() {
            @Override
            public void onResponse(Call<LikeBrandResponse> call, Response<LikeBrandResponse> response) {
                System.out.println("Pref set succeeded");

                if (response.body().code == 202){
                    DialogBox.show(EmotionSelection.this, R.string.message_not_enough_brand_star);
                }else{
                    JoinUserInfo userInfo = JoinUserInfo.getInstance();
                    userInfo.setBrandStar(userInfo.getBrandStar() - numOfStars);
                    Intent intent = new Intent(EmotionSelection.this, Scoreboard.class);
                    startActivity(intent);
                    finish();

                }

            }

            @Override
            public void onFailure(Call<LikeBrandResponse> call, Throwable t) {
                System.out.println("Like Brand Fail: " + t.getMessage());
            }
        });
    }

    private void dislikeBrand(RestApi apiService, HashMap<String, String> params){
        Call<ResponseBody> call = apiService.dislikeBrand(params);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                System.out.println("Pref set succeeded");

                finish();


            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                System.out.println("Dislike Brand Fail: " + t.getMessage());
            }
        });
    }



}
