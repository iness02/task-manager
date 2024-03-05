package com.example.taskmanager.service;

import com.example.taskmanager.model.Task;
import com.example.taskmanager.model.TaskStatus;
import com.example.taskmanager.model.User;
import com.example.taskmanager.repositoryimpl.TaskRepositoryImplementation;
import com.example.taskmanager.repositoryimpl.UserRepositoryImplementation;
import com.example.taskmanager.requests.TaskRequest;
import com.example.taskmanager.requests.TaskUpdateRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class TaskService {
    private final TaskRepositoryImplementation taskRepository;
    private final UserRepositoryImplementation userRepositoryImplementation;

    @Autowired
    public TaskService(TaskRepositoryImplementation taskRepository, UserRepositoryImplementation userRepositoryImplementation) {
        this.taskRepository = taskRepository;
        this.userRepositoryImplementation = userRepositoryImplementation;
    }

    public ResponseEntity<?> getAllTasks(String username) {
        User user = userRepositoryImplementation.findByEmail(username);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("User with username " + username + "does not exist");
        }
        List<Task> tasks = taskRepository.findAll();
        return ResponseEntity.status(HttpStatus.OK).body(tasks);
    }


    public ResponseEntity<?> createTask(TaskRequest task, String username) {
        User user1 = userRepositoryImplementation.findByEmail(username);
        if (user1 == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("User with username " + username + "does not exist");
        }

        //the user who crates a task cannot create them with the same name
        List<Task> usersTasks = taskRepository.findTaskByUser(user1);
        Optional<Task> existingTask = usersTasks.stream()
                .filter(usersTask -> usersTask.getName().equals(task.getName())).findFirst();
        if (existingTask.isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("You already have task with that name");
        }
        Task task1 = new Task();
        Long nextTaskId = getHighestTaskId() + 1;
        task1.setId(nextTaskId);
        task1.setCompleted(false);
        task1.setStatus(TaskStatus.PENDING);
        List<Long> userIds = task.getAssignees().stream()
                .map(userRepositoryImplementation::findByEmail)
                .peek(user -> {
                    if (user == null) {
                        throw new UsernameNotFoundException("Can't assign task to user as such user doesn't exist");
                    }
                })
                .map(User::getId)
                .collect(Collectors.toList());

        userIds.add(user1.getId());
        task1.setUserIds(userIds);
        task1.setCreatorName(user1.getFirstName() + user1.getLastName());
        task1.setDescription(task.getDescription());
        task1.setName(task.getName());
        for (Long userId : userIds) {
            Optional<User> optionalUser = userRepositoryImplementation.findByID(userId);
            if (optionalUser.isPresent()) {
                User user = optionalUser.get();
                user.addTaskId(nextTaskId);
                userRepositoryImplementation.update(user);
            } else {
                throw new UsernameNotFoundException("User with ID " + userId + " not found");
            }

        }
        taskRepository.save(task1);
        return ResponseEntity.status(HttpStatus.OK).body("Task has created");
    }

    public long getHighestTaskId() {
        long highestId = 0;
        List<Task> tasks = taskRepository.findAll();
        for (Task task1 : tasks) {
            if (task1.getId() > highestId) {
                highestId = task1.getId();
            }
        }
        return highestId;
    }

    public ResponseEntity<String> updateTask(TaskUpdateRequest taskUpdateRequest, String username) {
        User user = userRepositoryImplementation.findByEmail(username);
        Task taskToUpdate = taskRepository.findById(taskUpdateRequest.getId());
        List<Task> usersTasks = taskRepository.findTaskByUser(user);
        Optional<Task> existingTask = usersTasks.stream()
                .filter(usersTask -> usersTask.getName().equals(taskUpdateRequest.getName()))
                .findFirst();

        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("User with username " + username + "does not exist");
        } else if (taskToUpdate == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Task with name " + taskUpdateRequest.getName() + " does not exist");
        } else if (existingTask.isPresent() && !existingTask.get().getId().equals(taskToUpdate.getId())) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("A task with the name " + taskUpdateRequest.getName() + " already exists");
        }

        List<Long> userIds = taskUpdateRequest.getAssignees().stream()
                .map(email -> userRepositoryImplementation.findByEmail(email).getId()) // Assuming userRepositoryImplementation.findByUsername returns user ID
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        userIds.add(userRepositoryImplementation.findByEmail(username).getId());

        taskToUpdate.setDescription(taskUpdateRequest.getDescription());
        taskToUpdate.setStatus(taskUpdateRequest.getStatus());
        taskToUpdate.setName(taskUpdateRequest.getName());
        taskToUpdate.setUserIds(userIds);
        taskRepository.update(taskToUpdate);

        List<Long> currentAssignees = taskToUpdate.getUserIds();
        // Retrieve the new list of assignees from the request

        List<Long> newAssignees = taskUpdateRequest.getAssignees().stream()
                .map(userRepositoryImplementation::findByEmail) // Assuming userRepositoryImplementation.findByEmail returns a User object
                .filter(Objects::nonNull) // Filter out null results in case email lookup fails
                .map(User::getId) // Assuming getId() returns the user ID
                .collect(Collectors.toList());

        // For each user ID in the current assignees
        for (Long userId : currentAssignees) {
            // If the user ID is not present in the new list of assignees
            if (!newAssignees.contains(userId)) {
                // Remove the task ID from the user's list of tasks
                Optional<User> user1 = userRepositoryImplementation.findByID(userId);

                user1.get().getTaskIds().remove(taskToUpdate.getId());
                userRepositoryImplementation.update(user1.get());

            }
        }
        // For each user ID in the new list of assignees
        for (Long userId : newAssignees) {
            // If the user ID is not present in the current assignees
            if (!currentAssignees.contains(userId)) {
                // Add the task ID to the user's list of tasks
                Optional<User> user2 = userRepositoryImplementation.findByID(userId);
                user2.get().getTaskIds().add(taskToUpdate.getId());
                userRepositoryImplementation.update(user2.get());
            }

        }

        return ResponseEntity.status(HttpStatus.OK).body("Task has successfully updated");
    }

    public ResponseEntity<?> findTaskByName(String taskName, String username) {
        User user = userRepositoryImplementation.findByEmail(username);
        Task task = taskRepository.findByName(taskName);

        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("User with username " + username + "does not exist");
        } else if (task == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Task with name " + taskName + " does not exist");
        }
        return ResponseEntity.status(HttpStatus.OK).body(task);

    }


    public ResponseEntity<String> deleteTask(Long taskId, String username) {
        User user = userRepositoryImplementation.findByEmail(username);
        Task task = taskRepository.findById(taskId);

        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("User with username " + username + "does not exist");
        } else if (task == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Task with ID " + taskId + " not found");
        }

        // Remove task ID from associated users
        List<User> users = userRepositoryImplementation.findByTaskId(taskId);
        for (User user1 : users) {
            user1.getTaskIds().remove(taskId);
            userRepositoryImplementation.update(user1);
        }

        // Delete the task
        taskRepository.deleteById(taskId);
        return ResponseEntity.ok("Task with ID " + taskId + " deleted successfully");
    }

    public ResponseEntity<?> filterTasksByStatus(String nameOrStatus, String username) {

        User user1 = userRepositoryImplementation.findByEmail(username);
        if (user1 == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("User with username " + username + "does not exist");
        }
        Task task = taskRepository.findByName(nameOrStatus);
        List<Task> task1 = taskRepository.findByStatus(nameOrStatus);
        task1.add(task);
        if (task == null || task1.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("There is not task with such name or status");
        }
        return ResponseEntity.status(HttpStatus.OK).body(task1);
    }
}
