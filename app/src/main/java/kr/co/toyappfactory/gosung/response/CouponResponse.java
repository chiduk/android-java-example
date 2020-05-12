package kr.co.toyappfactory.gosung.response;

import java.util.ArrayList;

/**
 * Created by chiduk on 2016. 9. 1..
 */
public class CouponResponse {

    public ArrayList<Coupon> allCouponList;
    public ArrayList<IssuedCoupon> issuedCouponList;

    public class Coupon{
        public String _id;
        public String provider;
        public String name;
        public String image;
        public String startDate;
        public String endDate;
        public String stores;
        public String info;
    }

    public class IssuedCoupon{
        public String _id;
        public String userId;
        public String couponId;
        public String issuedDate;
        public String startDate;
        public String endDate;
        public String couponName;
        public String couponImage;
    }
}
