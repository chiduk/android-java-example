package kr.co.toyappfactory.gosung.menu;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import kr.co.toyappfactory.gosung.BrandSelection;
import kr.co.toyappfactory.gosung.Post;
import kr.co.toyappfactory.gosung.util.Constants;
import kr.co.toyappfactory.gosung.R;


public class CategorySelection extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_category_selection);

        String title = getResources().getString(R.string.evaluate);

        getSupportActionBar().setTitle(title);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);


        GridView categoryListView = (GridView) findViewById(R.id.gridview_category_selection);

        categoryListView.setAdapter(new CategorySelectionAdapter(this));

        categoryListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(CategorySelection.this, BrandSelection.class);
                int category = position + 1;
                Constants.getInstance().setCategory(category);

                startActivity(intent);
                overridePendingTransition(R.anim.slide_enter, R.anim.slide_exit);
            }
        });
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

    private class CategorySelectionAdapter extends BaseAdapter {

        private Context context;
        private LayoutInflater inflater = null;


        public CategorySelectionAdapter(Context c) {
            context = c;
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            return Constants.NUM_OF_CATEGORIES;
        }

        @Override
        public Object getItem(int position) {
            return Constants.categoryDrawables[position];
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = inflater.inflate(R.layout.categorylist, null);

            Holder holder = new Holder();
            holder.imageView = (ImageView) view.findViewById(R.id.imageview_categories);
            holder.imageView.setImageResource(Constants.categoryDrawables[position]);

            return view;
        }


    }

    private class Holder {
        public ImageView imageView;
    }
}
