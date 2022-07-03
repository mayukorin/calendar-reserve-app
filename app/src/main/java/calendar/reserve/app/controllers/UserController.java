package calendar.reserve.app.controllers;

import calendar.reserve.app.services.UserService;
import calendar.reserve.app.services.JsonTransformer;
import calendar.reserve.app.models.User;
import calendar.reserve.app.models.ErrorMessage;

import static spark.Spark.*;

public class UserController {

    public static void api() {

        try {
            UserService userService = new UserService();
            JsonTransformer jsonTransformer = new JsonTransformer();

            post("/signup", (req, res) -> {
                try {
                    String json = req.body();
                    res.type("application/json");
                    User user = jsonTransformer.fromJson(json, User.class);
                    return userService.create(user);
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
