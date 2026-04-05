package com.example.fitnationcommon.dto.request;

import lombok.Data;

@Data
public class MemberSearchRequest {

    private String search;
    private String status;
    private Integer page = 0;
    private Integer size = 20;

    public void setPage(Integer page) {
        this.page = page != null ? Math.max(0, page) : 0;
    }

    public void setSize(Integer size) {
        this.size = size != null ? Math.min(Math.max(1, size), 100) : 20;
    }
}
