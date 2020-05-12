package kr.co.toyappfactory.gosung.viewpager.coupon;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.HashMap;

import kr.co.toyappfactory.gosung.R;
import kr.co.toyappfactory.gosung.response.IssuedCouponResponse;
import kr.co.toyappfactory.gosung.rest.RestApi;
import kr.co.toyappfactory.gosung.util.Constants;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class ExpiredCoupon extends Fragment {

    private ArrayList<IssuedCouponResponse> expiredCouponList;
    private ListView expiredCouponListView;
    private ExpiredCouponAdapter expiredCouponAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_expired_coupon, container, false);

        expiredCouponList = new ArrayList<>();
        expiredCouponListView = (ListView)view.findViewById(R.id.listview_expired_coupon);
        expiredCouponAdapter = new ExpiredCouponAdapter();

        expiredCouponListView.setAdapter(expiredCouponAdapter);

        getExpiredCoupon();

        return view;
    }

    private void getExpiredCoupon(){
        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").create();
        Retrofit retrofit = new Retrofit.Builder().baseUrl(Constants.appServerHost).addConverterFactory(GsonConverterFactory.create(gson)).build();
        RestApi apiService = retrofit.create(RestApi.class);

        HashMap<String, String> params = new HashMap<>();
        params.put("userId", Constants.getInstance().getUserId());

        Call<ArrayList<IssuedCouponResponse>> call = apiService.getExpiredCoupon(params);
        call.enqueue(new Callback<ArrayList<IssuedCouponResponse>>() {
            @Override
            public void onResponse(Call<ArrayList<IssuedCouponResponse>> call, Response<ArrayList<IssuedCouponResponse>> response) {
                expiredCouponList = response.body();
                expiredCouponAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Call<ArrayList<IssuedCouponResponse>> call, Throwable t) {

            }
        });
    }

    private class ExpiredCouponAdapter extends BaseAdapter{

        private LayoutInflater inflater;

        public ExpiredCouponAdapter(){
            inflater = (LayoutInflater)getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            return expiredCouponList.size();
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
            View rowView = inflater.inflate(R.layout.couponlist, null);

            SimpleDraweeView providerImageView = (SimpleDraweeView)rowView.findViewById(R.id.imageview_coupons);
            TextView providerTextView = (TextView)rowView.findViewById(R.id.textview_coupon_provider);
            TextView couponNameTextView = (TextView)rowView.findViewById(R.id.textview_coupon_name);
            ImageView statusImageView = (ImageView)rowView.findViewById(R.id.imageview_coupon_status);


            providerImageView.setImageURI(expiredCouponList.get(i).couponImage);
            couponNameTextView.setText(expiredCouponList.get(i).couponName);


            return rowView;
        }
    }

}
