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
                new Put(new Key("user_email",email), new Key("day", day))
                .forNamespace("calendar")
                .forTable("schedules")
                .withValue("title",event_name)
                .withValue("schedule_id",scheduleId)
                .withValue("is_reserve_app_schedule", new Boolean(true))
            );


                              
            tx.commit();
			return new Reserve(email, newId, remaining_id);
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