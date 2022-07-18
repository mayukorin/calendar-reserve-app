package calendar.reserve.app.utils;

import com.scalar.db.api.Result;
import java.util.Optional;

public class ScalarUtil  {

    public static String getTextValue(Optional<Result> result, String field_name) {
        return result.get().getValue(field_name).get().getAsString().get();
    }

    public static int getIntValue(Optional<Result> result, String field_name) {
        return result.get().getValue(field_name).get().getAsInt();
    }   

    public static Boolean getBooleanValue(Optional<Result> result, String field_name) {
        return result.get().getValue(field_name).get().getAsBoolean();
    }


    public static String getResultTextValue(Result result, String field_name) {
        return result.getValue(field_name).get().getAsString().get();
    }   

     public static int getResultIntValue(Result result, String field_name) {
        return result.getValue(field_name).get().getAsInt();
    }   

    public static Boolean getResultBooleanValue(Result result, String field_name) {
        return result.getValue(field_name).get().getAsBoolean();
    }

}