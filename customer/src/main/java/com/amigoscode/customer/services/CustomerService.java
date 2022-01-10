package com.amigoscode.customer.services;

import com.amigoscode.amqpapp.configs.RabbitMQMessageProducer;
import com.amigoscode.clients.fraud.FraudCheckResponse;
import com.amigoscode.clients.fraud.FraudClient;
import com.amigoscode.clients.notification.NotificationClient;
import com.amigoscode.clients.notification.NotificationRequest;
import com.amigoscode.customer.domains.Customer;
import com.amigoscode.customer.dtos.CustomerRegistrationRequest;
import com.amigoscode.customer.dtos.CustomerResponse;
import com.amigoscode.customer.repositories.CustomerRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final FraudClient fraudClient;
    private final RabbitMQMessageProducer rabbitMQMessageProducer;

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

        FraudCheckResponse fraudCheckResponse = fraudClient.isFraudster(customer.getId());

        if (fraudCheckResponse.isFraudster()) {
            throw new IllegalStateException("fraudster");
        }

        NotificationRequest notificationRequest = new NotificationRequest(
                        customer.getId(),
                        customer.getEmail(),
                        String.format("Hi %s, welcome to Amigoscode...", customer.getFirstName())
        );
        rabbitMQMessageProducer.publish(
                notificationRequest,
                "internal.exchange",
                "internal.notification.routing-key"
        );
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