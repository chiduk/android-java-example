package kr.co.toyappfactory.gosung;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.facebook.AccessToken;
import com.facebook.login.LoginManager;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.HashMap;

import kr.co.toyappfactory.gosung.response.ChangeNameResponse;
import kr.co.toyappfactory.gosung.response.ChangePasswordResponse;
import kr.co.toyappfactory.gosung.rest.RestApi;
import kr.co.toyappfactory.gosung.util.Constants;
import kr.co.toyappfactory.gosung.util.DBHelper;
import kr.co.toyappfactory.gosung.util.DialogBox;
import kr.co.toyappfactory.gosung.util.JoinUserInfo;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MyLogInInfo extends AppCompatActivity {
    private EditText editTextName;
    private EditText editTextCurrentPassword;
    private EditText editTextNewPassword;
    private EditText editTextConfirmPassword;

    private Button changePasswordButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_log_in_info);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        editTextName = (EditText)findViewById(R.id.edittext_name);
        editTextCurrentPassword = (EditText)findViewById(R.id.edittext_current_password);
        editTextNewPassword = (EditText)findViewById(R.id.edittext_new_password);
        editTextConfirmPassword = (EditText)findViewById(R.id.edittext_confirm_password);
        editTextName.setText(JoinUserInfo.getInstance().getName());


        addChangeNameButton();
        addChangePasswordButton();

        addLogoutButton();

        if(JoinUserInfo.getInstance().isFacebookAccount()){
            editTextCurrentPassword.setEnabled(false);
            editTextNewPassword.setEnabled(false);
            editTextConfirmPassword.setEnabled(false);
            changePasswordButton.setEnabled(false);

        }
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

    private void addLogoutButton() {

        Button logoutButton = (Button) findViewById(R.id.button_logout);

        if (logoutButton != null) {
            logoutButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (AccessToken.getCurrentAccessToken() == null) {
                        //email logout

                    } else {
                        //facebook logout
                        LoginManager.getInstance().logOut();
                    }

                    Intent intent = new Intent(MyLogInInfo.this, Intro.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);

                    DBHelper dbHelper = new DBHelper(MyLogInInfo.this);
                    dbHelper.deleteAllUser();
                }
            });
        }
    }

    public void addChangeNameButton(){
        Button button = (Button)findViewById(R.id.button_change_name);
        if(button != null){
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String name = editTextName.getText().toString();

                    if(name.length() <= 0){
                        AlertDialog.Builder alertDialog = new AlertDialog.Builder(MyLogInInfo.this);
                        alertDialog.setMessage(R.string.message_name_must_be_longer_than_zero).setPositiveButton(R.string.ok,
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                    }
                                });
                        AlertDialog alert = alertDialog.create();
                        alert.show();

                        return;
                    }

                    Gson gson = new GsonBuilder()
                            .setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ")
                            .create();
                    Retrofit retrofit = new Retrofit.Builder()
                            .baseUrl(Constants.appServerHost)
                            .addConverterFactory(GsonConverterFactory.create(gson))
                            .build();

                    RestApi apiService = retrofit.create(RestApi.class);

                    HashMap<String, String> params = new HashMap<String, String>();

                    params.put("email", JoinUserInfo.getInstance().getEmail());
                    params.put("newName", name);
                    Call<ChangeNameResponse> call = apiService.changeName(params);
                    call.enqueue(new Callback<ChangeNameResponse>() {
                        @Override
                        public void onResponse(Call<ChangeNameResponse> call, Response<ChangeNameResponse> response) {
                            editTextName.setText(response.body().newName);

                            JoinUserInfo.getInstance().setName(response.body().newName);

                            DBHelper dbHelper = new DBHelper(MyLogInInfo.this);
                            dbHelper.updateName(1, response.body().newName);

                            AlertDialog.Builder alertDialog = new AlertDialog.Builder(MyLogInInfo.this);
                            alertDialog.setMessage(R.string.message_name_change_succeeded).setPositiveButton(R.string.ok,
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {

                                        }
                                    });
                            AlertDialog alert = alertDialog.create();
                            alert.show();
                        }

                        @Override
                        public void onFailure(Call<ChangeNameResponse> call, Throwable t) {
                            AlertDialog.Builder alertDialog = new AlertDialog.Builder(MyLogInInfo.this);
                            alertDialog.setMessage(R.string.message_failed_to_change_name).setPositiveButton(R.string.ok,
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {

                                        }
                                    });
                            AlertDialog alert = alertDialog.create();
                            alert.show();
                        }
                    });
                }
            });
        }
    }

    public void addChangePasswordButton(){
        changePasswordButton = (Button)findViewById(R.id.button_change_password);
        if(changePasswordButton != null){
            changePasswordButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final String currentPassword = editTextCurrentPassword.getText().toString();
                    final String newPassword = editTextNewPassword.getText().toString();
                    String confirmPassword = editTextConfirmPassword.getText().toString();

                    if (newPassword.length() < 8 || newPassword.length() > 16) {
                        DialogBox.show(MyLogInInfo.this, R.string.message_password_length_4_20);
                        return;
                    }

                    if(currentPassword.length() <= 0){
                        DialogBox.show(MyLogInInfo.this, R.string.message_enter_current_password);
                        return;
                    }

                    if(newPassword.length() <= 0){
                        DialogBox.show(MyLogInInfo.this, R.string.message_enter_new_password);
                        return;
                    }

                    if(!newPassword.equals(confirmPassword)){
                       DialogBox.show(MyLogInInfo.this, R.string.message_new_password_not_match);
                        return;
                    }

                    Gson gson = new GsonBuilder()
                            .setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ")
                            .create();
                    Retrofit retrofit = new Retrofit.Builder()
                            .baseUrl(Constants.appServerHost)
                            .addConverterFactory(GsonConverterFactory.create(gson))
                            .build();

                    RestApi apiService = retrofit.create(RestApi.class);

                    HashMap<String, String> params = new HashMap<String, String>();
                    params.put("email", JoinUserInfo.getInstance().getEmail());
                    params.put("currentPassword", currentPassword);
                    params.put("newPassword", newPassword);

                    Call<ChangePasswordResponse> call = apiService.changePassword(params);
                    call.enqueue(new Callback<ChangePasswordResponse>() {
                        @Override
                        public void onResponse(Call<ChangePasswordResponse> call, Response<ChangePasswordResponse> response) {

                            if(response.body().code == 1300){
                                AlertDialog.Builder alertDialog = new AlertDialog.Builder(MyLogInInfo.this);
                                alertDialog.setMessage(R.string.message_password_change_succeeded).setPositiveButton(R.string.ok,
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                finish();
                                            }
                                        });
                                AlertDialog alert = alertDialog.create();
                                alert.show();
                            }else if(response.body().code == 1301){
                                DialogBox.show(MyLogInInfo.this, R.string.message_user_not_exists);
                            }else if(response.body().code == 1302){
                                DialogBox.show(MyLogInInfo.this, R.string.message_current_password_not_match);
                            }


                        }

                        @Override
                        public void onFailure(Call<ChangePasswordResponse> call, Throwable t) {
                            AlertDialog.Builder alertDialog = new AlertDialog.Builder(MyLogInInfo.this);
                            alertDialog.setMessage(R.string.message_failed_to_change_password).setPositiveButton(R.string.ok,
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {

                                        }
                                    });
                            AlertDialog alert = alertDialog.create();
                            alert.show();
                        }
                    });
                }
            });
        }
    }
}
