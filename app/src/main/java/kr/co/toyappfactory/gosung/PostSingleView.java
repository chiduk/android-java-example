package kr.co.toyappfactory.gosung;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
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

import kr.co.toyappfactory.gosung.response.CommentResponse;
import kr.co.toyappfactory.gosung.response.LikedResponse;
import kr.co.toyappfactory.gosung.response.ReportResponse;
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

public class PostSingleView extends AppCompatActivity {

    private String feedId;
    private String date;
    private String uniqueId;
    private String name;
    private String postUserId;
    private String post;
    private ArrayList<String> imageList;
    private int likeCount;
    private ArrayList<CommentResponse.Comment> commentList;
    private LinearLayout commentParentLayout;
    private ImageButton likeButton;
    private TextView likeCountTextView;
    private Boolean createMenu;
    private boolean liked;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_single_view);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);


        Intent intent = getIntent();
        feedId = intent.getStringExtra("feedId");
        date = intent.getStringExtra("date");
        uniqueId = intent.getStringExtra("uniqueId");
        name = intent.getStringExtra("name");
        postUserId = intent.getStringExtra("userId");
        post = intent.getStringExtra("post");
        imageList = intent.getStringArrayListExtra("images");
        likeCount = Integer.valueOf(intent.getStringExtra("likeCount"));
        createMenu = intent.getBooleanExtra("createMenu", false);
        commentList = new ArrayList<>();


        commentParentLayout = (LinearLayout)findViewById(R.id.layout_comment);

        likeCountTextView = (TextView)findViewById(R.id.textview_like);
        likeCountTextView.setTypeface(null, Typeface.BOLD);
        likeCountTextView.setText("좋아요 " + likeCount + "개");

        getRecentComment();
        getLiked();
        setHeader();
        setPost();
        setImages();
        addCommentButton();
        addLikeButton();

    }


    private void setHeader(){
        SimpleDraweeView profileImageView = (SimpleDraweeView)findViewById(R.id.imageview_profile);
        profileImageView.setImageURI(Constants.appServerHost + "/ul/pf/" + uniqueId + ".jpg");


        TextView timeTextView = (TextView)findViewById(R.id.textview_time);
        timeTextView.setText(date.split("T")[0]);

        TextView nameTextView = (TextView)findViewById(R.id.textview_name);
        nameTextView.setText(name);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        if(createMenu){
            getMenuInflater().inflate(R.menu.action, menu);
        }
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu){

        if(createMenu){
            if(postUserId.equals(Constants.getInstance().getUserId())){
                menu.getItem(0).setEnabled(true);
                menu.getItem(1).setEnabled(true);
            }else{
                menu.getItem(0).setEnabled(false);
                menu.getItem(1).setEnabled(false);
            }

        }

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();


        switch(id) {
            case R.id.action_delete:
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
                alertDialog.setMessage(R.string.message_delete_feed)
                        .setPositiveButton(R.string.ok,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                deletePost();
                            }
                        })
                        .setNegativeButton(R.string.cancel,
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {

                                    }
                                });
                AlertDialog alert = alertDialog.create();
                alert.show();


                return true;

            case R.id.action_modify:
                Intent intent = new Intent(PostSingleView.this, ModifyPost.class);
                intent.putExtra("feedId", feedId);
                intent.putExtra("post", post);
                intent.putStringArrayListExtra("images", imageList);

                startActivityForResult(intent, 2);
                return true;

            case R.id.action_report:
                reportPost();
                return true;

            case android.R.id.home:

                setResult(RESULT_OK);
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1){
            if(resultCode == RESULT_OK){
                String name = data.getStringExtra("name");
                String comment = data.getStringExtra("comment");

                addCommentLayout(name, comment);
                //getRecentComment();

            }
        }else if(requestCode == 2){
            if(resultCode == RESULT_OK){
                Bundle bundle = data.getExtras();
                post = bundle.getString("post");
                setPost();

            }
        }
    }

    private void setPost(){
        ArrayList<int[]> hashTagSpans = HashTag.getSpans(post, '#');
        final SpannableString postContent = new SpannableString(post);

        for( int[] span : hashTagSpans){
            int hashTagStart = span[0];
            int hashTagEnd = span[1];
            postContent.setSpan(new HashTag(this), hashTagStart, hashTagEnd, 0);
        }

        final TextView smallPostTextView = (TextView)findViewById(R.id.textview_post_small);
        final TextView largePostTextView = (TextView)findViewById(R.id.textview_post_large);

        smallPostTextView.setMovementMethod(LinkMovementMethod.getInstance());
        smallPostTextView.setText(postContent);

        final TextView viewMorePostTextView = (TextView)findViewById(R.id.textview_view_more);

        smallPostTextView.post(new Runnable() {
            @Override
            public void run() {
                int lineCount = smallPostTextView.getLineCount();

                System.out.println("Line Count: " + lineCount);

                if(lineCount > Constants.FEED_MAX_LINE_COUNT){
                    viewMorePostTextView.setVisibility(View.VISIBLE);

                    SpannableString ss = new SpannableString("더보기");
                    ClickableSpan clickableSpan = new ClickableSpan() {
                        @Override
                        public void onClick(View view) {
                            largePostTextView.setText(postContent);
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

    }

    private void getLiked(){
        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").create();
        Retrofit retrofit = new Retrofit.Builder().baseUrl(Constants.appServerHost).addConverterFactory(GsonConverterFactory.create(gson)).build();
        RestApi apiService = retrofit.create(RestApi.class);

        HashMap<String, String> params = new HashMap<>();
        params.put("feedId", feedId);
        params.put("userId", Constants.getInstance().getUserId());

        Call<LikedResponse> call = apiService.getLikedNewsFeed(params);
        call.enqueue(new Callback<LikedResponse>() {
            @Override
            public void onResponse(Call<LikedResponse> call, Response<LikedResponse> response) {
                liked = response.body().liked;

                if(liked){
                    likeButton.setImageResource(R.drawable.like_btn_on);
                }else{
                    likeButton.setImageResource(R.drawable.like_btn);
                }
            }

            @Override
            public void onFailure(Call<LikedResponse> call, Throwable t) {

            }
        });
    }

    private void getRecentComment(){
        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").create();
        Retrofit retrofit = new Retrofit.Builder().baseUrl(Constants.appServerHost).addConverterFactory(GsonConverterFactory.create(gson)).build();
        RestApi apiService = retrofit.create(RestApi.class);

        HashMap<String, String> params = new HashMap<>();
        params.put("feedId", feedId);

        Call<CommentResponse> call = apiService.getRecentComment(params);
        call.enqueue(new Callback<CommentResponse>() {
            @Override
            public void onResponse(Call<CommentResponse> call, Response<CommentResponse> response) {
                int size = response.body().size;
                commentList = response.body().commentList;

                if(size > 0){



                    if(size > 1){
                        //view more comment
                        LinearLayout viewAllCommentsTextViewLayout = new LinearLayout(PostSingleView.this);
                        commentParentLayout.addView(viewAllCommentsTextViewLayout);

                        LinearLayout.LayoutParams viewMoreLayoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

                        TextView viewMoreTextView = new TextView(PostSingleView.this);
                        viewMoreTextView.setTextSize(10f);

                        viewMoreTextView.setLayoutParams(viewMoreLayoutParams);

                        viewAllCommentsTextViewLayout.addView(viewMoreTextView);

                        viewMoreTextView.setText("댓글 더 보기...");
                        viewMoreTextView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                System.out.println("댓글 모두 보기 클릭");
                                Intent intent = new Intent(PostSingleView.this, Comments.class);
                                intent.putExtra("feedId", feedId);
                                startActivityForResult(intent, 1);
                            }
                        });
                    }

                    String name = commentList.get(0).name;
                    String comment = commentList.get(0).comment;

                    addCommentLayout(name, comment);
                }

            }

            @Override
            public void onFailure(Call<CommentResponse> call, Throwable t) {

            }
        });

    }

    private void addCommentLayout(String name, String comment){
        LinearLayout commentLayout = new LinearLayout(PostSingleView.this);
        commentParentLayout.addView(commentLayout);
        commentLayout.setOrientation(LinearLayout.HORIZONTAL);

        LinearLayout.LayoutParams nameLayoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, 3f);
        LinearLayout.LayoutParams commentLayoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, 1f);

        TextView nameTextView = new TextView(PostSingleView.this);
        TextView commentTextView = new TextView(PostSingleView.this);

        nameTextView.setTextColor(Color.parseColor("#000000"));
        commentTextView.setTextColor(Color.parseColor("#000000"));
        nameTextView.setTextSize(12f);
        commentTextView.setTextSize(12f);

        nameTextView.setTypeface(null, Typeface.BOLD);
        nameTextView.setLayoutParams(nameLayoutParams);
        commentTextView.setLayoutParams(commentLayoutParams);

        commentLayout.addView(nameTextView);
        commentLayout.addView(commentTextView);

        nameTextView.setText(name);
        commentTextView.setText(comment);
    }

    private void addCommentButton(){
        ImageButton commentButton = (ImageButton)findViewById(R.id.button_comment);
        commentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PostSingleView.this, Comments.class);
                intent.putExtra("feedId", feedId);
                startActivityForResult(intent, 1);
            }
        });
    }

    private void addLikeButton(){
        likeButton = (ImageButton)findViewById(R.id.button_like);

        if(liked){
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

                if(liked){
                    liked = false;

                    Call<ResponseBody> call = apiService.unlikePost(feedId, Constants.getInstance().getUserId());
                    call.enqueue(new Callback<ResponseBody>() {
                        @Override
                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                            likeCount = likeCount - 1;
                            likeCountTextView.setText("좋아요 " + likeCount + "개");
                            likeButton.setImageResource(R.drawable.like_btn);
                        }

                        @Override
                        public void onFailure(Call<ResponseBody> call, Throwable t) {

                        }
                    });
                }else{
                    liked = true;

                    HashMap<String, String> params = new HashMap<String, String>();
                    params.put("feedId", feedId);
                    params.put("userId", Constants.getInstance().getUserId());
                    params.put("uniqueId", JoinUserInfo.getInstance().getUniqueId());
                    params.put("name", JoinUserInfo.getInstance().getName());


                    Call<ResponseBody> call = apiService.likePost(params);
                    call.enqueue(new Callback<ResponseBody>() {
                        @Override
                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                            likeCount = likeCount + 1;
                            likeCountTextView.setText("좋아요 " + likeCount + "개");
                            likeButton.setImageResource(R.drawable.like_btn_on);
                        }

                        @Override
                        public void onFailure(Call<ResponseBody> call, Throwable t) {

                        }
                    });
                }
            }
        });
    }



    @Override
    public void onBackPressed() {

        setResult(Activity.RESULT_OK);
        super.onBackPressed();

    }
    private void deletePost(){
        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").create();
        Retrofit retrofit = new Retrofit.Builder().baseUrl(Constants.appServerHost).addConverterFactory(GsonConverterFactory.create(gson)).build();

        RestApi apiService = retrofit.create(RestApi.class);

        Call<ResponseBody> call = apiService.deletePost(Constants.getInstance().getUserId(), feedId);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Intent intent = new Intent();
                setResult(RESULT_OK, intent);
                finish();
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                DialogBox.show(PostSingleView.this, R.string.message_failed);
            }
        });
    }

    private void reportPost(){
        Gson gson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ")
                .create();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.appServerHost)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        RestApi apiService = retrofit.create(RestApi.class);
        HashMap<String, String> params = new HashMap<>();
        params.put("userId", Constants.getInstance().getUserId());
        params.put("feedId", feedId);

        Call<ReportResponse> call = apiService.reportPost(params);
        call.enqueue(new Callback<ReportResponse>() {
            @Override
            public void onResponse(Call<ReportResponse> call, Response<ReportResponse> response) {
                //finish();
            }

            @Override
            public void onFailure(Call<ReportResponse> call, Throwable t) {

            }
        });
    }

    private void setImages(){
        LinearLayout parentLayout = (LinearLayout)findViewById(R.id.layout_images);
        LinearLayout imageLayout = new LinearLayout(this);
        imageLayout.setOrientation(LinearLayout.VERTICAL);
        parentLayout.addView(imageLayout);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 800);

        for(String image: imageList){
            SimpleDraweeView imageImageView = new SimpleDraweeView(this);
            imageImageView.setLayoutParams(params);
            imageLayout.addView(imageImageView);
            imageImageView.setImageURI(Constants.appServerHost + "/ul/ps/" + image);
        }
    }

    private class ImageAdapter extends BaseAdapter {

        private LayoutInflater inflater;

        public ImageAdapter(){
            inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            return imageList.size();
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
            View view = inflater.inflate(R.layout.post_view_image_row, null);
            SimpleDraweeView imageView = (SimpleDraweeView) view.findViewById(R.id.imageview_image);
            String url = Constants.appServerHost + "/ul/ps/" + imageList.get(position);
            imageView.setImageURI(url);
            return view;
        }
    }
}
