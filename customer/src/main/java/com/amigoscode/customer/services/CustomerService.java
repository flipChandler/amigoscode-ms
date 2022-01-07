package com.amigoscode.customer.services;

import com.amigoscode.customer.domains.Customer;
import com.amigoscode.customer.dtos.CustomerRegistrationRequest;
import com.amigoscode.customer.dtos.CustomerResponse;
import com.amigoscode.customer.dtos.FraudCheckResponse;
import com.amigoscode.customer.repositories.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CustomerService {

    @Autowired
    private CustomerRepository customerRepository;
    @Autowired
    private RestTemplate restTemplate;

    @Value("${fraud-check.host}")
    private String fraudCheckHost;

    public void registerCustomer(CustomerRegistrationRequest request) {
        Customer customer = Customer.builder()
                .firstName(request.firstName())
                .lastName(request.lastName())
                .email(request.email())
                .build();
        // TODO: check if email valid
        // TODO: check if email not taken
        customerRepository.saveAndFlush(customer);
        // TODO: check if fraudster
        Map<String, String> uriVariables = new HashMap<>();
        uriVariables.put("id", "" + customer.getId());
        FraudCheckResponse fraudCheckResponse = restTemplate.getForObject(
                fraudCheckHost + "/fraud-check/{id}",
                FraudCheckResponse.class,
                uriVariables
        );

        if (fraudCheckResponse.isFraudster()) {
            throw new IllegalStateException("fraudster");
        }
        // TODO: send notification
    }

    public List<CustomerResponse> findAll() {
        List<Customer> customers = customerRepository.findAll();
        return customers.stream().map(customer -> new CustomerResponse(
                customer.getFirstName(),
                customer.getLastName(),
                customer.getEmail())).collect(Collectors.toList());
    }

    public CustomerResponse findById(Integer id) {
        Optional<Customer> optional = customerRepository.findById(id);
        if (optional.isPresent()) {
            return new CustomerResponse(
                    optional.get().getFirstName(),
                    optional.get().getLastName(),
                    optional.get().getEmail());
        }
        throw new RuntimeException("Customer not found");
    }
}