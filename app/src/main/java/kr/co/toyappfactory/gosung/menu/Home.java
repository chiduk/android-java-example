package kr.co.toyappfactory.gosung.menu;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.view.SimpleDraweeView;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import kr.co.toyappfactory.gosung.BrandLikeAndDislikeCount;
import kr.co.toyappfactory.gosung.BrandSearch;
import kr.co.toyappfactory.gosung.Join;
import kr.co.toyappfactory.gosung.MyPage;
import kr.co.toyappfactory.gosung.Post;
import kr.co.toyappfactory.gosung.PostSingleView;
import kr.co.toyappfactory.gosung.R;
import kr.co.toyappfactory.gosung.Review;
import kr.co.toyappfactory.gosung.ReviewSingleView;
import kr.co.toyappfactory.gosung.response.BrandStarResponse;
import kr.co.toyappfactory.gosung.response.LikeAndDislikeCount;
import kr.co.toyappfactory.gosung.response.RefreshPostResponse;
import kr.co.toyappfactory.gosung.response.ReviewResponse;
import kr.co.toyappfactory.gosung.response.TopicResponse;
import kr.co.toyappfactory.gosung.rest.RestApi;
import kr.co.toyappfactory.gosung.util.Constants;
import kr.co.toyappfactory.gosung.util.JoinUserInfo;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


/**
 * A simple {@link Fragment} subclass.
 */
public class Home extends Fragment {
    private static int REFRESH_NEWS_FEED = 1;
    private SwipeRefreshLayout swipeRefreshLayout;
    private SwipeRefreshLayout reviewSwipeLayout;
    private ArrayList<RefreshPostResponse> feedList;
    private ArrayList<ReviewResponse> reviewList;
    private ArrayList<LikeAndDislikeCount> top5RankList;
    private NewsFeedAdapter newsFeedAdapter;
    private ReviewAdapter reviewAdapter;
    private ArrayList<TopicResponse> marqueeList;
    private int timerIndex;
    private RecyclerView horizontalRecyclerView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        setHasOptionsMenu(true);

        View view = inflater.inflate(R.layout.fragment_home, container, false);
        feedList = new ArrayList<>();
        reviewList = new ArrayList<>();
        top5RankList = new ArrayList<>();

        newsFeedAdapter = new NewsFeedAdapter(getActivity());
        reviewAdapter = new ReviewAdapter();

        marqueeList = new ArrayList<>();
        //marqueeList.add("http://images-resrc.staticlp.com/S=W1000M,H700M/O=85/http://media.lonelyplanet.com/a/g/hi/t/833f3235daa50ad348ea0f06b62c9337-jama-masjid.jpg");
        //marqueeList.add("http://image.shutterstock.com/z/stock-photo-image-of-wooden-table-in-front-of-abstract-blurred-background-of-resturant-lights-321651011.jpg");
        //marqueeList.add("http://image.shutterstock.com/z/stock-photo-woman-push-ups-on-the-floor-270877742.jpg");
        //marqueeList.add("https://www.enclean.com/_newimg/new_web/img/event/oilplus_1510/event01_img_main01.gif");
        timerIndex = 0;

        horizontalRecyclerView = (RecyclerView)view.findViewById(R.id.recyclerview_horizontal);

        LinearLayoutManager horizontalLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        horizontalRecyclerView.setLayoutManager(horizontalLayoutManager);

        setActionBar();
        addPostButton(view);
        addReviewButton(view);
        getTop5Ranking(view);
        setMarquee(view);
        getTopic();
        getPost();
        getReview();
        getBrandStar();
        addViewMoreNewsFeedButton(view);
        addViewMoreReview(view);

        ListView newsFeedListView = (ListView)view.findViewById(R.id.listview_news_feed);
        newsFeedListView.setAdapter(newsFeedAdapter);
        newsFeedListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity(), PostSingleView.class);
                RefreshPostResponse response = feedList.get(position);

                intent.putExtra("feedId", response._id);
                intent.putExtra("date", response.date);
                intent.putExtra("uniqueId", response.uniqueId);
                intent.putExtra("name", response.name);
                intent.putExtra("userId", response.userId);
                intent.putExtra("post", response.post);
                intent.putStringArrayListExtra("images", response.images);
                intent.putExtra("likeCount", response.like);
                intent.putExtra("createMenu", false);
                startActivityForResult(intent, REFRESH_NEWS_FEED);
            }
        });

        ListView reviewListView = (ListView)view.findViewById(R.id.listview_review);
        reviewListView.setAdapter(reviewAdapter);
        reviewListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                Intent intent = new Intent(getActivity(), ReviewSingleView.class);
                intent.putExtra("reviewId", reviewList.get(i)._id);
                intent.putExtra("date", reviewList.get(i).date);
                intent.putExtra("uniqueId", reviewList.get(i).uniqueId);
                intent.putExtra("userId", reviewList.get(i).userId);
                intent.putExtra("category", reviewList.get(i).category);
                intent.putExtra("title", reviewList.get(i).title);
                intent.putExtra("name", reviewList.get(i).name);
                intent.putExtra("review", reviewList.get(i).review);
                intent.putStringArrayListExtra("images", reviewList.get(i).images);
                intent.putExtra("likeCount", reviewList.get(i).like);

                startActivity(intent);

            }
        });

        swipeRefreshLayout = (SwipeRefreshLayout)view.findViewById(R.id.layout_swipe_news_feed);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Handler().post(new Runnable() {
                    @Override public void run() {
                        swipeRefreshLayout.setRefreshing(true);
                        getPost();
                    }
                });
            }
        });

        reviewSwipeLayout = (SwipeRefreshLayout)view.findViewById(R.id.layout_swipe_review);
        reviewSwipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Handler().post(new Runnable() {
                    @Override
                    public void run() {
                        reviewSwipeLayout.setRefreshing(true);
                        getReview();
                    }
                });
            }
        });

        return view;

    }

    @Override
    public void onActivityResult(int reqCode, int resCode, Intent data) {
        super.onActivityResult(reqCode, resCode, data);

        if (resCode == Activity.RESULT_OK) {
            if(reqCode == REFRESH_NEWS_FEED){
                getPost();
            }
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater){
        inflater.inflate(R.menu.my_page, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch(item.getItemId()){
            case R.id.my_page:
                Intent intent = new Intent(getActivity(), MyPage.class);
                startActivity(intent);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void setMarquee(final View view){
        final Handler handler = new Handler();
        final Runnable runResult = new Runnable() {
            @Override
            public void run() {
                timerIndex++;

                getPost();
                getReview();
                getTop5Ranking(view);
                getBrandStar();

            }
        };

        int delay = 1000;
        int period = 30000;
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                handler.post(runResult);
            }
        }, delay, period);
    }

    private void getBrandStar(){
        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").create();
        Retrofit retrofit = new Retrofit.Builder().baseUrl(Constants.appServerHost).addConverterFactory(GsonConverterFactory.create(gson)).build();
        RestApi apiService = retrofit.create(RestApi.class);

        HashMap<String, String> params = new HashMap<>();
        params.put("userId", Constants.getInstance().getUserId());

        Call<BrandStarResponse> call = apiService.getBrandStar(params);
        call.enqueue(new Callback<BrandStarResponse>() {
            @Override
            public void onResponse(Call<BrandStarResponse> call, Response<BrandStarResponse> response) {
                int brandStar = response.body().numOfStars;

                JoinUserInfo.getInstance().setBrandStar(brandStar);
            }

            @Override
            public void onFailure(Call<BrandStarResponse> call, Throwable t) {

            }
        });
    }

    private void getTop5Ranking(final View view){
        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").create();
        Retrofit retrofit = new Retrofit.Builder().baseUrl(Constants.appServerHost).addConverterFactory(GsonConverterFactory.create(gson)).build();
        RestApi apiService = retrofit.create(RestApi.class);

        HashMap<String, String> params = new HashMap<>();
        params.put("userId", Constants.getInstance().getUserId());

        Call<ArrayList<LikeAndDislikeCount>> call = apiService.getTop5Ranking(params);
        call.enqueue(new Callback<ArrayList<LikeAndDislikeCount>>() {
            @Override
            public void onResponse(Call<ArrayList<LikeAndDislikeCount>> call, Response<ArrayList<LikeAndDislikeCount>> response) {
                top5RankList = response.body();

                SimpleDraweeView[] brandIcon = new SimpleDraweeView[5];
                brandIcon[0] = (SimpleDraweeView)view.findViewById(R.id.imageview_brand_icon_1);
                brandIcon[1] = (SimpleDraweeView)view.findViewById(R.id.imageview_brand_icon_2);
                brandIcon[2] = (SimpleDraweeView)view.findViewById(R.id.imageview_brand_icon_3);
                brandIcon[3] = (SimpleDraweeView)view.findViewById(R.id.imageview_brand_icon_4);
                brandIcon[4] = (SimpleDraweeView)view.findViewById(R.id.imageview_brand_icon_5);

                TextView[] brandName = new TextView[5];
                brandName[0] = (TextView)view.findViewById(R.id.textview_brand_name_1);
                brandName[1] = (TextView)view.findViewById(R.id.textview_brand_name_2);
                brandName[2] = (TextView)view.findViewById(R.id.textview_brand_name_3);
                brandName[3] = (TextView)view.findViewById(R.id.textview_brand_name_4);
                brandName[4] = (TextView)view.findViewById(R.id.textview_brand_name_5);


                int index = 0;

                if(top5RankList != null){
                    for( final LikeAndDislikeCount elem : top5RankList){

                        brandIcon[index].setImageURI(Constants.appServerHost + "/big/b/" + elem.brandId + "/thm.jpeg");
                        brandName[index].setText(elem.name);
                        brandIcon[index].setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intent = new Intent(getActivity(), BrandLikeAndDislikeCount.class);
                                intent.putExtra("brandId", elem.brandId);
                                intent.putExtra("brandName", elem.name);
                                startActivity(intent);
                            }
                        });

                        index++;
                    }
                }
            }

            @Override
            public void onFailure(Call<ArrayList<LikeAndDislikeCount>> call, Throwable t) {

            }
        });
    }



    private void addViewMoreNewsFeedButton(View view){
        ImageButton button = (ImageButton)view.findViewById(R.id.button_view_more_news_feed);
        if(button != null){
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getActivity(), NewsFeed.class);
                    startActivity(intent);
                }
            });
        }
    }

    private void addViewMoreReview(View view){
        ImageButton button = (ImageButton)view.findViewById(R.id.button_view_more_review);
        if(button != null){
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getActivity(), kr.co.toyappfactory.gosung.menu.Review.class);
                    startActivity(intent);
                }
            });
        }
    }

    private void getPost(){
        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").create();
        Retrofit retrofit = new Retrofit.Builder().baseUrl(Constants.appServerHost).addConverterFactory(GsonConverterFactory.create(gson)).build();
        RestApi apiService = retrofit.create(RestApi.class);
        Call<ArrayList<RefreshPostResponse>> call = apiService.refreshPost();
        call.enqueue(new Callback<ArrayList<RefreshPostResponse>>() {
            @Override
            public void onResponse(Call<ArrayList<RefreshPostResponse>> call, Response<ArrayList<RefreshPostResponse>> response) {
                swipeRefreshLayout.setRefreshing(false);
                if( response.body() != null){
                    feedList = response.body();
                    newsFeedAdapter.notifyDataSetChanged();

                }

            }

            @Override
            public void onFailure(Call<ArrayList<RefreshPostResponse>> call, Throwable t) {
                swipeRefreshLayout.setRefreshing(false);
            }
        });
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
                reviewSwipeLayout.setRefreshing(false);

                if(response.body() != null){
                    reviewList = response.body();
                    reviewAdapter.notifyDataSetChanged();

                }
            }

            @Override
            public void onFailure(Call<ArrayList<ReviewResponse>> call, Throwable t) {

            }
        });
    }


    private void setActionBar(){
        View v = getActivity().getLayoutInflater().inflate(R.layout.brandfeed_search_action_bar, null);

        ImageButton button = (ImageButton)v.findViewById(R.id.imagebutton_search);

        ActionBar actionBar = ((AppCompatActivity)getActivity()).getSupportActionBar();
        actionBar.setTitle("");
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setCustomView(v);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), BrandSearch.class);
                startActivity(intent);
            }
        });
    }

    private void addPostButton(View view){
        Button postButton = (Button)view.findViewById(R.id.button_post);
        postButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), Post.class);
                startActivity(intent);
            }
        });
    }

    private void addReviewButton(View view){
        Button reviewButton = (Button)view.findViewById(R.id.button_review);
        reviewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), Review.class);
                startActivity(intent);
            }
        });
    }

    private void getTopic(){
        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").create();
        Retrofit retrofit = new Retrofit.Builder().baseUrl(Constants.appServerHost).addConverterFactory(GsonConverterFactory.create(gson)).build();

        RestApi apiService = retrofit.create(RestApi.class);
        Call<ArrayList<TopicResponse>> call = apiService.getTopic();
        call.enqueue(new Callback<ArrayList<TopicResponse>>() {
            @Override
            public void onResponse(Call<ArrayList<TopicResponse>> call, Response<ArrayList<TopicResponse>> response) {
                if(response.body() != null){
                    marqueeList = response.body();
                    horizontalRecyclerView.setAdapter(new HorizontalAdapter());

                }
            }

            @Override
            public void onFailure(Call<ArrayList<TopicResponse>> call, Throwable t) {

            }
        });

    }

    private class NewsFeedAdapter extends BaseAdapter{

        private LayoutInflater inflater;

        public NewsFeedAdapter(Context context){

            inflater = (LayoutInflater)context.getSystemService(context.LAYOUT_INFLATER_SERVICE);

        }

        @Override
        public int getCount() {
            return feedList.size();
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = inflater.inflate(R.layout.news_feed_list_row, null);

            SimpleDraweeView imageview_profile = (SimpleDraweeView)view.findViewById(R.id.imageview_profile);
            imageview_profile.setImageURI(Constants.appServerHost + "/ul/pf/" + feedList.get(position).uniqueId + ".jpg");

            TextView nameTextView = (TextView)view.findViewById(R.id.textview_name);
            nameTextView.setTypeface(null, Typeface.BOLD);
            nameTextView.setText(feedList.get(position).name);

            TextView textView = (TextView)view.findViewById(R.id.textview_feed);
            textView.setText(feedList.get(position).post);

            TextView timeTextView = (TextView)view.findViewById(R.id.textview_time);
            timeTextView.setText(feedList.get(position).date.split("T")[0]);

            SimpleDraweeView feedImage1 = (SimpleDraweeView) view.findViewById(R.id.imageview_feed_image1);
            SimpleDraweeView feedImage2 = (SimpleDraweeView) view.findViewById(R.id.imageview_feed_image2);

/*
            if (feedList.get(position).images.size() > 1){
                String url1 = Constants.appServerHost + "/ul/" + feedList.get(position).images.get(0);
                String url2 = Constants.appServerHost + "/ul/" + feedList.get(position).images.get(1);
                feedImage1.setImageURI(url1);
                feedImage2.setImageURI(url2);
            }else if(feedList.get(position).images.size() == 1){
                String url2 = Constants.appServerHost + "/ul/" + feedList.get(position).images.get(0);
                feedImage2.setImageURI(url2);
            }*/

            if(feedList.get(position).images.size() > 0){
                String url2 = Constants.appServerHost + "/ul/ps/" + feedList.get(position).images.get(0);
                feedImage2.setImageURI(url2);
            }


            return view;
        }
    }

    private class ReviewAdapter extends BaseAdapter{
        private LayoutInflater inflater;

        public ReviewAdapter(){
            inflater = (LayoutInflater)getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
        public View getView(int i, View view, ViewGroup viewGroup) {
            View rowView = inflater.inflate(R.layout.review_list_row, null);
            SimpleDraweeView imageviewProfile = (SimpleDraweeView)rowView.findViewById(R.id.imageview_profile);
            imageviewProfile.setImageURI(Constants.appServerHost + "/ul/pf/" + reviewList.get(i).uniqueId + ".jpg");

            TextView titleTextView = (TextView)rowView.findViewById(R.id.textview_title);
            titleTextView.setTypeface(null, Typeface.BOLD);
            titleTextView.setText(reviewList.get(i).title);

            TextView reviewTextView = (TextView)rowView.findViewById(R.id.textview_review);
            reviewTextView.setText(reviewList.get(i).review);

            TextView timeTextView = (TextView)rowView.findViewById(R.id.textview_time);
            timeTextView.setText(reviewList.get(i).date.split("T")[0]);

            TextView nameTextView = (TextView)rowView.findViewById(R.id.textview_name);
            nameTextView.setText(reviewList.get(i).name);

            SimpleDraweeView reviewImage1 = (SimpleDraweeView)rowView.findViewById(R.id.imageview_review_image1);
            SimpleDraweeView reviewImage2 = (SimpleDraweeView)rowView.findViewById(R.id.imageview_review_image2);
/*

            if(reviewList.get(i).images.size() > 1){
                String url1 = Constants.appServerHost + "/ul/" + reviewList.get(i).images.get(0);
                String url2 = Constants.appServerHost + "/ul/" + reviewList.get(i).images.get(1);

                reviewImage1.setImageURI(url1);
                reviewImage2.setImageURI(url2);
            }else if( reviewList.get(i).images.size() == 1){
                String url1 = Constants.appServerHost + "/ul/" + reviewList.get(i).images.get(0);
                reviewImage2.setImageURI(url1);

            }
*/

            if ( reviewList.get(i).images.size() > 0){
                String url1 = Constants.appServerHost + "/ul/ps/" + reviewList.get(i).images.get(0);
                reviewImage2.setImageURI(url1);
            }


            return rowView;
        }

    }

    private class HorizontalAdapter extends RecyclerView.Adapter<HorizontalAdapter.Holder>{

        @Override
        public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.topic_column, parent, false);

            return new Holder(view);
        }

        @Override
        public void onBindViewHolder(Holder holder, final int position) {
            holder.imageviewMarquee.setImageURI(marqueeList.get(position).imageName );
            holder.imageviewMarquee.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    System.out.println("Click index " + String.valueOf(position));
                }
            });
        }

        @Override
        public int getItemCount() {
            int size = marqueeList.size();
            return size;
        }

        public class Holder extends RecyclerView.ViewHolder{
            public SimpleDraweeView imageviewMarquee;

            public Holder(View view){
                super(view);
                   imageviewMarquee = (SimpleDraweeView)view.findViewById(R.id.imageview_marquee);

            }

        }
    }
}
