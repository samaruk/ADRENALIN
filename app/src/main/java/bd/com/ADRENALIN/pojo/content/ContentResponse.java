package bd.com.ADRENALIN.pojo.content;

/** Envelope of the content endpoints: IsError, Msg and Data. */
public class ContentResponse<T> {
    public boolean IsError;
    public String Msg;
    public T Data;
}
