package bd.com.ADRENALIN.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.gson.Gson;

import bd.com.ADRENALIN.pojo.Exam;
import bd.com.ADRENALIN.pojo.ExamDraft;
import bd.com.ADRENALIN.pojo.ExamType;
import bd.com.ADRENALIN.pojo.DeviceModel;
import bd.com.ADRENALIN.pojo.User;


public class PrefManager {

    SharedPreferences pref;
    SharedPreferences.Editor editor;
    Context _context;

    // shared pref mode
    int PRIVATE_MODE = 0;

    // Shared preferences file name
    private static final String PREF_NAME = "dvec";

    private static Gson gson;

    private static final String IS_FIRST_TIME_LAUNCH = "IS_FIRST_TIME_LAUNCH";
    private static final String USER_INFO = "USER_INFO";
    private static final String REMEMBER_ME = "REMEMBER_ME";
    private static final String EXAM_TYPE_SELECTED = "EXAM_TYPE_SELECTED";
    private static final String NEXT_EXAM_INFO = "NEXT_EXAM_INFO";
    private static final String IS_DEVICE_SET_INT = "IS_DEVICE_SET_INT";

    public PrefManager(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();

        gson = new Gson();
    }

    /**
     * Clear Shared Preference
     */
    public void clearPrefManager() {
        editor.clear().commit();
    }

    public void setFirstTimeLaunch(boolean isFirstTime) {
        editor.putBoolean(IS_FIRST_TIME_LAUNCH, isFirstTime).commit();
    }

    public boolean isFirstTimeLaunch() {
        return pref.getBoolean(IS_FIRST_TIME_LAUNCH, true);
    }

    public User getUserInfo() {
        String userJson = pref.getString(USER_INFO, null);
        if (userJson != null) {
            try {
                if(IsDeviceSet()<0){
                    DeviceModel.Save(_context);
                }
            } catch (Exception e) {
                e.printStackTrace();
                Log.e("SamarukWebSocket", "Preference  Error. "+e.getMessage() );
            }
            return gson.fromJson(userJson, User.class);
        }
        return null;
    }

    public void setUserInfo(User user) {
        editor.putString(USER_INFO, gson.toJson(user)).commit();
    }

    public ExamType getExamTypeSelected() {
        String examTypeJson = pref.getString(EXAM_TYPE_SELECTED, null);
        if (examTypeJson != null) return gson.fromJson(examTypeJson, ExamType.class);
        return null;
    }

    public void setExamTypeSelected(ExamType examType) {
        editor.putString(EXAM_TYPE_SELECTED, gson.toJson(examType)).commit();
    }

    public void setRememberMe(boolean rememberMe) {
        editor.putBoolean(REMEMBER_ME, rememberMe).commit();
    }

    public boolean isRememberMe() {
        return pref.getBoolean(REMEMBER_ME, false);
    }

    public void setIsDeviceSet(int rememberMe) {
        editor.putInt(IS_DEVICE_SET_INT, rememberMe).commit();
    }

    public int IsDeviceSet() {
        return pref.getInt(IS_DEVICE_SET_INT, 0);
    }

    public Exam getNexExamInfo() {
        String string = pref.getString(NEXT_EXAM_INFO, null);
        if (string != null) return gson.fromJson(string, Exam.class);

        return null;
    }

    public void setNextExamInfo(Exam nextExamInfo) {
        editor.putString(NEXT_EXAM_INFO, gson.toJson(nextExamInfo)).commit();
    }

    /* ---- Local exam drafts (resume after crash / offline) ---- */
    private static final String EXAM_DRAFT_PREFIX = "EXAM_DRAFT_";

    public static String examDraftKey(String userId, int examId, boolean reExam) {
        return EXAM_DRAFT_PREFIX + userId + "_" + examId + (reExam ? "_R" : "");
    }

    public ExamDraft getExamDraft(String userId, int examId, boolean reExam) {
        String string = pref.getString(examDraftKey(userId, examId, reExam), null);
        if (string == null) return null;
        try {
            return gson.fromJson(string, ExamDraft.class);
        } catch (Exception e) {
            return null;
        }
    }

    public void setExamDraft(String userId, int examId, boolean reExam, ExamDraft draft) {
        editor.putString(examDraftKey(userId, examId, reExam), gson.toJson(draft)).commit();
    }

    public void removeExamDraft(String userId, int examId, boolean reExam) {
        editor.remove(examDraftKey(userId, examId, reExam)).commit();
    }
}
