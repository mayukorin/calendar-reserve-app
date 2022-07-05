package calendar.reserve.app.models;

import java.io.Serializable;

public class Remain implements Serializable {

    private static final long serialVersionUID = 2L;// ここの値はuserと変えるべきか
	public static final String ID = "id";
	public static final String DAY = "day";
	public static final String REMAIN_NUM = "remain_num_of_people";
    public static final String EVENT_ID= "event_id";

    private int id;
    private String day;
    private int remain_num;
    private int event_id;


    public Remain(int id, String day, int remain_num, int event_id) {
        this.id = id;
        this.day = day;
        this.remain_num = remain_num;
        this.event_id = event_id;
    }

    public int getId() {
        return this.id;
    }
    public String getDay() {
        return this.day;
    }
    public int getRemainNum() {
        return this.remain_num;
    }
    public int getEventId() {
        return this.event_id;
    }

    public void SetId(int id) {
        this.id = id;
    }
    public void SetDay(String day) {
        this.day = day;
    }
    public void SetRemainNum(int remain_num) {
        this.remain_num = remain_num;
    }
    public void SetEventId(int event_id) {
        this.event_id = event_id;
    }
}