package calendar.reserve.app.services;

import com.scalar.db.api.DistributedTransaction;
import com.scalar.db.api.DistributedTransactionManager;
import com.scalar.db.api.Get;
import com.scalar.db.api.Put;
import com.scalar.db.api.Result;
import com.scalar.db.api.Scan;
import com.scalar.db.config.DatabaseConfig;
import com.scalar.db.exception.transaction.TransactionException;
import com.scalar.db.io.Key;
import com.scalar.db.service.TransactionFactory;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;


public class LoadInitialData extends ModelService{

    private final DistributedTransactionManager manager;

    public LoadInitialData() throws IOException {
        TransactionFactory factory = new TransactionFactory(dbConfig);
        manager = factory.getTransactionManager();
    }

    public void loadData() throws Exception{
        DistributedTransaction transaction = null;
        try {
        transaction = manager.start();
        String event_id = UUID.randomUUID().toString();
        loadEventIfNotExists(transaction,event_id,"manzai A");
        String remain_id = UUID.randomUUID().toString();
        loadRemainIfNotExists(transaction,remain_id, event_id, "2022-07-03", 10);

        event_id = UUID.randomUUID().toString();
        loadEventIfNotExists(transaction,event_id,"manzai B");
        remain_id = UUID.randomUUID().toString();
        loadRemainIfNotExists(transaction,remain_id, event_id, "2022-07-03", 5);
        remain_id = UUID.randomUUID().toString();
        loadRemainIfNotExists(transaction,remain_id, event_id, "2022-07-05", 5);
        remain_id = UUID.randomUUID().toString();
        loadRemainIfNotExists(transaction,remain_id, event_id, "2022-07-04", 5);

        transaction.commit();
        } catch (TransactionException e) {
        if (transaction != null) {
            // If an error occurs, abort the transaction
            transaction.abort();
        }
        throw e;
        }
    }


    private void loadEventIfNotExists(
        DistributedTransaction transaction,
        String event_id,
        String event_name)
        throws TransactionException {
        // Optional<Result> event =
        //     transaction.get(
        //         new Get(new Key("event_id", event_id))
        //             .forNamespace("reserve")
        //             .forTable("events"));
        // if (!event.isPresent()) {
        transaction.put(
            new Put(new Key("event_id", event_id), new Key("event_name",event_name))
                .forNamespace("reserve")
                .forTable("events"));
        // }
    }
    private void loadRemainIfNotExists(
        DistributedTransaction transaction,
        String remain_id,
        String event_id,
        String day,
        int remain_num_of_people)
        throws TransactionException {
        Optional<Result> event =
            transaction.get(
                new Get(new Key("remain_id", remain_id), new Key("event_id", event_id))
                    .forNamespace("reserve")
                    .forTable("remains"));
        if (!event.isPresent()) {
        transaction.put(
            new Put(new Key("remain_id", remain_id), new Key("event_id", event_id))
                .withValue("day", day)
                .withValue("remain_num_of_people", remain_num_of_people)
                .forNamespace("reserve")
                .forTable("remains"));
        }
    }

    // private Get createGetReservaion(Key reservation_pk) {
	// 	return new Get(reservation_pk).forNamespace(NAMESPACE_RESERVATION).forTable(TABLE_RESERVE);
	// }
    public void main(String args[]){
        try{
            loadData();
        }
        catch(Exception e){
            
        }
    }

}