package kr.co.toyappfactory.gosung;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.HashMap;

import kr.co.toyappfactory.gosung.response.SearchResponse;
import kr.co.toyappfactory.gosung.rest.RestApi;
import kr.co.toyappfactory.gosung.util.Constants;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class BrandSearch extends AppCompatActivity {

    private ArrayList<SearchResponse> searchList;
    private ListView searchListView;
    private EditText searchEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_brand_search);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("");
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);
        View v = getLayoutInflater().inflate(R.layout.brand_search_action_bar, null);
        actionBar.setCustomView(v);


        searchList = new ArrayList<>();

        searchListView = (ListView)findViewById(R.id.listview_search_result);

        if(searchListView != null){
            searchListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    SearchResponse searchResult = searchList.get(position);
                    //ArrayList<Integer> categoryList = searchResult.category;
                    int category = searchResult.category.get(0);
                    Constants.getInstance().setCategory(category);

                    String brandId = searchResult._id;
                    String name = searchResult.brandName;

                    Intent intent = new Intent(BrandSearch.this, BrandLikeAndDislikeCount.class);
                    intent.putExtra("brandId", brandId);
                    intent.putExtra("brandName", name);
                    startActivity(intent);

                    finish();

                }
            });
        }
        addSearchEditText(v);
        addSearchButton(v);

    }

    private void addSearchButton(View view){
        ImageButton button = (ImageButton)view.findViewById(R.id.imagebutton_search);
        if(button != null){
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    requestSearch(searchEditText.getText().toString());
                }
            });
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:

                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void addSearchEditText(View view){
        searchEditText = (EditText)view.findViewById(R.id.edittext_search);

        searchEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    //performSearch();
                    System.out.println("Search Search");
                    return true;
                }
                return false;
            }
        });

        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(count > 0){
                    //System.out.println(s.toString());
                    requestSearch(s.toString());
                }else if(count == 0){
                    searchList.clear();
                    searchListView.setAdapter(new BrandSearchAdapter(BrandSearch.this));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void requestSearch(String keyword){
        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").create();
        Retrofit retrofit = new Retrofit.Builder().baseUrl(Constants.appServerHost).addConverterFactory(GsonConverterFactory.create(gson)).build();

        RestApi apiService = retrofit.create(RestApi.class);

        HashMap<String, String> params = new HashMap<String, String>();

        params.put("userId", Constants.getInstance().getUserId());
        params.put("keyword", keyword);

        Call<ArrayList<SearchResponse>> call = apiService.requestSearch(params);
        call.enqueue(new Callback<ArrayList<SearchResponse>>() {
            @Override
            public void onResponse(Call<ArrayList<SearchResponse>> call, Response<ArrayList<SearchResponse>> response) {
                if(response.body() != null){
                    searchList = response.body();
                    searchListView.setAdapter(new BrandSearchAdapter(BrandSearch.this));

                    for(SearchResponse result : searchList ){
                        System.out.println(result.brandName);
                    }

                }
            }

            @Override
            public void onFailure(Call<ArrayList<SearchResponse>> call, Throwable t) {

            }
        });
    }

    private class BrandSearchAdapter extends BaseAdapter {

        private Context context;
        private LayoutInflater inflater;
        public BrandSearchAdapter(Context context){
            this.context = context;
            inflater = (LayoutInflater)context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            return searchList.size();
        }

        @Override
        public Object getItem(int position) {
            return searchList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = inflater.inflate(R.layout.brand_search_list_row, null);
            TextView brandNameTextView = (TextView)view.findViewById(R.id.textview_brand_name);

            brandNameTextView.setText(searchList.get(position).brandName);

            return view;
        }
    }
}
