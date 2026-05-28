package com.medicinelocator.auth.application.command;

public class RegisterPharmacyCommand {

    private final String email;
    private final String password;
    private final String pharmacyName;
    private final String licenseNumber;
    private final String phoneNumber;
    private final String address;
    private final String city;
    private final double latitude;
    private final double longitude;

    public RegisterPharmacyCommand(String email, String password, String pharmacyName,
                                   String licenseNumber, String phoneNumber, String address,
                                   String city, double latitude, double longitude) {
        this.email = email;
        this.password = password;
        this.pharmacyName = pharmacyName;
        this.licenseNumber = licenseNumber;
        this.phoneNumber = phoneNumber;
        this.address = address;
        this.city = city;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getEmail() { return email; }
    public String getPassword() { return password; }
    public String getPharmacyName() { return pharmacyName; }
    public String getLicenseNumber() { return licenseNumber; }
    public String getPhoneNumber() { return phoneNumber; }
    public String getAddress() { return address; }
    public String getCity() { return city; }
    public double getLatitude() { return latitude; }
    public double getLongitude() { return longitude; }
}