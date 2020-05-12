package kr.co.toyappfactory.gosung.viewpager.coupon;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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

public class UsedCoupon extends Fragment {

    private ArrayList<IssuedCouponResponse> usedCouponList;
    private ListView usedCouponListView;
    private UsedCouponAdapter usedCouponAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_used_coupon, container, false);
        usedCouponList = new ArrayList<>();
        usedCouponListView = (ListView)view.findViewById(R.id.listview_used_coupon);
        usedCouponAdapter = new UsedCouponAdapter();

        usedCouponListView.setAdapter(usedCouponAdapter);


        getUsedCoupon();

        return view;
    }

    private void getUsedCoupon(){
        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").create();
        Retrofit retrofit = new Retrofit.Builder().baseUrl(Constants.appServerHost).addConverterFactory(GsonConverterFactory.create(gson)).build();
        RestApi apiService = retrofit.create(RestApi.class);

        HashMap<String, String> params = new HashMap<>();
        params.put("userId", Constants.getInstance().getUserId());

        Call<ArrayList<IssuedCouponResponse>> call = apiService.getUsedCoupon(params);
        call.enqueue(new Callback<ArrayList<IssuedCouponResponse>>() {
            @Override
            public void onResponse(Call<ArrayList<IssuedCouponResponse>> call, Response<ArrayList<IssuedCouponResponse>> response) {
                usedCouponList = response.body();
                usedCouponAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Call<ArrayList<IssuedCouponResponse>> call, Throwable t) {

            }
        });

    }

    private class UsedCouponAdapter extends BaseAdapter{

        private LayoutInflater inflater;

        public UsedCouponAdapter(){
            inflater = (LayoutInflater)getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            return usedCouponList.size();
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


            providerImageView.setImageURI(usedCouponList.get(i).couponImage);
            couponNameTextView.setText(usedCouponList.get(i).couponName);

            return rowView;
        }
    }

}
