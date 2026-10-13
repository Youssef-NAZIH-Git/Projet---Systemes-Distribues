package ma.enset.ebankservice.service;

import ma.enset.ebankservice.entities.BankAccount;
import ma.enset.ebankservice.feign.CustomerRestClient;
import ma.enset.ebankservice.model.Customer;
import ma.enset.ebankservice.repository.BankAccountRepository;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
public class EbankService {
    private final BankAccountRepository accountRepository;
    private final CustomerRestClient customerRestClient;

    public EbankService(BankAccountRepository accountRepository, CustomerRestClient customerRestClient) {
        this.accountRepository = accountRepository;
        this.customerRestClient = customerRestClient;
    }

    public List<BankAccount> getAllBankAccounts() {
        return accountRepository.findAll();
    }

    public BankAccount getBankAccountById(String id) {
        BankAccount bankAccount = accountRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Account not found"));

        bankAccount.setCustomer(customerRestClient.getCustomerById(bankAccount.getCustomerId()));

        return bankAccount;
    }

    public BankAccount saveBankAccount(BankAccount bankAccount) {
        try {
            Customer customer = customerRestClient.getCustomerById(bankAccount.getCustomerId());
            bankAccount.setId(UUID.randomUUID().toString());
            bankAccount.setCreatedAt(new Date());
            bankAccount.setCustomer(customer); // TODO: Remember hadi mazadhach f video
            return accountRepository.save(bankAccount);
        } catch (Exception e){
            throw new RuntimeException(e);
        }
    }
}
