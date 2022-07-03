package calendar.reserve.app.controllers;

import calendar.reserve.app.services.ReservationService;
import calendar.reserve.app.services.JsonTransformer;
import calendar.reserve.app.models.User;
import calendar.reserve.app.models.ErrorMessage;

import static spark.Spark.*;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;


public class ReservationController {

    public static void api() {

        try {
            ReservationService reservationservice = new ReservationService();
            JsonTransformer jsonTransformer = new JsonTransformer();

            post("/register_reserve", (req, res) -> {
                try {
                    String json = req.body();
                    ObjectMapper mapper = new ObjectMapper();
                    JsonNode node = mapper.readTree(json);
                    String email = node.get("email").textValue();
                    String remaining_id = node.get("remaining_id").textValue();
                    res.type("application/json");

                    return reservationservice.create(email, remaining_id);
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
