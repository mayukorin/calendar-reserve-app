package calendar.reserve.app.services;

import calendar.reserve.app.models.Schedule;
import calendar.reserve.app.utils.ScalarUtil;


import com.scalar.db.api.DistributedTransaction;
import com.scalar.db.api.DistributedTransactionManager;
import com.scalar.db.service.TransactionFactory;
import com.scalar.db.api.Put;
import com.scalar.db.api.Result;
import com.scalar.db.api.Get;
import com.scalar.db.io.Key;
import com.scalar.db.io.TextValue;
import com.scalar.db.io.BooleanValue;
import com.scalar.db.api.Scan;

import java.io.IOException;
import java.util.UUID;
import java.util.Optional;
import java.util.List;
import java.util.ArrayList;

// ★ポイント8
public class ScheduleService extends ModelService {

    private final DistributedTransactionManager manager;

    private static final String NAMESPACE = "calendar";
    private static final String TABLE_NAME = "schedules";

    public ScheduleService() throws IOException {
        TransactionFactory factory = new TransactionFactory(dbConfig);
        manager = factory.getTransactionManager();
    }

    public String create(String user_email, String day, String title, Boolean is_reserve_app_schedule) throws Exception {

        DistributedTransaction tx = manager.start();

        try {
            Get get = new Get(new Key("email", user_email))
                .forNamespace(NAMESPACE)
                .forTable("users");
            getResultAndThrowsIfNotFound(tx, get, "ユーザー");

            String schedule_id = UUID.randomUUID().toString(); 

            Put put =
					new Put(
                        new Key(Schedule.USER_EMAIL, user_email),
                        new Key(Schedule.DAY, day, Schedule.SCHEDULE_ID, schedule_id))  
                    // .withValue(Schedule.SCHEDULE_ID, schedule_id)    
					.withValue(Schedule.TITLE, title)
                    .withValue(Schedule.IS_RESERVE_APP_SCHEDULE, is_reserve_app_schedule) // ここで error
                    .forNamespace(NAMESPACE)               // NameSpaceを指定
					.forTable(TABLE_NAME);

			tx.put(put);
            tx.commit();
            return schedule_id;                                  

        } catch (Exception e) {
            tx.abort();
            throw e;
        }

    }

    public Schedule getById(String user_email, String schedule_id) throws Exception {
        
       DistributedTransaction tx = manager.start();

        try {
            Get get =
					// new Get(new Key(Schedule.USER_EMAIL, user_email), new Key(Schedule.DAY, "2022-07-11", Schedule.SCHEDULE_ID, schedule_id))  
                    new Get(new Key(Schedule.SCHEDULE_ID, schedule_id))                              
					.forNamespace(NAMESPACE)               
					.forTable(TABLE_NAME);

			Optional<Result> result = getResultAndThrowsIfNotFound(tx, get, "スケジュール"); // TODO: スケジュール
            // その日の予定を取得
            List<Result> statements = tx.scan(
                new Scan(new Key(Schedule.USER_EMAIL, user_email)).withStart(new Key(Schedule.DAY, "2022-07-11")).forNamespace(NAMESPACE).forTable(TABLE_NAME));
            // String ok = (Integer.valueOf(statements.size())).toString();
            /*
            for (Result statement : statements) {
              ok = statement.getValue("title").get().getAsString().get(); 
            }
            */
            // return ok;
            
            tx.commit();   
            System.out.println(result.get().getValue("is_reserve_app_schedule").get().getAsBoolean());
            // return result.get().getValue("user_email").get().getAsString().get();
            return parse(result);                               

        } catch (Exception e) {
            tx.abort();
            throw e;
        }
    }

    public List<Schedule> getAllSchedules(String user_email) throws Exception {

        DistributedTransaction tx = manager.start();

        try {
            List<Result> results = tx.scan(
                new Scan(new Key(Schedule.USER_EMAIL, user_email)).forNamespace(NAMESPACE).forTable(TABLE_NAME));

            List<Schedule> schedules = new ArrayList<Schedule>();

            for (Result result: results) {
                schedules.add(resultParse(result));
            }

            return schedules;
        } catch (Exception e) {
            tx.abort();
            throw e;
        }
    }

    public List<Schedule> getDaySchedules(String user_email, String day) throws Exception {

        DistributedTransaction tx = manager.start();

        try {
            List<Result> results = tx.scan(
                new Scan(new Key(Schedule.USER_EMAIL, user_email)).withStart(new Key(Schedule.DAY, day)).forNamespace(NAMESPACE).forTable(TABLE_NAME));

            List<Schedule> schedules = new ArrayList<Schedule>();

            for (Result result: results) {
                schedules.add(resultParse(result));
            }

            return schedules;
        } catch (Exception e) {
            tx.abort();
            throw e;
        }
    }

    private Key createPk(String user_email) {
		return new Key(Schedule.USER_EMAIL, user_email);
	}

    private Key createIdCk(String schedule_id) {
        return new Key("schedule_id", schedule_id);
    }

    private Key createDayCk(String day) {
        return new Key(Schedule.DAY, day);
    }

    private Get createGet(Key user_email) {
		return new Get(user_email).forNamespace(NAMESPACE).forTable(TABLE_NAME);
	}

    Schedule parse(Optional<Result> result) {

        return new Schedule(
            ScalarUtil.getTextValue(result, Schedule.USER_EMAIL), 
            ScalarUtil.getTextValue(result, Schedule.SCHEDULE_ID),
            ScalarUtil.getTextValue(result, Schedule.DAY),
            ScalarUtil.getTextValue(result, Schedule.TITLE),
            ScalarUtil.getBooleanValue(result, Schedule.IS_RESERVE_APP_SCHEDULE)
        );
    }

    Schedule resultParse(Result result) {

        return new Schedule(
            ScalarUtil.getResultTextValue(result, Schedule.USER_EMAIL), 
            ScalarUtil.getResultTextValue(result, Schedule.SCHEDULE_ID),
            ScalarUtil.getResultTextValue(result, Schedule.DAY),
            ScalarUtil.getResultTextValue(result, Schedule.TITLE),
            ScalarUtil.getResultBooleanValue(result, Schedule.IS_RESERVE_APP_SCHEDULE)
        );
    }
}