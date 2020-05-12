package kr.co.toyappfactory.gosung.response;

import java.util.ArrayList;

/**
 * Created by chiduk on 2016. 9. 9..
 */
public class ReviewResponse {
    public String _id;
    public String date;
    public String uniqueId;
    public String userId;
    public String category;
    public String title;
    public String name;
    public String review;
    public ArrayList<String> images;
    public int like;
    public int report;
    public boolean deleted;
    public boolean hidden;
    public ArrayList<CommentResponse.Comment> comments;
    public boolean liked;
}
