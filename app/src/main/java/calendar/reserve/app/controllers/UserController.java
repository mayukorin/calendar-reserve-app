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
                    String aa = "aaaa";
                    String responseMessage = "{\"message\":\"" + aa + "\"}";
                    System.out.println(responseMessage);
                    String json = req.body();
                    res.type("application/json");
                    User user = jsonTransformer.fromJson(json, User.class);
                    return userService.create(user);
                } catch (Exception e) {
                    ErrorMessage em = new ErrorMessage(e.getMessage());
                    return em;
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
                    ErrorMessage em = new ErrorMessage(e.getMessage());
                    return em;
                }
            }, jsonTransformer);

            get("/hello", "application/json", (req, res) -> "Hello World");
            /*
            exception(Exception.class, (exception, request, response) -> {
                System.out.println("ooo");
                response.type("application/json");
                String responseMessage = "{\"messsage\":\"" + exception.getMessage() + "\"}";
            });
            */
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

    }

}
