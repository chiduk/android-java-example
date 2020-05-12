package kr.co.toyappfactory.gosung.menu;

import android.content.Context;
import android.app.Fragment;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
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
import android.view.MotionEvent;
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

import java.util.ArrayList;
import java.util.HashMap;

import kr.co.toyappfactory.gosung.Comments;
import kr.co.toyappfactory.gosung.HashTag;
import kr.co.toyappfactory.gosung.Post;
import kr.co.toyappfactory.gosung.R;
import kr.co.toyappfactory.gosung.response.NewsFeedResponse;
import kr.co.toyappfactory.gosung.rest.RestApi;
import kr.co.toyappfactory.gosung.util.Constants;
import kr.co.toyappfactory.gosung.util.JoinUserInfo;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class NewsFeed extends AppCompatActivity implements AbsListView.OnScrollListener {

    private ArrayList<NewsFeedResponse> newsFeedList;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ListView newsFeedListView;
    private NewsFeedAdapter newsFeedAdapter;
    private int currFirstVisibleItem;
    private int currVisibleItemCount;
    private int currTotalItemCount;
    private int currNewsFeedListPosition;
    private boolean loadData;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_newsfeed);


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);


        newsFeedList = new ArrayList<>();

        newsFeedAdapter = new NewsFeedAdapter();

        newsFeedListView = (ListView) findViewById(R.id.listview_newsfeed);
        newsFeedListView.setAdapter(newsFeedAdapter);
        newsFeedListView.setOnScrollListener(this);

        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.layout_swipe);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(true);
                getNewsFeedGreaterThan(newsFeedList.get(0)._id);
            }
        });

        loadData = true;
        getNewsFeed();

    }


    private void getNewsFeed() {
        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").create();
        Retrofit retrofit = new Retrofit.Builder().baseUrl(Constants.appServerHost).addConverterFactory(GsonConverterFactory.create(gson)).build();
        RestApi apiService = retrofit.create(RestApi.class);

        HashMap<String, String> params = new HashMap<>();
        params.put("userId", Constants.getInstance().getUserId());

        Call<ArrayList<NewsFeedResponse>> call = apiService.getNewsFeed(params);
        call.enqueue(new Callback<ArrayList<NewsFeedResponse>>() {
            @Override
            public void onResponse(Call<ArrayList<NewsFeedResponse>> call, Response<ArrayList<NewsFeedResponse>> response) {
                swipeRefreshLayout.setRefreshing(false);
                if (response.body() != null) {
                    newsFeedList = response.body();
                    newsFeedAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<ArrayList<NewsFeedResponse>> call, Throwable t) {
                swipeRefreshLayout.setRefreshing(false);

            }
        });
    }

    private void getNewsFeedLessThan(String feedId) {
        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").create();
        Retrofit retrofit = new Retrofit.Builder().baseUrl(Constants.appServerHost).addConverterFactory(GsonConverterFactory.create(gson)).build();
        RestApi apiService = retrofit.create(RestApi.class);

        HashMap<String, String> params = new HashMap<>();
        params.put("userId", Constants.getInstance().getUserId());
        params.put("feedId", feedId);

        Call<ArrayList<NewsFeedResponse>> call = apiService.getNewsFeedLessThan(params);
        call.enqueue(new Callback<ArrayList<NewsFeedResponse>>() {
            @Override
            public void onResponse(Call<ArrayList<NewsFeedResponse>> call, Response<ArrayList<NewsFeedResponse>> response) {
                swipeRefreshLayout.setRefreshing(false);

                ArrayList<NewsFeedResponse> list = response.body();
                for (NewsFeedResponse elem : list) {
                    newsFeedList.add(elem);
                }

                newsFeedAdapter.notifyDataSetChanged();
                loadData = true;
                System.out.println("load data succ " + loadData);
            }

            @Override
            public void onFailure(Call<ArrayList<NewsFeedResponse>> call, Throwable t) {
                swipeRefreshLayout.setRefreshing(false);

                System.out.println("Err " + t.getMessage());
                loadData = true;

            }
        });

    }


    private void getNewsFeedGreaterThan(String feedId) {
        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").create();
        Retrofit retrofit = new Retrofit.Builder().baseUrl(Constants.appServerHost).addConverterFactory(GsonConverterFactory.create(gson)).build();
        RestApi apiService = retrofit.create(RestApi.class);

        HashMap<String, String> params = new HashMap<>();
        params.put("userId", Constants.getInstance().getUserId());
        params.put("feedId", feedId);

        Call<ArrayList<NewsFeedResponse>> call = apiService.getNewsFeedGreaterThan(params);
        call.enqueue(new Callback<ArrayList<NewsFeedResponse>>() {
            @Override
            public void onResponse(Call<ArrayList<NewsFeedResponse>> call, Response<ArrayList<NewsFeedResponse>> response) {
                swipeRefreshLayout.setRefreshing(false);
                ArrayList<NewsFeedResponse> list = response.body();
                ArrayList<NewsFeedResponse> newList = new ArrayList<NewsFeedResponse>();

                for (NewsFeedResponse elem : list) {
                    newList.add(elem);
                }

                newList.addAll(newsFeedList);

                newsFeedList = newList;

                newsFeedAdapter.notifyDataSetChanged();
                loadData = true;
                System.out.println("load data succ " + loadData);
            }

            @Override
            public void onFailure(Call<ArrayList<NewsFeedResponse>> call, Throwable t) {
                swipeRefreshLayout.setRefreshing(false);

                System.out.println("Err " + t.getMessage());
                loadData = true;

            }
        });

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                getNewsFeed();
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
                Intent intent = new Intent(NewsFeed.this, Post.class);
                startActivityForResult(intent, 1);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }

    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {


        currFirstVisibleItem = firstVisibleItem;
        currVisibleItemCount = visibleItemCount;
        currTotalItemCount = totalItemCount;


    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        isScrollCompleted(scrollState);
    }

    private void isScrollCompleted(int currentScrollState) {
        int count = currTotalItemCount - currVisibleItemCount;


        if (currFirstVisibleItem >= count && currTotalItemCount != 0 && newsFeedList.size() - 1 == currNewsFeedListPosition && loadData) {

            if (currentScrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {
                System.out.println("Loading next items");
                loadData = false;
                getNewsFeedLessThan(newsFeedList.get(currNewsFeedListPosition)._id);
            }

        }

    }

    private class NewsFeedAdapter extends BaseAdapter {
        private LayoutInflater inflater;

        public NewsFeedAdapter() {
            inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            return newsFeedList.size();
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
            View rowView = inflater.inflate(R.layout.newsfeed_list_row, null);

            SimpleDraweeView profile = (SimpleDraweeView) rowView.findViewById(R.id.imageview_profile);
            profile.setImageURI(Constants.appServerHost + "/ul/pf/" + newsFeedList.get(i).uniqueId + ".jpg");

            TextView nickNameTextView = (TextView) rowView.findViewById(R.id.textview_nickname);
            nickNameTextView.setText(newsFeedList.get(i).name);
            nickNameTextView.setTypeface(null, Typeface.BOLD);
            TextView dateTextView = (TextView) rowView.findViewById(R.id.textview_date);
            dateTextView.setText(newsFeedList.get(i).date.split("T")[0]);


            ArrayList<int[]> hashTagSpans = HashTag.getSpans(newsFeedList.get(i).post, '#');
            final SpannableString newsFeedContent = new SpannableString(newsFeedList.get(i).post);

            for (int[] span : hashTagSpans) {
                int hashTagStart = span[0];
                int hashTagEnd = span[1];
                newsFeedContent.setSpan(new HashTag(NewsFeed.this), hashTagStart, hashTagEnd, 0);
            }

            final TextView smallPostTextView = (TextView) rowView.findViewById(R.id.textview_post_small);
            smallPostTextView.setMovementMethod(LinkMovementMethod.getInstance());
            smallPostTextView.setText(newsFeedContent);


            final TextView largePostTextView = (TextView)rowView.findViewById(R.id.textview_post_large);
            final TextView viewMorePostTextView = (TextView)rowView.findViewById(R.id.textview_view_more);

            smallPostTextView.post(new Runnable() {
                @Override
                public void run() {
                    if ( smallPostTextView.getLineCount() > Constants.FEED_MAX_LINE_COUNT) {

                        viewMorePostTextView.setVisibility(View.VISIBLE);

                        SpannableString ss = new SpannableString("더보기");
                        ClickableSpan clickableSpan = new ClickableSpan() {
                            @Override
                            public void onClick(View view) {
                                largePostTextView.setText(newsFeedContent);
                                smallPostTextView.setVisibility(View.GONE);
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


            currNewsFeedListPosition = i;


            final TextView likeCountTextView = (TextView) rowView.findViewById(R.id.textview_like);
            likeCountTextView.setTypeface(null, Typeface.BOLD);
            likeCountTextView.setText("좋아요 " + newsFeedList.get(i).like + "개");


            LinearLayout parentLinLayout = (LinearLayout) rowView.findViewById(R.id.layout_comment);


            LinearLayout commentLinLayout = new LinearLayout(NewsFeed.this);
            LinearLayout viewAllCommentsTextViewLayout = new LinearLayout(NewsFeed.this);
            parentLinLayout.addView(viewAllCommentsTextViewLayout);

            parentLinLayout.addView(commentLinLayout);
            commentLinLayout.setOrientation(LinearLayout.HORIZONTAL);
            LinearLayout.LayoutParams nameLayoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, 3f);
            LinearLayout.LayoutParams commentLayoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, 1f);

            TextView nameTextView = new TextView(NewsFeed.this);
            TextView commentTextView = new TextView(NewsFeed.this);
            nameTextView.setTypeface(null, Typeface.BOLD);

            if (newsFeedList.get(i).comments != null) {
                if (newsFeedList.get(i).comments.size() > 1) {

                    LinearLayout.LayoutParams viewMoreLayoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

                    TextView viewMoreTextView = new TextView(NewsFeed.this);
                    viewMoreTextView.setTextSize(12f);
                    viewMoreTextView.setLayoutParams(viewMoreLayoutParams);

                    viewAllCommentsTextViewLayout.addView(viewMoreTextView);

                    viewMoreTextView.setText("댓글 더 보기...");
                    viewMoreTextView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            System.out.println("댓글 모두 보기 클릭");
                            Intent intent = new Intent(NewsFeed.this, Comments.class);
                            intent.putExtra("feedId", newsFeedList.get(i)._id);
                            startActivityForResult(intent, 1);
                        }
                    });

                    nameTextView.setText(newsFeedList.get(i).comments.get(newsFeedList.get(i).comments.size() - 1).name);
                    commentTextView.setText(newsFeedList.get(i).comments.get(newsFeedList.get(i).comments.size() - 1).comment);
                } else if (newsFeedList.get(i).comments.size() == 1) {

                    nameTextView.setText(newsFeedList.get(i).comments.get(0).name);
                    commentTextView.setText(newsFeedList.get(i).comments.get(0).comment);
                }
            }

            nameTextView.setLayoutParams(nameLayoutParams);
            commentTextView.setLayoutParams(commentLayoutParams);

            commentLinLayout.addView(nameTextView);
            commentLinLayout.addView(commentTextView);

            for (String imageName : newsFeedList.get(i).images) {
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 800);
                LinearLayout imageLayout = (LinearLayout) rowView.findViewById(R.id.layout_images);
                SimpleDraweeView simpleDraweeView = new SimpleDraweeView(NewsFeed.this);
                simpleDraweeView.setLayoutParams(layoutParams);
                simpleDraweeView.setImageURI(Constants.appServerHost + "/ul/ps/" + imageName);
                simpleDraweeView.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View view, MotionEvent motionEvent) {
                        float x = motionEvent.getX();
                        float y = motionEvent.getY();

                        System.out.println("X: " + x);
                        System.out.println("Y: " + y);

                        return false;
                    }
                });
                imageLayout.addView(simpleDraweeView);
            }

            final ImageButton likeButton = (ImageButton) rowView.findViewById(R.id.button_like);
            if (newsFeedList.get(i).liked) {
                likeButton.setImageResource(R.drawable.like_btn_on);
            } else {
                likeButton.setImageResource(R.drawable.like_btn);

            }

            likeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").create();
                    Retrofit retrofit = new Retrofit.Builder().baseUrl(Constants.appServerHost).addConverterFactory(GsonConverterFactory.create(gson)).build();
                    RestApi apiService = retrofit.create(RestApi.class);

                    if (!newsFeedList.get(i).liked) {
                        newsFeedList.get(i).liked = true;
                        HashMap<String, String> params = new HashMap<String, String>();
                        params.put("feedId", newsFeedList.get(i)._id);
                        params.put("userId", Constants.getInstance().getUserId());
                        params.put("uniqueId", JoinUserInfo.getInstance().getUniqueId());
                        params.put("name", JoinUserInfo.getInstance().getName());

                        Call<ResponseBody> call = apiService.likePost(params);
                        call.enqueue(new Callback<ResponseBody>() {
                            @Override
                            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                likeButton.setImageResource(R.drawable.like_btn_on);

                                int likeCount = newsFeedList.get(i).like + 1;
                                newsFeedList.get(i).like = likeCount;
                                likeCountTextView.setText("좋아요 " + likeCount + "개");
                            }

                            @Override
                            public void onFailure(Call<ResponseBody> call, Throwable t) {

                            }
                        });

                    } else {
                        newsFeedList.get(i).liked = false;

                        Call<ResponseBody> call = apiService.unlikePost(newsFeedList.get(i)._id, Constants.getInstance().getUserId());
                        call.enqueue(new Callback<ResponseBody>() {
                            @Override
                            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                likeButton.setImageResource(R.drawable.like_btn);

                                int likeCount = newsFeedList.get(i).like - 1;
                                newsFeedList.get(i).like = likeCount;
                                likeCountTextView.setText("좋아요 " + likeCount + "개");
                            }

                            @Override
                            public void onFailure(Call<ResponseBody> call, Throwable t) {

                            }
                        });
                    }
                }
            });


            ImageButton commentButton = (ImageButton) rowView.findViewById(R.id.button_comment);
            commentButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(NewsFeed.this, Comments.class);
                    intent.putExtra("feedId", newsFeedList.get(i)._id);
                    startActivityForResult(intent, 1);
                }
            });

            return rowView;
        }
    }
}
