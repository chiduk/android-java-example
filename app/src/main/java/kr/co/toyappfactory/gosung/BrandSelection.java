package kr.co.toyappfactory.gosung;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.HashMap;

import kr.co.toyappfactory.gosung.response.BrandResponse;
import kr.co.toyappfactory.gosung.rest.RestApi;
import kr.co.toyappfactory.gosung.util.Constants;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class BrandSelection extends AppCompatActivity {

    private Context context;

    private String[] brandName;
    private Integer[] brandId;
    private Integer[] brandImageIds;
    private GridView gridView;
    private BrandImageAdapter adapter;
    ArrayList<BrandResponse> brandList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_brand_selection);

        context = this;
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


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        getSupportActionBar().setTitle(strCategory);

        gridView = (GridView) findViewById(R.id.gridview_brand_selection);

        addSearchEditText();

        getBrand();
    }

    private void addSearchEditText(){
        EditText searchEditText = (EditText)findViewById(R.id.edittext_search);
        if( searchEditText != null){
            searchEditText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    adapter.getFilter().filter(s);

                    if(count == 0){
                        adapter.resetList();
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });

        }
    }

    private void getBrand(){
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
        params.put("category", String.valueOf(Constants.getInstance().getCategory()));

        Call<ArrayList<BrandResponse>> call = apiService.getBrand(params);
        call.enqueue(new Callback<ArrayList<BrandResponse>>() {
            @Override
            public void onResponse(Call<ArrayList<BrandResponse>> call, Response<ArrayList<BrandResponse>> response) {
                //final ArrayList<BrandResponse> list = response.body();
                brandList = response.body();
                adapter = new BrandImageAdapter(BrandSelection.this);
                gridView.setAdapter(adapter);

                gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {

                        String selectedBrandId = brandList.get(position).brandId;
                        String selectedBrandName = brandList.get(position).name;

                        Intent intent = new Intent(context, BrandLikeAndDislikeCount.class);
                        intent.putExtra("brandId", selectedBrandId);
                        intent.putExtra("brandName", selectedBrandName);
                        startActivity(intent);

                    }
                });
            }

            @Override
            public void onFailure(Call<ArrayList<BrandResponse>> call, Throwable t) {

            }
        });
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

    private class BrandImageAdapter extends BaseAdapter implements Filterable{

        private Context context;
        private LayoutInflater inflater = null;

        ArrayList<BrandResponse> originalBrandList;

        public BrandImageAdapter(Context context) {
            this.context = context;
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            originalBrandList = brandList;
        }

        @Override
        public int getCount() {
            return brandList.size();
        }

        @Override
        public Object getItem(int position) {
            return brandList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = inflater.inflate(R.layout.brandlist, null);
            Holder holder = new Holder();
            BrandResponse brand = brandList.get(position);

            holder.imageView = (SimpleDraweeView) view.findViewById(R.id.imageview_brands);
            holder.textView = (TextView) view.findViewById(R.id.textview_brands);


            Uri uri = Uri.parse(Constants.appServerHost + "/big/b/" + brand.brandId + "/thm.jpeg");

            //holder.imageView.setImageResource(constants.getBrand().getDrawableIds()[position]);
            holder.imageView.setImageURI(uri);
            holder.textView.setText(brand.name);
            return view;
        }

        @Override
        public Filter getFilter() {
            Filter filter = new Filter() {
                @Override
                protected FilterResults performFiltering(CharSequence constraint) {
                    FilterResults filterResults = new FilterResults();
                    ArrayList<BrandResponse> filteredList = new ArrayList<BrandResponse>();

                    constraint = constraint.toString().toLowerCase();

                    for(BrandResponse response : brandList){
                        if(response.name.contains(constraint)){
                            filteredList.add(response);
                        }
                    }

                    filterResults.count = filteredList.size();
                    filterResults.values = filteredList;

                    return filterResults;
                }

                @Override
                protected void publishResults(CharSequence constraint, FilterResults results) {
                    ArrayList<BrandResponse> resultList = (ArrayList<BrandResponse>) results.values;
                    if( resultList != null){
                        brandList = resultList;
                        notifyDataSetChanged();

                    }
                }
            };

            return filter;
        }

        public void resetList(){
            brandList = originalBrandList;
        }
    }

    private class Holder{
        SimpleDraweeView imageView;
        TextView textView;
    }
}
