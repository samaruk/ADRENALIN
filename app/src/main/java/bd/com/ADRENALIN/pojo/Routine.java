package bd.com.ADRENALIN.pojo;

/**
 * Created by mahfuz on 7/4/17.
 */

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Routine implements Parcelable {

    @SerializedName("Name")
    @Expose
    private String name;
    @SerializedName("ExamDate")
    @Expose
    private String examDate;
    @SerializedName("Status")
    @Expose
    private String status;
    @SerializedName("Content")
    @Expose
    private String content;

    public final static Parcelable.Creator<Routine> CREATOR = new Creator<Routine>() {
        @SuppressWarnings({"unchecked"})
        public Routine createFromParcel(Parcel in) {
            Routine instance = new Routine();
            instance.name = ((String) in.readValue((String.class.getClassLoader())));
            instance.examDate = ((String) in.readValue((String.class.getClassLoader())));
            instance.status = ((String) in.readValue((String.class.getClassLoader())));
            instance.content = ((String) in.readValue((String.class.getClassLoader())));
            return instance;
        }

        public Routine[] newArray(int size) {
            return (new Routine[size]);
        }

    };

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getExamDate() {
        return examDate;
    }

    public void setExamDate(String examDate) {
        this.examDate = examDate;
    }

    public Routine withExamDate(String examDate) {
        this.examDate = examDate;
        return this;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Routine withStatus(String status) {
        this.status = status;
        return this;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Routine withSyllabus(String syllabus) {
        this.content = syllabus;
        return this;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(examDate);
        dest.writeValue(status);
        dest.writeValue(content);
    }

    public int describeContents() {
        return 0;
    }

    @Override
    public String toString() {
        return "Routine{" +
                "name='" + name + '\'' +
                ", examDate='" + examDate + '\'' +
                ", status='" + status + '\'' +
                ", content='" + content + '\'' +
                '}';
    }
}

