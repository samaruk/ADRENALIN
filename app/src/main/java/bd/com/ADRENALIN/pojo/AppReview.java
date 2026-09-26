package bd.com.ADRENALIN.pojo;

/**
 * Created by iqrasys on 8/18/2017.
 */

import com.google.gson.JsonArray;

import java.util.ArrayList;

public class AppReview  {

    public int Id;
    public String Content;
    public String CreatedAt;
    public String CreatedBy;
    public int LikeCount;
    public boolean IsActive;
    public boolean IsOnwer;

    public int getId() {
        return Id;
    }
    public String getContent() {
        return Content;
    }
    public String getCreatedAt() {
        return CreatedAt;
    }
    public String getCreatedBy() { return CreatedBy; }
    public int getLikeCount() {
        return LikeCount;
    }
    public boolean getIsActive() {
        return IsActive;
    }
    public void setId(int Id) {
        this.Id=Id;
    }
    public void setContent(String content) {
        this.Content= content;
    }
    public void setCreatedAt(String createdAt) {
        this.CreatedAt=createdAt;
    }
    public void setCreatedBy(String createdBy) { this.CreatedBy=createdBy; }
    public void setLikeCount(int likeCount) {
        this.LikeCount=likeCount;
    }
    public void setIsActive(boolean isActive) {
        this.IsActive=isActive;
    }


    public int describeContents() {
        return 0;
    }

    @Override
    public String toString() {
        return "AppReview{" +
                "Id='" + Id + '\'' +
                ", Content='" + Content + '\'' +
                ", CreatedAt='" + CreatedAt + '\'' +
                ", CreatedBy='" + CreatedBy + '\'' +
                ", LikeCount='" + LikeCount + '\'' +
                ", IsActive='" + IsActive + '\'' +
                '}';
    }
    public static AppReview get(ArrayList<Object> arr){
        AppReview review = new AppReview();
        review.Id=((Double)arr.get(0)).intValue();
        review.Content=(String) arr.get(1);
        review.CreatedAt=(String)arr.get(2);
        review.CreatedBy=(String)arr.get(3);
        review.LikeCount=((Double)arr.get(4)).intValue();
        review.IsActive=(Double)arr.get(5)==1?true:false;
        review.IsOnwer =(Double)arr.get(6)==1?true:false;

        return review;
    }
    public static ArrayList<AppReview> getList(ArrayList<JsonArray> arr){
        ArrayList<AppReview> list=new ArrayList<AppReview>(arr.size());
        return list;
    }
}
