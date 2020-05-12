package kr.co.toyappfactory.gosung.viewpager.search;

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
import kr.co.toyappfactory.gosung.response.HashTagSearchResponse;
import kr.co.toyappfactory.gosung.response.NewsFeedResponse;
import kr.co.toyappfactory.gosung.response.RefreshPostResponse;
import kr.co.toyappfactory.gosung.rest.RestApi;
import kr.co.toyappfactory.gosung.util.Constants;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class NewsFeedSearchResult extends Fragment {
    private ArrayList<RefreshPostResponse> newsFeedList;
    private ListView newsFeedListView;
    private NewsFeedAdapter newsFeedAdapter;
    private String hashTag;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_news_feed_search_result, container, false);

        hashTag = getArguments().getString("hashTag");

        newsFeedList = new ArrayList<>();
        newsFeedListView = (ListView)view.findViewById(R.id.listview_post);
        newsFeedAdapter = new NewsFeedAdapter();
        newsFeedListView.setAdapter(newsFeedAdapter);

        newsFeedListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(getActivity(), PostSingleView.class);
                intent.putExtra("feedId", newsFeedList.get(i)._id);
                intent.putExtra("date", newsFeedList.get(i).date);
                intent.putExtra("userId", newsFeedList.get(i).userId);
                intent.putExtra("post", newsFeedList.get(i).post);
                intent.putStringArrayListExtra("images", newsFeedList.get(i).images);
                intent.putExtra("likeCount", newsFeedList.get(i).like);
                intent.putExtra("createMenu", false);

                startActivity(intent);
            }
        });

        searchNewsFeedHashTag();

        return view;

    }

    private void searchNewsFeedHashTag(){
        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").create();
        Retrofit retrofit = new Retrofit.Builder().baseUrl(Constants.appServerHost).addConverterFactory(GsonConverterFactory.create(gson)).build();

        RestApi apiService = retrofit.create(RestApi.class);

        HashMap<String, String> params = new HashMap<>();
        params.put("hashTag", hashTag);

        Call<ArrayList<RefreshPostResponse>> call = apiService.searchNewsFeedHashTag(params);
        call.enqueue(new Callback<ArrayList<RefreshPostResponse>>() {
            @Override
            public void onResponse(Call<ArrayList<RefreshPostResponse>> call, Response<ArrayList<RefreshPostResponse>> response) {

                if(response.body() != null){
                    newsFeedList = response.body();
                    newsFeedAdapter.notifyDataSetChanged();

                }

            }

            @Override
            public void onFailure(Call<ArrayList<RefreshPostResponse>> call, Throwable t) {

            }
        });
    }

    private class NewsFeedAdapter extends BaseAdapter {
        private LayoutInflater inflater;

        public NewsFeedAdapter(){
            inflater = (LayoutInflater)getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
        public View getView(int i, View view, ViewGroup viewGroup) {
            View rowView = inflater.inflate(R.layout.post_row, null);

            SimpleDraweeView iconImageView = (SimpleDraweeView)rowView.findViewById(R.id.imageview_icon);
            TextView header1 = (TextView)rowView.findViewById(R.id.textview_header1);
            TextView header2 = (TextView)rowView.findViewById(R.id.textview_header2);
            TextView header3 = (TextView)rowView.findViewById(R.id.textview_header3);

            iconImageView.setImageURI(Constants.appServerHost + "/ul/pf/" + newsFeedList.get(i).uniqueId + ".jpg");
            header1.setText(newsFeedList.get(i).name);
            header2.setText(newsFeedList.get(i).post);
            header3.setText(newsFeedList.get(i).date.split("T")[0]);

            SimpleDraweeView post1ImageView = (SimpleDraweeView)rowView.findViewById(R.id.imageview_post_1);
            SimpleDraweeView post2ImageView = (SimpleDraweeView)rowView.findViewById(R.id.imageview_post_2);

            if(newsFeedList.get(i).images.size() > 1){
                post1ImageView.setImageURI(Constants.appServerHost +"/ul/ps/" + newsFeedList.get(i).images.get(0));
                post2ImageView.setImageURI(Constants.appServerHost +"/ul/ps/" + newsFeedList.get(i).images.get(1));
            } else if( newsFeedList.get(i).images.size() == 1){
                post1ImageView.setImageURI(Constants.appServerHost +"/ul/ps/" + newsFeedList.get(i).images.get(0));
            }
            return rowView;
        }
    }

}
