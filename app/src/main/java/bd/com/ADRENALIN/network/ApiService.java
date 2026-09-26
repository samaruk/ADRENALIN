package bd.com.ADRENALIN.network;

import java.util.ArrayList;
import java.util.List;

import bd.com.ADRENALIN.pojo.ApiGenericResponse;
import bd.com.ADRENALIN.pojo.Archive;
import bd.com.ADRENALIN.pojo.ResponseModel.QuestionExplanation;
import bd.com.ADRENALIN.pojo.Routine;
import bd.com.ADRENALIN.pojo.AnswerSummary;
import bd.com.ADRENALIN.pojo.Book;
import bd.com.ADRENALIN.pojo.Exam;
import bd.com.ADRENALIN.pojo.ExamType;
import bd.com.ADRENALIN.pojo.Merit;
import bd.com.ADRENALIN.pojo.NotificationEventModel;
import bd.com.ADRENALIN.pojo.PasswordUpdate;
import bd.com.ADRENALIN.pojo.PostModel.bd.com.dvec.pojo.PostModel.AppReviewPostModel;
import bd.com.ADRENALIN.pojo.PostModel.bd.com.dvec.pojo.PostModel.DevicePostModel;
import bd.com.ADRENALIN.pojo.PostModel.bd.com.dvec.pojo.PostModel.ExamDiscussionPostModel;
import bd.com.ADRENALIN.pojo.PostModel.bd.com.dvec.pojo.PostModel.ExamUserStatus;
import bd.com.ADRENALIN.pojo.ResponseJson;
import bd.com.ADRENALIN.pojo.ResponseJsonList;
import bd.com.ADRENALIN.pojo.ResponseModel.ResponseJsonGeneric;
import bd.com.ADRENALIN.pojo.User;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by mahfuz on 7/1/2017.
 */

public interface ApiService {

    @POST("/User/LogIn")
    Call<User> userLogin(@Body User user);

    @POST("/User/Registration")
    Call<User> userRegistration(@Body User user);

    @POST("/User/SocialLogin")
    Call<User> userSocialLogin(@Body User user);

    @POST("/User/Update")
    Call<ApiGenericResponse> userUpdate(@Body User user);

    @POST("/User/ChangePassword ")
    Call<ApiGenericResponse> changePassword(@Body PasswordUpdate passwordUpdate);

    @POST("/User/ChangePasswordForSocialUser")
    Call<ApiGenericResponse> changePasswordForSocialUser(@Body PasswordUpdate passwordUpdate);

    @POST("/AppReview/Create")
    Call<ResponseJson> createAppReview(@Body AppReviewPostModel review);

    @POST("/ExamDiscussion/Create")
    Call<ResponseJson> createDiscussion(@Body ExamDiscussionPostModel discussion);

    @GET("ExamType/Get")
    Call<List<ExamType>> getExamType();
//GetByUser(Guid userId)
    @GET("ExamType/GetByUser")
    Call<List<ExamType>> getExamTypeByUser(@Query("userId") String UserId);
    @GET("Book/List")
    Call<List<Book>> getBookList(@Query("typeId") int typeId);

    @GET("AppReview/GetByUser")
    Call<ResponseJsonList<ArrayList<Object>>> getAppReview(@Query("UserId") String UserId,@Query("PageSize") int PageSize,@Query("PageNumber") int PageNumber);
    @GET("ExamDiscussion/GetByUser")
    Call<ResponseJsonList<ArrayList<Object>>> getExamDiscussion(@Query("UserId") String UserId,@Query("ExamId") int ExamId,@Query("ParentId") int ParentId,@Query("PageSize") int PageSize,@Query("PageNumber") int PageNumber);
    @GET("Lecture/List")
    Call<ResponseJsonList<ArrayList<Object>>> getLecture(@Query("UserId") String UserId,@Query("TypeId") int TypeId,@Query("PageSize") int PageSize,@Query("PageNumber") int PageNumber);

    @GET("Exam/GetAnswerSummery/{answerSummaryId}")
    Call<AnswerSummary> getAnswerSummery(@Path("answerSummaryId") int answerSummaryId);

    @GET("Exam/GetAnswers")
    Call<List<AnswerSummary>> getAnswerSummeryList(@Query("UserId") String userId, @Query("typeId") int typeId);

    @GET("Exam/Next")
    Call<List<Exam>> getNextExamByType(@Query("typeId") int typeId, @Query("userId") String userId); //using @Query param. Generates like this  http://gis.laconicsoft.com/Exam/Next?typeId=1&userId=f0946d90

    @GET("Exam/MeritList")
    Call<List<Merit>> getMeritList(@Query("typeId") int typeId, @Query("userId") String userId);

    @GET("Exam/GetArchive")
    Call<List<Archive>> getArchives(@Query("typeId") int typeId, @Query("userId") String userId);

    @GET("Routine/GetByExamType/{routineId}")
    Call<List<Routine>> getRoutineByExamType(@Path("routineId") int routineId);

    @GET("/Notice/Events")
    Call<List<String>> getNotices();

    @GET("/Advisor/List")
    Call<List<Integer>> getAdvisorList();

    @GET("/AboutUs/List")
    Call<List<Integer>> getAboutUsList();

    @GET("/Notice/Notification")
    Call<List<String>> getNotifications(@Query("UserId") String userId);

    @GET("CoursePlan/List")
    Call<List<String>> getCoursePlan(@Query("TypeId") int typeId);

    @GET("ExamDiscussion/Images")
    Call<ResponseJsonList<Integer>> GetCommentImageId(@Query("Id") int commentId);
    @GET("ExamDiscussion/DeleteFromApp")
    Call<ResponseJson> DeleteExamDiscussion(@Query("UserId") String UserId,@Query("id") int commentId);
    @GET("ExamDiscussion/DeleteImage")
    Call<ResponseJson> DeleteCommentImage(@Query("Id") int ImageId);
    //DeleteFromApp(Guid UserId,long id)
    @Multipart
    @POST("ExamDiscussion/AddFiles")
    Call<ResponseBody> postImage(@Part MultipartBody.Part Image, @Part("Identifier") RequestBody Identifier, @Part("UserId") RequestBody UserId);

    @GET("Question/GetExplanation")
    Call<ResponseJsonGeneric<QuestionExplanation>> getQuestionExplanation(@Query("Id") int QuestionId);

    ///Here Exam is mistake. It will be LectureId
    @GET("Lecture/GetDetail")
    Call<ResponseJsonGeneric<QuestionExplanation>> getLectureDetail(@Query("ExamId") int LectureId);//Here Exam is mistake. It will be LectureId

    @POST("Device/Add")
    Call<ResponseJson> AddDevice(@Body DevicePostModel model);
    @GET("Notification/GetById")
    Call<ResponseJsonGeneric<NotificationEventModel>> getNotification(@Query("UserId") String UserId, @Query("Id") int NotificationId);
    //http://deshnow.com/Home/SetLog?DeviceId=Testing&Message=Testing
    @POST("Home/SetLog")
    Call<ResponseJson> SendErrorLog(@Query("DeviceId") String DeviceId, @Query("Message") String Message);


    ///ExamArea/ExamUserStatus/Create
    @Headers("Content-Type: application/json")
    @POST("/ExamArea/ExamUserStatus/Create")
    Call<ResponseJson> sendExamUserStatus(@Body ExamUserStatus status); //The return type isn't exactly an Exam object.


}
