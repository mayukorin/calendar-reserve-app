package calendar.reserve.app.utils;

import com.scalar.db.api.Result;
import java.util.Optional;

public class ScalarUtil  {

    public static String getTextValue(Optional<Result> result, String field_name) {
        return result.get().getValue(field_name).get().getAsString().get();
    }   

}