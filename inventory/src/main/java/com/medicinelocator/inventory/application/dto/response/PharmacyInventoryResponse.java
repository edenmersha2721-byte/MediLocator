package com.medicinelocator.inventory.application.dto.response;

import java.util.List;
import java.util.UUID;

public class PharmacyInventoryResponse {

    private UUID pharmacyId;
    private int totalItems;
    private int page;
    private int size;
    private long totalElements;
    private int totalPages;
    private List<MedicineResponse> medicines;

    public PharmacyInventoryResponse() {
    }

    public PharmacyInventoryResponse(UUID pharmacyId, int totalItems, int page, int size,
                                     long totalElements, int totalPages,
                                     List<MedicineResponse> medicines) {
        this.pharmacyId = pharmacyId;
        this.totalItems = totalItems;
        this.page = page;
        this.size = size;
        this.totalElements = totalElements;
        this.totalPages = totalPages;
        this.medicines = medicines;
    }

    public UUID getPharmacyId() { return pharmacyId; }
    public void setPharmacyId(UUID pharmacyId) { this.pharmacyId = pharmacyId; }

    public int getTotalItems() { return totalItems; }
    public void setTotalItems(int totalItems) { this.totalItems = totalItems; }

    public int getPage() { return page; }
    public void setPage(int page) { this.page = page; }

    public int getSize() { return size; }
    public void setSize(int size) { this.size = size; }

    public long getTotalElements() { return totalElements; }
    public void setTotalElements(long totalElements) { this.totalElements = totalElements; }

    public int getTotalPages() { return totalPages; }
    public void setTotalPages(int totalPages) { this.totalPages = totalPages; }

    public List<MedicineResponse> getMedicines() { return medicines; }
    public void setMedicines(List<MedicineResponse> medicines) { this.medicines = medicines; }
}