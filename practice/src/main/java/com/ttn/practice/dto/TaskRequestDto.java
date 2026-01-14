package com.ttn.practice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.Instant;

@Data
@AllArgsConstructor
public class TaskRequestDto {
    private String title;
    private String description;
    private String status;
    private int assigneeId;
    private int createdById;
    private Instant dueDate;
    private Instant createdAt;
}
