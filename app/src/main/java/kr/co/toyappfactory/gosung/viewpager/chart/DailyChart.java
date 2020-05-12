package kr.co.toyappfactory.gosung.viewpager.chart;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.HashMap;

import kr.co.toyappfactory.gosung.R;
import kr.co.toyappfactory.gosung.chart.LikeCountLineChart;
import kr.co.toyappfactory.gosung.response.CountDate;
import kr.co.toyappfactory.gosung.response.CountList;
import kr.co.toyappfactory.gosung.response.DailyLikeCountResponse;
import kr.co.toyappfactory.gosung.rest.RestApi;
import kr.co.toyappfactory.gosung.util.Constants;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class DailyChart extends Fragment {

    private String brandId;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_daily_chart, container, false);

        brandId = getArguments().getString("brandId");

        getChartData();
        return view;
    }

    private void getChartData(){
        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").create();
        Retrofit retrofit = new Retrofit.Builder().baseUrl(Constants.appServerHost).addConverterFactory(GsonConverterFactory.create(gson)).build();

        RestApi apiService = retrofit.create(RestApi.class);

        HashMap<String, String> parameters = new HashMap<String, String>();
        parameters.put("userId", Constants.getInstance().getUserId());
        parameters.put("brandId", brandId);

        Call<ArrayList<DailyLikeCountResponse>> call = apiService.getDailyLikeCount(parameters);
        call.enqueue(new Callback<ArrayList<DailyLikeCountResponse>>() {
            @Override
            public void onResponse(Call<ArrayList<DailyLikeCountResponse>> call, Response<ArrayList<DailyLikeCountResponse>> response) {

                CountList list = new CountList();

                for (DailyLikeCountResponse elem : response.body()) {
                    CountDate countDate = new CountDate(elem.year, elem.month, elem.day, elem.likeCount);
                    list.add(countDate);
                }

                Fragment fragment = new LikeCountLineChart();
                Bundle bundle = new Bundle();
                bundle.putParcelable("dataList", list);
                bundle.putInt("dataType", Constants.DATATYPE_DAILY);
                fragment.setArguments(bundle);
                FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.framelayout_daily_chart, fragment).commit();

            }

            @Override
            public void onFailure(Call<ArrayList<DailyLikeCountResponse>> call, Throwable t) {
                System.out.println("Get Daily Like Count Error " + t.getMessage());
            }
        });
    }


}
