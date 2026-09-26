package bd.com.ADRENALIN.pojo;

/**
 * Created by mahfuz on 7/11/17.
 */

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Merit {

    @SerializedName("Marks")
    @Expose
    private double marks;
    @SerializedName("Name")
    @Expose
    private String name;

    public double getMarks() {
        return marks;
    }

    public void setMarks(double marks) {
        this.marks = marks;
    }

    public Merit withMarks(double marks) {
        this.marks = marks;
        return this;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Merit withName(String name) {
        this.name = name;
        return this;
    }

    @Override
    public String toString() {
        return "Merit{" +
                "marks=" + marks +
                ", name='" + name + '\'' +
                '}';
    }
}