package kr.co.toyappfactory.gosung.rest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import kr.co.toyappfactory.gosung.gcm.PushRegistrationInfo;
import kr.co.toyappfactory.gosung.response.AddUserResponse;
import kr.co.toyappfactory.gosung.response.BrandResponse;
import kr.co.toyappfactory.gosung.response.BrandStarResponse;
import kr.co.toyappfactory.gosung.response.ChangeNameResponse;
import kr.co.toyappfactory.gosung.response.ChangePasswordResponse;
import kr.co.toyappfactory.gosung.response.CheckEmailResponse;
import kr.co.toyappfactory.gosung.response.CommentResponse;
import kr.co.toyappfactory.gosung.response.CouponResponse;
import kr.co.toyappfactory.gosung.response.DailyLikeCountResponse;
import kr.co.toyappfactory.gosung.response.FindEmailResponse;
import kr.co.toyappfactory.gosung.response.GetReviewResponse;
import kr.co.toyappfactory.gosung.response.GetTempPasswordResponse;
import kr.co.toyappfactory.gosung.response.HashTagSearchResponse;
import kr.co.toyappfactory.gosung.response.IssuedCouponResponse;
import kr.co.toyappfactory.gosung.response.LikeAndDislikeCount;
import kr.co.toyappfactory.gosung.response.LikeBrandResponse;
import kr.co.toyappfactory.gosung.response.LikedResponse;
import kr.co.toyappfactory.gosung.response.LoginResponse;
import kr.co.toyappfactory.gosung.response.MonthlyLikeCountResponse;
import kr.co.toyappfactory.gosung.response.NewsFeedResponse;
import kr.co.toyappfactory.gosung.response.PostResponse;
import kr.co.toyappfactory.gosung.response.RefreshPostResponse;
import kr.co.toyappfactory.gosung.response.ReportResponse;
import kr.co.toyappfactory.gosung.response.ReviewResponse;
import kr.co.toyappfactory.gosung.response.SearchResponse;
import kr.co.toyappfactory.gosung.response.TopicResponse;
import kr.co.toyappfactory.gosung.response.UploadSSResponse;
import okhttp3.MultipartBody;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.PartMap;
import retrofit2.http.QueryMap;

/**
 * Created by chiduk on 2016. 5. 25..
 */
public interface RestApi {

    @GET("/likeBrand")
    Call<LikeBrandResponse> likeBrand(@QueryMap HashMap<String,String> params);

    @GET("/dislikeBrand")
    Call<ResponseBody> dislikeBrand(@QueryMap HashMap<String,String> params);

    @GET("/getLikeAndDislikeCount")
    Call<LikeAndDislikeCount> getLikeAndDislikeCount(@QueryMap HashMap<String,String> params);

    @GET("/getLikedBrands")
    Call<ArrayList<LikeAndDislikeCount>> getLikedBrands(@QueryMap HashMap<String, String> params);

    @GET("/getLikeRanking")
    Call<ArrayList<LikeAndDislikeCount>> getLikeRanking(@QueryMap HashMap<String,String> params);

    @GET("/getTop5Ranking")
    Call<ArrayList<LikeAndDislikeCount>> getTop5Ranking(@QueryMap HashMap<String,String> params);

    @GET("/checkEmail")
    Call<CheckEmailResponse> checkEmail(@QueryMap HashMap<String, String> params);

    @GET("/addUser")
    Call<AddUserResponse> addUser(@QueryMap HashMap<String, String> params);

    @GET("/login")
    Call<LoginResponse> login(@QueryMap HashMap<String, String> params);

    @GET("/facebookLogin")
    Call<LoginResponse> facebookLogin(@QueryMap HashMap<String, String> params);

    @GET("/changeName")
    Call<ChangeNameResponse> changeName(@QueryMap HashMap<String, String> params);

    @GET("/changePassword")
    Call<ChangePasswordResponse> changePassword(@QueryMap HashMap<String, String> params);

    @GET("/findEmail")
    Call<FindEmailResponse> findEmail(@QueryMap HashMap<String, String> params);

    @GET("/getTempPassword")
    Call<GetTempPasswordResponse> getTempPassword(@QueryMap HashMap<String, String> params);

    @GET("/getDailyLikeCount")
    Call<ArrayList<DailyLikeCountResponse>> getDailyLikeCount(@QueryMap HashMap<String, String> params);

    @GET("/getMonthlyLikeCount")
    Call<ArrayList<MonthlyLikeCountResponse>> getMonthlyLikeCount(@QueryMap HashMap<String, String> params);

    @GET("/getBrand")
    Call<ArrayList<BrandResponse>> getBrand(@QueryMap HashMap<String, String> params);

    @GET("/requestSearch")
    Call<ArrayList<SearchResponse>> requestSearch(@QueryMap HashMap<String, String> params);

    @GET("/getPost")
    Call<ArrayList<RefreshPostResponse>> refreshPost();

    @GET("/getReview")
    Call<ArrayList<ReviewResponse>> getReview(@QueryMap HashMap<String, String> params);

    @GET("/getReviewLessThan")
    Call<ArrayList<ReviewResponse>> getReviewLessThan(@QueryMap HashMap<String, String> params);

    @GET("/getReviewGreaterThan")
    Call<ArrayList<ReviewResponse>> getReviewGreaterThan(@QueryMap HashMap<String, String> params);

    @GET("/getMyPost")
    Call<ArrayList<RefreshPostResponse>> getMyPost(@QueryMap HashMap<String, String> params);

    @GET("/getMyReview")
    Call<ArrayList<GetReviewResponse>> getMyReview(@QueryMap HashMap<String, String> params);

    @GET("/reportPost")
    Call<ReportResponse> reportPost(@QueryMap Map<String, String> params);

    @GET("/getTopic")
    Call<ArrayList<TopicResponse>> getTopic();

    @GET("/getRecentComment")
    Call<CommentResponse> getRecentComment(@QueryMap HashMap<String, String> params);

    @GET("/getAllComment")
    Call<CommentResponse> getAllComment(@QueryMap HashMap<String, String> params);

    @GET("/getComment")
    Call<CommentResponse> getComment(@QueryMap HashMap<String, String> params);

    @GET("/getNewsFeed")
    Call<ArrayList<NewsFeedResponse>> getNewsFeed(@QueryMap HashMap<String, String> params);

    @GET("/getNewsFeedLessThan")
    Call<ArrayList<NewsFeedResponse>> getNewsFeedLessThan(@QueryMap HashMap<String, String> params);

    @GET("/getNewsFeedGreaterThan")
    Call<ArrayList<NewsFeedResponse>> getNewsFeedGreaterThan(@QueryMap HashMap<String, String> params);

    @GET("/getCoupon")
    Call<CouponResponse> getCoupon(@QueryMap HashMap<String, String> params);

    @GET("/issueCoupon")
    Call<ResponseBody> issueCoupon(@QueryMap HashMap<String, String> params);

    @GET("/checkCoupon")
    Call<ResponseBody> checkCoupon(@QueryMap HashMap<String, String> params);

    @GET("/getCurrentCoupon")
    Call<ArrayList<IssuedCouponResponse>> getCurrentCoupon(@QueryMap HashMap<String, String> params);

    @GET("/getUsedCoupon")
    Call<ArrayList<IssuedCouponResponse>> getUsedCoupon(@QueryMap HashMap<String, String> params);

    @GET("/getExpiredCoupon")
    Call<ArrayList<IssuedCouponResponse>> getExpiredCoupon(@QueryMap HashMap<String, String> params);

    @GET("/searchNewsFeedHashTag")
    Call<ArrayList<RefreshPostResponse>> searchNewsFeedHashTag(@QueryMap HashMap<String, String> params);

    @GET("/searchReviewHashTag")
    Call<ArrayList<GetReviewResponse>> searchReviewHashTag(@QueryMap HashMap<String, String> params);

    @GET("/getLikedNewsFeed")
    Call<LikedResponse> getLikedNewsFeed(@QueryMap HashMap<String, String> params);

    @GET("/getLikedReview")
    Call<LikedResponse> getLikedReview(@QueryMap HashMap<String, String> params);

    @GET("/getBrandStar")
    Call<BrandStarResponse> getBrandStar(@QueryMap HashMap<String, String> params);

    @POST("/likePost")
    @FormUrlEncoded
    //Call<ResponseBody> likePost(@Field("feedId") String feedId, @Field("userId") String userId);
    Call<ResponseBody> likePost(@FieldMap HashMap<String, String> params);

    @POST("/unlikePost")
    @FormUrlEncoded
    Call<ResponseBody> unlikePost(@Field("feedId") String feedId, @Field("userId") String userId);

    @POST("/likeReview")
    @FormUrlEncoded
    Call<ResponseBody> likeReview(@FieldMap HashMap<String, String> params);

    @POST("unlikeReview")
    @FormUrlEncoded
    Call<ResponseBody> unlikeReview(@Field("feedId") String feedId, @Field("userId") String userId);

    @POST("/uploadScreenShot")
    @Multipart
    Call<UploadSSResponse> uploadSS(@Part("userId") RequestBody userId, @Part MultipartBody.Part file);

    @POST("/uploadPost")
    @Multipart
    Call<PostResponse> uploadPost(@Part("uniqueId") RequestBody uniqueId, @Part("userId") RequestBody userId, @Part("name") RequestBody name, @Part("hashTags") ArrayList<String> hashTags, @Part("hashTagSize") int size, @Part("post") RequestBody post, @PartMap Map<String, RequestBody> images);

    @POST("/uploadReview")
    @Multipart
    Call<ResponseBody> uploadReview(@Part("uniqueId") RequestBody uniqueId, @Part("userId") RequestBody userId, @Part("category") RequestBody category, @Part("title") RequestBody title, @Part("name") RequestBody name,  @Part("hashTags") ArrayList<String> hashTags, @Part("hashTagSize") int size, @Part("review") RequestBody review, @PartMap Map<String, RequestBody> images);

    @POST("/updatePost")
    @FormUrlEncoded
    Call<PostResponse> updatePost(@Field("userId") String userId, @Field("feedId") String feedId, @Field("hashTags") ArrayList<String> hashTags, @Field("hashTagSize") int size, @Field("post") String post /*@PartMap Map<String, RequestBody> images*/);

    @POST("/deletePost")
    @FormUrlEncoded
    Call<ResponseBody> deletePost(@Field("userId") String userId, @Field("feedId") String feedId);

    @POST("/updateReview")
    @FormUrlEncoded
    Call<ResponseBody> updateReview(@Field("userId") String userId, @Field("feedId") String feedId, @Field("title") String title, @Field("category") String category ,  @Field("hashTags") ArrayList<String> hashTags, @Field("hashTagSize") int size, @Field("review") String review /*@PartMap Map<String, RequestBody> images*/);

    @POST("/deleteReview")
    @FormUrlEncoded
    Call<ResponseBody> deleteReview(@Field("userId") String userId, @Field("feedId") String feedId);


    @POST("/addComment")
    @FormUrlEncoded
    Call<ResponseBody> addComment(@FieldMap HashMap<String, String> params);

    @POST("/uploadProfile")
    @Multipart
    Call<ResponseBody> uploadProfilePic(@Part("uniqueId") RequestBody uniqueId, @Part MultipartBody.Part profilePic);

    @POST("/regId")
    Call<PushRegistrationInfo> postRegId(@Body PushRegistrationInfo info);

}
