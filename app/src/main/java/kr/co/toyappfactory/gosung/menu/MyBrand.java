package kr.co.toyappfactory.gosung.menu;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;


import com.facebook.drawee.view.SimpleDraweeView;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.HashMap;

import kr.co.toyappfactory.gosung.BrandPage;
import kr.co.toyappfactory.gosung.util.Constants;
import kr.co.toyappfactory.gosung.response.LikeAndDislikeCount;
import kr.co.toyappfactory.gosung.R;
import kr.co.toyappfactory.gosung.rest.RestApi;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MyBrand extends AppCompatActivity {

    private GridView gridView;
    private ArrayList<LikeAndDislikeCount> likeAndDislikeCountList;
    private Context context;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_my_brand);

        context = this;

        Constants.getInstance().setCategory(Constants.ALL);

        String title = getResources().getString(R.string.my_brand);
        getSupportActionBar().setTitle(title);

        gridView = (GridView) findViewById(R.id.gridview_fav_brands);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                LikeAndDislikeCount likeAndDislikeCount = likeAndDislikeCountList.get(position);

                Intent intent = new Intent(MyBrand.this, BrandPage.class);
                intent.putExtra("brandId", likeAndDislikeCount.brandId);
                intent.putExtra("brandName", likeAndDislikeCount.name);
                intent.putExtra("likeCount", likeAndDislikeCount.likeCount);
                intent.putExtra("dislikeCount", likeAndDislikeCount.dislikeCount);

                Constants.getInstance().setCategory(likeAndDislikeCount.category);

                startActivity(intent);
            }
        });


        getLikedBrands();


    }


    private void getLikedBrands() {
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

        Call<ArrayList<LikeAndDislikeCount>> call = apiService.getLikedBrands(params);
        call.enqueue(new Callback<ArrayList<LikeAndDislikeCount>>() {
            @Override
            public void onResponse(Call<ArrayList<LikeAndDislikeCount>> call, Response<ArrayList<LikeAndDislikeCount>> response) {
                ArrayList<String> list = new ArrayList<String>();
                Constants constants = Constants.getInstance();

                likeAndDislikeCountList = response.body();

                for (LikeAndDislikeCount elem : response.body()) {
                    String brandId = elem.brandId;
                    //Todo: Show favorite brands per category
                    String url = Constants.appServerHost + "/big/b/" + brandId + "/thm.jpeg";
                    list.add(url);
                }

                gridView.setAdapter(new FavBrandImageAdapter(context, list));

            }

            @Override
            public void onFailure(Call<ArrayList<LikeAndDislikeCount>> call, Throwable t) {

            }
        });
    }

    private class FavBrandImageAdapter extends BaseAdapter {
        private Context context;
        private LayoutInflater inflater = null;
        private ArrayList<String> brandImageList;

        public FavBrandImageAdapter(Context context, ArrayList<String> brandImageList) {
            this.context = context;
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            this.brandImageList = brandImageList;
        }

        @Override
        public int getCount() {
            return brandImageList.size();
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
            View view = inflater.inflate(R.layout.mybrandlist, null);
            Holder holder = new Holder();
            holder.imageView = (SimpleDraweeView) view.findViewById(R.id.imageview_brands);
            holder.imageView.setImageURI(brandImageList.get(position));

            return view;
        }
    }

    private class Holder{
        SimpleDraweeView imageView;
    }
}
