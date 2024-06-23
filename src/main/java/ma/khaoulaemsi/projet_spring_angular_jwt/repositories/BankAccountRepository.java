package ma.khaoulaemsi.projet_spring_angular_jwt.repositories;

import ma.khaoulaemsi.projet_spring_angular_jwt.entities.BankAccount;
import ma.khaoulaemsi.projet_spring_angular_jwt.entities.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BankAccountRepository extends JpaRepository<BankAccount,String> {

}
