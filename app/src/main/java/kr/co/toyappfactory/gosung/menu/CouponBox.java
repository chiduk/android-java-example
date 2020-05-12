package kr.co.toyappfactory.gosung.menu;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
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

import java.util.ArrayList;
import java.util.HashMap;

import kr.co.toyappfactory.gosung.Coupon;
import kr.co.toyappfactory.gosung.R;
import kr.co.toyappfactory.gosung.response.CouponResponse;
import kr.co.toyappfactory.gosung.rest.RestApi;
import kr.co.toyappfactory.gosung.util.Constants;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class CouponBox extends AppCompatActivity {
    private ArrayList<CouponResponse.Coupon> allCouponList;
    private ArrayList<CouponResponse.IssuedCoupon> issuedCouponList;
    private ListView couponListView;
    private CouponBoxAdapter couponBoxAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_coupon_box);

        String title = getResources().getString(R.string.couponBox);

        getSupportActionBar().setTitle(title);


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);


        couponListView = (ListView) findViewById(R.id.listview_coupon);
        allCouponList = new ArrayList<>();
        issuedCouponList = new ArrayList<>();

        couponBoxAdapter = new CouponBoxAdapter();
        couponListView.setAdapter(couponBoxAdapter);


        couponListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                CouponResponse.Coupon couponInfo = allCouponList.get(position);

                Intent intent = new Intent(CouponBox.this, Coupon.class);
                intent.putExtra("couponId", couponInfo._id);
                intent.putExtra("provider", couponInfo.provider);
                intent.putExtra("name", couponInfo.name);
                intent.putExtra("image", couponInfo.image);
                intent.putExtra("startDate", couponInfo.startDate);
                intent.putExtra("endDate", couponInfo.endDate);
                intent.putExtra("stores", couponInfo.stores);
                intent.putExtra("info", couponInfo.info);
                startActivity(intent);
            }
        });

        getCoupon();

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
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    private void getCoupon(){
        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").create();
        Retrofit retrofit = new Retrofit.Builder().baseUrl(Constants.appServerHost).addConverterFactory(GsonConverterFactory.create(gson)).build();

        RestApi apiService = retrofit.create(RestApi.class);

        HashMap<String, String> params = new HashMap<>();
        params.put("userId", Constants.getInstance().getUserId());
        
        Call<CouponResponse> call = apiService.getCoupon(params);
        call.enqueue(new Callback<CouponResponse>() {
            @Override
            public void onResponse(Call<CouponResponse> call, Response<CouponResponse> response) {
                allCouponList = response.body().allCouponList;
                issuedCouponList = response.body().issuedCouponList;

                couponBoxAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Call<CouponResponse> call, Throwable t) {

            }
        });
    }

    private class CouponBoxAdapter extends BaseAdapter {

        private LayoutInflater inflater;

        public CouponBoxAdapter() {
            inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            return allCouponList.size();
        }

        @Override
        public Object getItem(int position) {
            return allCouponList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = inflater.inflate(R.layout.couponlist, null);
            SimpleDraweeView imageView = (SimpleDraweeView) view.findViewById(R.id.imageview_coupons);
            TextView couponProviderTextView = (TextView)view.findViewById(R.id.textview_coupon_provider);
            TextView couponNameTextView = (TextView)view.findViewById(R.id.textview_coupon_name);

            imageView.setImageURI(allCouponList.get(position).image);
            couponProviderTextView.setText(allCouponList.get(position).provider);
            couponNameTextView.setText(allCouponList.get(position).name);
            couponNameTextView.setTypeface(null, Typeface.BOLD);

            ImageView couponStatusImageView = (ImageView)view.findViewById(R.id.imageview_coupon_status);

            if(issuedCouponList.size() > 0){

                for(CouponResponse.IssuedCoupon issuedCoupon : issuedCouponList){
                    if(allCouponList.get(position)._id.equals(issuedCoupon.couponId)){
                        couponStatusImageView.setImageResource(R.drawable.coupon_get_end_btn);
                        break;
                    }else{
                        couponStatusImageView.setImageResource(R.drawable.coupon_get_2_btn);
                    }
                }

            }else{
                couponStatusImageView.setImageResource(R.drawable.coupon_get_2_btn);
            }

            return view;
        }
    }
}
