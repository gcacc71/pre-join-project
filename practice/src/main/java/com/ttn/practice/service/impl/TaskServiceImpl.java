package com.ttn.practice.service.impl;

import com.ttn.practice.dto.TaskRequestDto;
import com.ttn.practice.dto.TaskResponseDto;
import com.ttn.practice.model.Task;
import com.ttn.practice.model.User;
import com.ttn.practice.repository.TaskRepository;
import com.ttn.practice.repository.UserRepository;
import com.ttn.practice.service.TaskService;
import com.ttn.practice.service.UserService;
import org.hibernate.query.sqm.mutation.internal.TableKeyExpressionCollector;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.file.AccessDeniedException;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TaskServiceImpl implements TaskService {
    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;


    @Override
    public List<TaskResponseDto> getAllTasksAsDto() {
        List<Task> tasks = taskRepository.findAllTasks();

        return tasks.stream()
                .map(this::convertToDto)
                .toList();
    }

    @Override
    public List<TaskResponseDto> getTasksByStatus(String status) {
        List<Task> tasks = taskRepository.findTasksByStatus(status);

        return tasks.stream()
                .map(this::convertToDto)
                .toList();
    }


    @Override
    public TaskResponseDto createTask(TaskRequestDto taskRequestDto) throws AccessDeniedException {
        User currentUser = userService.getCurrentUser();

        boolean isManager = currentUser.getRole().equals("MANAGER");

        if(!isManager){
            throw new AccessDeniedException("You do not have role to create task!");
        }

        Task task = new Task();
        task.setTitle(taskRequestDto.getTitle());
        task.setDescription(taskRequestDto.getDescription());
        task.setStatus(taskRequestDto.getStatus());
        task.setDueDate(taskRequestDto.getDueDate());


        User assignee = userRepository.findById(taskRequestDto.getAssigneeId())
                .orElseThrow(() -> new RuntimeException("Error: Assignee not found with id " + taskRequestDto.getAssigneeId()));

        User createdBy = userRepository.findById(taskRequestDto.getCreatedById())
                .orElseThrow(() -> new RuntimeException("Error: Creator not found with id " + taskRequestDto.getCreatedById()));

        task.setAssignee(assignee);
        task.setCreatedBy(createdBy);

        return convertToDto(taskRepository.save(task));
    }

    @Override
    public TaskResponseDto updateTaskStatusByTaskID(int taskId, String status) throws AccessDeniedException {
        User currentUser = userService.getCurrentUser();
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Error: Task not found with id " + taskId));

        boolean isManager = currentUser.getRole().equals("MANAGER");
        boolean isAssignee = task.getAssignee() != null && task.getAssignee().getId().equals(currentUser.getId());

        if(!isManager && !isAssignee){
            throw new AccessDeniedException("You are not allowed to edit other people's tasks!");
        }

        task.setStatus(status);

        return convertToDto(taskRepository.save(task));
    }

    @Override
    public List<TaskResponseDto> getMyTasks() {
        User currentUser = userService.getCurrentUser();
        List<Task> myTasks = taskRepository.findTasksByAssigneeID(currentUser.getId());
        return myTasks.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    private TaskResponseDto convertToDto(Task task){
        return TaskResponseDto.builder()
                .title(task.getTitle())
                .description(task.getDescription())
                .status(task.getStatus())
                // Xử lý null safety
                .assignee(task.getAssignee() != null ? task.getAssignee().getFullName() : "Unassigned")
                .createdBy(task.getCreatedBy() != null ? task.getCreatedBy().getFullName() : "Unknown")
                .dueDate(task.getDueDate())
                .createdAt(task.getCreatedAt())
                .build();
    }

}
