package com.medicinelocator.search.application.query.handler;

import com.medicinelocator.search.application.dto.response.PagedResponse;
import com.medicinelocator.search.application.dto.response.PharmacySearchResult;
import com.medicinelocator.search.application.mapper.SearchMapper;
import com.medicinelocator.search.application.query.SearchNearbyPharmaciesQuery;
import com.medicinelocator.search.application.service.SearchService;
import com.medicinelocator.search.domain.projection.PharmacySearchView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class SearchNearbyPharmaciesHandler {

    private static final Logger log = LoggerFactory.getLogger(SearchNearbyPharmaciesHandler.class);

    private final SearchService searchService;
    private final SearchMapper searchMapper;

    public SearchNearbyPharmaciesHandler(SearchService searchService, SearchMapper searchMapper) {
        this.searchService = searchService;
        this.searchMapper = searchMapper;
    }

    @Transactional(readOnly = true)
    public PagedResponse<PharmacySearchResult> handle(SearchNearbyPharmaciesQuery query) {
        log.debug("SearchNearbyPharmaciesHandler: lat={} lng={} radius={}km",
                query.getLatitude(), query.getLongitude(), query.getRadiusKm());

        PageRequest pageable = PageRequest.of(query.getPage(), query.getSize());

        Page<PharmacySearchView> results = searchService.searchNearbyPharmacies(
                query.getLatitude(),
                query.getLongitude(),
                query.getRadiusKm(),
                pageable
        );

        return new PagedResponse<>(
                results.getContent().stream()
                        .map(searchMapper::toPharmacySearchResult)
                        .toList(),
                query.getPage(),
                query.getSize(),
                results.getTotalElements()
        );
    }
}