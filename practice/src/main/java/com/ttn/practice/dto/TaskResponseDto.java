package com.ttn.practice.dto;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Builder
public class TaskResponseDto {
    private String title;
    private String description;
    private String status;
    private String assignee;
    private String createdBy;
    private Instant dueDate;
    private Instant createdAt;
}
