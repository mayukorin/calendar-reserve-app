package calendar.reserve.app.controllers;

import calendar.reserve.app.services.ReservationService;
import calendar.reserve.app.services.LoadInitialData;
import calendar.reserve.app.services.JsonTransformer;
import calendar.reserve.app.models.User;
import calendar.reserve.app.models.ErrorMessage;

import static spark.Spark.*;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;


public class LoadController {

    public static void api() {

        try {
            LoadInitialData load_data= new LoadInitialData();
            JsonTransformer jsonTransformer = new JsonTransformer();

            post("/load_inini", (req, res) -> {
                try {
                    load_data.loadData();

                    return "initial data loaded";
                } catch (Exception e) {
                    throw(e);
                }
            }, jsonTransformer);


            exception(Exception.class, (exception, request, response) -> {
                response.type("application/json");
                String responseMessage = "{\"message\":" + exception.getMessage() + "}";
                response.status(400);
                response.body(responseMessage);
            });
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

    }

}
