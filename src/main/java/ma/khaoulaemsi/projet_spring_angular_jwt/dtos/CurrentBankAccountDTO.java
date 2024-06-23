package ma.khaoulaemsi.projet_spring_angular_jwt.dtos;

import lombok.Data;
import ma.khaoulaemsi.projet_spring_angular_jwt.enums.AccountStatus;

import java.util.Date;

@Data
public class CurrentBankAccountDTO extends BankAccountDTO{
    private String id;
    private double balance;
    private  Date createdAt;
    private AccountStatus status ;
    private CustomerDTO customerDTO;
    private double overDraft;
}
