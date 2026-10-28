package bd.com.ADRENALIN.pojo;

import com.google.gson.annotations.SerializedName;

/**
 * Body of UserArea/AppUser/AddAppUser, the same fields the web registration page sends. The candidate type,
 * medical college, faculty, subject, batch and remarks are set by the admin (App Users), not at sign-up.
 */
public class RegistrationRequest {
    @SerializedName("Name")
    public String name;
    @SerializedName("Email")
    public String email;
    @SerializedName("Phone")
    public String phone;
    @SerializedName("Password")
    public String password;
    /** Optional postal address. */
    @SerializedName("Address")
    public String address;
}
