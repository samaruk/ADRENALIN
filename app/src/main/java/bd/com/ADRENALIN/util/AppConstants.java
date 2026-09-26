package bd.com.ADRENALIN.util;


public class AppConstants {

    public static final int SPLASH_DURATION = 500;
    public static boolean SHOULD_RELOAD_NEXT_EXAM_INFO = false;

    public static final class ExamConstants {
        public static final String INTENT_CODE = "INTENT_CODE";
        public static final String INTENT_EXAM_ID = "INTENT_EXAM_ID";
        public static final String INTENT_COMMENT_ID = "INTENT_EXAM_ID";
        public static final String INTENT_ANSWER_ID = "INTENT_EXAM_ID";
        public static final String INTENT_EXAM_CATEGORY = "INTENT_EXAM_ANS_SUMMERY";
        public static final String INTENT_FROM_ARCHIVE = "INTENT_FROM_ARCHIVE";
    }
    public static final class NotificatioEvent {
        public static final String TYPE = "TYPE";
        public static final String COMMENT_ID = "COMMENT_ID";
        public static final String EVENT_ID = "EVENT_ID";
        public static final String EXAM_ID = "EXAM_ID";
        public static final String TYPE_ID = "TYPE_ID";
        public static final String TYPE_NAME = "TYPE_NAME";
    }
    public static final class ExamDiscussionConstants {
        public static final String EXAM_DISCUSSION_ID = "EXAM_DISCUSSION_ID";
        public static final String EXAM_DISCUSSION_IMAGE_POSITION = "EXAM_DISCUSSION_IMAGE_POSITION";
        public static final String EXAM_DISCUSSION_ALL_IMAGES = "EXAM_DISCUSSION_ALL_IMAGES";
        public static final String EXAM_DISCUSSION_IMAGE_URL = "EXAM_DISCUSSION_IMAGE_URL";
        public static final String EXAM_DISCUSSION_IMAGE_IS_EDITABLE = "EXAM_DISCUSSION_IMAGE_IS_EDITABLE";
        public static final String EXAM_DISCUSSION_IMAGE_ID = "EXAM_DISCUSSION_IMAGE_ID";
    }

    public static final class AnswerSummaryConstants {
        public static final String ANSWER_SUMMARY_INTENT_CODE = "ANSWER_SUMMARY_INTENT_CODE";
    }

    public static final class NotificationConstants {
        public static final String USER_ID = "USER_ID";
        public static final int INTERVAL = 2 * 60 * 60 * 1000; // in hour
    }

    public static final class PaymentConstants {
        public static final int CALLBACK_REDIRECT_INTERVAL = 3 * 1000; // in seconds
    }
}
