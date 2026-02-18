package com.zennitech.digital.pojo;

import com.zennitech.digital.model.StockAtendidoModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PageResponse<T> {

    private List<T> content;
    private int totalElements;
    private int page;
    private int totalPages;
    private int currentPage;
    private int pageSize;
    private boolean last;

}
