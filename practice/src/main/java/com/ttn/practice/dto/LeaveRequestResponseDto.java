package com.ttn.practice.dto;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.time.LocalDate;

@Data
@Builder
public class LeaveRequestResponseDto {
    private LocalDate startDate;
    private LocalDate endDate;
    private String reason;
    private String status;
    private String managerComment;
    private Instant createdAt;
}
