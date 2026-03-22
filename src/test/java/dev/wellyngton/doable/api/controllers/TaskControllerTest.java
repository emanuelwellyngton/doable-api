package dev.wellyngton.doable.api.controllers;

import dev.wellyngton.doable.api.models.Status;
import dev.wellyngton.doable.api.models.Task;
import dev.wellyngton.doable.api.models.User;
import dev.wellyngton.doable.api.repositories.TaskRepository;
import dev.wellyngton.doable.api.repositories.UserRepository;
import dev.wellyngton.doable.security.JwtService;
import dev.wellyngton.doable.security.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TaskController.class)
class TaskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TaskRepository taskRepository;

    @MockitoBean
    private UserRepository userRepository;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private UserService userService;

    private User mockUser;

    @BeforeEach
    void setUp() {
        mockUser = new User();
        mockUser.setId(1L);
        mockUser.setUsername("user");
        mockUser.setPassword("encoded-password");
        mockUser.setRole("ROLE_USER");

        when(userRepository.findByUsername("user")).thenReturn(Optional.of(mockUser));
    }

    @Test
    @WithMockUser
    void getAllTasks_shouldReturnToDoAndInProgressTasks() throws Exception {
        Task todoTask = new Task();
        todoTask.setId(1L);
        todoTask.setTitle("Todo Task");
        todoTask.setStatus(Status.TO_DO);
        todoTask.setOwner(mockUser);

        Task inProgressTask = new Task();
        inProgressTask.setId(2L);
        inProgressTask.setTitle("In Progress Task");
        inProgressTask.setStatus(Status.IN_PROGRESS);
        inProgressTask.setOwner(mockUser);

        when(taskRepository.findByStatusNotAndOwner(Status.DONE, mockUser)).thenReturn(List.of(todoTask, inProgressTask));

        mockMvc.perform(get("/api/tasks"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].status", is("TO_DO")))
                .andExpect(jsonPath("$[1].status", is("IN_PROGRESS")));
    }

    @Test
    @WithMockUser
    void getAllTasks_shouldReturnEmptyListWhenAllTasksAreDone() throws Exception {
        when(taskRepository.findByStatusNotAndOwner(Status.DONE, mockUser)).thenReturn(List.of());

        mockMvc.perform(get("/api/tasks"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    @WithMockUser
    void getAllTasks_shouldReturnOnlyTasksOwnedByCurrentUser() throws Exception {
        Task ownedTask = new Task();
        ownedTask.setId(1L);
        ownedTask.setTitle("My Task");
        ownedTask.setStatus(Status.TO_DO);
        ownedTask.setOwner(mockUser);

        when(taskRepository.findByStatusNotAndOwner(Status.DONE, mockUser)).thenReturn(List.of(ownedTask));

        mockMvc.perform(get("/api/tasks"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].title", is("My Task")));
    }
}
