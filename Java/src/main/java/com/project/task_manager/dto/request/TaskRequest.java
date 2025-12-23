package com.project.task_manager.dto.request;

import com.project.task_manager.enums.Priority;
import com.project.task_manager.enums.TaskStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TaskRequest {
    Long id;
    Long projectId;
    String title;
    String description;
    TaskStatus status;  // ← Correct
    Priority priority;  // ← Correct
    Long assignedToId;
    LocalDate startDate;
    LocalDate dueDate;
    Long parentTaskId;
    List<Long> tagIds;
}
