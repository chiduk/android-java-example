package kr.co.toyappfactory.gosung.util;

/**
 * Created by chiduk on 2016. 6. 7..
 */
public class JoinUserInfo {



    private String facebookId;
    private String name;
    private String email;
    private String password;
    private boolean receiveEmail;
    private String birthDate;
    private String gender;
    private boolean agreeTermsOfUse;
    private boolean agreePrivacyPolicy;
    private String uniqueId;
    private int brandStar;

    private boolean isFacebookAccount;

    private String phone;


    private static JoinUserInfo instance = null;

    public static JoinUserInfo getInstance(){
        if(instance == null){
            instance = new JoinUserInfo();
        }
        return instance;
    }

    public boolean agreePrivacyPolicy() {
        return agreePrivacyPolicy;
    }

    public JoinUserInfo setAgreePrivacyPolicy(boolean agreePrivacyPolicy) {
        this.agreePrivacyPolicy = agreePrivacyPolicy;
        return this;
    }

    public String getEmail() {
        return email;
    }

    public JoinUserInfo setEmail(String email) {
        this.email = email;
        return this;

    }

    public String getPassword() {
        return password;
    }

    public JoinUserInfo setPassword(String password) {
        this.password = password;
        return this;

    }

    public boolean receiveEmail() {
        return receiveEmail;
    }

    public JoinUserInfo setReceiveEmail(boolean receiveEmail) {
        this.receiveEmail = receiveEmail;
        return this;

    }

    public String getBirthDate() {
        return birthDate;
    }

    public JoinUserInfo setBirthDate(String birthDate) {
        this.birthDate = birthDate;
        return this;
    }

    public String getGender() {
        return gender;
    }

    public JoinUserInfo setGender(String gender) {
        this.gender = gender;
        return this;

    }

    public boolean agreeTermsOfUse() {
        return agreeTermsOfUse;
    }

    public JoinUserInfo setAgreeTermsOfUse(boolean agreeTermsOfUse) {
        this.agreeTermsOfUse = agreeTermsOfUse;
        return this;

    }

    public String getName() {
        return name;
    }

    public JoinUserInfo setName(String name) {
        this.name = name;
        return this;

    }

    public String getPhone() {
        return phone;
    }

    public JoinUserInfo setPhone(String phone) {
        this.phone = phone;
        return this;
    }

    public String getFacebookId() {
        return facebookId;
    }

    public void setFacebookId(String facebookId) {
        this.facebookId = facebookId;
    }

    public boolean isFacebookAccount() {
        return isFacebookAccount;
    }

    public JoinUserInfo setIsFacebookAccount(boolean isFacebookAccount) {
        this.isFacebookAccount = isFacebookAccount;

        return this;
    }

    public JoinUserInfo setUniqueId(String uniqueId){
        this.uniqueId = uniqueId;

        return this;
    }

    public String getUniqueId (){
        return this.uniqueId;
    }

    public JoinUserInfo setBrandStar(int star){
        brandStar = star;

        return this;
    }

    public int getBrandStar(){
        return brandStar;
    }
}
