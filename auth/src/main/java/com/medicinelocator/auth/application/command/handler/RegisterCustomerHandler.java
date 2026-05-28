package com.medicinelocator.auth.application.command.handler;

import com.medicinelocator.auth.application.command.RegisterCustomerCommand;
import com.medicinelocator.auth.application.service.CustomerService;
import com.medicinelocator.auth.application.service.EmailService;
import com.medicinelocator.auth.application.service.PasswordHasher;
import com.medicinelocator.auth.domain.enums.AccountStatus;
import com.medicinelocator.auth.domain.model.Customer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Component
public class RegisterCustomerHandler {

    private static final Logger log = LoggerFactory.getLogger(RegisterCustomerHandler.class);

    private final CustomerService customerService;
    private final PasswordHasher passwordHasher;
    private final EmailService emailService;
    private final StringRedisTemplate redisTemplate;

    public RegisterCustomerHandler(CustomerService customerService,
                                   PasswordHasher passwordHasher,
                                   EmailService emailService,
                                   StringRedisTemplate redisTemplate) {
        this.customerService = customerService;
        this.passwordHasher = passwordHasher;
        this.emailService = emailService;
        this.redisTemplate = redisTemplate;
    }

    @Transactional
    public void handle(RegisterCustomerCommand command) {
        if (customerService.existsByEmail(command.getEmail())) {
            throw new IllegalArgumentException("Email already registered: " + command.getEmail());
        }

        String hashedPassword = passwordHasher.hash(command.getPassword());
        String verificationToken = UUID.randomUUID().toString();

        Customer customer = new Customer(
                UUID.randomUUID(),
                command.getEmail(),
                hashedPassword,
                command.getFirstName(),
                command.getLastName(),
                command.getPhoneNumber(),
                AccountStatus.PENDING,
                false,
                0,
                null,
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        customerService.save(customer);

        redisTemplate.opsForValue().set(
                "email_verify:" + verificationToken,
                "CUSTOMER:" + customer.getId().toString(),
                24,
                TimeUnit.HOURS
        );

        emailService.sendEmailVerification(customer.getEmail(), verificationToken);

        log.info("Customer registered successfully: email={}", customer.getEmail());
    }
}