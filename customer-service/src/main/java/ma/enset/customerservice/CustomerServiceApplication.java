package ma.enset.customerservice;

import ma.enset.customerservice.entities.Customer;
import ma.enset.customerservice.repository.CustomerRepository;
import ma.enset.customerservice.service.CustomerService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.List;

@SpringBootApplication
public class CustomerServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(CustomerServiceApplication.class, args);
    }


    @Bean
    CommandLineRunner init(CustomerService customerService) {
     return args -> {
         List<String> names = List.of("Mohamed", "Imane", "Yassine");
         names.forEach(
                 name -> {
                     customerService.saveCustomer(Customer.builder()
                             .name(name)
                             .email(name + "@gmail.com")
                             .build());
                 }
         );
     };
    }

}
