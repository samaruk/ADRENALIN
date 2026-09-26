package bd.com.ADRENALIN.pojo;

/**
 * Created by mahfuz on 7/12/17.
 */

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Archive implements Parcelable {

    @SerializedName("CategoryId")
    @Expose
    private int categoryId;

    @SerializedName("Candidates")
    @Expose
    private int candidates;
    @SerializedName("TotalMarks")
    @Expose
    private double totalMarks;
    @SerializedName("MaxMarks")
    @Expose
    private double maxMarks;
    @SerializedName("IsPaid")
    @Expose
    private double isPaid;
    @SerializedName("MinMarks")
    @Expose
    private double minMarks;
    @SerializedName("Id")
    @Expose
    private int id; // This is actually Exam Id
    @SerializedName("Name")
    @Expose
    private String name;
    @SerializedName("Content")
    @Expose
    private String content;
    @SerializedName("TotalQuestion")
    @Expose
    private int totalQuestion;
    @SerializedName("D")
    @Expose
    private int d;
    @SerializedName("StartAt")
    @Expose
    private String startAt;
    @SerializedName("EndAt")
    @Expose
    private String endAt;
    @SerializedName("Phone")
    @Expose
    private String phone;

    public final static Parcelable.Creator<Archive> CREATOR = new Creator<Archive>() {
        @SuppressWarnings({"unchecked"})
        public Archive createFromParcel(Parcel in) {
            Archive instance = new Archive();
            instance.candidates = ((int) in.readValue((int.class.getClassLoader())));
            instance.totalMarks = ((int) in.readValue((int.class.getClassLoader())));
            instance.isPaid = ((int) in.readValue((int.class.getClassLoader())));
            instance.maxMarks = ((double) in.readValue((double.class.getClassLoader())));
            instance.minMarks = ((double) in.readValue((double.class.getClassLoader())));
            instance.id = ((int) in.readValue((int.class.getClassLoader())));
            instance.content = ((String) in.readValue((String.class.getClassLoader())));
            instance.totalQuestion = ((int) in.readValue((int.class.getClassLoader())));
            instance.d = ((int) in.readValue((int.class.getClassLoader())));
            instance.startAt = ((String) in.readValue((String.class.getClassLoader())));
            instance.endAt = ((String) in.readValue((String.class.getClassLoader())));
            instance.phone = ((String) in.readValue((String.class.getClassLoader())));
            instance.name = ((String) in.readValue((String.class.getClassLoader())));
            return instance;
        }

        public Archive[] newArray(int size) {
            return (new Archive[size]);
        }

    };

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }
    public int getCategoryId() {
        return categoryId;
    }
    public int getCandidates() {
        return candidates;
    }

    public void setCandidates(int candidates) {
        this.candidates = candidates;
    }

    public Archive withCandidates(int candidates) {
        this.candidates = candidates;
        return this;
    }

    public double getIsPaid() {
        return isPaid;
    }

    public void setIsPaid(double isPaid) {
        this.isPaid = isPaid;
    }

    public double getTotalMarks() {
        return totalMarks;
    }

    public void setTotalMarks(double totalMarks) {
        this.totalMarks = totalMarks;
    }

    public Archive withTotalMarks(int totalMarks) {
        this.totalMarks = totalMarks;
        return this;
    }

    public double getMaxMarks() {
        return maxMarks;
    }

    public void setMaxMarks(double maxMarks) {
        this.maxMarks = maxMarks;
    }

    public Archive withMaxMarks(double maxMarks) {
        this.maxMarks = maxMarks;
        return this;
    }

    public double getMinMarks() {
        return minMarks;
    }

    public void setMinMarks(double minMarks) {
        this.minMarks = minMarks;
    }

    public Archive withMinMarks(double minMarks) {
        this.minMarks = minMarks;
        return this;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Archive withId(int id) {
        this.id = id;
        return this;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Archive withName(String name) {
        this.name = name;
        return this;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Archive withContent(String content) {
        this.content = content;
        return this;
    }

    public int getTotalQuestion() {
        return totalQuestion;
    }

    public void setTotalQuestion(int totalQuestion) {
        this.totalQuestion = totalQuestion;
    }

    public Archive withTotalQuestion(int totalQuestion) {
        this.totalQuestion = totalQuestion;
        return this;
    }

    public int getD() {
        return d;
    }

    public void setD(int d) {
        this.d = d;
    }

    public Archive withD(int d) {
        this.d = d;
        return this;
    }

    public String getStartAt() {
        return startAt;
    }

    public void setStartAt(String startAt) {
        this.startAt = startAt;
    }

    public Archive withStartAt(String startAt) {
        this.startAt = startAt;
        return this;
    }

    public String getEndAt() {
        return endAt;
    }

    public void setEndAt(String endAt) {
        this.endAt = endAt;
    }

    public Archive withEndAt(String endAt) {
        this.endAt = endAt;
        return this;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Archive withPhone(String phone) {
        this.phone = phone;
        return this;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(candidates);
        dest.writeValue(totalMarks);
        dest.writeValue(isPaid);
        dest.writeValue(maxMarks);
        dest.writeValue(minMarks);
        dest.writeValue(id);
        dest.writeValue(content);
        dest.writeValue(totalQuestion);
        dest.writeValue(d);
        dest.writeValue(startAt);
        dest.writeValue(endAt);
        dest.writeValue(phone);
    }

    public int describeContents() {
        return 0;
    }

    @Override
    public String toString() {
        return "Archive{" +
                "candidates=" + candidates +
                ", totalMarks=" + totalMarks +
                ", maxMarks=" + maxMarks +
                ", isPaid=" + isPaid +
                ", minMarks=" + minMarks +
                ", id=" + id +
                ", name='" + name + '\'' +
                ", content='" + content + '\'' +
                ", totalQuestion=" + totalQuestion +
                ", d=" + d +
                ", startAt='" + startAt + '\'' +
                ", endAt='" + endAt + '\'' +
                ", phone='" + phone + '\'' +
                '}';
    }
}