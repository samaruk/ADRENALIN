package bd.com.ADRENALIN.pojo;

import com.google.gson.annotations.SerializedName;

/** Body of UserArea/AppUser/AddAppUser, the same fields the web registration page sends. */
public class RegistrationRequest {
    @SerializedName("Name")
    public String name;
    @SerializedName("Email")
    public String email;
    @SerializedName("Phone")
    public String phone;
    @SerializedName("Password")
    public String password;
    @SerializedName("Remarks")
    public String remarks;
    @SerializedName("CategoryId")
    public long categoryId;
    @SerializedName("MedicalCollageId")
    public long medicalCollageId;
    @SerializedName("FacultyId")
    public long facultyId;
    @SerializedName("DepartmentId")
    public long departmentId;
    @SerializedName("StudentBatchId")
    public long studentBatchId;
}
