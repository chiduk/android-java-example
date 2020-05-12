package kr.co.toyappfactory.gosung;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;

import android.app.ListFragment;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TableRow;
import android.widget.TextView;

import kr.co.toyappfactory.gosung.menu.*;
import kr.co.toyappfactory.gosung.menu.Review;
import kr.co.toyappfactory.gosung.util.Constants;
import kr.co.toyappfactory.gosung.util.JoinUserInfo;

public class NavigationMenu extends AppCompatActivity {
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private ListView listViewDrawer;
    private RelativeLayout relativeLayout;
    private Context context;
    private TextView textViewName;
    private TextView textViewEmail;
    private int selectedCategory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation_menu);
        selectedCategory = 0;

        context = this;
        drawerLayout = (DrawerLayout) findViewById(R.id.drawerlayout);
        listViewDrawer = (ListView) findViewById(R.id.listview_navMenu);
        relativeLayout = (RelativeLayout) findViewById(R.id.rel_drawer);



        String[] menuItemArray = getResources().getStringArray(R.array.navMenuItems);

        ArrayAdapter<CharSequence> aa = ArrayAdapter.createFromResource(this, R.array.navMenuItems, android.R.layout.simple_list_item_1);

        listViewDrawer.setAdapter(new NavMenuListAdapter(this));

        listViewDrawer.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                displayView(position);
            }
        });



        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.app_name, R.string.app_name) {
            public void onDrawerClosed(View view) {
                // calling onPrepareOptionsMenu() to show action bar icons
                invalidateOptionsMenu();
            }

            public void onDrawerOpened(View drawerView) {
                // calling onPrepareOptionsMenu() to hide action bar icons

                invalidateOptionsMenu();
            }
        };

        drawerLayout.addDrawerListener(actionBarDrawerToggle);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        TextView textViewVersion = (TextView)findViewById(R.id.textview_version);
        textViewVersion.setText("Version " + Constants.major + "." + Constants.minor + "." + Constants.patch);

        actionBarDrawerToggle.syncState();

        displayView(0);


        setMenu();


    }

    private void setMenu(){
        final ImageView menu11 = (ImageView)findViewById(R.id.row1col1);

        if(menu11 != null){
            menu11.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    selectedCategory = getCategory(R.id.row1col1);
                    Constants.getInstance().setCategory(selectedCategory);
                    Intent intent = new Intent(NavigationMenu.this, Scoreboard.class);
                    startActivity(intent);

                }
            });
        }

        final ImageView menu12 = (ImageView)findViewById(R.id.row1col2);
        if( menu12 != null){
            menu12.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    selectedCategory = getCategory(R.id.row1col2);
                    Constants.getInstance().setCategory(selectedCategory);

                    TableRow row2 = (TableRow)findViewById(R.id.row2);
                    if( row2.getVisibility() == View.VISIBLE){
                        menu12.setImageDrawable(getResources().getDrawable(R.drawable.cate_idol_male_btn));

                        row2.setVisibility(View.GONE);
                    }else{
                        menu12.setImageDrawable(getResources().getDrawable(R.drawable.cate_idol_male_over_btn));

                        row2.setVisibility(View.VISIBLE);
                    }
                }
            });
        }

        final ImageView menu13 = (ImageView)findViewById(R.id.row1col3);
        if(menu13 != null){
            menu13.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    selectedCategory = getCategory(R.id.row1col3);
                    Constants.getInstance().setCategory(selectedCategory);

                    TableRow row2 = (TableRow)findViewById(R.id.row2);
                    if( row2.getVisibility() == View.VISIBLE){
                        menu13.setImageDrawable(getResources().getDrawable(R.drawable.cate_idol_female_btn));

                        row2.setVisibility(View.GONE);
                    }else{
                        menu13.setImageDrawable(getResources().getDrawable(R.drawable.cate_idol_female_over_btn));

                        row2.setVisibility(View.VISIBLE);
                    }
                }
            });
        }

        final ImageView menu14 = (ImageView)findViewById(R.id.row1col4);
        if(menu14 != null){
            menu14.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    selectedCategory = getCategory(R.id.row1col4);
                    Constants.getInstance().setCategory(selectedCategory);

                    TableRow row2 = (TableRow)findViewById(R.id.row2);
                    if( row2.getVisibility() == View.VISIBLE){
                        menu14.setImageDrawable(getResources().getDrawable(R.drawable.cate_enter_btn));

                        row2.setVisibility(View.GONE);
                    }else{
                        menu14.setImageDrawable(getResources().getDrawable(R.drawable.cate_enter_over_btn));

                        row2.setVisibility(View.VISIBLE);
                    }
                }
            });
        }

        ImageView menu21 = (ImageView)findViewById(R.id.row2col1);
        if(menu21 != null){
            menu21.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //Rank
                    Intent intent = new Intent(NavigationMenu.this, Scoreboard.class);
                    startActivity(intent);

                }
            });
        }

        ImageView menu22 = (ImageView)findViewById(R.id.row2col2);
        if(menu22 != null){
            menu22.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //Rate
                    Intent intent = new Intent(NavigationMenu.this, BrandSelection.class);
                    startActivity(intent);
                }
            });
        }

        ImageView menu31 = (ImageView)findViewById(R.id.row3col1);
        if(menu31 != null){
            menu31.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    selectedCategory = getCategory(R.id.row3col1);
                    Constants.getInstance().setCategory(selectedCategory);

                    TableRow row4 = (TableRow)findViewById(R.id.row4);
                    if(row4.getVisibility() == View.VISIBLE){
                        row4.setVisibility(View.GONE);
                    }else{
                        row4.setVisibility(View.VISIBLE);
                    }
                }
            });
        }

        ImageView menu32 = (ImageView)findViewById(R.id.row3col2);
        if(menu32 != null){
            menu32.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    selectedCategory = getCategory(R.id.row3col2);
                    Constants.getInstance().setCategory(selectedCategory);

                    TableRow row4 = (TableRow)findViewById(R.id.row4);
                    if(row4.getVisibility() == View.VISIBLE){
                        row4.setVisibility(View.GONE);
                    }else{
                        row4.setVisibility(View.VISIBLE);
                    }
                }
            });
        }

        ImageView menu33 = (ImageView)findViewById(R.id.row3col3);
        if(menu33 != null){
            menu33.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    selectedCategory = getCategory(R.id.row3col3);
                    Constants.getInstance().setCategory(selectedCategory);

                    TableRow row4 = (TableRow)findViewById(R.id.row4);
                    if(row4.getVisibility() == View.VISIBLE){
                        row4.setVisibility(View.GONE);
                    }else{
                        row4.setVisibility(View.VISIBLE);
                    }
                }
            });
        }

        ImageView menu34 = (ImageView)findViewById(R.id.row3col4);
        if(menu34 != null){
            menu34.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    selectedCategory = getCategory(R.id.row3col4);
                    Constants.getInstance().setCategory(selectedCategory);

                    TableRow row4 = (TableRow)findViewById(R.id.row4);
                    if(row4.getVisibility() == View.VISIBLE){
                        row4.setVisibility(View.GONE);
                    }else{
                        row4.setVisibility(View.VISIBLE);
                    }
                }
            });
        }

        ImageView menu51 = (ImageView)findViewById(R.id.row5col1);
        if(menu51 != null){
            menu51.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    selectedCategory = getCategory(R.id.row5col1);
                    Constants.getInstance().setCategory(selectedCategory);

                    TableRow row6 = (TableRow)findViewById(R.id.row6);
                    if(row6.getVisibility() == View.VISIBLE){
                        row6.setVisibility(View.GONE);
                    }else{
                        row6.setVisibility(View.VISIBLE);
                    }
                }
            });
        }

        ImageView menu52 = (ImageView)findViewById(R.id.row5col2);
        if(menu52 != null){
            menu52.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    selectedCategory = getCategory(R.id.row5col2);
                    Constants.getInstance().setCategory(selectedCategory);

                    TableRow row6 = (TableRow)findViewById(R.id.row6);
                    if(row6.getVisibility() == View.VISIBLE){
                        row6.setVisibility(View.GONE);
                    }else{
                        row6.setVisibility(View.VISIBLE);
                    }
                }
            });
        }

        ImageView menu53 = (ImageView)findViewById(R.id.row5col3);
        if(menu53 != null){
            menu53.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    selectedCategory = getCategory(R.id.row5col3);
                    Constants.getInstance().setCategory(selectedCategory);

                    TableRow row6 = (TableRow)findViewById(R.id.row6);
                    if (row6.getVisibility() == View.VISIBLE){
                        row6.setVisibility(View.GONE);
                    }else{
                        row6.setVisibility(View.VISIBLE);
                    }
                }
            });
        }

        ImageView menu54 = (ImageView)findViewById(R.id.row5col4);
        if(menu54 != null){
            menu54.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    selectedCategory = getCategory(R.id.row5col4);
                    Constants.getInstance().setCategory(selectedCategory);

                    TableRow row6 = (TableRow)findViewById(R.id.row6);
                    if(row6.getVisibility() == View.VISIBLE){
                        row6.setVisibility(View.GONE);
                    }else{
                        row6.setVisibility(View.VISIBLE);
                    }
                }
            });
        }
    }

    private int getCategory(int id){
        switch (id){
            case R.id.row1col1:

                return 0;

            case R.id.row1col2:

                return 4;

            case R.id.row1col3:

                return 3;

            case R.id.row1col4:

                return 1;

            case R.id.row3col1:

                return 0;

            case R.id.row3col2:

                return 0;

            case R.id.row3col3:

                return 0;

            case R.id.row3col4:

                return 0;

            case R.id.row5col1:

                return 0;

            case R.id.row5col2:

                return 0;

            case R.id.row5col3:

                return 0;

            case R.id.row5col4:

                return 0;

            default:

                return 0;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // toggle nav drawer on selecting action bar app icon/title
        if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        // Handle action bar actions click
        switch (item.getItemId()) {

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // if nav drawer is opened, hide the action items
        boolean drawerOpen = drawerLayout.isDrawerOpen(relativeLayout);
        //menu.findItem(R.id.menu_settings).setVisible(false);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        actionBarDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggls
        actionBarDrawerToggle.onConfigurationChanged(newConfig);
    }

    private void displayView(int position) {



        switch (position) {

            case 0:
                Fragment fragment = new Home();
                FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.framelayout, fragment).commit();

                // update selected item and title, then close the drawer
                listViewDrawer.setItemChecked(position, true);
                listViewDrawer.setSelection(position);

                drawerLayout.closeDrawer(relativeLayout);
                break;

            case 1:
                //nonListFragment = new ScoreCategorySelection();
                Intent reviewIntent = new Intent(this, NewsFeed.class);
                startActivity(reviewIntent);
                drawerLayout.closeDrawer(relativeLayout);
                break;

            case 2:
                Intent catSelIntent = new Intent(this, Review.class);
                startActivity(catSelIntent);
                drawerLayout.closeDrawer(relativeLayout);
                break;

            case 3:
                Intent couponBoxIntent = new Intent(this, CouponBox.class);
                startActivity(couponBoxIntent);
                drawerLayout.closeDrawer(relativeLayout);
                break;

            case 4:
                Intent newsFeedIntent = new Intent(this, CouponBox.class);
                startActivity(newsFeedIntent);
                drawerLayout.closeDrawer(relativeLayout);
                break;

            case 5:
                Intent contentsIntent = new Intent(this, Contents.class);
                startActivity(contentsIntent);
                drawerLayout.closeDrawer(relativeLayout);
                break;
            default:
                break;
        }

    }

    private class NavMenuListAdapter extends BaseAdapter {
        private Context context;
        private LayoutInflater inflater;


        public NavMenuListAdapter(Context c) {
            context = c;

            inflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            return Constants.naviMenuDrawables.length;
        }

        @Override
        public Object getItem(int position) {
            return Constants.naviMenuDrawables[position];
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = inflater.inflate(R.layout.drawer_list_item, null);

            ImageView imageViewMenuItem = (ImageView) view.findViewById(R.id.imageview_drawer_item);
            imageViewMenuItem.setImageResource(Constants.naviMenuDrawables[position]);

            return view;
        }
    }
}
