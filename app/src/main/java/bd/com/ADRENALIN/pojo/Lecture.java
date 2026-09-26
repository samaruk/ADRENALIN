package bd.com.ADRENALIN.pojo;

import android.util.Log;

import java.util.ArrayList;

import bd.com.ADRENALIN.pojo.ResponseModel.QuestionExplanation;

/**
 * Created by iqrasys on 9/8/2017.
 */

public class Lecture {
    public int Id;
    public String Title;
    public String Content;
    public String PublishedAt;
    public QuestionExplanation Details;
    public boolean IsOpened;
    public ArrayList<ImageModel> Images;

    public static Lecture get(ArrayList<Object> arr){
        Lecture review = new Lecture();
        try {
            review.Id=((Double)arr.get(0)).intValue();
            review.Content=(String) arr.get(1);
            review.PublishedAt=(String)arr.get(2);
            review.Title=(String)arr.get(3);
            review.Images=new ArrayList<ImageModel>();
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("SamarukWebSocket",e.getMessage());
        }
        return review;
    }
}
