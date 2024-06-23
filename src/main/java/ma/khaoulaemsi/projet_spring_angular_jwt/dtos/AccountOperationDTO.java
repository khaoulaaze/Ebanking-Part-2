package ma.khaoulaemsi.projet_spring_angular_jwt.dtos;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ma.khaoulaemsi.projet_spring_angular_jwt.entities.BankAccount;
import ma.khaoulaemsi.projet_spring_angular_jwt.enums.OperationType;

import java.util.Date;

@Data
public class AccountOperationDTO {
    private Long id;
    private Date operationDate;
    private double amount;
    private OperationType type;
    private BankAccount bankAccount;
    private String description;
}
