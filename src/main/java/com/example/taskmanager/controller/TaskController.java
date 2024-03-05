package com.example.taskmanager.controller;

import com.example.taskmanager.requests.TaskRequest;
import com.example.taskmanager.requests.TaskUpdateRequest;
import com.example.taskmanager.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/tasks")
public class TaskController {
    private final TaskService taskService;

    @Autowired
    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @GetMapping
    public ResponseEntity<?> getAllTasks() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        return  taskService.getAllTasks(username);
    }

    @PostMapping
    public ResponseEntity<?> createTask(@RequestBody TaskRequest task) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();
            return taskService.createTask(task, username);
        }catch (Exception e){
            e.printStackTrace();
            return ResponseEntity.badRequest().body("bad request");
        }
    }

    @PutMapping("/update")
    public ResponseEntity<String> updateTask(@RequestBody TaskUpdateRequest task) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        taskService.updateTask(task,username);
        return ResponseEntity.status(HttpStatus.OK).body("Task updated successfully");
    }

    @GetMapping("/details/{taskName}")
    public ResponseEntity<?> getTaskDetailsByName(@PathVariable String taskName) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        return taskService.findTaskByName(taskName,username);
    }

    @DeleteMapping("/{taskId}")
    public ResponseEntity<String> deleteTask(@PathVariable Long taskId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        return taskService.deleteTask(taskId,username);
    }
    @GetMapping("/filter")
    public ResponseEntity<?> listTasks(@RequestParam String word) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        return taskService.filterTasksByStatus(word,username);
    }
}
