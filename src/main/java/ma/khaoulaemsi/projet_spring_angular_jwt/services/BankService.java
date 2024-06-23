package ma.khaoulaemsi.projet_spring_angular_jwt.services;

import jakarta.transaction.Transactional;
import ma.khaoulaemsi.projet_spring_angular_jwt.entities.BankAccount;
import ma.khaoulaemsi.projet_spring_angular_jwt.entities.CurrentAccount;
import ma.khaoulaemsi.projet_spring_angular_jwt.entities.SavingAccount;
import ma.khaoulaemsi.projet_spring_angular_jwt.repositories.BankAccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class BankService {
    @Autowired
    private BankAccountRepository bankAccountRepository;
    public void consulter(){
        BankAccount bankAccount = bankAccountRepository.findById("19486c98-d56e-45b9-856f-ebfc3b8fa4a7").orElse(null);
        if (bankAccount != null){
            System.out.println("**************************************");
            System.out.println(bankAccount.getId());
            System.out.println(bankAccount.getBalance());
            System.out.println(bankAccount.getStatus());
            System.out.println(bankAccount.getCustomer().getName());
            System.out.println(bankAccount.getCreatedAt());
            System.out.println(bankAccount.getClass().getSimpleName());
            if (bankAccount instanceof CurrentAccount){
                System.out.println("Over Draft =>"+((CurrentAccount)bankAccount).getOverDraft());
            } else if (bankAccount instanceof SavingAccount){
                System.out.println("Rate =>"+((SavingAccount)bankAccount).getInterestRate());
            }
            bankAccount.getAccountOperations().forEach(op->{
                System.out.println("=======================================");
                System.out.println(op.getAmount() +"\t"+op.getOperationDate()+"\t"+op.getType());
                System.out.println(op.getOperationDate());
                System.out.println(op.getAmount());
                System.out.println(op.getType());

            });
        }

    }
}
