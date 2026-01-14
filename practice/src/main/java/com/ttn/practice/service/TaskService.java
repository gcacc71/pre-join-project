package com.ttn.practice.service;


import com.ttn.practice.dto.TaskRequestDto;
import com.ttn.practice.dto.TaskResponseDto;

import java.nio.file.AccessDeniedException;
import java.util.List;

public interface TaskService {
    List<TaskResponseDto> getAllTasksAsDto();
    List<TaskResponseDto> getTasksByStatus(String status);
    TaskResponseDto createTask(TaskRequestDto request) throws AccessDeniedException;
    TaskResponseDto updateTaskStatusByTaskID(int taskId, String status) throws AccessDeniedException;
    List<TaskResponseDto> getMyTasks();
}
