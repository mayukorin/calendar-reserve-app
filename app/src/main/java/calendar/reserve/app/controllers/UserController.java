package calendar.reserve.app.controllers;

import calendar.reserve.app.services.UserService;
import calendar.reserve.app.services.JsonTransformer;
import calendar.reserve.app.models.User;

import static spark.Spark.*;

public class UserController {

    public static void api() {

        try {
            UserService userService = new UserService();
            JsonTransformer jsonTransformer = new JsonTransformer();

            post("/signup", (req, res) -> {
                try {
                    String json = req.body();
                    User user = jsonTransformer.fromJson(json, User.class);
                    return userService.create(user);
                } catch (Exception e) {
                    throw(e);
                }
            }, jsonTransformer);

            get("/hello", (req, res) -> "Hello World");

            exception(Exception.class, (exception, request, response) -> {
                System.out.println(exception.getMessage());
            });
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

    }

}
