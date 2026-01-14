package com.ttn.practice.controller;


import com.ttn.practice.dto.TaskRequestDto;
import com.ttn.practice.dto.TaskResponseDto;
import com.ttn.practice.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.file.AccessDeniedException;
import java.util.List;

@RestController
@CrossOrigin("*")
@RequestMapping("/api/task")
public class TaskController {
    @Autowired
    private TaskService taskService;


    @GetMapping("/get-all-tasks")
    public ResponseEntity<List<TaskResponseDto>> getAllTask(){
        return ResponseEntity.status(HttpStatus.OK).body(taskService.getAllTasksAsDto());
    }


    @GetMapping("/get-tasks-by-status/{status}")
    public ResponseEntity<List<TaskResponseDto>> getTasksByStatus(@PathVariable String status){
        return ResponseEntity.status(HttpStatus.OK).body(taskService.getTasksByStatus(status));
    }


    @PostMapping("/create-task")
    public ResponseEntity<TaskResponseDto> createTask(@RequestBody TaskRequestDto taskRequestDto) throws AccessDeniedException {
        return ResponseEntity.status(HttpStatus.CREATED).body(taskService.createTask(taskRequestDto));
    }

    @PutMapping("/update-task-status/{taskId}/{status}")
    public ResponseEntity<TaskResponseDto> updateTask(@PathVariable int taskId, @PathVariable String status) throws AccessDeniedException {
        return ResponseEntity.status(HttpStatus.OK).body(taskService.updateTaskStatusByTaskID(taskId, status));
    }

    @GetMapping("/get-my-tasks")
    public ResponseEntity<List<TaskResponseDto>> getMyTasks(){
        return ResponseEntity.status(HttpStatus.OK).body(taskService.getMyTasks());
    }
}
