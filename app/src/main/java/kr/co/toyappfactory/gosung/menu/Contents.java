package kr.co.toyappfactory.gosung.menu;

import android.app.Activity;
import android.app.Fragment;
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

import kr.co.toyappfactory.gosung.util.Constants;
import kr.co.toyappfactory.gosung.ContentPage;
import kr.co.toyappfactory.gosung.R;


public class Contents extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_contents);

        String title = getResources().getString(R.string.contents);

        getSupportActionBar().setTitle(title);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);


        ListView listViewContent = (ListView)findViewById(R.id.listview_content);
        listViewContent.setAdapter(new ContentsAdapter(this));
        listViewContent.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(Contents.this, ContentPage.class);
                intent.putExtra("index", position);
                startActivity(intent);
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
    private class ContentsAdapter extends BaseAdapter {
        private Context context;
        private LayoutInflater inflater = null;

        public ContentsAdapter(Context c) {
            context = c;
            inflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            return Constants.contentTemplateDrawables.length;
        }

        @Override
        public Object getItem(int position) {
            return Constants.contentTemplateDrawables[position];
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = inflater.inflate(R.layout.contentlist, null);
            Holder holder = new Holder();
            holder.imageView = (ImageView) view.findViewById(R.id.imageview_content);
            holder.imageView.setImageResource(Constants.contentTemplateDrawables[position]);
            return view;
        }
    }

    private class Holder {
        public ImageView imageView;
    }
}
