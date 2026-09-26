package bd.com.ADRENALIN.pojo;

/**
 * Created by mahfuz on 7/10/17.
 */

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PasswordUpdate implements Parcelable {

    @SerializedName("oldPassword")
    @Expose
    private String oldPassword;
    @SerializedName("newPassword")
    @Expose
    private String newPassword;
    @SerializedName("Id")
    @Expose
    private String id;
    public final static Parcelable.Creator<PasswordUpdate> CREATOR = new Creator<PasswordUpdate>() {
        @SuppressWarnings({"unchecked"})
        public PasswordUpdate createFromParcel(Parcel in) {
            PasswordUpdate instance = new PasswordUpdate();
            instance.oldPassword = ((String) in.readValue((String.class.getClassLoader())));
            instance.newPassword = ((String) in.readValue((String.class.getClassLoader())));
            instance.id = ((String) in.readValue((String.class.getClassLoader())));
            return instance;
        }

        public PasswordUpdate[] newArray(int size) {
            return (new PasswordUpdate[size]);
        }

    };

    public String getOldPassword() {
        return oldPassword;
    }

    public void setOldPassword(String oldPassword) {
        this.oldPassword = oldPassword;
    }

    public PasswordUpdate withOldPassword(String oldPassword) {
        this.oldPassword = oldPassword;
        return this;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    public PasswordUpdate withNewPassword(String newPassword) {
        this.newPassword = newPassword;
        return this;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public PasswordUpdate withId(String id) {
        this.id = id;
        return this;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(oldPassword);
        dest.writeValue(newPassword);
        dest.writeValue(id);
    }

    public int describeContents() {
        return 0;
    }

    @Override
    public String toString() {
        return "PaswordUpdate{" +
                "oldPassword='" + oldPassword + '\'' +
                ", newPassword='" + newPassword + '\'' +
                ", id='" + id + '\'' +
                '}';
    }
}