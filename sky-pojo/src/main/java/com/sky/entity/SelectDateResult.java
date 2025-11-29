package com.sky.entity;

import lombok.Data;

import java.time.LocalDate;

@Data
public class SelectDateResult {
    private LocalDate date;
    private Double amount;
}
