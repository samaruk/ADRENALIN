package bd.com.ADRENALIN.pojo;

/**
 * Created by mahfuz on 7/4/17.
 */

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Book implements Parcelable {


    @SerializedName("Name")
    @Expose
    private String name;

    @SerializedName("Subject")
    @Expose
    private String subject;

    public final static Parcelable.Creator<Book> CREATOR = new Creator<Book>() {
        @SuppressWarnings({"unchecked"})
        public Book createFromParcel(Parcel in) {
            Book instance = new Book();

            instance.name = ((String) in.readValue((String.class.getClassLoader())));
            instance.subject = ((String) in.readValue((String.class.getClassLoader())));
            return instance;
        }

        public Book[] newArray(int size) {
            return (new Book[size]);
        }

    };

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Book withName(String name) {
        this.name = name;
        return this;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(name);
        dest.writeValue(subject);
    }

    public int describeContents() {
        return 0;
    }

    @Override
    public String toString() {
        return "Book{" +
                "name='" + name + '\'' +
                ", subject='" + subject + '\'' +
                '}';
    }
}