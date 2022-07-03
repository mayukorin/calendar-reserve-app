/*
 * This Java source file was generated by the Gradle 'init' task.
 */
package calendar.reserve.app;
import static spark.Spark.*;

import calendar.reserve.app.controllers.UserController;
import calendar.reserve.app.controllers.ReservationController;

public class App {
    public String getGreeting() {
        return "Hello World!";
    }

    public static void main(String[] args) {
        initialize();
        UserController.api();
        ReservationController.api();
    }

    private static void initialize() {
        // server port
        port(8090);
    }
}
