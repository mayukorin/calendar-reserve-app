package calendar.reserve.app.controllers;

import calendar.reserve.app.services.UserService;
import calendar.reserve.app.services.JsonTransformer;
import calendar.reserve.app.models.User;
import calendar.reserve.app.models.ErrorMessage;

import static spark.Spark.*;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



public class UserController {

    Logger logger = LoggerFactory.getLogger("root");

    public static void api() {

        try {
            UserService userService = new UserService();
            JsonTransformer jsonTransformer = new JsonTransformer();

            post("/signup", (req, res) -> {
                try {
                    // logger.info("test operation info"); これで標準出力できる
                    
                    String json = req.body();
                    res.type("application/json");
                    User user = jsonTransformer.fromJson(json, User.class);
                    return userService.create(user);
                } catch (Exception e) {
                    throw(e);
                }
            }, jsonTransformer);

            post("/login", (req, res) -> {
                try {
                    String json = req.body();
                    ObjectMapper mapper = new ObjectMapper();
                    JsonNode node = mapper.readTree(json);
                    String email = node.get("email").textValue();
                    String password = node.get("password").textValue();
                    res.type("application/json");

                    return userService.login(email, password);
                } catch (Exception e) {
                    throw(e);
                }
            }, jsonTransformer);

            get("/hello", "application/json", (req, res) -> "Hello World");

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
