package kr.co.toyappfactory.gosung.gcm;

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import kr.co.toyappfactory.gosung.R;
import kr.co.toyappfactory.gosung.rest.RestApi;
import kr.co.toyappfactory.gosung.util.Constants;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by chiduk on 2016. 10. 27..
 */

public class RegistrationIntentService extends IntentService {
    private static final String TAG = "RegIntentService";

    public RegistrationIntentService(){
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        try{
            InstanceID instanceID = InstanceID.getInstance(this);
            String token = instanceID.getToken(getString(R.string.senderId), GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);
            sendRegistrationTokenToServer(token);

            sharedPreferences.edit().putBoolean(GcmPreferences.SENT_TOKEN_TO_SERVER, true).apply();
        }catch (Exception e){
            e.printStackTrace();
            sharedPreferences.edit().putBoolean(GcmPreferences.SENT_TOKEN_TO_SERVER, false).apply();
        }

        Intent registrationComplete = new Intent(GcmPreferences.REGISTRATION_COMPLETE);
        LocalBroadcastManager.getInstance(this).sendBroadcast(registrationComplete);
    }

    private void sendRegistrationTokenToServer(String token){
        Log.i("TOKEN:", token);

        String android_id = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);

        Log.i("Android ID", android_id);

        Gson gson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ")
                .create();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.appServerHost)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        RestApi apiService = retrofit.create(RestApi.class);

        PushRegistrationInfo info = new PushRegistrationInfo(android_id, token, "android");

        Call<PushRegistrationInfo> call = apiService.postRegId(info);
        call.enqueue(new Callback<PushRegistrationInfo>() {
            @Override
            public void onResponse(Call<PushRegistrationInfo> call, Response<PushRegistrationInfo> response) {
                int statusCode = response.code();
                Log.i("Response status code", ""+statusCode);
            }

            @Override
            public void onFailure(Call<PushRegistrationInfo> call, Throwable t) {

            }
        });
    }
}
