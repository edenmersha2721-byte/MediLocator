package com.medicinelocator.auth.application.command;

public class RegisterCustomerCommand {

    private final String email;
    private final String password;
    private final String firstName;
    private final String lastName;
    private final String phoneNumber;

    public RegisterCustomerCommand(String email, String password, String firstName,
                                   String lastName, String phoneNumber) {
        this.email = email;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phoneNumber = phoneNumber;
    }

    public String getEmail() { return email; }
    public String getPassword() { return password; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public String getPhoneNumber() { return phoneNumber; }
}