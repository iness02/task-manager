package com.example.taskmanager.repositoryimpl;

import com.example.taskmanager.model.Task;
import com.example.taskmanager.model.TaskStatus;
import com.example.taskmanager.model.User;
import com.example.taskmanager.repository.TaskRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Repository;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Repository
public class TaskRepositoryImplementation implements TaskRepository {
    private String FILE_PATH = "C:\\Users\\User\\Downloads\\task-manager\\task-manager\\src\\main\\resources\\tasks.json";
    private final File jsonFile = new File(FILE_PATH);
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public List<Task> findAll() {
        return loadTasksFromFile();
    }

    @Override
    public Task findById(long id) {
        List<Task> tasks = loadTasksFromFile();
        for (Task task : tasks) {
            if (task.getId() == id) {
                return task;
            }
        }
        return null; // Task not found
    }

    @Override
    public Task findByName(String name) {
        List<Task> tasks = loadTasksFromFile();
        for (Task task : tasks) {
            if (task.getName().equals(name)) {
                return task;
            }
        }
        return null; // Task not found
    }

    @Override
    public List<Task> findTaskByUser(User user) {
        List<Task> tasks = loadTasksFromFile();
        return tasks.stream()
                .filter(task -> task.getUserIds().contains(user.getId()))
                .collect(Collectors.toList());
    }

    @Override
    public void save(Task task) {
        List<Task> tasks = loadTasksFromFile();
        tasks.add(task);
        saveTasksToFile(tasks);

    }

    @Override
    public void deleteById(Long id) {
        List<Task> tasks = loadTasksFromFile();
        tasks.removeIf(task -> task.getId().equals(id));
        saveTasksToFile(tasks);
    }

    @Override
    public void update(Task task) {
        List<Task> tasks = loadTasksFromFile();
        for (int i = 0; i < tasks.size(); i++) {
            Task existingTask = tasks.get(i);
            if (existingTask.getId().equals(task.getId())) {
                existingTask.setName(task.getName());
                existingTask.setDescription(task.getDescription());
                existingTask.setStatus(task.getStatus());
                if (task.getStatus() == TaskStatus.COMPLETED)
                    existingTask.setCompleted(true);
                existingTask.setCreatorName(task.getCreatorName());
                existingTask.setUserIds(task.getUserIds());
            }
        }
        saveTasksToFile(tasks);
    }

    @Override
    public List<Task> findByStatus(String status) {
        List<Task> tasks = loadTasksFromFile();
        return tasks.stream()
                .filter(task -> task.getStatus().name().equals(status))
                .collect(Collectors.toList());
    }

    private List<Task> loadTasksFromFile() {
        try {
            // Check if the file exists and is not empty
            if (jsonFile.exists() && jsonFile.length() > 0) {
                return objectMapper.readValue(jsonFile, new TypeReference<List<Task>>() {
                });
            } else {
                return new ArrayList<>();
            }
        } catch (IOException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    private void saveTasksToFile(List<Task> tasks) {
        try {
            objectMapper.writeValue(jsonFile, tasks);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
