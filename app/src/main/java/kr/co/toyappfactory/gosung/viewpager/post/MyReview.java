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

import kr.co.toyappfactory.gosung.R;
import kr.co.toyappfactory.gosung.ReviewSingleView;
import kr.co.toyappfactory.gosung.response.GetReviewResponse;
import kr.co.toyappfactory.gosung.rest.RestApi;
import kr.co.toyappfactory.gosung.util.Constants;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class MyReview extends Fragment {
    private static int REFRESH_REVIEW = 1;
    private ArrayList<GetReviewResponse> reviewList;
    private ListView reviewListView;
    private ReviewAdapter reviewAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_review, container, false);

        reviewList = new ArrayList<>();
        reviewListView = (ListView)view.findViewById(R.id.listview_review);
        reviewAdapter = new ReviewAdapter();

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
                intent.putExtra("createMenu", true);
                startActivityForResult(intent, REFRESH_REVIEW);
            }
        });

        getMyReview();

        return view;

    }

    @Override
    public void onActivityResult(int reqCode, int resCode, Intent data) {
        super.onActivityResult(reqCode, resCode, data);

        if (resCode == Activity.RESULT_OK) {
            if(reqCode == REFRESH_REVIEW){
                getMyReview();
            }
        }
    }

    private void getMyReview(){
        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").create();
        Retrofit retrofit = new Retrofit.Builder().baseUrl(Constants.appServerHost).addConverterFactory(GsonConverterFactory.create(gson)).build();
        RestApi apiService = retrofit.create(RestApi.class);

        HashMap<String, String> params = new HashMap<>();
        params.put("userId", Constants.getInstance().getUserId());

        Call<ArrayList<GetReviewResponse>> call = apiService.getMyReview(params);
        call.enqueue(new Callback<ArrayList<GetReviewResponse>>() {
            @Override
            public void onResponse(Call<ArrayList<GetReviewResponse>> call, Response<ArrayList<GetReviewResponse>> response) {
                reviewList = response.body();
                reviewAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Call<ArrayList<GetReviewResponse>> call, Throwable t) {

            }
        });
    }

    private class ReviewAdapter extends BaseAdapter{
        private LayoutInflater inflater;

        public ReviewAdapter(){
            inflater = (LayoutInflater)getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            return reviewList.size();
            //return 0;
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

            iconImageView.setImageURI(Constants.appServerHost + "/ul/pf/" + reviewList.get(i).uniqueId + ".jpg");
            header1.setText(reviewList.get(i).title);
            header2.setText(reviewList.get(i).review);
            header3.setText(reviewList.get(i).date.split("T")[0]);

            SimpleDraweeView post1ImageView = (SimpleDraweeView)rowView.findViewById(R.id.imageview_post_1);
            SimpleDraweeView post2ImageView = (SimpleDraweeView)rowView.findViewById(R.id.imageview_post_2);

            if(reviewList.get(i).images.size() > 1){
                post1ImageView.setImageURI(Constants.appServerHost +"/ul/ps/" + reviewList.get(i).images.get(0));
                post2ImageView.setImageURI(Constants.appServerHost +"/ul/ps/" + reviewList.get(i).images.get(1));
            } else if( reviewList.get(i).images.size() == 1){
                post1ImageView.setImageURI(Constants.appServerHost +"/ul/ps/" + reviewList.get(i).images.get(0));
            }

            return rowView;
        }
    }
}

