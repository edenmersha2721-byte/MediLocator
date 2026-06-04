package com.medicinelocator.search.application.query.handler;

import com.medicinelocator.search.application.dto.response.NearbyMedicineResponse;
import com.medicinelocator.search.application.dto.response.PagedResponse;
import com.medicinelocator.search.application.mapper.SearchMapper;
import com.medicinelocator.search.application.query.SearchMedicineByNameQuery;
import com.medicinelocator.search.application.service.SearchService;
import com.medicinelocator.search.domain.projection.NearbyMedicineProjection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class SearchMedicineByNameHandler {

    private static final Logger log = LoggerFactory.getLogger(SearchMedicineByNameHandler.class);

    private final SearchService searchService;
    private final SearchMapper searchMapper;

    public SearchMedicineByNameHandler(SearchService searchService, SearchMapper searchMapper) {
        this.searchService = searchService;
        this.searchMapper = searchMapper;
    }

    @Transactional(readOnly = true)
    public PagedResponse<NearbyMedicineResponse> handle(SearchMedicineByNameQuery query) {
        log.debug("SearchMedicineByNameHandler: term='{}' lat={} lng={} radius={}km page={}",
                query.getSearchTerm(), query.getLatitude(), query.getLongitude(),
                query.getRadiusKm(), query.getPage());

        PageRequest pageable = PageRequest.of(query.getPage(), query.getSize());

        Page<NearbyMedicineProjection> results = searchService.searchMedicinesByName(
                query.getSearchTerm(),
                query.getLatitude(),
                query.getLongitude(),
                query.getRadiusKm(),
                query.getRequiresPrescription(),
                query.getCategory(),
                pageable
        );

        return new PagedResponse<>(
                results.getContent().stream()
                        .map(searchMapper::toNearbyMedicineResponse)
                        .toList(),
                query.getPage(),
                query.getSize(),
                results.getTotalElements()
        );
    }
}