package bd.com.ADRENALIN.pojo;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/** Lookup lists for the sign-up screen (UserArea/AppUser/RegistrationOptions). */
public class RegistrationOptions {
    public static class Item {
        @SerializedName("Id")
        private long id;
        @SerializedName("Name")
        private String name;

        public Item() { }

        public Item(long id, String name) {
            this.id = id;
            this.name = name;
        }

        public long getId() { return id; }
        public String getName() { return name; }

        @Override
        public String toString() { return name == null ? "" : name; }
    }

    @SerializedName("IsError")
    private boolean isError;
    @SerializedName("Msg")
    private String msg;
    @SerializedName("Phone")
    private String phone;
    @SerializedName("SuccessMessage")
    private String successMessage;
    @SerializedName("Categories")
    private List<Item> categories;
    @SerializedName("MedicalColleges")
    private List<Item> medicalColleges;
    @SerializedName("Faculties")
    private List<Item> faculties;
    @SerializedName("Departments")
    private List<Item> departments;
    @SerializedName("Batches")
    private List<Item> batches;

    public boolean isError() { return isError; }
    public String getMsg() { return msg; }
    public String getPhone() { return phone; }
    public String getSuccessMessage() { return successMessage; }
    public List<Item> getCategories() { return categories == null ? new ArrayList<Item>() : categories; }
    public List<Item> getMedicalColleges() { return medicalColleges == null ? new ArrayList<Item>() : medicalColleges; }
    public List<Item> getFaculties() { return faculties == null ? new ArrayList<Item>() : faculties; }
    public List<Item> getDepartments() { return departments == null ? new ArrayList<Item>() : departments; }
    public List<Item> getBatches() { return batches == null ? new ArrayList<Item>() : batches; }
}
