package ma.khaoulaemsi.projet_spring_angular_jwt.services;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ma.khaoulaemsi.projet_spring_angular_jwt.dtos.*;
import ma.khaoulaemsi.projet_spring_angular_jwt.entities.*;
import ma.khaoulaemsi.projet_spring_angular_jwt.enums.OperationType;
import ma.khaoulaemsi.projet_spring_angular_jwt.exceptions.BalanceNotSufficentException;
import ma.khaoulaemsi.projet_spring_angular_jwt.exceptions.BankAccountNotFoundException;
import ma.khaoulaemsi.projet_spring_angular_jwt.exceptions.CustomerNotFoundException;
import ma.khaoulaemsi.projet_spring_angular_jwt.mappers.BankAccountMapperImpl;
import ma.khaoulaemsi.projet_spring_angular_jwt.repositories.AccountOperationRepository;
import ma.khaoulaemsi.projet_spring_angular_jwt.repositories.BankAccountRepository;
import ma.khaoulaemsi.projet_spring_angular_jwt.repositories.CustomerRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
@AllArgsConstructor
@Slf4j
public class BankAccountServiceImpl implements BankAccountService {
    private CustomerRepository customerRepository;
    private BankAccountRepository bankAccountRepository;
    private AccountOperationRepository accountOperationRepository;
    private BankAccountMapperImpl dtoMapper;
    //Logger log= LoggerFactory.getLogger(this.getClass().getName());
    @Override
    public CustomerDTO saveCustomer(CustomerDTO customerDTO) {
        log.info("saving new customer");
        Customer customer=dtoMapper.fromCustomerDTO(customerDTO);
        Customer savedCustomer = customerRepository.save(customer);
        return dtoMapper.fromCustomer(savedCustomer);
    }

    @Override
    public CurrentBankAccountDTO saveCurrentBackAccount(double initialBalance, double overDraft, Long customerId) throws CustomerNotFoundException {
        Customer customer=customerRepository.findById(customerId).orElse(null);
        if (customer == null){
            //throw new RuntimeException("Customer not found");
            throw new CustomerNotFoundException("Customer not found");
        }
        CurrentAccount currentAccount=new CurrentAccount();
        currentAccount.setId(UUID.randomUUID().toString());
        currentAccount.setCreatedAt(new Date());
        currentAccount.setBalance(initialBalance);
        currentAccount.setCustomer(customer);
        currentAccount.setOverDraft(overDraft);
        CurrentAccount savedBankAccount=bankAccountRepository.save(currentAccount);
        return dtoMapper.fromCurrentBankAccount(savedBankAccount);
    }

    @Override
    public SavingBankAccountDTO saveSavingBackAccount(double initialBalance, double interestRate, Long customerId) throws CustomerNotFoundException {
        Customer customer=customerRepository.findById(customerId).orElse(null);
        if (customer == null){
            //throw new RuntimeException("Customer not found");
            throw new CustomerNotFoundException("Customer not found");
        }
        SavingAccount savingAccount=new SavingAccount();
        savingAccount.setId(UUID.randomUUID().toString());
        savingAccount.setCreatedAt(new Date());
        savingAccount.setBalance(initialBalance);
        savingAccount.setCustomer(customer);
        savingAccount.setInterestRate(interestRate);
        SavingAccount savedBankAccount=bankAccountRepository.save(savingAccount);
        return dtoMapper.fromSavingBankAccount(savedBankAccount);
    }


    @Override
    public List<CustomerDTO> ListCustomers() {
        List<Customer> customers = customerRepository.findAll();
        List<CustomerDTO> customerDTOS =customers.stream().map(customer -> dtoMapper.fromCustomer(customer)).collect(Collectors.toList());
//        List<CustomerDTO> customerDTOS=new ArrayList<>();
//        for (Customer customer:customers){
//            CustomerDTO customerDTO=dtoMapper.fromCustomer(customer);
//            customerDTOS.add(customerDTO);
//        }
        return customerDTOS;
    }

    @Override
    public BankAccountDTO getBankAccount(String accountId) throws BankAccountNotFoundException {
        BankAccount bankAccount=bankAccountRepository.findById(accountId).
                orElseThrow(()->new BankAccountNotFoundException("BankAccount Not Found!!"));
        if (bankAccount instanceof SavingAccount){
            SavingAccount savingAccount=(SavingAccount) bankAccount;
            return dtoMapper.fromSavingBankAccount(savingAccount);
        } else {
            CurrentAccount currentAccount=(CurrentAccount) bankAccount;
            return dtoMapper.fromCurrentBankAccount(currentAccount);
        }

    }

    @Override
    public void debit(String accountId, double amount, String description) throws BankAccountNotFoundException, BalanceNotSufficentException {
        //BankAccount bankAccount=getBankAccount(accountId);
        BankAccount bankAccount=bankAccountRepository.findById(accountId).
                orElseThrow(()->new BankAccountNotFoundException("BankAccount Not Found!!"));
        if(bankAccount.getBalance()<amount) throw new BalanceNotSufficentException("Balance Not Sufficent!!");
        AccountOperation accountOperation=new AccountOperation();
        accountOperation.setType(OperationType.DEBIT);
        accountOperation.setAmount(amount);
        accountOperation.setOperationDate(new Date());
        accountOperation.setDescription(description);
        accountOperation.setBankAccount(bankAccount);
        accountOperationRepository.save(accountOperation);
        bankAccount.setBalance(bankAccount.getBalance()-amount);
        bankAccountRepository.save(bankAccount);
    }

    @Override
    public void credit(String accountId, double amount, String description) throws BankAccountNotFoundException {
        //BankAccount bankAccount=getBankAccount(accountId);
        BankAccount bankAccount=bankAccountRepository.findById(accountId).
                orElseThrow(()->new BankAccountNotFoundException("BankAccount Not Found!!"));
        AccountOperation accountOperation=new AccountOperation();
        accountOperation.setType(OperationType.CREDIT);
        accountOperation.setAmount(amount);
        accountOperation.setOperationDate(new Date());
        accountOperation.setDescription(description);
        accountOperation.setBankAccount(bankAccount);
        accountOperationRepository.save(accountOperation);
        bankAccount.setBalance(bankAccount.getBalance()+amount);
        bankAccountRepository.save(bankAccount);
    }

    @Override
    public void transfer(String accountIdSource, String accountIdDestination, double amount, String description) throws BankAccountNotFoundException, BalanceNotSufficentException {
        debit(accountIdSource,amount,"Transfer to" + accountIdDestination);
        credit(accountIdDestination,amount,"Transfer from"+accountIdSource);
    }

    @Override
    public List<BankAccountDTO> bankAccountList(){
        List<BankAccount> bankAccounts = bankAccountRepository.findAll();
        List<BankAccountDTO> bankAccountDTOS = bankAccounts.stream().map(bankAccount -> {
            if (bankAccount instanceof SavingAccount) {
                SavingAccount savingAccount = (SavingAccount) bankAccount;
                return dtoMapper.fromSavingBankAccount(savingAccount);
            } else {
                CurrentAccount currentAccount = (CurrentAccount) bankAccount;
                return dtoMapper.fromCurrentBankAccount(currentAccount);
            }
        }).collect(Collectors.toList());
        return bankAccountDTOS;
    }
    @Override
    public CustomerDTO getCustomer(Long customerId) throws CustomerNotFoundException {
        Customer customer= customerRepository.findById(customerId)
                .orElseThrow(()->new CustomerNotFoundException("Customer Not found"));
        return dtoMapper.fromCustomer(customer);
    }

    @Override
    public CustomerDTO updateCustomer(CustomerDTO customerDTO) {
        log.info("saving new customer");
        Customer customer=dtoMapper.fromCustomerDTO(customerDTO);
        Customer savedCustomer = customerRepository.save(customer);
        return dtoMapper.fromCustomer(savedCustomer);
    }

    @Override
    public void deleteCustomer(Long customerId){
        customerRepository.deleteById(customerId);
    }

    @Override
    public List<AccountOperationDTO> accountHistory(String accountId){
        List<AccountOperation> accountOperations = accountOperationRepository.findByBankAccountId(accountId);
        return accountOperations.stream().map(op->dtoMapper.fromAccountOperation(op)).collect(Collectors.toList());
    }

    @Override
    public AccountHistoryDTO getAccountHistory(String accountId, int page, int size) throws BankAccountNotFoundException {
        BankAccount bankAccount=bankAccountRepository.findById(accountId).orElse(null);
        if(bankAccount==null) throw new BankAccountNotFoundException("Account not Found");
        Page<AccountOperation> accountOperations = accountOperationRepository.findByBankAccountIdOrderByOperationDateDesc(accountId, PageRequest.of(page, size));
        AccountHistoryDTO accountHistoryDTO=new AccountHistoryDTO();
        List<AccountOperationDTO> accountOperationDTOS = accountOperations.getContent().stream().map(op -> dtoMapper.fromAccountOperation(op)).collect(Collectors.toList());
        accountHistoryDTO.setAccountOperationDTOS(accountOperationDTOS);
        accountHistoryDTO.setAccountId(bankAccount.getId());
        accountHistoryDTO.setBalance(bankAccount.getBalance());
        accountHistoryDTO.setCurrentPage(page);
        accountHistoryDTO.setPageSize(size);
        accountHistoryDTO.setTotalPages(accountOperations.getTotalPages());
        return accountHistoryDTO;
    }

    @Override
    public List<CustomerDTO> searchCustomers(String keyword){
        List<Customer> customers=customerRepository.searchCustomer(keyword);
        List<CustomerDTO> customerDTOS=customers.stream().map(cust ->dtoMapper.fromCustomer(cust)).collect(Collectors.toList());
        return customerDTOS;
    }
}
