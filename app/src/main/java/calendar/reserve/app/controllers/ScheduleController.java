package calendar.reserve.app.controllers;

import calendar.reserve.app.services.ScheduleService;
import calendar.reserve.app.services.JsonTransformer;
import calendar.reserve.app.models.Schedule;
import calendar.reserve.app.models.ErrorMessage;

import static spark.Spark.*;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ScheduleController {

    public static void api() {

        try {
            ScheduleService scheduleService = new ScheduleService();
            JsonTransformer jsonTransformer = new JsonTransformer();
            ObjectMapper mapper = new ObjectMapper(); 

            post("/register_schedules", (req, res) -> {
                try {
                    JsonNode node = mapper.readTree(req.body());
                    // System.out.println(node.get("title").textValue());
                    String id = scheduleService.create(node.get("user_email").textValue(), node.get("day").textValue(), node.get("title").textValue(), new Boolean(node.get("is_reserve_app_schedule").textValue()));
                    
                    res.status(200);
                    return scheduleService.getById(node.get("user_email").textValue(), id);
                    // return node.get("title").textValue();
                } catch (Exception e) {
                    throw(e);
                }
            });

            exception(Exception.class, (exception, request, response) -> {
                response.type("application/json");
                String responseMessage = "{\"message\":" + exception + "}";
                response.status(400);
                response.body(responseMessage);
            });
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

    }

}
