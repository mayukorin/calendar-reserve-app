package calendar.reserve.app.services;

import calendar.reserve.app.models.User;
import calendar.reserve.app.models.Reserve;

import com.scalar.db.api.DistributedTransaction;
import com.scalar.db.api.DistributedTransactionManager;
import com.scalar.db.service.TransactionFactory;
import com.scalar.db.api.Put;
import com.scalar.db.api.Get;
import com.scalar.db.io.Key;
import com.scalar.db.io.TextValue;

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
    private static final String TABLE_REMAIN = "remain";
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

            Put put =new Put(new Key("user_email", "hoge@gmail.com"))
            .forNamespace("reserve")
            .forTable("reserves")
            .withValue("UUid", "testtest")
            .withValue("remain_id", "test"); 
            tx.put(put);
            // remain tableの残席数を減らす
            

            //schedule tableに予定を追加
                              
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