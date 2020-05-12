package kr.co.toyappfactory.gosung;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Paint;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.HashMap;

import kr.co.toyappfactory.gosung.response.AddUserResponse;
import kr.co.toyappfactory.gosung.response.CheckEmailResponse;
import kr.co.toyappfactory.gosung.rest.RestApi;
import kr.co.toyappfactory.gosung.terms.PrivacyPolicy;
import kr.co.toyappfactory.gosung.terms.TermsOfUse;
import kr.co.toyappfactory.gosung.util.Constants;
import kr.co.toyappfactory.gosung.util.DBHelper;
import kr.co.toyappfactory.gosung.util.DialogBox;
import kr.co.toyappfactory.gosung.util.JoinUserInfo;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class Join extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        String strJoinMembership = getResources().getString(R.string.join_membership);
        getSupportActionBar().setTitle(strJoinMembership);

        Button buttonJoin = (Button)findViewById(R.id.button_join);

        if(buttonJoin != null){
            buttonJoin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(!checkJoinUserInfo()){

                    }else{
                        checkEmail(JoinUserInfo.getInstance().getEmail());
                    }
                }
            });

        }

        TextView textViewTOU = (TextView)findViewById(R.id.textview_join_termsofuse);
        TextView textViewPrivacy = (TextView)findViewById(R.id.textview_join_privacy);

        textViewTOU.setPaintFlags(textViewTOU.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        textViewTOU.setText(getResources().getString(R.string.termsofuse));
        textViewTOU.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Join.this, TermsOfUse.class);
                startActivity(intent);
            }
        });

        textViewPrivacy.setPaintFlags(textViewPrivacy.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        textViewPrivacy.setText(getResources().getString(R.string.privacy));
        textViewPrivacy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Join.this, PrivacyPolicy.class);
                startActivity(intent);
            }
        });

        final CheckBox checkBoxAgreeAll = (CheckBox) findViewById(R.id.checkbox_agree_all);
        final CheckBox checkBoxAgreeTOU = (CheckBox) findViewById(R.id.checkbox_terms_of_use_agree);
        final CheckBox checkBoxAgreePrivacy = (CheckBox) findViewById(R.id.checkbox_privacy);

        if (checkBoxAgreeAll != null) {
            checkBoxAgreeAll.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    checkBoxAgreeTOU.setChecked(checkBoxAgreeAll.isChecked());
                    checkBoxAgreePrivacy.setChecked(checkBoxAgreeAll.isChecked());
                }
            });
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.

        return true;
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

    private void checkEmail(String email){
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

        Call<CheckEmailResponse> call = apiService.checkEmail(params);
        call.enqueue(new Callback<CheckEmailResponse>() {
            @Override
            public void onResponse(Call<CheckEmailResponse> call, Response<CheckEmailResponse> response) {
                System.out.println("Response " + response.body().code);
                int resultCode = response.body().code;

                if(resultCode == 200){

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
                    params.put("phoneNumber", "unknown");
                    params.put("facebookAccount", Boolean.toString(userInfo.isFacebookAccount()));
                    params.put("facebookId", "unknown");

                    Call<AddUserResponse> addUserCall = apiService.addUser(params);
                    addUserCall.enqueue(new Callback<AddUserResponse>() {
                        @Override
                        public void onResponse(Call<AddUserResponse> call, Response<AddUserResponse> response) {
                            if (response.body().code == 200) {
                                DBHelper dbHelper = new DBHelper(Join.this);
                                dbHelper.insertUser(userInfo.getName(), response.body().uniqueId, userInfo.getEmail(),userInfo.getPhone());
                                userInfo.setUniqueId(response.body().uniqueId);
                                userInfo.setBrandStar(response.body().brandStar);
                                Constants.getInstance().setUserId(userInfo.getEmail());
                                Intent intent  = new Intent(Join.this, NavigationMenu.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                                finish();
                            }
                        }

                        @Override
                        public void onFailure(Call<AddUserResponse> call, Throwable t) {
                            System.out.println("Add user error " + t.getMessage());
                        }
                    });

                }else if(resultCode == 201){
                    DialogBox.show(Join.this, R.string.message_email_already_exists);
                }
            }

            @Override
            public void onFailure(Call<CheckEmailResponse> call, Throwable t) {

            }
        });

    }

    private boolean checkJoinUserInfo() {
        EditText editTextEmail = (EditText) findViewById(R.id.edittext_join_email);
        EditText editTextPassword = (EditText) findViewById(R.id.edittext_join_password);
        EditText editTextPasswordConfirm = (EditText) findViewById(R.id.edittext_join_password_confirm);
        EditText editTextBirthdate = (EditText) findViewById(R.id.edittext_join_birthdate);

        JoinUserInfo userInfo = JoinUserInfo.getInstance();

        String email = editTextEmail.getText().toString();
        String password = editTextPassword.getText().toString();
        String confirmPassword = editTextPasswordConfirm.getText().toString();
        String birthdate = editTextBirthdate.getText().toString();

        if (email.isEmpty()) {
            DialogBox.show(Join.this, R.string.message_email_empty);
            return false;
        }

        if(!email.contains("@") || !email.contains(".")){
            DialogBox.show(Join.this, R.string.message_email_wrong_format);
            return false;
        }

        if(password.isEmpty()){
            DialogBox.show(Join.this, R.string.message_password_must_enter);

            return false;
        }

        if(!confirmPassword.equals(password)){
            DialogBox.show(Join.this, R.string.message_password_not_match);
            return false;
        }

        if(password.length() < 8 || password.length() > 16){
            DialogBox.show(Join.this, R.string.message_password_length);
            return false;
        }

        if(birthdate.isEmpty()){
            DialogBox.show(Join.this, R.string.message_enter_birthdate);
            return false;
        }

        if(birthdate.length() < 8 || birthdate.length() > 8){
            DialogBox.show(Join.this, R.string.message_wrong_bithdate_format);
            return false;
        }


        //boolean receiveEmail = ((CheckBox) findViewById(R.id.checkbox_agree_email)).isChecked();

        RadioButton radioButtonMale = (RadioButton) findViewById(R.id.radiobutton_male);
        RadioButton radioButtonFemale = (RadioButton) findViewById(R.id.radiobutton_female);
        RadioButton radioButtonOther = (RadioButton) findViewById(R.id.radiobutton_other);

        boolean male = radioButtonMale.isChecked();
        boolean female = radioButtonFemale.isChecked();
        boolean other = radioButtonOther.isChecked();

        boolean agreeTermsOfUse = ((CheckBox) findViewById(R.id.checkbox_terms_of_use_agree)).isChecked();
        boolean agreePrivacyPolicy = ((CheckBox) findViewById(R.id.checkbox_privacy)).isChecked();

        String gender = "";
        if (male) {
            gender = "male";
        } else if (female) {
            gender = "female";
        } else if(other){
            gender = "other";
        }else{
            DialogBox.show(Join.this, R.string.message_select_gender);
            return false;
        }

        if(!agreeTermsOfUse){
            DialogBox.show(Join.this, R.string.message_agree_TOU);
            return false;
        }

        if(!agreePrivacyPolicy){
            DialogBox.show(Join.this, R.string.message_agree_privacy_policy);

            return false;
        }

        userInfo.setName(email.split("@")[0]).setEmail(email).setPassword(password)
                .setReceiveEmail(/*receiveEmail*/false).setBirthDate(birthdate).setGender(gender).setIsFacebookAccount(false).setFacebookId("unknown");

        return true;
    }
}
