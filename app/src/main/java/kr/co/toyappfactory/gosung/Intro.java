package kr.co.toyappfactory.gosung;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

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
import com.kakao.auth.AuthType;
import com.kakao.auth.ISessionCallback;
import com.kakao.network.ErrorResult;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.MeResponseCallback;
import com.kakao.usermgmt.response.model.UserProfile;
import com.kakao.util.exception.KakaoException;

import org.json.JSONObject;

import java.security.MessageDigest;
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

public class Intro extends AppCompatActivity {
    private CallbackManager callbackManager;
    private LoginButton facebookLoginButton;
    private Button buttonFB;
    private SessionCallback mKakaocallback;

    private String userName;
    private String userId;
    private String profileUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);

        getSupportActionBar().hide();

        buttonFB = (Button)findViewById(R.id.button_facebook_login);

        getAppKeyHash();
        addFBLoginButton();
        addEmailLoginButton();

        Button buttonKakao = (Button)findViewById(R.id.button_kakao_login);
        Button buttonFindPassword = (Button)findViewById(R.id.button_find_password);
        Button buttonJoin = (Button)findViewById(R.id.button_join);

        if(buttonKakao != null){
            buttonKakao.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    KakaoLogin();
                }
            });
        }

        if(buttonFindPassword != null){
            buttonFindPassword.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Intro.this, FindPassword.class);
                    startActivity(intent);

                }
            });
        }

        if(buttonJoin != null){
            buttonJoin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Intro.this, Join.class);
                    startActivity(intent);
                }
            });
        }
    }

    public void addEmailLoginButton(){
        Button buttonEmailLogin = (Button)findViewById(R.id.button_email_login);
        if(buttonEmailLogin != null){
            buttonEmailLogin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Intro.this, Login.class);
                    startActivity(intent);

                }
            });
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int responseCode,
                                    Intent data) {
        super.onActivityResult(requestCode, responseCode, data);
        callbackManager.onActivityResult(requestCode, responseCode, data);
    }

    public void onClick(View v) {
        if (v == buttonFB) {
            facebookLoginButton.performClick();
        }
    }
    private void addFBLoginButton(){
        callbackManager = CallbackManager.Factory.create();

        facebookLoginButton = (LoginButton)findViewById(R.id.fblogin_button);
        ArrayList<String> permissionList = new ArrayList<String>();
        permissionList.add("email");
        permissionList.add("public_profile");
        facebookLoginButton.setReadPermissions(permissionList);

        AccessTokenTracker accessTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken currentAccessToken) {
                if(currentAccessToken != null){
                    System.out.println("I'm logged in");
                }else{
                    System.out.println("You have to login");

                }
            }
        };

        if(AccessToken.getCurrentAccessToken() != null){
            System.out.println("Get Acc Tok: I'm logged in");
            DBHelper dbHelper = new DBHelper(Intro.this);
            dbHelper.updateLoggedIn(1, 1);
            ArrayList<String> list = dbHelper.getAllUsers();
            Intent intent = new Intent(this, NavigationMenu.class);
            startActivity(intent);
            finish();

        }else{
            System.out.println("Get Acc Tok: You must log in");

        }

        facebookLoginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                System.out.println("Login success");
                GraphRequest request = GraphRequest.newMeRequest(
                        loginResult.getAccessToken(),
                        new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(JSONObject object, GraphResponse response) {
                                Log.v("LoginActivity", response.toString());

                                try {
                                    String email = object.getString("email");
                                    String facebookId = object.getString("id");
                                    String name = object.getString("name");
                                    name = name.replace(" ", "");
                                    String gender = object.getString("gender");
                                    System.out.println("GRAPH REQ SUCC");

                                    JoinUserInfo joinUserInfo = JoinUserInfo.getInstance();
                                    joinUserInfo.setName(name);
                                    joinUserInfo.setEmail(email);
                                    joinUserInfo.setGender(gender);
                                    joinUserInfo.setPhone("unknown");
                                    joinUserInfo.setBirthDate("unknown");
                                    joinUserInfo.setIsFacebookAccount(true).setFacebookId(facebookId);

                                    Constants.getInstance().setUserId(email);

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
                                    params.put("password", "unknown");
                                    params.put("receiveEmail", Boolean.toString(userInfo.receiveEmail()));
                                    params.put("birthDate", userInfo.getBirthDate());
                                    params.put("gender", userInfo.getGender());
                                    params.put("phoneNumber", userInfo.getPhone());
                                    params.put("facebookAccount", Boolean.toString(userInfo.isFacebookAccount()));
                                    params.put("facebookId", facebookId);

                                    Call<LoginResponse> call = apiService.facebookLogin(params);
                                    call.enqueue(new Callback<LoginResponse>() {
                                        @Override
                                        public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                                            DBHelper dbHelper = new DBHelper(Intro.this);
                                            dbHelper.insertUser(userInfo.getName(), response.body().uniqueId, userInfo.getEmail(), userInfo.getPhone());
                                            userInfo.setUniqueId(response.body().uniqueId).setBrandStar(response.body().brandStar);
                                            Intent intent = new Intent(Intro.this, NavigationMenu.class);
                                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                            startActivity(intent);
                                            finish();
                                        }

                                        @Override
                                        public void onFailure(Call<LoginResponse> call, Throwable t) {
                                            System.out.println("Facebook login error " + t.getMessage());
                                        }
                                    });


                                } catch (org.json.JSONException e) {
                                    e.printStackTrace();
                                }
                                // Application code
                            }
                        });
                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,name,email,gender,birthday");
                request.setParameters(parameters);
                request.executeAsync();

            }

            @Override
            public void onCancel() {
                System.out.println("Login cancel");

            }

            @Override
            public void onError(FacebookException error) {
                System.out.println("Login fail");

            }
        });
    }

    private void KakaoLogin(){
        // 카카오 세션을 오픈한다
        mKakaocallback = new SessionCallback();
        com.kakao.auth.Session.getCurrentSession().addCallback(mKakaocallback);
        com.kakao.auth.Session.getCurrentSession().checkAndImplicitOpen();
        com.kakao.auth.Session.getCurrentSession().open(AuthType.KAKAO_TALK_EXCLUDE_NATIVE_LOGIN, Intro.this);
    }

    private class SessionCallback implements ISessionCallback {
        @Override
        public void onSessionOpened() {
            Log.d("TAG" , "세션 오픈됨");
            // 사용자 정보를 가져옴, 회원가입 미가입시 자동가입 시킴
            KakaorequestMe();
        }

        @Override
        public void onSessionOpenFailed(KakaoException exception) {
            if(exception != null) {
                Log.d("TAG" , exception.getMessage());
            }
        }
    }

    protected void KakaorequestMe() {
        UserManagement.requestMe(new MeResponseCallback() {
            @Override
            public void onFailure(ErrorResult errorResult) {
                int ErrorCode = errorResult.getErrorCode();
                int ClientErrorCode = -777;

                if (ErrorCode == ClientErrorCode) {
                    Toast.makeText(getApplicationContext(), "카카오톡 서버의 네트워크가 불안정합니다. 잠시 후 다시 시도해주세요.", Toast.LENGTH_SHORT).show();
                } else {
                    Log.d("TAG", "오류로 카카오로그인 실패 ");
                }
            }

            @Override
            public void onSessionClosed(ErrorResult errorResult) {
                Log.d("TAG", "오류로 카카오로그인 실패 ");
            }

            @Override
            public void onSuccess(UserProfile userProfile) {
                profileUrl = userProfile.getProfileImagePath();
                userId = String.valueOf(userProfile.getId());
                userName = userProfile.getNickname();


            }

            @Override
            public void onNotSignedUp() {
                // 자동가입이 아닐경우 동의창
            }
        });
    }

    private void getAppKeyHash() {
        try {
            PackageInfo info = getPackageManager().getPackageInfo(getPackageName(), PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md;
                md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                String something = new String(Base64.encode(md.digest(), 0));
                Log.d("Hash key", something);
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            Log.e("name not found", e.toString());
        }
    }


}
