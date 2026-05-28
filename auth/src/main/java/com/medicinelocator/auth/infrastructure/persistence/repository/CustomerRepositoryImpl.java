package com.medicinelocator.auth.infrastructure.persistence.repository;

import com.medicinelocator.auth.application.service.CustomerService;
import com.medicinelocator.auth.domain.enums.AccountStatus;
import com.medicinelocator.auth.domain.model.Customer;
import com.medicinelocator.auth.infrastructure.persistence.entity.CustomerEntity;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
public class CustomerRepositoryImpl implements CustomerService {

    private final CustomerJpaRepository jpaRepository;

    public CustomerRepositoryImpl(CustomerJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Customer save(Customer customer) {
        CustomerEntity entity = toEntity(customer);
        CustomerEntity saved = jpaRepository.save(entity);
        return toDomain(saved);
    }

    @Override
    public Optional<Customer> findByEmail(String email) {
        return jpaRepository.findByEmail(email).map(this::toDomain);
    }

    @Override
    public Optional<Customer> findById(UUID id) {
        return jpaRepository.findById(id).map(this::toDomain);
    }

    @Override
    public boolean existsByEmail(String email) {
        return jpaRepository.existsByEmail(email);
    }

    @Override
    public Customer update(Customer customer) {
        CustomerEntity entity = jpaRepository.findById(customer.getId())
                .orElseThrow(() -> new IllegalArgumentException("Customer not found: " + customer.getId()));
        updateEntity(entity, customer);
        CustomerEntity updated = jpaRepository.save(entity);
        return toDomain(updated);
    }

    private CustomerEntity toEntity(Customer customer) {
        CustomerEntity entity = new CustomerEntity();
        entity.setId(customer.getId());
        entity.setEmail(customer.getEmail());
        entity.setPasswordHash(customer.getPasswordHash());
        entity.setFirstName(customer.getFirstName());
        entity.setLastName(customer.getLastName());
        entity.setPhoneNumber(customer.getPhoneNumber());
        entity.setAccountStatus(customer.getAccountStatus());
        entity.setEmailVerified(customer.isEmailVerified());
        entity.setFailedLoginAttempts(customer.getFailedLoginAttempts());
        entity.setLockedUntil(customer.getLockedUntil());
        return entity;
    }

    private void updateEntity(CustomerEntity entity, Customer customer) {
        entity.setEmail(customer.getEmail());
        entity.setPasswordHash(customer.getPasswordHash());
        entity.setFirstName(customer.getFirstName());
        entity.setLastName(customer.getLastName());
        entity.setPhoneNumber(customer.getPhoneNumber());
        entity.setAccountStatus(customer.getAccountStatus());
        entity.setEmailVerified(customer.isEmailVerified());
        entity.setFailedLoginAttempts(customer.getFailedLoginAttempts());
        entity.setLockedUntil(customer.getLockedUntil());
    }

    private Customer toDomain(CustomerEntity entity) {
        return new Customer(
                entity.getId(),
                entity.getEmail(),
                entity.getPasswordHash(),
                entity.getFirstName(),
                entity.getLastName(),
                entity.getPhoneNumber(),
                entity.getAccountStatus(),
                entity.isEmailVerified(),
                entity.getFailedLoginAttempts(),
                entity.getLockedUntil(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }
}