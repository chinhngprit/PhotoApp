package thanhdnh.ueh.edu.article_app;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import java.util.ArrayList;

public class UserList {

    // Hỗ trợ tự động nhận diện key JSON dù trên server đặt là "users", "articles" hay "data"
    @SerializedName(value = "users", alternate = {"articles", "data"})
    @Expose
    private ArrayList<UserProfile> users;

    public UserList(ArrayList<UserProfile> users) {
        this.users = users;
    }

    public ArrayList<UserProfile> getUsers() {
        return users;
    }

    public void setUsers(ArrayList<UserProfile> users) {
        this.users = users;
    }
}