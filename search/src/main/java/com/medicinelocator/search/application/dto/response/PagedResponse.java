package com.medicinelocator.search.application.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "Generic paged response wrapper")
public class PagedResponse<T> {

    @Schema(description = "List of results for this page")
    private final List<T> content;

    @Schema(description = "Current zero-based page number", example = "0")
    private final int page;

    @Schema(description = "Number of results per page", example = "20")
    private final int size;

    @Schema(description = "Total number of matching results", example = "143")
    private final long totalElements;

    @Schema(description = "Total number of pages", example = "8")
    private final int totalPages;

    @Schema(description = "Whether this is the last page", example = "false")
    private final boolean last;

    public PagedResponse(List<T> content, int page, int size, long totalElements) {
        this.content = content;
        this.page = page;
        this.size = size;
        this.totalElements = totalElements;
        this.totalPages = size == 0 ? 0 : (int) Math.ceil((double) totalElements / size);
        this.last = page >= this.totalPages - 1;
    }

    public List<T> getContent() { return content; }
    public int getPage() { return page; }
    public int getSize() { return size; }
    public long getTotalElements() { return totalElements; }
    public int getTotalPages() { return totalPages; }
    public boolean isLast() { return last; }
}