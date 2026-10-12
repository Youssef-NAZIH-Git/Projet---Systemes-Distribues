package ma.enset.ebankservice;

import ma.enset.ebankservice.entities.BankAccount;
import ma.enset.ebankservice.repository.BankAccountRepository;
import ma.enset.ebankservice.service.EbankService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class EbankServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(EbankServiceApplication.class, args);
	}

	@Bean
	CommandLineRunner init(EbankService ebankService) {
		return args -> {
			for (int i = 1; i <=3; i ++){
				for (int j = 1; j <=5; j ++) {
					ebankService.saveBankAccount(
							BankAccount.builder()
									.type(Math.random() > 0.5 ? "CURRENT-ACCOUNT" : "SAVING-ACCOUNT")
									.balance(1000 + Math.random() * 60000)
									.customerId(i)
									.build()
					);
				}
			}
		};
	}
}
