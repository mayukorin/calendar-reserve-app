package calendar.reserve.app.services;

import calendar.reserve.app.models.User;
import calendar.reserve.app.models.Reserve;

import com.scalar.db.api.DistributedTransaction;
import com.scalar.db.api.DistributedTransactionManager;
import com.scalar.db.service.TransactionFactory;
import com.scalar.db.api.Put;
import com.scalar.db.api.Get;
import com.scalar.db.api.Scan;
import com.scalar.db.io.Key;
import com.scalar.db.io.TextValue;

import java.util.List;
import java.io.IOException;
import java.util.Optional;
import com.scalar.db.api.Result;
import java.util.UUID;
import java.util.ArrayList;
import java.util.List;

// ★ポイント8
public class ReservationService extends ModelService {

    private final DistributedTransactionManager manager;

    // // 複数DB扱うのでこれはいらない？
    private static final String NAMESPACE_RESERVATION = "reserve";
    private static final String NAMESPACE_USER = "user";
    private static final String TABLE_REMAIN = "remains";
    private static final String TABLE_RESERVE = "reserves";
    private static final String TABLE_EVENT = "event";

    public ReservationService() throws IOException {
        TransactionFactory factory = new TransactionFactory(dbConfig);
        manager = factory.getTransactionManager();
    }

    public Reserve create(String email, String remaining_id) throws Exception{

        DistributedTransaction tx = manager.start();

        try {
            String newId = UUID.randomUUID().toString();

            Put p1  = new Put(new Key("user_email",email), new Key("remain_id", remaining_id))
            .forNamespace("reserve")
            .forTable("reserves")
            .withValue("reserve_id", newId);
            tx.put(p1);

            // remain tableの残席数を減らす
            Key remain_key = new Key("remain_id",remaining_id);
            List<Result> remain_data = tx.scan(
            new Scan(remain_key).forNamespace("reserve").forTable("remains")); //partition keyで一意に取れないのでscan使う
            
            if (remain_data == null || remain_data.size() == 0) {
                throw new RuntimeException("Remain not found");
            }
            Result r = remain_data.get(0);
            int update_remain_num = r.getValue("remain_num_of_people").get().getAsInt() - 1;
            String event_id = r.getValue("event_id").get().getAsString().get();
            String day = r.getValue("day").get().getAsString().get();
            
            // 0以下になってないかチェック
            if (update_remain_num < 0){
                throw new RuntimeException("満席です。予約できません。");
            };
            tx.put(
                new Put(new Key("remain_id", remaining_id),new Key("event_id", event_id))
                .withValue("remain_num_of_people", update_remain_num)
                .withValue("day", day)
                .withValue("common_key","common")
                .forNamespace("reserve")
                .forTable("remains")
            );

            // eventテーブルからevent名を取得
            List<Result> event_data = tx.scan(
                new Scan(new Key("event_id", event_id))
                .forNamespace("reserve")
                .forTable("events")
            );
            if (event_data == null || event_data.size() == 0) {
                throw new RuntimeException("Remain not found");
            }
            Result d = event_data.get(0);
            String event_name = d.getValue("event_name").get().getAsString().get();



            //schedule tableに予定を追加
            String scheduleId = UUID.randomUUID().toString();
            tx.put(
                new Put(new Key("user_email",email), new Key("day", day, "schedule_id",scheduleId))
                .forNamespace("calendar")
                .forTable("schedules")
                .withValue("title",event_name)
                .withValue("is_reserve_app_schedule", new Boolean(true))
            );


                              
            tx.commit();
			return new Reserve(email, newId, remaining_id);
        } catch (Exception e) {
            tx.abort();
            throw e;
        }
    }
    public String show_user_reservation(String email) throws Exception{

        DistributedTransaction tx = manager.start();

        try {

            // reservesテーブルからreservationを取得
            List<Result> user_reservations = tx.scan(
                new Scan(new Key("user_email", email))
                .forNamespace("reserve")
                .forTable("reserves")
            );
            if (user_reservations == null || user_reservations.size() == 0) {
                throw new RuntimeException("No reservation");
            }
            // Make the statements JSONs
            List<String> reservationJsons = new ArrayList<>();
            for (Result user_reservation : user_reservations) {

                String reserve_id = user_reservation.getValue("reserve_id").get().getAsString().get();
                String remain_id = user_reservation.getValue("remain_id").get().getAsString().get();

                // remain_idからremainレコードを抽出

                List<Result> items = tx.scan(
                    new Scan(new Key("remain_id", remain_id))
                    .forNamespace("reserve")
                    .forTable("remains")
                );
                if (items == null || items.size() == 0) {
                    throw new RuntimeException("Remain not found");
                }
                Result item = items.get(0);

                String day = item.getValue("day").get().getAsString().get();
                String event_id = item.getValue("event_id").get().getAsString().get();

                // event_idからeventレコードを抽出
                List<Result> events = tx.scan(
                    new Scan(new Key("event_id", event_id))
                    .forNamespace("reserve")
                    .forTable("events")
                );
                if (events == null || events.size() == 0) {
                    throw new RuntimeException("Event not found");
                }
                Result event = events.get(0);
                String event_name = event.getValue("event_name").get().getAsString().get();
                // どのIDを返すか問題

                reservationJsons.add(
                    String.format(
                        "{\"id\": \"%s\",\"day\": \"%s\",\"event_name\": \"%s\"}",
                        reserve_id, day, event_name
                    )
                );
            }   
            tx.commit();
			return String.format(
                "{\"reservation\": %s}",
                reservationJsons);
        } catch (Exception e) {
            tx.abort();
            throw e;
        }
    }

    public String get_all_events_data() throws Exception{

        DistributedTransaction tx = manager.start();

        try {
            List<Result> events = tx.scan(
                new Scan(new Key("common_key","common"))
                .forNamespace("reserve")
                .forTable("events")
            );
            if (events == null || events.size() == 0) {
                throw new RuntimeException("No reservation");
            }

            List<String> eventJsons = new ArrayList<>();
            for (Result event : events) {
                List<String> remainJsons = new ArrayList<>();
                String event_id = event.getValue("event_id").get().getAsString().get();
                String event_name = event.getValue("event_name").get().getAsString().get();
                List<Result> remains = tx.scan(
                new Scan(new Key("event_id",event_id))
                .forNamespace("reserve")
                .forTable("remains")
                );
                if (remains == null || remains.size() == 0) {
                    eventJsons.add(
                        String.format(
                            "{\"event_id\": \"%s\",\"event_name\": \"%s\", \"remains\": []}",
                            event_id, event_name
                        )
                    );
                    continue;
                }
                for(Result remain : remains){
                    String remain_id = remain.getValue("remain_id").get().getAsString().get();
                    String day = remain.getValue("day").get().getAsString().get();
                    int remain_num_of_people = remain.getValue("remain_num_of_people").get().getAsInt();
                    remainJsons.add(
                        String.format(
                            "{\"remain_id\": \"%s\",\"day\": \"%s\",\"remain_num_of_people\": %d}",
                            remain_id, day, remain_num_of_people
                        )
                    );
                }
                eventJsons.add(
                    String.format(
                        "{\"event_id\": \"%s\",\"event_name\": \"%s\",\"remains\": %s}",
                        event_id, event_name, remainJsons
                    )
                );                
            }



 
            tx.commit();
			return String.format(
                "{\"events\": %s}",
                eventJsons);
        } catch (Exception e) {
            tx.abort();
            throw e;
        }
    }



    private Key createUserPk(String email) {
		return new Key(new TextValue(User.EMAIL, email));
	}
    private Key createReservationPk(String id){
        return new Key(new TextValue(Reserve.ID, id));
    }

    // private Get createGetReservaion(Key reservation_pk) {
	// 	return new Get(reservation_pk).forNamespace(NAMESPACE_RESERVATION).forTable(TABLE_RESERVE);
	// }

}