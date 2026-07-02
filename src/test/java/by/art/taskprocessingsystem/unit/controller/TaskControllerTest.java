package by.art.taskprocessingsystem.unit.controller;

import by.art.taskprocessingsystem.TestData;
import by.art.taskprocessingsystem.controller.TaskController;
import by.art.taskprocessingsystem.dto.CreateTaskRequest;
import by.art.taskprocessingsystem.dto.TaskResponse;
import by.art.taskprocessingsystem.dto.UpdateTaskRequest;
import by.art.taskprocessingsystem.entity.TaskStatus;
import by.art.taskprocessingsystem.exception.TaskNotFoundException;
import by.art.taskprocessingsystem.service.TaskService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TaskController.class)
class TaskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TaskService taskService;

    @Autowired
    private ObjectMapper objectMapper;

    @Value("${api.base.url}/tasks")
    private String restApiUrl;


    @Test
    void shouldCreateTask() throws Exception {
        CreateTaskRequest createTaskRequest = TestData.getCreateTaskRequest();
        TaskResponse taskResponse = TestData.getTaskResponse();
        when(taskService.createTask(any(CreateTaskRequest.class)))
                .thenReturn(taskResponse);
        MvcResult mvcResult = mockMvc.perform(post(restApiUrl)
                        .content(objectMapper.writeValueAsBytes(createTaskRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isCreated())
                .andReturn();

        verify(taskService, times(1)).createTask(any(CreateTaskRequest.class));

        TaskResponse actual = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), TaskResponse.class);
        assertThat(actual).isEqualTo(taskResponse);
    }

    @Test
    void shouldReturnBadRequestWhenCreateTaskRequestIsInvalid() throws Exception {
        CreateTaskRequest invalidCreateTaskRequest = TestData.getInvalidCreateTaskRequest();
        mockMvc.perform(post(restApiUrl)
                        .content(objectMapper.writeValueAsBytes(invalidCreateTaskRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest());
        verify(taskService, never()).createTask(any(CreateTaskRequest.class));
    }

    @Test
    void shouldReturnTask() throws Exception {
        TaskResponse taskResponse = TestData.getTaskResponse();
        when(taskService.getTask(taskResponse.id())).thenReturn(taskResponse);
        MvcResult mvcResult = mockMvc.perform(get(String.join("/", restApiUrl, taskResponse.id().toString()))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andReturn();
        TaskResponse actual = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), TaskResponse.class);

        verify(taskService, times(1)).getTask(any(UUID.class));
        assertThat(actual).isEqualTo(taskResponse);
    }

    @Test
    void shouldReturnNotFoundIfTaskDoesNotExist() throws Exception {
        when(taskService.getTask(any(UUID.class))).thenThrow(TaskNotFoundException.class);
        mockMvc.perform(get(String.join("/", restApiUrl, UUID.randomUUID().toString()))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound());

        verify(taskService, times(1)).getTask(any(UUID.class));
    }

    @Test
    void shouldReturnTasks() throws Exception {
        mockMvc.perform(get(restApiUrl)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().is2xxSuccessful());

        verify(taskService, times(1)).getTasks(any(Pageable.class));
    }

    @Test
    void shouldUpdateTask() throws Exception {
        UpdateTaskRequest updateTaskRequest = TestData.getUpdateTaskRequest();
        when(taskService.updateTask(any(UUID.class), any(UpdateTaskRequest.class))).thenReturn(
                TestData.getTaskResponse()
                        .toBuilder()
                        .status(TaskStatus.DONE)
                        .build());

        mockMvc.perform(patch(String.join("/", restApiUrl, UUID.randomUUID().toString()))
                        .content(objectMapper.writeValueAsBytes(updateTaskRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.status").value(updateTaskRequest.status().toString()));

        verify(taskService, times(1)).updateTask(any(UUID.class), any(UpdateTaskRequest.class));

    }

    @Test
    void shouldReturnNotFoundWhenUpdatingNotExistingTask() throws Exception {
        UpdateTaskRequest updateTaskRequest = TestData.getUpdateTaskRequest();
        when(taskService.updateTask(any(UUID.class), any(UpdateTaskRequest.class))).thenThrow(TaskNotFoundException.class);

        mockMvc.perform(patch(String.join("/", restApiUrl, UUID.randomUUID().toString()))
                        .content(objectMapper.writeValueAsBytes(updateTaskRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound());

        verify(taskService, times(1)).updateTask(any(UUID.class), any(UpdateTaskRequest.class));

    }

    @Test
    void shouldReturnBadRequestWhenUpdateTaskRequestIsInvalid() throws Exception {
        UpdateTaskRequest updateTaskRequest = TestData.getInvalidUpdateTaskRequest();

        mockMvc.perform(patch(String.join("/", restApiUrl, UUID.randomUUID().toString()))
                        .content(objectMapper.writeValueAsBytes(updateTaskRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest());

        verify(taskService, never()).updateTask(any(UUID.class), any(UpdateTaskRequest.class));
    }
}