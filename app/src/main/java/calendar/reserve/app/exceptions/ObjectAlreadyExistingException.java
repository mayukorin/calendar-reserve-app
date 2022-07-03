package calendar.reserve.app.exceptions;

public class ObjectAlreadyExistingException extends Exception {

    private static final long serialVersionUID = 1L; 

	public ObjectAlreadyExistingException (String msg){
		super(msg);
	}
}
