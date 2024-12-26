package com.supermall.backend.dto;

import jakarta.validation.constraints.Min;
import lombok.Data;

@Data
public class IncreaseSalesDTO {
    @Min(value = 1, message = "数量必须大于0")
    private Integer quantity;
} 