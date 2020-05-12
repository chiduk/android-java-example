package kr.co.toyappfactory.gosung.util;

import java.util.ArrayList;

import kr.co.toyappfactory.gosung.response.LikeAndDislikeCount;
import retrofit2.Response;

/**
 * Created by chiduk on 2016. 7. 14..
 */
public class Ranker {
    public static ArrayList<LikeAndDislikeCount> getRanking(Response<ArrayList<LikeAndDislikeCount>> response){
        ArrayList<LikeAndDislikeCount> responseList = response.body();
        ArrayList<ArrayList<LikeAndDislikeCount>> orderedList = new ArrayList<ArrayList<LikeAndDislikeCount>>();


        ArrayList<LikeAndDislikeCount> sameCountList = new ArrayList<LikeAndDislikeCount>();
        LikeAndDislikeCount count = new LikeAndDislikeCount();
        count.name = responseList.get(0).name;
        count.likeCount = responseList.get(0).likeCount;
        count.brandId = responseList.get(0).brandId;
        sameCountList.add(count);

        int likeCount = count.likeCount;

        if(responseList.size() == 1){
            orderedList.add(sameCountList);
        }else{

            for(int position = 1; position < responseList.size(); position++ ){
                if(likeCount > responseList.get(position).likeCount){
                    orderedList.add(sameCountList);
                    likeCount = responseList.get(position).likeCount;
                    sameCountList = new ArrayList<LikeAndDislikeCount>();
                    LikeAndDislikeCount newObj = new LikeAndDislikeCount();
                    newObj.name = responseList.get(position).name;
                    newObj.likeCount = responseList.get(position).likeCount;
                    newObj.brandId = responseList.get(position).brandId;
                    sameCountList.add(newObj);
                    //orderedList.add(sameCountList);

                }else{
                    LikeAndDislikeCount newObj = new LikeAndDislikeCount();
                    newObj.name = responseList.get(position).name;
                    newObj.likeCount = responseList.get(position).likeCount;
                    newObj.brandId = responseList.get(position).brandId;
                    sameCountList.add(newObj);
                }

                if(position + 1 == responseList.size()){
                    orderedList.add(sameCountList);
                }
            }

        }

        ArrayList<LikeAndDislikeCount> finalList = new ArrayList<LikeAndDislikeCount>();

        int rank = 1;
        for(ArrayList<LikeAndDislikeCount> elemList: orderedList){
            for(LikeAndDislikeCount elem : elemList){
                elem.rank = rank;
                finalList.add(elem);
            }

            ++rank;
        }

        return finalList;
    }
}
