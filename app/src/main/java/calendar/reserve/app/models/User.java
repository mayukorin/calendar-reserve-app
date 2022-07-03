package calendar.reserve.app.models;

import java.io.Serializable;

public class User implements Serializable {

    private static final long serialVersionUID = 1L;
	public static final String USER_NAME = "username";
	public static final String PASSWORD = "password";
	public static final String EMAIL = "email";

    private String email;
    private String password;
    private String username;

    public User(String email, String password, String username) {
        this.email = email;
        this.password = password;
        this.username = username;
    }

    public String getEmail() {
        return this.email;
    }
    public String getPassword() {
        return this.password;
    }
    public String getUsername() {
        return this.username;
    }

    public void SetEmail(String email) {
        this.email = email;
    }
    public void SetPassword(String password) {
        this.password = password;
    }
    public void SetUsername(String username) {
        this.username = username;
    }
}