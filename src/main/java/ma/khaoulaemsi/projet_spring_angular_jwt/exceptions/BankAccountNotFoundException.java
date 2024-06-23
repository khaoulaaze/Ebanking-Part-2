package ma.khaoulaemsi.projet_spring_angular_jwt.exceptions;

public class BankAccountNotFoundException extends Exception {
    public BankAccountNotFoundException(String message){
        super(message);
    }
}
