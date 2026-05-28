package com.medicinelocator.auth.application.service;

import com.medicinelocator.auth.domain.model.Customer;

import java.util.Optional;
import java.util.UUID;

public interface CustomerService {

    Customer save(Customer customer);

    Optional<Customer> findByEmail(String email);

    Optional<Customer> findById(UUID id);

    boolean existsByEmail(String email);

    Customer update(Customer customer);
}