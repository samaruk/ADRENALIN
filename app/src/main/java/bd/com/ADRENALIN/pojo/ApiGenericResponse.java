package bd.com.ADRENALIN.pojo;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by mahfuz on 7/7/17.
 */

public class ApiGenericResponse implements Parcelable {

    @SerializedName("Id")
    @Expose
    private int id;
    @SerializedName("IsError")
    @Expose
    private boolean isError;
    @SerializedName("Msg")
    @Expose
    private String msg;
    /** True when the server stored the submission as a re-exam (a late first attempt after seeing the answers). */
    @SerializedName("IsReExam")
    private boolean storedAsReExam;

    public boolean isStoredAsReExam() {
        return storedAsReExam;
    }

    public final static Parcelable.Creator<ApiGenericResponse> CREATOR = new Creator<ApiGenericResponse>() {
        @SuppressWarnings({"unchecked"})
        public ApiGenericResponse createFromParcel(Parcel in) {
            ApiGenericResponse instance = new ApiGenericResponse();
            instance.id = ((int) in.readValue((int.class.getClassLoader())));
            instance.isError = ((boolean) in.readValue((boolean.class.getClassLoader())));
            instance.msg = ((String) in.readValue((String.class.getClassLoader())));
            return instance;
        }

        public ApiGenericResponse[] newArray(int size) {
            return (new ApiGenericResponse[size]);
        }

    };

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public ApiGenericResponse withId(int id) {
        this.id = id;
        return this;
    }

    public boolean isIsError() {
        return isError;
    }

    public void setIsError(boolean isError) {
        this.isError = isError;
    }

    public ApiGenericResponse withIsError(boolean isError) {
        this.isError = isError;
        return this;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public ApiGenericResponse withMsg(String msg) {
        this.msg = msg;
        return this;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(id);
        dest.writeValue(isError);
        dest.writeValue(msg);
    }

    public int describeContents() {
        return 0;
    }

    @Override
    public String toString() {
        return "ApiGenericResponse{" +
                "id=" + id +
                ", isError=" + isError +
                ", msg='" + msg + '\'' +
                '}';
    }
}