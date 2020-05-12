package kr.co.toyappfactory.gosung.gcm;

import com.google.gson.annotations.SerializedName;

/**
 * Created by chiduk on 2016. 10. 27..
 */

public class PushRegistrationInfo {
    @SerializedName("userId")
    String userId;

    @SerializedName("token")
    String token;

    @SerializedName("platform")
    String platform;


    public PushRegistrationInfo(String userId, String token, String platform){
        this.userId = userId;
        this.token = token;
        this.platform = platform;
    }
}
