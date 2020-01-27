package hu.ponte.hr.customexception;

public class SizeTooBigException extends Exception {
    public SizeTooBigException(String errorMessage) {
        super(errorMessage);
    }
}
