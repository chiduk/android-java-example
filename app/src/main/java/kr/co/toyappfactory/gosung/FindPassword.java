package kr.co.toyappfactory.gosung;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.HashMap;

import kr.co.toyappfactory.gosung.response.CheckEmailResponse;
import kr.co.toyappfactory.gosung.response.GetTempPasswordResponse;
import kr.co.toyappfactory.gosung.rest.RestApi;
import kr.co.toyappfactory.gosung.util.Constants;
import kr.co.toyappfactory.gosung.util.DialogBox;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class FindPassword extends AppCompatActivity {

    private EditText getTempPasswdEmailEditText;
    private EditText confirmEmailEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_password);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);


        getTempPasswdEmailEditText = (EditText)findViewById(R.id.edittext_get_temp_passwd_email);
        confirmEmailEditText = (EditText)findViewById(R.id.edittext_confirm_email);


        addGetTempPasswdButton();
        addConfirmEmailButton();

    }
    
    private void addConfirmEmailButton(){
        Button confirmEmailButton = (Button)findViewById(R.id.button_confirm_join);

        if(confirmEmailButton != null){
            confirmEmailButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Gson gson = new GsonBuilder()
                            .setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ")
                            .create();
                    Retrofit retrofit = new Retrofit.Builder()
                            .baseUrl(Constants.appServerHost)
                            .addConverterFactory(GsonConverterFactory.create(gson))
                            .build();
                    String email = confirmEmailEditText.getText().toString();

                    if(email.isEmpty()){
                        DialogBox.show(FindPassword.this, R.string.message_email_empty);
                        return;
                    }

                    if(!email.contains("@") || !email.contains(".")){
                        DialogBox.show(FindPassword.this, R.string.message_email_wrong_format);
                        return;
                    }

                    RestApi apiService = retrofit.create(RestApi.class);

                    HashMap<String, String> params = new HashMap<String, String>();
                    params.put("email", email);

                    Call<CheckEmailResponse> call = apiService.checkEmail(params);
                    call.enqueue(new Callback<CheckEmailResponse>() {
                        @Override
                        public void onResponse(Call<CheckEmailResponse> call, Response<CheckEmailResponse> response) {
                            int code = response.body().code;

                            if( code == 201){
                                DialogBox.show(FindPassword.this, R.string.message_email_currently_joined);
                            }else if( code == 200 ){
                                DialogBox.show(FindPassword.this, R.string.message_email_currently_not_joined);
                            }
                        }

                        @Override
                        public void onFailure(Call<CheckEmailResponse> call, Throwable t) {

                        }
                    });
                }
            });
        }
    }


    private void addGetTempPasswdButton(){
        Button getTempPasswdButton = (Button)findViewById(R.id.button_get_temp_password);

        if(getTempPasswdButton != null){
            getTempPasswdButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Gson gson = new GsonBuilder()
                            .setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ")
                            .create();
                    Retrofit retrofit = new Retrofit.Builder()
                            .baseUrl(Constants.appServerHost)
                            .addConverterFactory(GsonConverterFactory.create(gson))
                            .build();

                    RestApi apiService = retrofit.create(RestApi.class);

                    HashMap<String, String> params = new HashMap<String, String>();
                    params.put("email", getTempPasswdEmailEditText.getText().toString());

                    Call<GetTempPasswordResponse> call = apiService.getTempPassword(params);
                    call.enqueue(new Callback<GetTempPasswordResponse>() {
                        @Override
                        public void onResponse(Call<GetTempPasswordResponse> call, Response<GetTempPasswordResponse> response) {
                            int code = response.body().code;
                            if(code == 1501){

                            }else if(code == 1502){
                                DialogBox.show(FindPassword.this, R.string.message_facebook_tempPasswd);
                            }else {
                                AlertDialog.Builder alertDialog = new AlertDialog.Builder(FindPassword.this);
                                alertDialog.setMessage(R.string.message_temp_password_sent).setPositiveButton(R.string.ok,
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {

                                            }
                                        });
                                AlertDialog alert = alertDialog.create();
                                alert.show();
                                //finish();
                            }
                        }

                        @Override
                        public void onFailure(Call<GetTempPasswordResponse> call, Throwable t) {

                        }
                    });
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
}
