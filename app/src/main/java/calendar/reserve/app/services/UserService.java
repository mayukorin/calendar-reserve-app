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
import java.util.Optional;
import com.scalar.db.api.Result;

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

            Get get = new Get(new Key(User.EMAIL, user.getEmail()))
                .forNamespace(NAMESPACE)
                .forTable("users");
            getResultAndThrowsIfNotFound(tx, get, "ユーザー");

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

    public User login(String email, String password) throws Exception{

        DistributedTransaction tx = manager.start();

        try {
            Key email_key = createPk(email); // パーティションキーの作成

            Optional<Result> user = 
                tx.get(
                    new Get(email_key)
                    .forNamespace(NAMESPACE)               // NameSpaceを指定
					.forTable(TABLE_NAME)                  // テーブルを指定
                );
            if (!user.isPresent()){                     // ユーザが見つからなかったらエラー
                throw new RuntimeException("User not found");
            }
            String correct_password = user.get().getValue("password").get().getAsString().get();
            if (!correct_password.equals(password)){                    // パスワードが合わなかったらエラー
                throw new RuntimeException("Incorrect password");
            }
            String username = user.get().getValue("username").get().getAsString().get(); // usernameを取得


            tx.commit();

            User login_user = new User(email, password, username);       

			return login_user; // 取得したユーザを返す
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