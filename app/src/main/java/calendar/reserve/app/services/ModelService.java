package calendar.reserve.app.services;

import calendar.reserve.app.exceptions.ObjectAlreadyExistingException;

import com.scalar.db.config.DatabaseConfig;
import com.scalar.db.api.DistributedTransaction;
import com.scalar.db.api.Get;
import com.scalar.db.exception.transaction.CrudConflictException;
import com.scalar.db.exception.transaction.CrudException;

import java.io.IOException;
import java.io.FileInputStream;
import java.io.File;

public class ModelService  {

    // 違うかも
    private static final String SCALARDB_PROPERTIES =
      System.getProperty("user.dir") + File.separator + "src/main/resources/database.properties";
    protected DatabaseConfig dbConfig;

    public ModelService() throws IOException {
        dbConfig = new DatabaseConfig(new FileInputStream(SCALARDB_PROPERTIES));
    }

    public void getAndThrowsIfAlreadyExist(DistributedTransaction tx, Get get)
			throws ObjectAlreadyExistingException, CrudConflictException, CrudException {
      if (tx.get(get).isPresent()) {
        throw new ObjectAlreadyExistingException("同じemailアドレスのuserが既に存在しています");
      }
    }
    public void getAndThrowsIfNotExist(DistributedTransaction tx, Get get)
			throws ObjectAlreadyExistingException, CrudConflictException, CrudException {
      if (!tx.get(get).isPresent()) {
        throw new ObjectAlreadyExistingException("入力したemailは登録されていません。");
      }
    }

    

}