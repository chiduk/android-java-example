package kr.co.toyappfactory.gosung;


import android.content.ContentUris;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.facebook.AccessToken;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.drawee.backends.pipeline.Fresco;

import java.io.File;

import kr.co.toyappfactory.gosung.util.Constants;
import kr.co.toyappfactory.gosung.util.DBHelper;
import kr.co.toyappfactory.gosung.util.JoinUserInfo;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FacebookSdk.sdkInitialize(getApplicationContext());
        Fresco.initialize(this);
        Constants constants = Constants.getInstance();

        constants.setCategory(0);

        File folder = new File(Environment.getExternalStorageDirectory() + "/MobyDick");
        boolean success = true;
        if (!folder.exists()) {
            success = folder.mkdir();
        }

        Intent intent;

        DBHelper dbHelper = new DBHelper(MainActivity.this);

        if(dbHelper.isLoggedIn()){
            dbHelper.setUserInfo(1);
            constants.setUserId(JoinUserInfo.getInstance().getEmail());


           // intent = new Intent(this, NavigationMenu.class);

            //561630871

            Uri uri = Uri.parse("fb-messenger://user/100011220270682");
            //uri = ContentUris.withAppendedId(uri,100011220270682);
            intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);



        }else{
            intent = new Intent(this, Intro.class);
        }

        /*startActivity(intent);
        finish();*/
    }


}
