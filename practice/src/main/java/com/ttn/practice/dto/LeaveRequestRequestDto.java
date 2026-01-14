package com.ttn.practice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LeaveRequestRequestDto {
    private int userId;
    private LocalDate startDate;
    private LocalDate endDate;
    private String reason;
    private String status;
    private String managerComment;
    private Instant createdAt;
}
