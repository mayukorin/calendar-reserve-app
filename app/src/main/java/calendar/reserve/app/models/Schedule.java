package calendar.reserve.app.models;

import java.io.Serializable;

public class Schedule implements Serializable {

    private static final long serialVersionUID = 1L;
	public static final String USER_EMAIL = "user_email";
	public static final String SCHEDULE_ID = "schedule_id";
	public static final String DAY = "day";
    public static final String TITLE = "title";
    public static final String IS_RESERVE_APP_SCHEDULE = "is_reserve_app_schedule";

    private String user_email;
    private String schedule_id;
    private String day;
    private String title;
    private Boolean is_reserve_app_schedule;

    public Schedule(String user_email, String schedule_id, String day, String title, Boolean is_reserve_app_schedule) {
        this.user_email = user_email;
        this.schedule_id = schedule_id;
        this.day = day;
        this.title = title;
        this.is_reserve_app_schedule = is_reserve_app_schedule;
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
    public Boolean getIsReserveAppSchedule() {
        return this.is_reserve_app_schedule;
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
    public void setIsReserveAppSchedule(Boolean is_reserve_app_schedule) {
        this.is_reserve_app_schedule = is_reserve_app_schedule;
    }


}