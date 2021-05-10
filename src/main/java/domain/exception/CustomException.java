package domain.exception;

public class CustomException extends Exception{
    public CustomException(String exception) {
        super(exception);
    }

    public CustomException(Exception e) {
        super(e);
    }

}
