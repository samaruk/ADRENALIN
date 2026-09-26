package bd.com.ADRENALIN.pojo;

import android.util.Log;

import java.util.ArrayList;

/**
 * Created by iqrasys on 8/19/2017.
 */

public class ExamDiscussion {
    public int Id;
    public String Content;
    public String CreatedAt;
    public String CreatedBy;
    public int ImageCount;
    public int LikeCount;
    public int ReplyCount;
    public boolean IsActive;
    public boolean IsOnwer;
    public ArrayList<ImageModel> Images;
    public ExamDiscussion(){
        Images=new ArrayList<ImageModel>();
    }
    public ExamDiscussion(int id,String txt,String at,String by, ArrayList<ImageModel> images){
        Id=id;
        Content=txt;
        CreatedAt=at;
        CreatedBy=by;
        Images=images;
        IsActive=true;
    }

    public static ExamDiscussion get(ArrayList<Object> arr){
        ExamDiscussion review = new ExamDiscussion();
        try {
            review.Id=((Double)arr.get(0)).intValue();
            review.Content=(String) arr.get(1);
            review.CreatedAt=(String)arr.get(2);
            review.CreatedBy=(String)arr.get(3);
            review.LikeCount=((Double)arr.get(4)).intValue();
            review.ImageCount=((Double)arr.get(5)).intValue();
            review.IsActive=(Double)arr.get(6)==1?true:false;
            review.ReplyCount=((Double)arr.get(7)).intValue();
            review.IsOnwer =(Double)arr.get(8)==1?true:false;
            review.Images=new ArrayList<ImageModel>();
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("SamarukWebSocket",e.getMessage());
        }
        return review;
    }
}
