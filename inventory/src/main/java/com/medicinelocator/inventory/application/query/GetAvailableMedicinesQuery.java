package com.medicinelocator.inventory.application.query;

import java.util.UUID;

public class GetAvailableMedicinesQuery {

    private final UUID medicineId;
    private final int page;
    private final int size;

    public GetAvailableMedicinesQuery(UUID medicineId, int page, int size) {
        this.medicineId = medicineId;
        this.page = page;
        this.size = size;
    }

    public UUID getMedicineId() { return medicineId; }
    public int getPage() { return page; }
    public int getSize() { return size; }
}