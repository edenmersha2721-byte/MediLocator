package com.medicinelocator.search.application.service;

import com.medicinelocator.search.application.command.IndexMedicineCommand;


public interface UpsertSearchIndexService {


    void upsert(IndexMedicineCommand command);
}