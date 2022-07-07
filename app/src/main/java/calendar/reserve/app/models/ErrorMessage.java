package calendar.reserve.app.models;

import java.io.Serializable;

public class ErrorMessage implements Serializable {

    private static final long serialVersionUID = 1L;
	public static final String MESSAGE = "message";
	

    private String message;
   

    public ErrorMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return this.message;
    }
 
    public void SetMessage(String message) {
        this.message = message;
    }
}