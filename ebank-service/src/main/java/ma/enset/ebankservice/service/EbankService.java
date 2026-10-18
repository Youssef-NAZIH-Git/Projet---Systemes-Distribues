package ma.enset.ebankservice.service;

import ma.enset.ebankservice.entities.BankAccount;
import ma.enset.ebankservice.feign.CustomerRestClient;
import ma.enset.ebankservice.model.Customer;
import ma.enset.ebankservice.repository.BankAccountRepository;
import org.springframework.ai.mcp.annotation.McpTool;
import org.springframework.ai.mcp.annotation.McpToolParam;
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

    @McpTool(description = "Get all bank accounts")
    public List<BankAccount> getAllBankAccounts() {
        return accountRepository.findAll();
    }

    @McpTool(description = "Get a bank accounts by ID")
    public BankAccount getBankAccountById(@McpToolParam(description = "The bank account ID") String id) {
        BankAccount bankAccount = accountRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Account not found"));

        bankAccount.setCustomer(customerRestClient.getCustomerById(bankAccount.getCustomerId()));

        return bankAccount;
    }

    @McpTool(description = "Save a bank account")
    public BankAccount saveBankAccount(@McpToolParam(description = "The bank account to save (balance, type, customerId)") BankAccount bankAccount) {
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
