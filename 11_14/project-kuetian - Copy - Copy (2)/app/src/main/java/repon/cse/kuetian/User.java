package repon.cse.kuetian;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;
import java.util.Map;

@IgnoreExtraProperties
public class User {

    public String un;
    public String ph;
    public String ml;
    public String nn;
    public String ht;
    public String cl;
    public String bd;
    public String bg;
    public String dp;
    public String nt;
    public String rl;
    HashMap<String,String> settings = new HashMap<>();
    HashMap<String,String> profile = new HashMap<>();
    HashMap<String,String> hall = new HashMap<>();
    HashMap<String,String> academic = new HashMap<>();
    HashMap<String,String> library = new HashMap<>();

    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public User(String username, String email) {
        this.un = username;
        this.ml = email;
    }
    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("username", un);
        result.put("email", ml);
        result.put("notes", nt);
        result.put("settings", settings);
        result.put("profile", profile);
        result.put("hall", hall);
        result.put("academic", academic);
        result.put("library", library);
        return result;
    }

}