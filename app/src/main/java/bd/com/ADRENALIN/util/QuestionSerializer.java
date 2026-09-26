package bd.com.ADRENALIN.util;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;
import java.util.List;

import bd.com.ADRENALIN.pojo.Answer;
import bd.com.ADRENALIN.pojo.Question;
import bd.com.ADRENALIN.pojo.Exam;

/**
 * Created by mahfuz on 7/1/2017.
 */

public class QuestionSerializer implements JsonSerializer<Exam> {
    //    https://futurestud.io/tutorials/gson-advanced-custom-serialization-part-1

    @Override
    public JsonElement serialize(Exam exam, Type typeOfSrc, JsonSerializationContext context) {
//        Log.e("serialize", new Gson().toJson(exam));
        JsonObject examJsonObject = new JsonObject();
        examJsonObject.addProperty("Id", exam.getId());
        examJsonObject.addProperty("U", exam.getUserId());
        examJsonObject.addProperty("D", exam.getDuration());
        examJsonObject.addProperty("StartAt", exam.getStartAt());

        JsonArray questionListJsonArray = new JsonArray();

        List<Question> questionListFromExamObj = exam.getQuestions();
        for (Question question : questionListFromExamObj) {
            JsonObject questionJsonObject = new JsonObject();
            questionJsonObject.addProperty("Id", question.getQuestionId());
            questionJsonObject.addProperty("T", question.getTypeId());

            JsonArray answerListJsonArray = new JsonArray();

            List<Answer> answerList = question.getAnswers();
            for (Answer answer : answerList) {
                JsonArray answerJsonArray = new JsonArray();
                answerJsonArray.add(answer.getId());
                answerJsonArray.add(answer.getPickedAnswer());
                answerJsonArray.add(answer.getAnswerStatus());

                answerListJsonArray.add(answerJsonArray);
            }
            questionJsonObject.add("I", answerListJsonArray);

            questionListJsonArray.add(questionJsonObject);
        }

        examJsonObject.add("Q", questionListJsonArray);

        return examJsonObject;
    }

    /* Expected format

        {
            "Id": 1,
            "Q": [{
            "Id": 5,
                    "I": [[9, 1], [10, 0], [11, 0], [12, 1], [13, 1]]
        },
            {
                "Id": 6,
                    "I": [[14, 0], [15, 0], [16, 1], [17, 2], [18, 2]]
            }]
        }
 */
}
