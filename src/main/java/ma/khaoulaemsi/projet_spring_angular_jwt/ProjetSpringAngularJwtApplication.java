package ma.khaoulaemsi.projet_spring_angular_jwt;

import ma.khaoulaemsi.projet_spring_angular_jwt.dtos.BankAccountDTO;
import ma.khaoulaemsi.projet_spring_angular_jwt.dtos.CurrentBankAccountDTO;
import ma.khaoulaemsi.projet_spring_angular_jwt.dtos.CustomerDTO;
import ma.khaoulaemsi.projet_spring_angular_jwt.dtos.SavingBankAccountDTO;
import ma.khaoulaemsi.projet_spring_angular_jwt.entities.*;
import ma.khaoulaemsi.projet_spring_angular_jwt.enums.AccountStatus;
import ma.khaoulaemsi.projet_spring_angular_jwt.enums.OperationType;
import ma.khaoulaemsi.projet_spring_angular_jwt.exceptions.BalanceNotSufficentException;
import ma.khaoulaemsi.projet_spring_angular_jwt.exceptions.BankAccountNotFoundException;
import ma.khaoulaemsi.projet_spring_angular_jwt.exceptions.CustomerNotFoundException;
import ma.khaoulaemsi.projet_spring_angular_jwt.repositories.AccountOperationRepository;
import ma.khaoulaemsi.projet_spring_angular_jwt.repositories.BankAccountRepository;
import ma.khaoulaemsi.projet_spring_angular_jwt.repositories.CustomerRepository;
import ma.khaoulaemsi.projet_spring_angular_jwt.services.BankAccountService;
import ma.khaoulaemsi.projet_spring_angular_jwt.services.BankService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

@SpringBootApplication
public class ProjetSpringAngularJwtApplication {

    public static void main(String[] args) {

        SpringApplication.run(ProjetSpringAngularJwtApplication.class, args);
    }

    @Bean
    CommandLineRunner commandLineRunner(BankAccountService bankAccountService){
        return args -> {
            //bankService.consulter();
            Stream.of("Hassan","Imane","Mohammed").forEach(
                    name->{
                        CustomerDTO customer = new CustomerDTO();
                        customer.setName(name);
                        customer.setEmail(name+"@gmail.com");
                        bankAccountService.saveCustomer(customer);
                    }
            );
            bankAccountService.ListCustomers().forEach(customer->{
                try {
                    bankAccountService.saveCurrentBackAccount(Math.random()*90000,9000,customer.getId());
                    bankAccountService.saveSavingBackAccount(Math.random()*120000,5.5,customer.getId());

                } catch (CustomerNotFoundException e) {
                    e.printStackTrace();
                }
            });
            List<BankAccountDTO> bankAccounts = bankAccountService.bankAccountList();
            for (BankAccountDTO bankAccount:bankAccounts){
                for (int i = 0; i <10 ; i++) {
                    String accountId;
                    if(bankAccount instanceof SavingBankAccountDTO){
                        accountId=((SavingBankAccountDTO) bankAccount).getId();
                    } else{
                        accountId=((CurrentBankAccountDTO) bankAccount).getId();
                    }
                    bankAccountService.credit(accountId,10000+Math.random()*120000,"Credit");
                    bankAccountService.debit(accountId,1000+Math.random()*9000,"Debit");
                }
            }
        };
    }


    //@Bean
    CommandLineRunner start(CustomerRepository customerRepository,
                            BankAccountRepository bankAccountRepository,
                            AccountOperationRepository accountOperationRepository){
        return args -> {
            Stream.of("Hassan","Yassine","Aicha").forEach(name->{
                Customer customer=new Customer();
                customer.setName(name);
                customer.setEmail(name+"@gmail.com");
                customerRepository.save(customer);
            });
            customerRepository.findAll().forEach(cust->{
                CurrentAccount currentAccount=new CurrentAccount();
                currentAccount.setId(UUID.randomUUID().toString());
                currentAccount.setBalance(Math.random()*90000);
                currentAccount.setCreatedAt(new Date());
                currentAccount.setStatus(AccountStatus.CREATED);
                currentAccount.setCustomer(cust);
                currentAccount.setOverDraft(9000);
                bankAccountRepository.save(currentAccount);

                SavingAccount savingAccount=new SavingAccount();
                savingAccount.setId(UUID.randomUUID().toString());
                savingAccount.setBalance(Math.random()*90000);
                savingAccount.setCreatedAt(new Date());
                savingAccount.setStatus(AccountStatus.CREATED);
                savingAccount.setCustomer(cust);
                savingAccount.setInterestRate(5.5);
                bankAccountRepository.save(savingAccount);

            });
            bankAccountRepository.findAll().forEach(acc->{
                for (int i = 0; i <10 ; i++) {
                    AccountOperation accountOperation=new AccountOperation();
                    accountOperation.setOperationDate(new Date());
                    accountOperation.setAmount(Math.random()*12000);
                    accountOperation.setType(Math.random()>0.5? OperationType.DEBIT: OperationType.CREDIT);
                    accountOperation.setBankAccount(acc);
                    accountOperationRepository.save(accountOperation);
                }

            });
        };

    }}