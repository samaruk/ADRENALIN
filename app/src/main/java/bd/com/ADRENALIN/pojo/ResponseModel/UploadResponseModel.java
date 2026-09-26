package bd.com.ADRENALIN.pojo.ResponseModel;
import bd.com.ADRENALIN.pojo.ResponseJson;

/**
 * Created by iqrasys on 8/24/2017.
 * IsError = false, model.Image.FileName,model.Image.ContentLength,model.Image.ContentType
 */

public class UploadResponseModel extends ResponseJson {
    public String FileName;
    public  int ContentLength;
    public String ContentType;
}
