package bd.com.ADRENALIN.util;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import bd.com.ADRENALIN.pojo.Answer;
import bd.com.ADRENALIN.pojo.Exam;
import bd.com.ADRENALIN.pojo.Question;

/**
 * Created by mahfuz on 7/1/2017.
 */

public class ExamDeserializerForAllAnswers implements JsonDeserializer<Exam> {

    @Override
    public Exam deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        Exam exam = new Exam();
        JsonObject examJsonObject = json.getAsJsonObject();
        exam.setCategoryId(examJsonObject.get("CategoryId").getAsInt());
        exam.setCorrectAnswersTotal(examJsonObject.get("C").getAsInt());
        exam.setWrongAnswersTotal(examJsonObject.get("W").getAsInt());
        exam.setNoAnswersTotal(examJsonObject.get("N").getAsInt());
        exam.setDuration(examJsonObject.get("D").getAsInt());
//        exam.set(examJsonObject.get("D").getAsInt());
        exam.setStartAt(examJsonObject.get("StartAt").getAsString());

        JsonArray questionJsonArray = examJsonObject.getAsJsonArray("Q");
        List<Question> questionList = new ArrayList<>(questionJsonArray.size());
        if (!questionJsonArray.isJsonNull()) {
            for (JsonElement questionElement : questionJsonArray) {

                //LOG.e("questionElement","questionElement >= "+questionElement);
                Question question = new Question();

                question.setQuestionName(((JsonArray) questionElement).get(0).getAsString()); // Getting Question Name
                question.setTypeId(((JsonArray) questionElement).get(2).getAsInt());
                question.Explanation = ((JsonArray) questionElement).get(3).getAsString();
                question.setQuestionId(((JsonArray) questionElement).get(4).getAsInt());

                JsonArray answerJsonArray = ((JsonArray) questionElement).get(1).getAsJsonArray();
                List<Answer> answerList = new ArrayList<>(answerJsonArray.size());
                if (!answerJsonArray.isJsonNull()) {
                    for (JsonElement answerElement : answerJsonArray) {
                        Answer answer = new Answer();
                        answer.setText(((JsonArray) answerElement).get(0).getAsString()); // Getting Answer text
                        answer.setAnswerStatus(((JsonArray) answerElement).get(1).getAsInt()); // Getting Correct Answer
                        answer.setPickedAnswer(((JsonArray) answerElement).get(2).getAsInt()); // User picked Answer
                        answerList.add(answer);
                    }
                    question.setAnswers(answerList);
                }
                questionList.add(question);
            }
        }
        exam.setQuestions(questionList);
        return exam;
    }
}
