package bd.com.ADRENALIN.network;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;
import java.util.concurrent.TimeUnit;

import bd.com.ADRENALIN.BuildConfig;
import bd.com.ADRENALIN.pojo.Exam;
import bd.com.ADRENALIN.pojo.Question;
import bd.com.ADRENALIN.pojo.ResponseJson;
import bd.com.ADRENALIN.util.ExamDeserializerForAllAnswers;
import bd.com.ADRENALIN.util.QuestionDeserializer;
import bd.com.ADRENALIN.util.QuestionSerializer;
import bd.com.ADRENALIN.util.TestDeserialiser;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by mahfuz on 7/1/2017.
 */

public class RetrofitClient {

    //http://www.docxambd.com/
//    public static final String BASE_URL = "http://gis.laconicsoft.com/";
    //public static final String BASE_URL = "http://docxambd.com/";
    /** Server address; set in app/build.gradle (a test build may pass -PbaseUrl=http://10.0.2.2:5094/). */
    public static final String BASE_URL = BuildConfig.BASE_URL;
    public static final String BASE_URL_MOCKY = "http://www.mocky.io/v2/";

    public static final String QUESTION_SERIALIZER = "QUESTION_SERIALIZER";
    public static final String EXAM_DESERIALIZER = "EXAM_DESERIALIZER";
    public static final String EXAM_DESERIALIZER_FOR_ALL_ANSWERS = "EXAM_DESERIALIZER_FOR_ALL_ANSWERS";


    public static final String ADVISOR_IMG_URL = BASE_URL + "Advisor/Picture/";
    public static final String ABOUT_US_IMG_URL = BASE_URL + "AboutUs/Picture/";

    public static String getPaymantUrl(String userId, int examId) {
        return BASE_URL + "Payment/Service?version=1&UserId=" + userId + "&ExamId=" + examId;
    }


    private static Retrofit retrofit = null;

    private static OkHttpClient getOkHttpClient(Context context) {
        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .connectTimeout(50, TimeUnit.MINUTES)
                .writeTimeout(50, TimeUnit.MINUTES)
                .readTimeout(50, TimeUnit.MINUTES);
        if (BuildConfig.DEBUG) {
            HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();
            httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            builder.addInterceptor(httpLoggingInterceptor);
        }
        builder.addInterceptor(new ConnectivityInterceptor(context));
        return builder.build();
    }

    private static Retrofit getClient(Context context, String baseUrl) {
        if (retrofit == null) {
            Gson gson = new GsonBuilder().setLenient().setPrettyPrinting().create();
            retrofit = new Retrofit.Builder()
                    .baseUrl(baseUrl)
                    .client(getOkHttpClient(context))
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build();
        }
        return retrofit;
    }
    /** Gson configured with one of the custom (de)serializers, for local JSON work (exam drafts). */
    public static Gson getGson(String typeAdapter) {
        GsonBuilder gsonBuilder = new GsonBuilder().setLenient();
        if (typeAdapter.equals(QUESTION_SERIALIZER)) {
            gsonBuilder.registerTypeAdapter(Exam.class, new QuestionSerializer());
        } else if (typeAdapter.equals(EXAM_DESERIALIZER)) {
            Type listType = new TypeToken<List<Question>>() {
            }.getType();
            gsonBuilder.registerTypeAdapter(listType, new QuestionDeserializer());
        } else if (typeAdapter.equals(EXAM_DESERIALIZER_FOR_ALL_ANSWERS)) {
            gsonBuilder.registerTypeAdapter(Exam.class, new ExamDeserializerForAllAnswers());
        }
        return gsonBuilder.create();
    }
    private static Retrofit getClientExamQuestion(Context context, String baseUrl, String typeAdapter) {

        GsonBuilder gsonBuilder = new GsonBuilder().setLenient().setPrettyPrinting();

        if (typeAdapter.equals(QUESTION_SERIALIZER)) {
            gsonBuilder.registerTypeAdapter(Exam.class, new QuestionSerializer());
        } else if (typeAdapter.equals(EXAM_DESERIALIZER)) {
            Type listType = new TypeToken<List<Question>>() {
            }.getType();
            gsonBuilder.registerTypeAdapter(listType, new QuestionDeserializer());
        } else if (typeAdapter.equals(EXAM_DESERIALIZER_FOR_ALL_ANSWERS)) {
            gsonBuilder.registerTypeAdapter(Exam.class, new ExamDeserializerForAllAnswers());
        }

        Gson gson = gsonBuilder.create();

        return new Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(getOkHttpClient(context))
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
    }
    private static Retrofit getTestClient(Context context, String baseUrl) {
        Retrofit model=null;
        try {
            GsonBuilder gsonBuilder = new GsonBuilder().setLenient().setPrettyPrinting();
            Type listType = new TypeToken<ResponseJson>() {
            }.getType();
            gsonBuilder.registerTypeAdapter(listType, new TestDeserialiser());
            Gson gson = gsonBuilder.create();
            model=new Retrofit.Builder()
                    .baseUrl(baseUrl)
                    .client(getOkHttpClient(context))
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build();
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("SamarukWebSocket", "getTestClient  "+e.getMessage() );
        }
        return model;
    }
    public static ApiService getApiService(Context context) {
        return RetrofitClient.getClient(context, BASE_URL)
                .create(ApiService.class);
    }
    public static ApiServiceExamQuestion getApiServiceQuestion(Context context, String typeAdapter) {
        return RetrofitClient
                .getClientExamQuestion(context, BASE_URL, typeAdapter)
                .create(ApiServiceExamQuestion.class);
    }
    public static ApiService ApiServiceTest(Context context) {
        return RetrofitClient
                .getTestClient(context, BASE_URL)
                .create(ApiService.class);
    }
}
