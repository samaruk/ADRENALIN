package bd.com.ADRENALIN.pojo;

/**
 * Created by iqrasys on 8/18/2017.
 */

public class ResponseJson {
    public boolean IsError;
    public String Msg;
    public int Id;
    public boolean getIsError(){return IsError;};
    public String getMsg(){return Msg;};
    public void setIsError(boolean isError){this.IsError=isError;};
    public void setMsg(String msg){ this.Msg=msg;};
}
