package com.example.taskmanager.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class User {
    private Long id;
    private String email;
    private String firstName;
    private String lastName;
    private String password;
    private String photo;
    private Role role;
    private List<Long> taskIds;

    public void addTaskId(Long taskId) {
        if (taskIds == null) {
            taskIds = new ArrayList<>();
        }
        taskIds.add(taskId);
    }
}
