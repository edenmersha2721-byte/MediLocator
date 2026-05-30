package com.medicinelocator.inventory.application.command;

public class CreateCategoryCommand {

    private final String name;
    private final String description;

    public CreateCategoryCommand(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public String getName() { return name; }
    public String getDescription() { return description; }
}