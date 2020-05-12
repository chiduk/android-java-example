package kr.co.toyappfactory.gosung;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telecom.CallScreeningService;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.HashMap;

import kr.co.toyappfactory.gosung.response.CommentResponse;
import kr.co.toyappfactory.gosung.rest.RestApi;
import kr.co.toyappfactory.gosung.util.Constants;
import kr.co.toyappfactory.gosung.util.JoinUserInfo;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Comments extends AppCompatActivity {

    private ArrayList<CommentResponse.Comment> commentList;
    private int totalNumOfComments;
    private ListView commentListView;
    private CommentAdapter commentAdapter;
    private EditText commentEditText;
    private String feedId;
    private Button viewMoreButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);


        commentListView = (ListView)findViewById(R.id.listview_comments);
        commentList = new ArrayList<>();

        commentAdapter = new CommentAdapter();
        commentListView.setAdapter(commentAdapter);

        commentEditText = (EditText)findViewById(R.id.edittext_comment);

        feedId = getIntent().getStringExtra("feedId");

        addReplyButton();
        addViewMoreButton();
        getAllComments();
    }

    private void addViewMoreButton(){
        viewMoreButton = (Button)findViewById(R.id.button_view_more);
        viewMoreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String commentId = commentList.get(0)._id;
                getComment(commentId);
            }
        });
    }


    private void addReplyButton(){
        Button replyButton = (Button)findViewById(R.id.button_add_comment);
        if(replyButton != null){
            replyButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    addComment();
                }
            });
        }
    }




    private void addComment(){
        Gson gson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ")
                .create();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.appServerHost)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        RestApi apiService = retrofit.create(RestApi.class);

        HashMap<String,String> params = new HashMap<>();
        params.put("feedId", feedId);
        params.put("userId", Constants.getInstance().getUserId());
        params.put("name", JoinUserInfo.getInstance().getName());
        params.put("comment", commentEditText.getText().toString());
        params.put("uniqueId", JoinUserInfo.getInstance().getUniqueId());

        Call<ResponseBody> call = apiService.addComment(params);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                System.out.println(response.code());

                if(response.code() == 200){
                    Intent intent = new Intent();
                    intent.putExtra("name", JoinUserInfo.getInstance().getName());
                    intent.putExtra("comment", commentEditText.getText().toString());
                    setResult(RESULT_OK, intent);
                    finish();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });
    }
    private void getAllComments(){
        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").create();
        Retrofit retrofit = new Retrofit.Builder().baseUrl(Constants.appServerHost).addConverterFactory(GsonConverterFactory.create(gson)).build();
        RestApi apiService = retrofit.create(RestApi.class);

        HashMap<String, String> params = new HashMap<>();
        params.put("feedId", feedId);

        Call<CommentResponse> call = apiService.getAllComment(params);
        call.enqueue(new Callback<CommentResponse>() {
            @Override
            public void onResponse(Call<CommentResponse> call, Response<CommentResponse> response) {
                commentList = response.body().commentList;

                totalNumOfComments = response.body().size;

                if(totalNumOfComments > commentList.size()){
                    viewMoreButton.setVisibility(View.VISIBLE);
                }else{
                    viewMoreButton.setVisibility(View.GONE);
                }

                commentAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Call<CommentResponse> call, Throwable t) {

            }
        });
    }

    private void getComment(String commentId){
        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").create();
        Retrofit retrofit = new Retrofit.Builder().baseUrl(Constants.appServerHost).addConverterFactory(GsonConverterFactory.create(gson)).build();
        RestApi apiService = retrofit.create(RestApi.class);

        HashMap<String, String> params = new HashMap<>();
        params.put("feedId", feedId);
        params.put("commentId", commentId);

        Call<CommentResponse> call = apiService.getComment(params);
        call.enqueue(new Callback<CommentResponse>() {
            @Override
            public void onResponse(Call<CommentResponse> call, Response<CommentResponse> response) {
                int i = 0;
                for(CommentResponse.Comment comment : response.body().commentList){
                    commentList.add(i,comment);
                    i++;
                }

                totalNumOfComments = response.body().size;

                if(totalNumOfComments > commentList.size()){
                    viewMoreButton.setVisibility(View.VISIBLE);
                }else{
                    viewMoreButton.setVisibility(View.GONE);
                }

                commentAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Call<CommentResponse> call, Throwable t) {

            }
        });
    }

    private class CommentAdapter extends BaseAdapter{

        private LayoutInflater inflater;

        public CommentAdapter() {
            inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            return commentList.size();
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
            View rowView = inflater.inflate(R.layout.comment_list_row, null);
            CommentResponse.Comment comment = commentList.get(i);

            SimpleDraweeView imageViewProfile = (SimpleDraweeView)rowView.findViewById(R.id.imageview_profile);
            TextView nameTextView = (TextView)rowView.findViewById(R.id.textview_name);
            TextView commentTextView = (TextView)rowView.findViewById(R.id.textview_comment);

            imageViewProfile.setImageURI(Constants.appServerHost + "/ul/pf/" + comment.uniqueId + ".jpg");
            nameTextView.setText(comment.name);
            commentTextView.setText(comment.comment);
            return rowView;
        }
    }
}
