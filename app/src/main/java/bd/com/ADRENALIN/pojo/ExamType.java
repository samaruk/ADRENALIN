package bd.com.ADRENALIN.pojo;

import com.google.gson.annotations.SerializedName;

/**
 * Created by mahfuz on 7/1/2017.
 */

public class ExamType {
    @SerializedName("Id")
    private int id;
    @SerializedName("Name")
    private String name;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "ExamType{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}
