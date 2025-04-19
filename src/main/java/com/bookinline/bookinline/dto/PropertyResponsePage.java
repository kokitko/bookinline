package com.bookinline.bookinline.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PropertyResponsePage {
    private int page;
    private int size;
    private int totalPages;
    private long totalElements;
    private boolean last;
    private List<PropertyResponseDto> properties;
}
