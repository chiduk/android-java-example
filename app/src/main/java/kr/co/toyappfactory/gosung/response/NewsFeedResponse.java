package kr.co.toyappfactory.gosung.response;

import java.util.ArrayList;

/**
 * Created by chiduk on 2016. 8. 25..
 */
public class NewsFeedResponse {
    public String _id;
    public String date;
    public String uniqueId;
    public String userId;
    public String name;
    public String post;
    public ArrayList<String> hashTags;
    public ArrayList<String> images;
    public int like;
    public int report;
    public String deleted;
    public String hidden;
    public ArrayList<CommentResponse.Comment> comments;
    public boolean liked;
}
