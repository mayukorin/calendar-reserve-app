package calendar.reserve.app.services;

import calendar.reserve.app.exceptions.ObjectAlreadyExistingException;

import com.scalar.db.config.DatabaseConfig;
import com.scalar.db.api.DistributedTransaction;
import com.scalar.db.api.Get;
import com.scalar.db.exception.transaction.CrudConflictException;
import com.scalar.db.exception.transaction.CrudException;
import com.scalar.db.api.Result;

import java.io.IOException;
import java.io.FileInputStream;
import java.io.File;
import java.util.Optional;

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
        throw new ObjectAlreadyExistingException("A user with the same email address already exists.");
      }
    }
    public void getAndThrowsIfNotExist(DistributedTransaction tx, Get get)
			throws ObjectAlreadyExistingException, CrudConflictException, CrudException {
      if (!tx.get(get).isPresent()) {
        throw new ObjectAlreadyExistingException("The entered email is not registered.");
      }
    }

    public Optional<Result> getResultAndThrowsIfNotFound(DistributedTransaction tx, Get get, String className)
      throws Exception {
      Optional<Result> result = tx.get(get);
      if (!tx.get(get).isPresent()) {
        throw new Exception("not exist "+className); // TODO : 該当のもの
      }
      return result;
    }

    

}