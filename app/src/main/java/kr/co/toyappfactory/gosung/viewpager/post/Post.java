package kr.co.toyappfactory.gosung.viewpager.post;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.HashMap;

import kr.co.toyappfactory.gosung.PostSingleView;
import kr.co.toyappfactory.gosung.R;
import kr.co.toyappfactory.gosung.response.RefreshPostResponse;
import kr.co.toyappfactory.gosung.rest.RestApi;
import kr.co.toyappfactory.gosung.util.Constants;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Post extends Fragment {
    private static int REFRESH_NEWS_FEED = 1;
    private ArrayList<RefreshPostResponse> postList;
    private ListView postListView;
    private PostAdapter postAdapter;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_post, container, false);

        postList = new ArrayList<>();
        postListView = (ListView)view.findViewById(R.id.listview_post);
        postAdapter = new PostAdapter();
        postListView.setAdapter(postAdapter);

        postListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(getActivity(), PostSingleView.class);
                intent.putExtra("feedId", postList.get(i)._id);
                intent.putExtra("date", postList.get(i).date);
                intent.putExtra("userId", postList.get(i).userId);
                intent.putExtra("post", postList.get(i).post);
                intent.putStringArrayListExtra("images", postList.get(i).images);
                intent.putExtra("likeCount", postList.get(i).like);
                intent.putExtra("createMenu", true);

                startActivityForResult(intent, REFRESH_NEWS_FEED);
            }
        });

        getPost();
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

    private void getPost(){
        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").create();
        Retrofit retrofit = new Retrofit.Builder().baseUrl(Constants.appServerHost).addConverterFactory(GsonConverterFactory.create(gson)).build();
        RestApi apiService = retrofit.create(RestApi.class);

        HashMap<String, String> params = new HashMap<>();
        params.put("userId", Constants.getInstance().getUserId());

        Call<ArrayList<RefreshPostResponse>> call = apiService.getMyPost(params);
        call.enqueue(new Callback<ArrayList<RefreshPostResponse>>() {
            @Override
            public void onResponse(Call<ArrayList<RefreshPostResponse>> call, Response<ArrayList<RefreshPostResponse>> response) {
                postList = response.body();
                postAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Call<ArrayList<RefreshPostResponse>> call, Throwable t) {

            }
        });
    }

    private class PostAdapter extends BaseAdapter{
        private LayoutInflater inflater;

        public PostAdapter(){
            inflater = (LayoutInflater)getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            return postList.size();
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
            View rowView = inflater.inflate(R.layout.post_row, null);

            SimpleDraweeView iconImageView = (SimpleDraweeView)rowView.findViewById(R.id.imageview_icon);
            TextView header1 = (TextView)rowView.findViewById(R.id.textview_header1);
            TextView header2 = (TextView)rowView.findViewById(R.id.textview_header2);
            TextView header3 = (TextView)rowView.findViewById(R.id.textview_header3);

            iconImageView.setImageURI(Constants.appServerHost + "/ul/pf/" + postList.get(i).uniqueId + ".jpg");
            header1.setText(postList.get(i).name);
            header2.setText(postList.get(i).post);
            header3.setText(postList.get(i).date);

            SimpleDraweeView post1ImageView = (SimpleDraweeView)rowView.findViewById(R.id.imageview_post_1);
            SimpleDraweeView post2ImageView = (SimpleDraweeView)rowView.findViewById(R.id.imageview_post_2);

            if(postList.get(i).images.size() > 1){
                post1ImageView.setImageURI(Constants.appServerHost +"/ul/ps/" + postList.get(i).images.get(0));
                post2ImageView.setImageURI(Constants.appServerHost +"/ul/ps/" + postList.get(i).images.get(1));
            } else if( postList.get(i).images.size() == 1){
                post1ImageView.setImageURI(Constants.appServerHost +"/ul/ps/" + postList.get(i).images.get(0));
            }
            return rowView;
        }
    }
}
