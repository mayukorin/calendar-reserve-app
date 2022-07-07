package calendar.reserve.app.controllers;

import calendar.reserve.app.services.ScheduleService;
import calendar.reserve.app.services.JsonTransformer;
import calendar.reserve.app.models.Schedule;
import calendar.reserve.app.models.ErrorMessage;

import static spark.Spark.*;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ScheduleController {

    public static void api() {

        try {
            ScheduleService scheduleService = new ScheduleService();
            JsonTransformer jsonTransformer = new JsonTransformer();
            ObjectMapper mapper = new ObjectMapper(); 

            post("/register_schedules", (req, res) -> {
                try {
                    JsonNode node = mapper.readTree(req.body());
                    String schedule_id = scheduleService.create(node.get("user_email").textValue(), node.get("day").textValue(), node.get("title").textValue(), node.get("reserve_id").textValue());
                    
                    res.status(200);
                    res.type("application/json");
                    return scheduleService.getById(node.get("user_email").textValue(), schedule_id);
                } catch (Exception e) {
                    throw(e);
                }
            }, jsonTransformer);

            post("/calender", (req, res) -> {
                try {
                    System.out.println("okkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkk");
                    Logger logger = LoggerFactory.getLogger("root");
                    logger.info("test operation info");
                    logger.error("test operation error");
                    JsonNode node = mapper.readTree(req.body());
                    res.status(200);
                    res.type("application/json");
                    return scheduleService.getAllSchedules(node.get("user_email").textValue());
                } catch (Exception e) {
                    throw(e);
                }
            }, jsonTransformer);

            post("/day_calender", (req, res) -> {
                try {
                    JsonNode node = mapper.readTree(req.body());
                    res.status(200);
                    res.type("application/json");
                    return scheduleService.getDaySchedules(node.get("user_email").textValue(), node.get("day").textValue());
                } catch (Exception e) {
                    throw(e);
                }
            }, jsonTransformer);

            exception(Exception.class, (exception, req, res) -> {
                res.type("application/json");
                String responseMessage = "{\"message\":" + exception.getMessage() + "}";
                res.status(400);
                res.body(responseMessage);
            });  

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

    }

}
