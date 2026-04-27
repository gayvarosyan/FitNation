package com.example.fitnationcommon.dto.response;

import lombok.Builder;
import lombok.Data;
import java.util.List;

@Data
@Builder
public class PagedResponse<T> {
    private List<T> items;
    private int page;
    private int size;
    private long totalElements;
    private int totalPages;
    private boolean hasNext;
    private String sort;

    public static <T> PagedResponse<T> of(org.springframework.data.domain.Page<T> springPage, String sort) {
        return PagedResponse.<T>builder()
                .items(springPage.getContent())
                .page(springPage.getNumber())
                .size(springPage.getSize())
                .totalElements(springPage.getTotalElements())
                .totalPages(springPage.getTotalPages())
                .hasNext(springPage.hasNext())
                .sort(sort)
                .build();
    }
}
