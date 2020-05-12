package kr.co.toyappfactory.gosung;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.RadialGradient;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import java.util.ArrayList;


public class ContentPage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_content_page);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        Intent intent = getIntent();
        int index = 0;
        index = intent.getIntExtra("index", index);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        Button participateButton = (Button)findViewById(R.id.button_participate);



        ArrayList<Integer> drawableList = new ArrayList<Integer>();
        int eventType = 0;
        switch (index){
            case 0:

                eventType = 1;

                break;

            case 1:

                eventType = 2;
                break;

            case 2:

                eventType = 3;
                break;

            case 3:

                break;

            case 4:

                break;

            case 5:

                break;

            case 6:

                break;

            case 7:

                break;
        }

        ListView listView = (ListView)findViewById(R.id.listview_content_page);
        listView.setAdapter(new ContentPageAdapter(this, drawableList, eventType));

        addParticipateButton();

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

    private void addParticipateButton(){
        Button participateButton = (Button)findViewById(R.id.button_participate);
        if(participateButton != null){
            participateButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(ContentPage.this);
                    alertDialog.setMessage(R.string.message_you_voted).setPositiveButton(R.string.ok,
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                    finish();

                                }
                            });

                    AlertDialog alert = alertDialog.create();
                    alert.show();
                }
            });

        }
    }

    private class ContentPageAdapter extends BaseAdapter{

        private Context context;
        private LayoutInflater inflater = null;
        private ArrayList<Integer> drawableList;
        private int eventType;
        public ContentPageAdapter(Context c, ArrayList<Integer> list, int eventType){
            context = c;
            inflater = (LayoutInflater)context.getSystemService(LAYOUT_INFLATER_SERVICE);
            drawableList = list;
            this.eventType = eventType;
        }

        @Override
        public int getCount() {
            return drawableList.size();
        }

        @Override
        public Object getItem(int position) {
            return drawableList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = inflater.inflate(R.layout.contentpagelist, null);

            RadioGroup radioGroup = (RadioGroup)view.findViewById(R.id.radiogroup_survey);

            if(eventType == 1){
                addCoffeeEvent(radioGroup);
            }else if(eventType == 2){
                addCosmeticEvent(radioGroup);
            }else if(eventType == 3){
                addDeptStoreEvent(radioGroup);
            }else{

            }


            Holder holder = new Holder();
            holder.imageView = (ImageView)view.findViewById(R.id.imageview_content_page);
            holder.imageView.setImageResource(drawableList.get(position));
            return view;
        }

        private void addCoffeeEvent(RadioGroup radioGroup){
            RadioButton[] radioButton = new RadioButton[5];
            for(int i = 0; i < 5; i++){
                radioButton[i] = new RadioButton(ContentPage.this);
                switch (i){
                    case 0:
                        break;
                    case 1:
                        break;

                    case 2:
                        break;

                    case 3:
                        break;

                    case 4:
                        break;
                }
                //radioButton[i].setCompoundDrawablesWithIntrinsicBounds(0, 0, drawable, 0);
                //radioGroup.addView(radioButton[i]);
            }


        }

        private void addCosmeticEvent(RadioGroup radioGroup){
            RadioButton[] radioButtons = new RadioButton[5];
            for(int i = 0; i < 5; i++){
                radioButtons[i] = new RadioButton(ContentPage.this);
                switch (i){
                    case 0:
                        break;

                    case 1:
                        break;

                    case 2:
                        break;

                    case 3:
                        break;

                    case 4:
                        break;
                }
                //radioButtons[i].setCompoundDrawablesWithIntrinsicBounds(0, 0, drawable, 0);
               // radioGroup.addView(radioButtons[i]);
            }
        }

        private void addDeptStoreEvent(RadioGroup radioGroup){
            RadioButton[] radioButtons = new RadioButton[5];
            for(int i = 0; i < 5; i++){
                radioButtons[i] = new RadioButton(ContentPage.this);
                switch (i){
                    case 0:
                        break;

                    case 1:
                        break;

                    case 2:
                        break;

                    case 3:
                        break;
                }
                //radioButtons[i].setCompoundDrawablesWithIntrinsicBounds(0, 0, drawable, 0);
                //radioGroup.addView(radioButtons[i]);
            }
        }
    }



    private class Holder {
        public ImageView imageView;
    }


}
