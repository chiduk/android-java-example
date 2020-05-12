package kr.co.toyappfactory.gosung.menu;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;

import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.internal.LinkedHashTreeMap;


import java.util.ArrayList;
import java.util.HashMap;

import kr.co.toyappfactory.gosung.Comments;
import kr.co.toyappfactory.gosung.HashTag;
import kr.co.toyappfactory.gosung.Post;
import kr.co.toyappfactory.gosung.R;
import kr.co.toyappfactory.gosung.response.ReviewResponse;
import kr.co.toyappfactory.gosung.rest.RestApi;
import kr.co.toyappfactory.gosung.util.Constants;
import kr.co.toyappfactory.gosung.util.JoinUserInfo;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Review extends AppCompatActivity implements AbsListView.OnScrollListener{

    private ArrayList<ReviewResponse> reviewList;
    private ReviewAdapter reviewAdapter;
    private ListView reviewListView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private LinearLayout commentParentLayout;
    private int currFirstVisibleItem;
    private int currVisibleItemCount;
    private int currTotalItemCount;
    private int currNewsFeedListPosition;
    private boolean loadData;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_review);


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);


        reviewList = new ArrayList<>();
        reviewAdapter = new ReviewAdapter();
        reviewListView = (ListView)findViewById(R.id.listview_review);
        reviewListView.setAdapter(reviewAdapter);

        swipeRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.layout_swipe);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(true);
                getReviewGreaterThan(reviewList.get(0)._id);
            }
        });

        loadData = true;
        getReview();
    }


    private void getReview(){
        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").create();
        Retrofit retrofit = new Retrofit.Builder().baseUrl(Constants.appServerHost).addConverterFactory(GsonConverterFactory.create(gson)).build();
        RestApi apiService = retrofit.create(RestApi.class);

        HashMap<String, String> params = new HashMap<>();
        params.put("userId", Constants.getInstance().getUserId());

        Call<ArrayList<ReviewResponse>> call = apiService.getReview(params);
        call.enqueue(new Callback<ArrayList<ReviewResponse>>() {
            @Override
            public void onResponse(Call<ArrayList<ReviewResponse>> call, Response<ArrayList<ReviewResponse>> response) {
                swipeRefreshLayout.setRefreshing(false);
                reviewList = response.body();
                reviewAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Call<ArrayList<ReviewResponse>> call, Throwable t) {

            }
        });

    }

    private void getReviewLessThan(String feedId){
        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").create();
        Retrofit retrofit = new Retrofit.Builder().baseUrl(Constants.appServerHost).addConverterFactory(GsonConverterFactory.create(gson)).build();
        RestApi apiService = retrofit.create(RestApi.class);

        HashMap<String, String> params = new HashMap<>();
        params.put("userId", Constants.getInstance().getUserId());
        params.put("feedId", feedId);

        Call<ArrayList<ReviewResponse>> call = apiService.getReviewLessThan(params);
        call.enqueue(new Callback<ArrayList<ReviewResponse>>() {
            @Override
            public void onResponse(Call<ArrayList<ReviewResponse>> call, Response<ArrayList<ReviewResponse>> response) {
                ArrayList<ReviewResponse> list = response.body();

                for(ReviewResponse elem : list){
                    reviewList.add(elem);
                }
                loadData = true;
                reviewAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Call<ArrayList<ReviewResponse>> call, Throwable t) {
                loadData = true;

            }
        });

    }

    private void getReviewGreaterThan(String feedId){
        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").create();
        Retrofit retrofit = new Retrofit.Builder().baseUrl(Constants.appServerHost).addConverterFactory(GsonConverterFactory.create(gson)).build();
        RestApi apiService = retrofit.create(RestApi.class);

        HashMap<String, String> params = new HashMap<>();
        params.put("userId", Constants.getInstance().getUserId());
        params.put("feedId", feedId);

        Call<ArrayList<ReviewResponse>> call = apiService.getReviewGreaterThan(params);
        call.enqueue(new Callback<ArrayList<ReviewResponse>>() {
            @Override
            public void onResponse(Call<ArrayList<ReviewResponse>> call, Response<ArrayList<ReviewResponse>> response) {
                swipeRefreshLayout.setRefreshing(false);
                ArrayList<ReviewResponse> list = response.body();
                ArrayList<ReviewResponse> newList = new ArrayList<ReviewResponse>();

                for(ReviewResponse elem : list){
                    newList.add(elem);
                }
                loadData = true;

                newList.addAll(reviewList);
                reviewList = newList;

                reviewAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Call<ArrayList<ReviewResponse>> call, Throwable t) {
                loadData = true;

            }
        });

    }

    private void addCommentLayout(String name, String comment){
        LinearLayout commentLayout = new LinearLayout(this);
        commentParentLayout.addView(commentLayout);
        commentLayout.setOrientation(LinearLayout.HORIZONTAL);

        LinearLayout.LayoutParams nameLayoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, 3f);
        LinearLayout.LayoutParams commentLayoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, 1f);

        TextView nameTextView = new TextView(this);
        TextView commentTextView = new TextView(this);
        nameTextView.setLayoutParams(nameLayoutParams);
        commentTextView.setLayoutParams(commentLayoutParams);

        commentLayout.addView(nameTextView);
        commentLayout.addView(commentTextView);

        nameTextView.setText(name);
        commentTextView.setText(comment);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if(resultCode == RESULT_OK){
                getReview();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.write, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;

            case R.id.write:
                Intent intent = new Intent(this, kr.co.toyappfactory.gosung.Review.class);
                startActivityForResult(intent, 1);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }

    }

    @Override
    public void onScrollStateChanged(AbsListView absListView, int scrollState) {
        isScrollCompleted(scrollState);
    }

    @Override
    public void onScroll(AbsListView absListView, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

        currFirstVisibleItem = firstVisibleItem;
        currVisibleItemCount = visibleItemCount;
        currTotalItemCount = totalItemCount;
    }


    private void isScrollCompleted(int currentScrollState) {
        int count = currTotalItemCount - currVisibleItemCount;


        if(currFirstVisibleItem >= count && currTotalItemCount != 0 && reviewList.size() - 1 == currNewsFeedListPosition && loadData){

            if (currentScrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {
                System.out.println("Loading next items");
                loadData = false;
                getReviewLessThan(reviewList.get(currNewsFeedListPosition)._id);
            }

        }

    }


    private class ReviewAdapter extends BaseAdapter{
        private LayoutInflater inflater;

        public ReviewAdapter(){
            inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            return reviewList.size();
        }

        @Override
        public Object getItem(int i) {
            return i;
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(final int i, View view, ViewGroup viewGroup) {
            View rowView = inflater.inflate(R.layout.review_row, null);

            SimpleDraweeView profileImageView = (SimpleDraweeView)rowView.findViewById(R.id.imageview_profile);
            profileImageView.setImageURI(Constants.appServerHost + "/ul/pf/" + reviewList.get(i).uniqueId + ".jpg");

            TextView titleTextView = (TextView)rowView.findViewById(R.id.textview_title);
            titleTextView.setTypeface(null, Typeface.BOLD);
            titleTextView.setText(reviewList.get(i).title);

            TextView timeTextView = (TextView)rowView.findViewById(R.id.textview_time);
            timeTextView.setText(reviewList.get(i).date.split("T")[0]);

            TextView nameTextView = (TextView)rowView.findViewById(R.id.textview_name);
            nameTextView.setText("작성자: " + reviewList.get(i).name);

            LinearLayout parentLayout = (LinearLayout)rowView.findViewById(R.id.layout_images);
            LinearLayout imageLayout = new LinearLayout(Review.this);
            imageLayout.setOrientation(LinearLayout.VERTICAL);
            parentLayout.addView(imageLayout);

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 800);

            for(String image : reviewList.get(i).images){
                SimpleDraweeView imageImageView = new SimpleDraweeView(Review.this);
                imageImageView.setLayoutParams(params);
                imageLayout.addView(imageImageView);
                imageImageView.setImageURI(Constants.appServerHost + "/ul/ps/" + image);
            }

            final TextView likeCountTextView = (TextView)rowView.findViewById(R.id.textview_like);
            likeCountTextView.setText("좋아요 " + reviewList.get(i).like + "개");


            LinearLayout parentLinLayout = (LinearLayout)rowView.findViewById(R.id.layout_comment);
            LinearLayout viewAllCommentsTextViewLayout = new LinearLayout(Review.this);
            parentLinLayout.addView(viewAllCommentsTextViewLayout);

            LinearLayout commentLinLayout = new LinearLayout(Review.this);
            parentLinLayout.addView(commentLinLayout);
            commentLinLayout.setOrientation(LinearLayout.HORIZONTAL);
            LinearLayout.LayoutParams nameLayoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, 3f);
            LinearLayout.LayoutParams commentLayoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, 1f);

            TextView commentNameTextView = new TextView(Review.this);
            TextView commentTextView = new TextView(Review.this);

            commentNameTextView.setTextColor(Color.parseColor("#000000"));
            commentTextView.setTextColor(Color.parseColor("#000000"));

            commentNameTextView.setTextSize(12f);
            commentTextView.setTextSize(12f);

            commentNameTextView.setTypeface(null, Typeface.BOLD);

            commentNameTextView.setLayoutParams(nameLayoutParams);
            commentTextView.setLayoutParams(commentLayoutParams);

            commentLinLayout.addView(commentNameTextView);
            commentLinLayout.addView(commentTextView);

            if( reviewList.get(i).comments.size() > 1){


                LinearLayout.LayoutParams viewMoreLayoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

                TextView viewMoreTextView = new TextView(Review.this);
                viewMoreTextView.setTextSize(12f);
                viewMoreTextView.setLayoutParams(viewMoreLayoutParams);

                viewAllCommentsTextViewLayout.addView(viewMoreTextView);

                viewMoreTextView.setText("댓글 더 보기...");
                viewMoreTextView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        Intent intent = new Intent(Review.this, Comments.class);
                        intent.putExtra("feedId", reviewList.get(i)._id);
                        startActivityForResult(intent, 1);
                    }
                });

                commentNameTextView.setText(reviewList.get(i).comments.get(reviewList.get(i).comments.size() - 1).name);
                commentTextView.setText(reviewList.get(i).comments.get(reviewList.get(i).comments.size() - 1).comment);
            }else if (reviewList.get(i).comments.size()  == 1){

                commentNameTextView.setText(reviewList.get(i).comments.get(0).name);
                commentTextView.setText(reviewList.get(i).comments.get(0).comment);
            }


            final ImageButton likeButton = (ImageButton)rowView.findViewById(R.id.button_like);
            if(reviewList.get(i).liked){
                likeButton.setImageResource(R.drawable.like_btn_on);
            }else{
                likeButton.setImageResource(R.drawable.like_btn);
            }


            likeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").create();
                    Retrofit retrofit = new Retrofit.Builder().baseUrl(Constants.appServerHost).addConverterFactory(GsonConverterFactory.create(gson)).build();
                    RestApi apiService = retrofit.create(RestApi.class);


                    if(!reviewList.get(i).liked){
                        reviewList.get(i).liked = true;

                        HashMap<String, String> params = new HashMap<String, String>();
                        params.put("feedId",reviewList.get(i)._id );
                        params.put("userId", Constants.getInstance().getUserId());
                        params.put("uniqueId", JoinUserInfo.getInstance().getUniqueId());
                        params.put("name", JoinUserInfo.getInstance().getName());

                        Call<ResponseBody> call = apiService.likeReview(params);
                        call.enqueue(new Callback<ResponseBody>() {
                            @Override
                            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                likeButton.setImageResource(R.drawable.like_btn_on);

                                int likeCount = reviewList.get(i).like + 1;
                                reviewList.get(i).like = likeCount;
                                likeCountTextView.setText("좋아요 " + likeCount + "개");

                            }

                            @Override
                            public void onFailure(Call<ResponseBody> call, Throwable t) {

                            }
                        });


                    }else{
                        reviewList.get(i).liked = false;

                        Call<ResponseBody> call = apiService.unlikeReview(reviewList.get(i)._id, Constants.getInstance().getUserId());
                        call.enqueue(new Callback<ResponseBody>() {
                            @Override
                            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                likeButton.setImageResource(R.drawable.like_btn);

                                int likeCount = reviewList.get(i).like - 1;
                                reviewList.get(i).like = likeCount;
                                likeCountTextView.setText("좋아요 " + likeCount + "개");

                            }

                            @Override
                            public void onFailure(Call<ResponseBody> call, Throwable t) {

                            }
                        });
                    }
                }
            });

            ImageButton commentButton = (ImageButton)rowView.findViewById(R.id.button_comment);
            commentButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(Review.this, Comments.class);
                    intent.putExtra("feedId", reviewList.get(i)._id);
                    startActivityForResult(intent, 1);
                }
            });


            ArrayList<int[]> hashTagSpans = HashTag.getSpans(reviewList.get(i).review, '#');
            final SpannableString reviewContent = new SpannableString(reviewList.get(i).review);

            for(int[] span : hashTagSpans){
                int hashTagStart = span[0];
                int hashTagEnd = span[1];
                reviewContent.setSpan(new HashTag(Review.this), hashTagStart, hashTagEnd, 0);
            }

            final TextView smallReviewTextView = (TextView)rowView.findViewById(R.id.textview_post_small);
            smallReviewTextView.setMovementMethod(LinkMovementMethod.getInstance());
            smallReviewTextView.setText(reviewContent);

            final TextView largePostTextView = (TextView)rowView.findViewById(R.id.textview_post_large);
            final TextView viewMorePostTextView = (TextView)rowView.findViewById(R.id.textview_view_more);

            smallReviewTextView.post(new Runnable() {
                @Override
                public void run() {
                    if(smallReviewTextView.getLineCount() > Constants.FEED_MAX_LINE_COUNT){
                        viewMorePostTextView.setVisibility(View.VISIBLE);

                        SpannableString ss = new SpannableString("더보기");
                        ClickableSpan clickableSpan = new ClickableSpan() {
                            @Override
                            public void onClick(View view) {
                                largePostTextView.setText(reviewContent);
                                smallReviewTextView.setVisibility(View.GONE);
                                largePostTextView.setVisibility(View.VISIBLE);

                                viewMorePostTextView.setVisibility(View.GONE);

                            }
                        };
                        ss.setSpan(clickableSpan, 0, 3, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

                        viewMorePostTextView.setText(ss);
                        viewMorePostTextView.setMovementMethod(LinkMovementMethod.getInstance());
                        viewMorePostTextView.setHighlightColor(Color.BLUE);
                    }
                }
            });

            smallReviewTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    final TextView largeReviewTextView = (TextView)findViewById(R.id.textview_post_large);

                    smallReviewTextView.setVisibility(View.GONE);
                    largeReviewTextView.setVisibility(View.VISIBLE);
                    largeReviewTextView.setText(reviewContent);


                    largeReviewTextView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            smallReviewTextView.setVisibility(View.VISIBLE);
                            largeReviewTextView.setVisibility(View.GONE);

                            smallReviewTextView.setText(reviewContent);
                        }
                    });

                }
            });

            return rowView;
        }
    }
}
