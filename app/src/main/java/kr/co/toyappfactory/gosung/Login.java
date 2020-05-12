package kr.co.toyappfactory.gosung;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import kr.co.toyappfactory.gosung.response.LoginResponse;
import kr.co.toyappfactory.gosung.rest.RestApi;
import kr.co.toyappfactory.gosung.util.Constants;
import kr.co.toyappfactory.gosung.util.DBHelper;
import kr.co.toyappfactory.gosung.util.JoinUserInfo;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Login extends AppCompatActivity {

    private CallbackManager callbackManager;
    private LoginButton facebookLoginButton;
    private Button emailLoginButton;
    private EditText emailEditText;
    private EditText passwordEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        getSupportActionBar().hide();

        emailEditText = (EditText)findViewById(R.id.edittext_email);
        passwordEditText = (EditText)findViewById(R.id.edittext_password);

        addEmailLoginButton();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    private void addEmailLoginButton(){
        emailLoginButton = (Button)findViewById(R.id.button_login);

        emailLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (emailEditText.getText().length() == 0 || passwordEditText.getText().length() == 0) {
                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(Login.this);
                    alertDialog.setMessage(R.string.message_email_password_empty).setPositiveButton(R.string.ok,
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            });
                    AlertDialog alert = alertDialog.create();
                    alert.show();
                } else {
                    String email = emailEditText.getText().toString();
                    String password = passwordEditText.getText().toString();

                    Gson gson = new GsonBuilder()
                            .setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ")
                            .create();
                    Retrofit retrofit = new Retrofit.Builder()
                            .baseUrl(Constants.appServerHost)
                            .addConverterFactory(GsonConverterFactory.create(gson))
                            .build();

                    RestApi apiService = retrofit.create(RestApi.class);

                    HashMap<String, String> params = new HashMap<String, String>();
                    params.put("email", email);
                    params.put("password", password);

                    Call<LoginResponse> call = apiService.login(params);
                    call.enqueue(new Callback<LoginResponse>() {
                        @Override
                        public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                            int code = response.body().code;
                            String name = response.body().name;
                            String email = response.body().email;

                            String phone = response.body().phone;
                            int brandStar = response.body().brandStar;

                            if (code == 200) {
                                DBHelper dbHelper = new DBHelper(Login.this);
                                dbHelper.insertUser(name, response.body().uniqueId, email, phone);

                                JoinUserInfo userInfo = JoinUserInfo.getInstance();
                                userInfo.setName(name).setEmail(email)
                                        .setPhone(phone)
                                        .setIsFacebookAccount(false)
                                        .setUniqueId(response.body().uniqueId)
                                        .setBrandStar(brandStar);

                                Constants.getInstance().setUserId(userInfo.getEmail());
                                Intent intent = new Intent(Login.this, NavigationMenu.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                                finish();
                            } else if (code == 301) {
                                AlertDialog.Builder alertDialog = new AlertDialog.Builder(Login.this);
                                alertDialog.setMessage(R.string.message_invalid_email_password).setPositiveButton(R.string.ok,
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {

                                            }
                                        });
                                AlertDialog alert = alertDialog.create();
                                alert.show();
                            }
                        }

                        @Override
                        public void onFailure(Call<LoginResponse> call, Throwable t) {

                        }
                    });
                }
            }
        });
    }

}
