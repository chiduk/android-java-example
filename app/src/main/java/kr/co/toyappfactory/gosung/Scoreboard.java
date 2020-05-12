package kr.co.toyappfactory.gosung;

import android.content.Context;
import android.content.Intent;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;


import com.facebook.drawee.view.SimpleDraweeView;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;

import kr.co.toyappfactory.gosung.response.LikeAndDislikeCount;
import kr.co.toyappfactory.gosung.rest.RestApi;
import kr.co.toyappfactory.gosung.util.Constants;
import kr.co.toyappfactory.gosung.util.Ranker;
import kr.co.toyappfactory.gosung.util.Util;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Scoreboard extends AppCompatActivity {
    private ListView listView;
    private ArrayList<LikeAndDislikeCount> rankingList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scoreboard);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);


        listView = (ListView) findViewById(R.id.listview_brand_score);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                LikeAndDislikeCount likeAndDislikeCount = rankingList.get(position);
                Constants constants = Constants.getInstance();
                //constants.setBrand(new AllBrands());
                String brandId = likeAndDislikeCount.brandId;
                String brandName = likeAndDislikeCount.name;

                Intent intent = new Intent(Scoreboard.this, BrandLikeAndDislikeCount.class);
                intent.putExtra("brandId", brandId);
                intent.putExtra("brandName", brandName);
                intent.putExtra("likeCount", likeAndDislikeCount.likeCount);
                intent.putExtra("dislikeCount", likeAndDislikeCount.dislikeCount);
                Constants.getInstance().setCategory(likeAndDislikeCount.category);
                startActivity(intent);
            }
        });

        int category = Constants.getInstance().getCategory();
        String strCategory = "";
        switch (category) {
            case Constants.ALL:
                strCategory = "전체";
                break;

            case Constants.ENTERTAINER:
                strCategory = "예능인";
                break;

            case Constants.ACTORS:
                strCategory = "배우";
                break;

            case Constants.GIRL_IDOL:
                strCategory = "여자아이돌";
                break;

            case Constants.BOY_IDOL:
                strCategory = "남자아이돌";
                break;

            case Constants.COMMERCIAL_MODEL:
                strCategory = "광고모델";
                break;

            case Constants.SINGER:
                strCategory = "가수";
                break;
        }

        getSupportActionBar().setTitle(strCategory);

        getLikeRanking(category);


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

    private void getLikeRanking(int category) {
        Gson gson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ")
                .create();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.appServerHost)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        RestApi apiService = retrofit.create(RestApi.class);

        HashMap<String, String> params = new HashMap<String, String>();
        params.put("userId", Constants.getInstance().getUserId());
        params.put("category", String.valueOf(category));

        Call<ArrayList<LikeAndDislikeCount>> call = apiService.getLikeRanking(params);
        call.enqueue(new Callback<ArrayList<LikeAndDislikeCount>>() {
            @Override
            public void onResponse(Call<ArrayList<LikeAndDislikeCount>> call, Response<ArrayList<LikeAndDislikeCount>> response) {

                rankingList = response.body();

                ArrayList<LikeAndDislikeCount> finalList = Ranker.getRanking(response);

                listView.setAdapter(new ScoreboardAdapter(Scoreboard.this, finalList));

            }

            @Override
            public void onFailure(Call<ArrayList<LikeAndDislikeCount>> call, Throwable t) {
                System.out.println("Get like ranking fail " + t.getMessage());
            }
        });
    }



    private class ScoreboardAdapter extends BaseAdapter {

        private Context context;
        private LayoutInflater inflater;
        private ArrayList<LikeAndDislikeCount> rankingList;

        public ScoreboardAdapter(Context c, ArrayList<LikeAndDislikeCount> list) {
            this.context = c;
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            rankingList = list;
        }

        @Override
        public int getCount() {
            return rankingList.size();
        }

        @Override
        public Object getItem(int position) {
            return rankingList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View rowView = inflater.inflate(R.layout.scoreboard_listview_row, null);
            Constants constants = Constants.getInstance();


            LikeAndDislikeCount elem = rankingList.get(position);

            String brandImgUrl = Constants.appServerHost + "/big/b/" + elem.brandId + "/thm.jpeg";

            String brandName = elem.name;
            int likeCount = elem.likeCount;

            Holder holder = new Holder();
            holder.textViewRanking = (TextView) rowView.findViewById(R.id.textview_ranking);
            holder.imageRankingIcon = (ImageView) rowView.findViewById(R.id.imageview_ranking_icon);
            holder.imageViewBrandIcon = (SimpleDraweeView) rowView.findViewById(R.id.imageview_brand_icon);
            holder.textViewBrandName = (TextView) rowView.findViewById(R.id.textview_brand_name);
            holder.textViewLikeCount = (TextView) rowView.findViewById(R.id.textview_like_count);

            if (elem.rank == 1) {
                holder.imageRankingIcon.setImageResource(R.drawable.rank_gold_img);
            } else if (elem.rank == 2) {
                holder.imageRankingIcon.setImageResource(R.drawable.rank_silver_img);
            } else if (elem.rank == 3) {
                holder.imageRankingIcon.setImageResource(R.drawable.rank_bronze_img);
            }

            String ranking = String.valueOf(elem.rank) + ".";
            holder.textViewRanking.setText(ranking);
            holder.imageViewBrandIcon.setImageURI(brandImgUrl);
            holder.textViewBrandName.setText(brandName);

            DecimalFormat formatter = new DecimalFormat("#,###");


            holder.textViewLikeCount.setText(String.valueOf(formatter.format(likeCount)) + "개");

            return rowView;
        }

        private class Holder {
            public TextView textViewRanking;
            public ImageView imageRankingIcon;
            public SimpleDraweeView imageViewBrandIcon;
            public TextView textViewBrandName;
            public TextView textViewLikeCount;
        }
    }
}
