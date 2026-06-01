package com.forex.common.base.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PageResp<T> {

    @Schema(description = "总条数")
    private long total;

    @Schema(description = "当前页数据")
    private List<T> records;

    @Schema(description = "当前页码")
    private int pageNum;

    @Schema(description = "每页条数")
    private int pageSize;

    @Schema(description = "总页数")
    private int totalPages;

    public static <T> PageResp<T> of(long total, List<T> records, int pageNum, int pageSize) {
        int totalPages = pageSize > 0 ? (int) Math.ceil((double) total / pageSize) : 0;
        return new PageResp<>(total, records, pageNum, pageSize, totalPages);
    }
}
