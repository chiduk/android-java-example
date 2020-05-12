package kr.co.toyappfactory.gosung;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class Share extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share);

        ListView listView = (ListView)findViewById(R.id.listview_share);
        ArrayList<String> list = new ArrayList<String>();
        list.add("카카오톡");
    }

    private class ShareAdapter extends BaseAdapter {
        private Context context;
        private ArrayList<String> itemList;
        private LayoutInflater inflater;

        public ShareAdapter(Context c, ArrayList<String> list){
            context = c;
            itemList = list;
            inflater = (LayoutInflater)context.getSystemService(LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            return itemList.size();
        }

        @Override
        public Object getItem(int position) {
            return itemList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = inflater.inflate(R.layout.sharelist, null);
            ImageView imageView = (ImageView)view.findViewById(R.id.imageview_share);
            TextView textView = (TextView)view.findViewById(R.id.textview_share);


            return null;
        }
    }
}
