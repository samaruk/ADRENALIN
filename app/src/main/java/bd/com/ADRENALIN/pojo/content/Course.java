package bd.com.ADRENALIN.pojo.content;

import java.util.List;

/** A course (exam type) as the All Courses list shows it. */
public class Course {
    public long TypeId;
    public String Title;
    public String Name;
    public String BatchLabel;
    public int Status;
    public String StatusText;
    public String ShortDescription;
    public String Description;
    public int ClassCount;
    public int TestCount;
    public boolean CountsAreMinimum;
    public double RegularPrice;
    public double Price;
    public double DiscountAmount;
    public int DiscountPercent;
    public String DiscountLabel;
    public String Duration;
    public String StartDate;
    public List<String> Highlights;
    public String ImageUrl;
    public int PlanCount;
}
