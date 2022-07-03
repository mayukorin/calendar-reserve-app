package calendar.reserve.app.models;

import java.io.Serializable;

public class Reserve implements Serializable {

    private static final long serialVersionUID = 2L;// ここの値はuserと変えるべきか
	public static final String ID = "id";
	public static final String EMAIL = "user_email";
	public static final String REMAIN_ID = "remain_id";

    private String id;
    private String user_email;
    private String remain_id;


    public Reserve(String user_email, String id, String remain_id ) {
        this.id = id;
        this.user_email = user_email;
        this.remain_id = remain_id;
    }

    public String getEmail() {
        return this.user_email;
    }
    public String getRemainId() {
        return this.remain_id;
    }
    public String getId() {
        return this.id;
    }

    public void SetId(String id) {
        this.id = id;
    }
    public void SetRemainId(String remain_id) {
        this.remain_id = remain_id;
    }
    public void SetEmail(String user_email) {
        this.user_email = user_email;
    }
}