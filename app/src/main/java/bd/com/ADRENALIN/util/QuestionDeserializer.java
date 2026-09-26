package bd.com.ADRENALIN.util;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import bd.com.ADRENALIN.pojo.Answer;
import bd.com.ADRENALIN.pojo.Question;

/**
 * Created by mahfuz on 7/1/2017.
 */

public class QuestionDeserializer implements JsonDeserializer<List<Question>> {

    @Override
    public List<Question> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonArray questionJsonArray = json.getAsJsonArray();
        List<Question> questionList = new ArrayList<>(questionJsonArray.size());
        if (!questionJsonArray.isJsonNull()) {
            for (JsonElement questionElement : questionJsonArray) {
                Question question = new Question();
                question.setQuestionId(((JsonArray) questionElement).get(0).getAsInt()); // Getting Question Id
                question.setQuestionName(((JsonArray) questionElement).get(1).getAsString()); // Getting Question Name

                JsonArray answerJsonArray = ((JsonArray) questionElement).get(2).getAsJsonArray();
                List<Answer> answerList = new ArrayList<>(answerJsonArray.size());
                if (!answerJsonArray.isJsonNull()) {
                    for (JsonElement answerElement : answerJsonArray) {
                        answerList.add(new Answer(
                                ((JsonArray) answerElement).get(0).getAsInt(), // Getting Answer id
                                ((JsonArray) answerElement).get(1).getAsString(), // Getting Answer text
                                ((JsonArray) answerElement).get(2).getAsInt()  // Getting Answer status
                        ));
                    }

                    question.setAnswers(answerList);
                }
                question.setTypeId(((JsonArray) questionElement).get(3).getAsInt());
                try {
                    if(((JsonArray) questionElement).size()>4&&!((JsonArray) questionElement).get(4).isJsonNull())
                        question.Explanation=((JsonArray) questionElement).get(4).getAsString();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                questionList.add(question);
            }
        }
        return questionList;
    }
}
