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
import android.widget.GridView;
import android.widget.ImageView;

//import com.google.android.gms.analytics.HitBuilders;
//import com.google.android.gms.analytics.Tracker;

import kr.co.toyappfactory.gosung.GlobalApplication;
import kr.co.toyappfactory.gosung.util.Constants;
import kr.co.toyappfactory.gosung.R;
import kr.co.toyappfactory.gosung.Scoreboard;

public class ScoreCategorySelection extends AppCompatActivity {
    //private Tracker tracker;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
         setContentView(R.layout.fragment_score_category_selection);


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);


        GlobalApplication application = (GlobalApplication)getApplication();
        //tracker = application.getDefaultTracker();

        String title = getResources().getString(R.string.ranking);

        getSupportActionBar().setTitle(title);

        GridView gridView = (GridView)findViewById(R.id.gridview_score_category_selection);
        gridView.setAdapter(new ScoreCategoryAdapter(this));

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Constants constants = Constants.getInstance();
                constants.setCategory(position);

                Intent intent = new Intent(ScoreCategorySelection.this, Scoreboard.class);
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

    @Override
    public void onResume() {
        super.onResume();

        System.out.println("Setting screen name " + "Scoreboard category selection");
        //tracker.setScreenName("Scoreboard category selection");
        //tracker.send(new HitBuilders.ScreenViewBuilder().build());
    }

    private class ScoreCategoryAdapter extends BaseAdapter{
        private Context context;
        private LayoutInflater inflater = null;

        public ScoreCategoryAdapter(Context c){
            context = c;
            inflater = (LayoutInflater)context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            return Constants.scoreCategoryDrawables.length;
        }

        @Override
        public Object getItem(int position) {
            return Constants.scoreCategoryDrawables[position];
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = inflater.inflate(R.layout.scorecategorylist, null);

            Holder holder = new Holder();

            holder.imageView = (ImageView)view.findViewById(R.id.imageview_score_categories);
            holder.imageView.setImageResource(Constants.scoreCategoryDrawables[position]);

            return view;
        }


    }

    private class Holder {
        public ImageView imageView;
    }


}
