package by.art.taskprocessingsystem.integration;

import by.art.taskprocessingsystem.TestData;
import by.art.taskprocessingsystem.dto.CreateTaskRequest;
import by.art.taskprocessingsystem.dto.TaskResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc()
public class TaskControllerIT extends AbstractIT {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @Value("${api.base.url}/tasks")
    private String restApiUrl;


    @Test
    void shouldCreateTask() throws Exception {
        CreateTaskRequest createTaskRequest = TestData.getCreateTaskRequest();
        mockMvc.perform(post(restApiUrl)
                        .content(objectMapper.writeValueAsBytes(createTaskRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    void shouldReturnTask() throws Exception {
        CreateTaskRequest createTaskRequest = TestData.getCreateTaskRequest();
        MvcResult createResult = mockMvc.perform(post(restApiUrl)
                        .content(objectMapper.writeValueAsBytes(createTaskRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().is2xxSuccessful())
                .andReturn();


        String contentAsString = createResult.getResponse().getContentAsString();
        TaskResponse createTaskResponse = objectMapper.readValue(contentAsString, TaskResponse.class);

        MvcResult getResult = mockMvc.perform(get(restApiUrl.concat("/").concat(createTaskResponse.id().toString())))
                .andExpect(status().is2xxSuccessful())
                .andDo(print())
                .andReturn();

        String getTaskContent = getResult.getResponse().getContentAsString();
        TaskResponse getTaskResponse = objectMapper.readValue(getTaskContent, TaskResponse.class);

        assertThat(getTaskResponse).isEqualTo(createTaskResponse);

    }
}
