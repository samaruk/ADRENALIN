package bd.com.ADRENALIN.util;

import android.util.Log;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;

import bd.com.ADRENALIN.pojo.ResponseJson;

/**
 * Created by iqrasys on 9/11/2017.
 */

public class TestDeserialiser implements JsonDeserializer<ResponseJson> {

    @Override
    public ResponseJson deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {


        Log.e("SamarukWebSocket", "TestDeserialiser "+json.toString() );

        return new ResponseJson();
    }
}
