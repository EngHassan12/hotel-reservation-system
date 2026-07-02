package project.hotelreservationsystem.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import project.hotelreservationsystem.dto.CustomerDto;
import project.hotelreservationsystem.entity.Customer;
import project.hotelreservationsystem.repository.CustomerRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerRepository customerRepository;

    public Customer createCustomer(CustomerDto dto) {
        if (customerRepository.existsByEmail(dto.getEmail()))
            throw new RuntimeException("Email already exists");

        Customer customer = Customer.builder()
                .firstName(dto.getFirstName())
                .lastName(dto.getLastName())
                .phone(dto.getPhone())
                .email(dto.getEmail())
                .address(dto.getAddress())
                .build();

        return customerRepository.save(customer);
    }

    public List<Customer> getAllCustomers() {
        return customerRepository.findAll();
    }

    public Customer getCustomerById(Integer id) {
        return customerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Customer not found"));
    }

    public Customer updateCustomer(Integer id, CustomerDto dto) {
        Customer customer = getCustomerById(id);
        customer.setFirstName(dto.getFirstName());
        customer.setLastName(dto.getLastName());
        customer.setPhone(dto.getPhone());
        customer.setEmail(dto.getEmail());
        customer.setAddress(dto.getAddress());
        return customerRepository.save(customer);
    }

    public void deleteCustomer(Integer id) {
        Customer customer = getCustomerById(id);
        customerRepository.delete(customer);
    }
}