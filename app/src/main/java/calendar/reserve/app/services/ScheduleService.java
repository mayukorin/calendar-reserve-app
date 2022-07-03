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

import java.io.IOException;
import java.util.UUID;
import java.util.Optional;

// ★ポイント8
public class ScheduleService extends ModelService {

    private final DistributedTransactionManager manager;

    private static final String NAMESPACE = "calendar";
    private static final String TABLE_NAME = "schedules";

    public ScheduleService() throws IOException {
        TransactionFactory factory = new TransactionFactory(dbConfig);
        manager = factory.getTransactionManager();
        manager.with(NAMESPACE, TABLE_NAME);
    }

    public String create(String user_email, String day, String title, Boolean is_reserve_app_schedule) throws Exception {

        DistributedTransaction tx = manager.start();

        try {
            String id = UUID.randomUUID().toString(); // 多分重複しないから大丈夫
            Put put =
					new Put(
                        new Key("user_email", user_email),
                        new Key("uuid", id))      
                    // .withValue(Schedule.USER_EMAIL, user_email)                          
                    // .withValue(Schedule.DAY, day)                  // テーブルを指定
                    // .withValue(Schedule.ID, id)
					// .withValue(Schedule.TITLE, title)
                    // .withValue(Schedule.IS_RESERVE_APP_SCHEDULE, is_reserve_app_schedule) // ここで error
                    .forNamespace(NAMESPACE)               // NameSpaceを指定
					.forTable(TABLE_NAME);
            /*
            Put put = Put.newBuilder()
                .namespace(NAMESPACE)
                .table(TABLE_NAME)
                .partitionKey(new Key("user_email", user_email))
                .clusteringKey(new Key("id", id))
                .textValue(Schedule.DAY, day)
                .textValue(Schedule.TITLE, title)
                .booleanValue(Schedule.IS_RESERVE_APP_SCHEDULE, is_reserve_app_schedule)
                .build();
            */
			tx.put(put);
            tx.commit();
            return id;                                  

        } catch (Exception e) {
            tx.abort();
            throw e;
        }

    }

    public Schedule getById(String user_email, String id) throws Exception {
        
       DistributedTransaction tx = manager.start();

        try {
            Get get =
					new Get(createPk(user_email), createIdCk(id))                                
					.forNamespace(NAMESPACE)               // NameSpaceを指定
					.forTable(TABLE_NAME);

			Optional<Result> schedule = tx.get(get);
            tx.commit();   
            return parse(schedule);                               

        } catch (Exception e) {
            tx.abort();
            throw e;
        }
    }

    private Key createPk(String user_email) {
		return new Key(Schedule.USER_EMAIL, user_email);
	}

    private Key createIdCk(String id) {
        return new Key("uuid", id);
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
            ScalarUtil.getTextValue(result, Schedule.ID),
            ScalarUtil.getTextValue(result, Schedule.DAY),
            ScalarUtil.getTextValue(result, Schedule.TITLE),
            ScalarUtil.getTextValue(result, Schedule.IS_RESERVE_APP_SCHEDULE)
            
        );
  }
}