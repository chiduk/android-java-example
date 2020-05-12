package kr.co.toyappfactory.gosung;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.HashMap;

import kr.co.toyappfactory.gosung.response.AddUserResponse;
import kr.co.toyappfactory.gosung.rest.RestApi;
import kr.co.toyappfactory.gosung.util.Constants;
import kr.co.toyappfactory.gosung.util.DBHelper;
import kr.co.toyappfactory.gosung.util.JoinUserInfo;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class PhoneNumberVerification extends AppCompatActivity {

    private EditText editTextPhone1;
    private EditText editTextPhone2;
    private EditText editTextPhone3;

    private EditText editTextVerification;
    private Button buttonPhoneNumber;
    private Button buttonVerification;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_number_verification);


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        editTextPhone1 = (EditText) findViewById(R.id.edittext_phone_num_1);
        editTextPhone2 = (EditText) findViewById(R.id.edittext_phone_num_2);
        editTextPhone3 = (EditText) findViewById(R.id.edittext_phone_num_3);


        editTextVerification = (EditText) findViewById(R.id.edittext_verification_num);
        buttonPhoneNumber = (Button) findViewById(R.id.button_send_phone_number);
        buttonVerification = (Button) findViewById(R.id.button_verify);

        if (buttonPhoneNumber != null) {
            addButtonPhoneNumberClickListener();
        }

        if (buttonVerification != null) {
            addButtonVerificationListener();
        }
    }

    private void addButtonPhoneNumberClickListener() {
        buttonPhoneNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String phoneNumber = editTextPhone1.getText().toString() + editTextPhone2.getText().toString() + editTextPhone3.getText().toString();

                if (!editTextPhone1.getText().toString().equals("010") || editTextPhone2.getText().length() < 3 || editTextPhone3.getText().length() != 4) {

                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(PhoneNumberVerification.this);
                    alertDialog.setMessage(R.string.message_phone_number_wrong_digit).setPositiveButton(R.string.ok,
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            });
                    AlertDialog alert = alertDialog.create();
                    alert.show();
                    return;
                }

                buttonVerification.setEnabled(true);
                editTextVerification.setEnabled(true);
            }
        });
    }

    private void addButtonVerificationListener() {
        buttonVerification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JoinUserInfo joinUserInfo = JoinUserInfo.getInstance();
                joinUserInfo.setPhone(editTextPhone1.getText().toString() + editTextPhone2.getText().toString() + editTextPhone3.getText().toString());

                Gson gson = new GsonBuilder()
                        .setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ")
                        .create();
                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl(Constants.appServerHost)
                        .addConverterFactory(GsonConverterFactory.create(gson))
                        .build();

                RestApi apiService = retrofit.create(RestApi.class);

                HashMap<String, String> params = new HashMap<String, String>();

                final JoinUserInfo userInfo = JoinUserInfo.getInstance();
                params.put("name", userInfo.getName());
                params.put("email", userInfo.getEmail());
                params.put("password", userInfo.getPassword());
                params.put("receiveEmail", Boolean.toString(userInfo.receiveEmail()));
                params.put("birthDate", userInfo.getBirthDate());
                params.put("gender", userInfo.getGender());
                params.put("phoneNumber", userInfo.getPhone());
                params.put("facebookAccount", Boolean.toString(userInfo.isFacebookAccount()));
                params.put("facebookId", "unknown");

                Call<AddUserResponse> call = apiService.addUser(params);
                call.enqueue(new Callback<AddUserResponse>() {
                    @Override
                    public void onResponse(Call<AddUserResponse> call, Response<AddUserResponse> response) {
                        if (response.body().code == 200) {
                            DBHelper dbHelper = new DBHelper(PhoneNumberVerification.this);
                            dbHelper.insertUser(userInfo.getName(), response.body().uniqueId, userInfo.getEmail(),userInfo.getPhone());
                            userInfo.setUniqueId(response.body().uniqueId);
                            Constants.getInstance().setUserId(userInfo.getEmail());
                            Intent intent  = new Intent(PhoneNumberVerification.this, NavigationMenu.class);
                            startActivity(intent);
                            finish();
                        }
                    }

                    @Override
                    public void onFailure(Call<AddUserResponse> call, Throwable t) {
                        System.out.println("Add user error " + t.getMessage());
                    }
                });

            }
        });
    }
}
