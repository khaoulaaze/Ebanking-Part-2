package ma.khaoulaemsi.projet_spring_angular_jwt.dtos;

import lombok.Data;

@Data
public class CreditDTO {
    private String accountId;
    private double amount;
    private String description;
}
