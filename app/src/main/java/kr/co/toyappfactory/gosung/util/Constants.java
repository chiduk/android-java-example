package kr.co.toyappfactory.gosung.util;



import kr.co.toyappfactory.gosung.R;


/**
 * Created by chiduk on 2016. 5. 24..
 */
public class Constants {
    //version
    public static final String major = "0";
    public static final String minor = "2";
    public static final String patch = "0";

    public static final int FEED_MAX_LINE_COUNT = 3;

    //104.199.164.41
    //61.43.49.127
    //52.78.108.30 //aws appserver
    //
    //brandr2.net  //1.234.63.195
    public static String appServerHost = "http://localhost";
    public static String appServerPort = "39090";

    //catetory
    public static final int NUM_OF_CATEGORIES = 5;
    public static final int ALL = 0;
    public static final int ENTERTAINER = 1;
    public static final int ACTORS = 2;
    public static final int GIRL_IDOL = 3;
    public static final int BOY_IDOL = 4;
    public static final int COMMERCIAL_MODEL = 5;
    public static final int SINGER = 6;

    //Data type
    public static final int DATATYPE_DAILY = 1;
    public static final int DATATYPE_MONTHLY = 2;

    public static final int REFRESH_REVIEW = 2;


    private static Constants instance = null;
    private String userId = null;
    private int category =0;

    public static Integer categoryDrawables[] = { };

    public static Integer contentTemplateDrawables[] = {};

    public static Integer scoreCategoryDrawables[] = {};

    public static Integer naviMenuDrawables[] = {R.drawable.navi_01_home_btn, R.drawable.navi_02_nf_btn, R.drawable.navi_03_rv_btn, R.drawable.navi_04_rs_btn, R.drawable.navi_06_cp_btn, R.drawable.navi_05_ev_btn};



    public static Constants getInstance(){
        if(instance == null){
            instance = new Constants();
        }
        return instance;
    }

    public String getUserId(){
        return userId;
    }

    public void setUserId(String userId){
        this.userId = userId;
    }


    public void setCategory(int category){
        this.category = category;
    }

    public int getCategory(){
        return category;
    }



}
