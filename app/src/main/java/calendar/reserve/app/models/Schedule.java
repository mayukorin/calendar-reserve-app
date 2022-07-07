package calendar.reserve.app.models;

import java.io.Serializable;

public class Schedule implements Serializable {

    private static final long serialVersionUID = 1L;
	public static final String USER_EMAIL = "user_email";
	public static final String SCHEDULE_ID = "schedule_id";
	public static final String DAY = "day";
    public static final String TITLE = "title";
    public static final String RESERVE_ID = "reserve_id";

    private String user_email;
    private String schedule_id;
    private String day;
    private String title;
    private String reserve_id;

    public Schedule(String user_email, String schedule_id, String day, String title, String reserve_id) {
        this.user_email = user_email;
        this.schedule_id = schedule_id;
        this.day = day;
        this.title = title;
        this.reserve_id = reserve_id;
    }

    public String getUserEmail() {
        return this.user_email;
    }
    public String getScheduleId() {
        return this.schedule_id;
    }
    public String getDay() {
        return this.day;
    }
    public String getTitle() {
        return this.title;
    }
    public String getReserveId() {
        return this.reserve_id;
    }

    public void setUserEmail(String user_email) {
        this.user_email = user_email;
    }
    public void setScheduleId(String schedule_id) {
        this.schedule_id = schedule_id;
    }
    public void setDay(String day) {
        this.day = day;
    }
    public void SetTitle(String title) {
        this.title = title;
    }
    public void setReserveId(String reserve_id) {
        this.reserve_id = reserve_id;
    }


}