package dev.wellyngton.doable.api.controllers;

import dev.wellyngton.doable.api.models.Status;
import dev.wellyngton.doable.api.models.Task;
import dev.wellyngton.doable.api.models.User;
import dev.wellyngton.doable.api.repositories.TaskRepository;
import dev.wellyngton.doable.api.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private UserRepository userRepository;

    private User getCurrentUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found or not authenticated"));
    }

    @GetMapping
    public List<Task> getAllTasks() {
        User currentUser = getCurrentUser();
        return taskRepository.findByStatusNotAndOwner(Status.DONE, currentUser);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Task> getTaskById(@PathVariable Long id) {
        User currentUser = getCurrentUser();
        Optional<Task> task = taskRepository.findByIdAndOwner(id, currentUser);
        return task.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Task> createTask(@RequestBody Task task) {
        User currentUser = getCurrentUser();
        task.setOwner(currentUser);
        Task createdTask = taskRepository.save(task);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdTask);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Task> updateTask(@PathVariable Long id, @RequestBody Task taskDetails) {
        User currentUser = getCurrentUser();
        Optional<Task> optionalTask = taskRepository.findByIdAndOwner(id, currentUser);

        if (optionalTask.isPresent()) {
            Task task = optionalTask.get();
            task.setTitle(taskDetails.getTitle());
            task.setDescription(taskDetails.getDescription());
            task.setStatus(taskDetails.getStatus());
            task.setOwner(currentUser);

            Task updatedTask = taskRepository.save(task);
            return ResponseEntity.ok(updatedTask);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long id) {
        User currentUser = getCurrentUser();
        Optional<Task> optionalTask = taskRepository.findByIdAndOwner(id, currentUser);

        if (optionalTask.isPresent()) {
            taskRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
