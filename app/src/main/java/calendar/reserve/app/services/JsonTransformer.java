package calendar.reserve.app.services;

import com.google.gson.Gson;
import spark.ResponseTransformer;

// ★ポイント8
public class JsonTransformer implements ResponseTransformer {

    private Gson gson = new Gson();

    // ★ポイント8        
    @Override
    public String render(Object model) throws Exception {
        return gson.toJson(model);
    }

    // ★ポイント9
    public <T> T fromJson(String json, Class<T> classOfT) {
        return gson.fromJson(json, classOfT);
    }
}