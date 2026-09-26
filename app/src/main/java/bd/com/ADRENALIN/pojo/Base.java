package bd.com.ADRENALIN.pojo;

/**
 * Created by mahfuz on 7/4/17.
 */

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public abstract class Base {

    @SerializedName("Msg")
    @Expose
    private String msg;
    @SerializedName("IsError")
    @Expose
    private boolean isError;

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public boolean isIsError() {
        return isError;
    }

    public void setIsError(boolean isError) {
        this.isError = isError;
    }

    @Override
    public String toString() {
        return "Base{" +
                "msg='" + msg + '\'' +
                ", isError=" + isError +
                '}';
    }
}