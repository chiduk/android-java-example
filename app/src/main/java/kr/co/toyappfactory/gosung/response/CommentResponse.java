package kr.co.toyappfactory.gosung.response;

import java.util.ArrayList;

/**
 * Created by chiduk on 2016. 8. 25..
 */
public class CommentResponse {

    public int size;
    public ArrayList<Comment> commentList;

    public class Comment {
        public String _id;
        public String userId;
        public String uniqueId;
        public String name;
        public String date;
        public String comment;

    }
}
