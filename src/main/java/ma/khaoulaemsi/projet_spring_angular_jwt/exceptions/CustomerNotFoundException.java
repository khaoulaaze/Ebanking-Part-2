package ma.khaoulaemsi.projet_spring_angular_jwt.exceptions;

public class CustomerNotFoundException extends Exception {
    public CustomerNotFoundException(String message) {
        super(message);
    }
}
