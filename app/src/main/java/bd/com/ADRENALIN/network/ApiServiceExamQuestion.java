package bd.com.ADRENALIN.network;

import java.util.List;

import bd.com.ADRENALIN.pojo.ApiGenericResponse;
import bd.com.ADRENALIN.pojo.Exam;
import bd.com.ADRENALIN.pojo.Question;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by mahfuz on 7/1/2017.
 */

public interface ApiServiceExamQuestion {

    @GET("Exam/Questions/{examId}")
    Call<List<Question>> getQuestions(@Path("examId") int examId,@Query("withExplanation") boolean withExplanation);

    @GET("Exam/GetAnswer/{answerId}")
    Call<Exam> getAnswersByExamId(@Path("answerId") int answerId);

    @Headers("Content-Type: application/json")
    @POST("Exam/PostAnswer")
    Call<ApiGenericResponse> sendPostAnswer(@Body Exam exam); //The return type isn't exactly an Exam object.
    // It actually returns the whole serialized json of Exam that we sent. But we can still get some
    // common property of Exam.
    @Headers("Content-Type: application/json")
    @POST("Exam/PostAnswerForBCS")
    Call<ApiGenericResponse> sendPostAnswerForBCS(@Body Exam exam); //The return type isn't exactly an Exam object.
    // It actually returns the whole serialized json of Exam that we sent. But we can still get some
    // common property of Exam.

}
