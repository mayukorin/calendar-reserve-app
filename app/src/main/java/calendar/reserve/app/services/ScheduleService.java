package calendar.reserve.app.services;

import calendar.reserve.app.models.Schedule;
import calendar.reserve.app.models.User;
import calendar.reserve.app.models.Reserve;
import calendar.reserve.app.models.Remain;
import calendar.reserve.app.utils.ScalarUtil;


import com.scalar.db.api.DistributedTransaction;
import com.scalar.db.api.DistributedTransactionManager;
import com.scalar.db.service.TransactionFactory;
import com.scalar.db.api.Put;
import com.scalar.db.api.Result;
import com.scalar.db.api.Get;
import com.scalar.db.api.Delete;
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

    public String create(String user_email, String day, String title, String reserve_id) throws Exception {

        DistributedTransaction tx = manager.start();

        try {
  
            Get get = new Get(new Key(User.EMAIL, user_email))
                .forNamespace(NAMESPACE)
                .forTable("users");
            getResultAndThrowsIfNotFound(tx, get, "ユーザー");

            String schedule_id = UUID.randomUUID().toString(); 

            Put put =
					new Put(
                        new Key(Schedule.USER_EMAIL, user_email),
                        new Key(Schedule.DAY, day, Schedule.SCHEDULE_ID, schedule_id, Schedule.RESERVE_ID, reserve_id))   
					.withValue(Schedule.TITLE, title)
                    .forNamespace(NAMESPACE)               
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
            Get get = new Get(new Key(Schedule.SCHEDULE_ID, schedule_id))                              
					.forNamespace(NAMESPACE)               
					.forTable(TABLE_NAME);

			Optional<Result> result = getResultAndThrowsIfNotFound(tx, get, "スケジュール"); // TODO: スケジュール
            
            tx.commit();   
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
                new Scan(new Key(Schedule.USER_EMAIL, user_email)).withStart(new Key(Schedule.DAY, day)).withEnd(new Key(Schedule.DAY, day)).forNamespace(NAMESPACE).forTable(TABLE_NAME));

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

    public void destroy(String scheduleId) throws Exception {

        DistributedTransaction tx = manager.start();

        try {
            // 削除対象のScheduleをGet
            Get getSchedule =
                    new Get(new Key(Schedule.SCHEDULE_ID, scheduleId))                              
					.forNamespace(NAMESPACE)               
					.forTable(TABLE_NAME);
			Optional<Result> resultSchedule = getResultAndThrowsIfNotFound(tx, getSchedule, "スケジュール");
            String reserveId = ScalarUtil.getTextValue(resultSchedule, Schedule.RESERVE_ID);
            String userEmail = ScalarUtil.getTextValue(resultSchedule, Schedule.USER_EMAIL);
            String scheduleDay = ScalarUtil.getTextValue(resultSchedule, Schedule.DAY);

            if (!(reserveId.equals("xx"))) {
                // 削除対象のScheduleがReserveに紐づいている場合，そのReserveも削除

                // 削除対象のReserveをGet
                Get getReserve = 
                    new Get(
                        new Key(Reserve.ID, reserveId)
                    )
                    .forNamespace("reserve")               
					.forTable("reserves");
                Optional<Result> resultReserve = getResultAndThrowsIfNotFound(tx, getReserve, "予約");
                String remainId = ScalarUtil.getTextValue(resultReserve, Reserve.REMAIN_ID);

                // 削除対象のReserveをDelete
                Get getReserveForDelete =
                    new Get(
                        new Key(Reserve.EMAIL, userEmail),
                        new Key(Reserve.REMAIN_ID, remainId)
                    )                              
					.forNamespace("reserve")               
					.forTable("reserves");
                getResultAndThrowsIfNotFound(tx, getReserveForDelete, "予約");
                Delete deleteReserve = new Delete(
                        new Key(Reserve.EMAIL, userEmail),
                        new Key(Reserve.REMAIN_ID, remainId)
                    )
                    .forNamespace("reserve")               
					.forTable("reserves");
                tx.delete(deleteReserve);

                // Remain の remain_num_of_people を1個追加
                List<Result> remains = 
                    tx.scan(new Scan(
                            new Key(Remain.ID, remainId)
                        )
                        .forNamespace("reserve")
                        .forTable("remains")); 
                
                if (remains == null || remains.size() == 0) {
                    throw new RuntimeException("Remain not found");
                }
                Result resultRemain = remains.get(0);
                int update_remain_num = ScalarUtil.getResultIntValue(resultRemain, Remain.REMAIN_NUM) + 1;
                Put put =
					new Put(
                        new Key(Remain.ID, remainId),
                        new Key(Remain.EVENT_ID, ScalarUtil.getResultTextValue(resultRemain, Remain.EVENT_ID))) 
                    .withValue(Remain.REMAIN_NUM, update_remain_num)  
					.withValue(Remain.DAY, ScalarUtil.getResultTextValue(resultRemain, Remain.DAY))
                    .forNamespace("reserve")               
					.forTable("remains");
                tx.put(put);
            }

            // 削除対象のScheduleをDelete
            Get getScheduleForDelete =
                    new Get(
                        new Key(Schedule.USER_EMAIL, userEmail),
                        new Key(Schedule.DAY, scheduleDay, Schedule.SCHEDULE_ID, scheduleId, Schedule.RESERVE_ID, reserveId)
                    )                              
					.forNamespace(NAMESPACE)               
					.forTable(TABLE_NAME);
            getResultAndThrowsIfNotFound(tx, getScheduleForDelete, "スケジュール");
            Delete delete = 
                new Delete(
                        new Key(Schedule.USER_EMAIL, userEmail),
                        new Key(Schedule.DAY, scheduleDay, Schedule.SCHEDULE_ID, scheduleId, Schedule.RESERVE_ID, reserveId)
                    )
                    .forNamespace(NAMESPACE)               
					.forTable(TABLE_NAME);
            tx.delete(delete);


            tx.commit();  
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
            ScalarUtil.getTextValue(result, Schedule.RESERVE_ID)
        );
    }

    Schedule resultParse(Result result) {

        return new Schedule(
            ScalarUtil.getResultTextValue(result, Schedule.USER_EMAIL), 
            ScalarUtil.getResultTextValue(result, Schedule.SCHEDULE_ID),
            ScalarUtil.getResultTextValue(result, Schedule.DAY),
            ScalarUtil.getResultTextValue(result, Schedule.TITLE),
            ScalarUtil.getResultTextValue(result, Schedule.RESERVE_ID)
        );
    }
}