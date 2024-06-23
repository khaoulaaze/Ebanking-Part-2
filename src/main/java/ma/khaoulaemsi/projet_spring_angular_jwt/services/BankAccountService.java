package ma.khaoulaemsi.projet_spring_angular_jwt.services;

import ma.khaoulaemsi.projet_spring_angular_jwt.dtos.*;
import ma.khaoulaemsi.projet_spring_angular_jwt.exceptions.BalanceNotSufficentException;
import ma.khaoulaemsi.projet_spring_angular_jwt.exceptions.BankAccountNotFoundException;
import ma.khaoulaemsi.projet_spring_angular_jwt.exceptions.CustomerNotFoundException;

import java.util.List;

public interface BankAccountService {
    CustomerDTO saveCustomer(CustomerDTO customerDTO);
    CurrentBankAccountDTO saveCurrentBackAccount(double initialBalance, double overDraft, Long customerId) throws CustomerNotFoundException;
    SavingBankAccountDTO saveSavingBackAccount(double initialBalance, double interestRate, Long customerId) throws CustomerNotFoundException;
    List<CustomerDTO> ListCustomers();
    BankAccountDTO getBankAccount(String accountId ) throws BankAccountNotFoundException;
    void debit (String accountId,double amount,String description) throws BankAccountNotFoundException, BalanceNotSufficentException;
    void credit (String accountId,double amount,String description) throws BalanceNotSufficentException, BankAccountNotFoundException;
    void transfer(String accountIdSource, String accountIdDestination, double amount, String description) throws BankAccountNotFoundException, BalanceNotSufficentException;

    List<BankAccountDTO> bankAccountList();

    CustomerDTO getCustomer(Long customerId) throws CustomerNotFoundException;

    CustomerDTO updateCustomer(CustomerDTO customerDTO);

    void deleteCustomer(Long customerId);
    List<AccountOperationDTO> accountHistory(String accountId);
    AccountHistoryDTO getAccountHistory(String accountId, int page, int size) throws BankAccountNotFoundException;

    List<CustomerDTO> searchCustomers(String keyword);
}