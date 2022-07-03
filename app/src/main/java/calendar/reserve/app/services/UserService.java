package calendar.reserve.app.services;

import calendar.reserve.app.models.User;

import com.scalar.db.api.DistributedTransaction;
import com.scalar.db.api.DistributedTransactionManager;
import com.scalar.db.service.TransactionFactory;
import com.scalar.db.api.Put;
import com.scalar.db.api.Get;
import com.scalar.db.io.Key;
import com.scalar.db.io.TextValue;

import java.io.IOException;

// ★ポイント8
public class UserService extends ModelService {

    private final DistributedTransactionManager manager;

    private static final String NAMESPACE = "calendar";
    private static final String TABLE_NAME = "users";

    public UserService() throws IOException {
        TransactionFactory factory = new TransactionFactory(dbConfig);
        manager = factory.getTransactionManager();
        manager.with(NAMESPACE, TABLE_NAME);
    }

    public User create(User user) throws Exception{

        DistributedTransaction tx = manager.start();

        try {
            Key email = createPk(user.getEmail()); // パーティションキーの作成
			getAndThrowsIfAlreadyExist(tx, createGet(email)); // 同じIDを持つレコードが存在しないかをチェック
			Put put =
					new Put(email)                                // usersテーブルに登録/更新するPutオブジェクトを作成
					.forNamespace(NAMESPACE)               // NameSpaceを指定
					.forTable(TABLE_NAME)                  // テーブルを指定
					.withValue(User.PASSWORD, user.getPassword()) // TODO: ハッシュ化
					.withValue(User.USER_NAME, user.getUsername());
			tx.put(put);
            tx.commit();                                  
			return user;
        } catch (Exception e) {
            tx.abort();
            throw e;
        }
    }

    private Key createPk(String email) {
		return new Key(new TextValue(User.EMAIL, email));
	}

    private Get createGet(Key email) {
		return new Get(email).forNamespace(NAMESPACE).forTable(TABLE_NAME);
	}
}