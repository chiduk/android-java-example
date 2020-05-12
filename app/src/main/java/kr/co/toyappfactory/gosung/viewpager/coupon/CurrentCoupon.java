package kr.co.toyappfactory.gosung.viewpager.coupon;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
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

import kr.co.toyappfactory.gosung.IssuedCouponDetail;
import kr.co.toyappfactory.gosung.R;
import kr.co.toyappfactory.gosung.response.IssuedCouponResponse;
import kr.co.toyappfactory.gosung.rest.RestApi;
import kr.co.toyappfactory.gosung.util.Constants;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class CurrentCoupon extends Fragment {

    private ArrayList<IssuedCouponResponse> currentCouponList;
    private ListView couponListView;
    private CurrentCouponAdapter currentCouponAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_current_coupon, container, false);

        currentCouponList = new ArrayList<>();

        couponListView = (ListView)view.findViewById(R.id.listview_current_coupon);
        currentCouponAdapter = new CurrentCouponAdapter();
        couponListView.setAdapter(currentCouponAdapter);

        couponListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                IssuedCouponResponse coupon = currentCouponList.get(i);
                Intent intent = new Intent(getActivity(), IssuedCouponDetail.class);

                intent.putExtra("couponId", coupon.couponId);
                intent.putExtra("serialCode", coupon.serialCode);
                intent.putExtra("couponProvider", coupon.couponProvider);
                intent.putExtra("couponName", coupon.couponName);
                intent.putExtra("couponImage", coupon.couponImage);
                intent.putExtra("startDate", coupon.startDate);
                intent.putExtra("endDate", coupon.endDate);
                startActivity(intent);
            }
        });
        getCurrentCoupon();

        return view;
    }

    private void getCurrentCoupon(){
        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").create();
        Retrofit retrofit = new Retrofit.Builder().baseUrl(Constants.appServerHost).addConverterFactory(GsonConverterFactory.create(gson)).build();
        RestApi apiService = retrofit.create(RestApi.class);

        HashMap<String, String> params = new HashMap<>();
        params.put("userId", Constants.getInstance().getUserId());

        Call<ArrayList<IssuedCouponResponse>> call = apiService.getCurrentCoupon(params);
        call.enqueue(new Callback<ArrayList<IssuedCouponResponse>>() {
            @Override
            public void onResponse(Call<ArrayList<IssuedCouponResponse>> call, Response<ArrayList<IssuedCouponResponse>> response) {
                currentCouponList = response.body();
                currentCouponAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Call<ArrayList<IssuedCouponResponse>> call, Throwable t) {

            }
        });
    }


    private class CurrentCouponAdapter extends BaseAdapter{
        private LayoutInflater inflater;

        public CurrentCouponAdapter(){
            inflater = (LayoutInflater)getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            return currentCouponList.size();
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

            providerImageView.setImageURI(currentCouponList.get(i).couponImage);
            couponNameTextView.setText(currentCouponList.get(i).couponName);

            return rowView;
        }
    }

}
